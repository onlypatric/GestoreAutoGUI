import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.prefs.Preferences;
import java.awt.event.ActionEvent;
/**
 * GUI class
 */
public class Application extends JFrame {
    private static final String WIDTH_KEY = "width";
    private static final String HEIGHT_KEY = "height";
    private static final String POS_X = "x";
    private static final String POS_Y = "y";
    private Container cp;
    JPanel carPanel;
    
    private Preferences preferences;
    public Application(){
        super();
        cp=this.getContentPane();
        cp.setLayout(new BoxLayout(cp,BoxLayout.PAGE_AXIS));
        this.setTitle("Application");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        preferences = Preferences.userNodeForPackage(Application.class);
        int width = preferences.getInt(WIDTH_KEY, 300);
        int height = preferences.getInt(HEIGHT_KEY, 400);
        int posx = preferences.getInt(POS_X, 100);
        int posy = preferences.getInt(POS_Y, 100);
        
        this.setSize(width, height);
        this.setLocation(posx, posy);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveUserDimensions();
                System.exit(0);
            }
        });
        this.setupApp();
    }
    private void setupApp(){
        cp.setBackground(Color.BLACK);
        JLabel label = new JLabel("Car manager v1.0");
        label.setBorder(makeEmptyBorder(20));
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SF Pro", Font.BOLD, 25));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        cp.add(label);


        JPanel carPanelHolder = new JPanel();
        carPanelHolder.setLayout(new BoxLayout(carPanelHolder, BoxLayout.LINE_AXIS));
        carPanel = new JPanel();
        carPanel.setLayout(new BoxLayout(carPanel, BoxLayout.PAGE_AXIS));
        carPanel.setAlignmentX(LEFT_ALIGNMENT);
        // set to be scrollable
        JScrollPane scrollPane = new JScrollPane(carPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setAlignmentX(LEFT_ALIGNMENT);
        carPanelHolder.add(scrollPane); // Add scrollPane to carPanelHolder
        cp.add(carPanelHolder); // Add carPanelHolder to cp

        // create a file menu with an add new car button with CMD and '+' accelerator
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        JMenu fileMenu = new JMenu("File");
        fileMenu.setBackground(Color.WHITE);
        menuBar.add(fileMenu);
        JMenuItem addNewCar = new JMenuItem("New vehicle");
        addNewCar.addActionListener(e->{
            new CarDialog(this,new PressedEvent() {
                public int actionPerformed(JComboBox<String> colorField, JTextField registrationPlateField, JTextField maxSpeedField, JTextField horsepowerField) {
                    // check if max speed and horsepower are valid numbers
                    if(!maxSpeedField.getText().matches("[0-9]+")){
                        JOptionPane.showMessageDialog(null, "La velocità massima deve essere un numero intero", "Errore", JOptionPane.ERROR_MESSAGE);
                        return SPEED_INVALID;
                    }
                    if(!horsepowerField.getText().matches("[0-9]+")){
                        JOptionPane.showMessageDialog(null, "La potenza motrice deve essere un numero intero", "Errore", JOptionPane.ERROR_MESSAGE);
                        return HORSEPOWER_INVALID;
                    }
                    try {
                        Car car = new Car(colorField.getSelectedItem().toString(), registrationPlateField.getText(), Integer.parseInt(maxSpeedField.getText()), Integer.parseInt(horsepowerField.getText()),carPanel);
                        carPanel.add(car);
                    } catch (Exception e) {
                        return PLATE_INVALID;
                    }
                    return SUCCESS;
                }
            });
        });
        JMenuItem exportCars = new JMenuItem("Export cars to CSV");
        exportCars.addActionListener(e->{
            exportCarsToCSV();
        });
        JMenuItem importCars = new JMenuItem("Import cars from CSV");
        importCars.addActionListener(e->{
            importCarsFromCSV();
        });
        fileMenu.add(addNewCar);
        fileMenu.add(exportCars);
        fileMenu.add(importCars);
        this.setJMenuBar(menuBar);
    }
    private void importCarsFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Cars from CSV");
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File fileToImport = fileChooser.getSelectedFile();
                BufferedReader reader = new BufferedReader(new FileReader(fileToImport));

                // Skip header
                reader.readLine();

                // Read data for each car and create Car objects
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    String color = data[0];
                    String plate = data[1];
                    int maxSpeed = Integer.parseInt(data[2]);
                    int horsePower = Integer.parseInt(data[3]);

                    // Create and add Car to the panel
                    new Car(color, plate, maxSpeed, horsePower, carPanel);
                }

                reader.close();
                JOptionPane.showMessageDialog(this, "Cars imported from CSV successfully!");
            } catch (IOException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error importing cars from CSV", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportCarsToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Cars to CSV");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                File fileToSave = fileChooser.getSelectedFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave));

                // Write header
                writer.write("Color,Plate,MaxSpeed,HorsePower");
                writer.newLine();

                // Write data for each car
                Component[] components = carPanel.getComponents();
                for (Component component : components) {
                    if (component instanceof Car) {
                        Car car = (Car) component;
                        writer.write(String.format("%s,%s,%d,%d",
                                car.getColor(), car.getPlate(), car.getMaxSpeed(), car.getHorsePower()));
                        writer.newLine();
                    }
                }

                writer.close();
                JOptionPane.showMessageDialog(this, "Cars exported to CSV successfully!");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error exporting cars to CSV", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Border makeEmptyBorder(int i) {
        return new EmptyBorder(i, i, i, i);
    }
    public void saveUserDimensions() {
        preferences.putInt(WIDTH_KEY, getWidth());
        preferences.putInt(HEIGHT_KEY, getHeight());
        preferences.putInt(POS_X, getX());
        preferences.putInt(POS_Y, getY());
    }

    public void startApp(boolean packElements){
        if(packElements) this.pack();
        this.setVisible(true);
    }
}
abstract class PressedEvent{
    public static final int SUCCESS = 0;
    public static final int PLATE_INVALID = 1;
    public static final int SPEED_INVALID = 2;
    public static final int HORSEPOWER_INVALID = 3;
    
    public abstract int actionPerformed(
        JComboBox<String> colorField,
        JTextField registrationPlateField,
        JTextField maxSpeedField,
        JTextField horsepowerField
    );
}
// class CarDialog for getting in input a car with attributes: Color, registration plate, owner, max speed and horsepower
class CarDialog extends JDialog{
    private JComboBox<String> colorField;
    private JTextField registrationPlateField;
    private JTextField maxSpeedField;
    private JTextField horsepowerField;
    private JButton okButton;
    private JButton cancelButton;
    public CarDialog(JFrame parent,PressedEvent evt){
        super(parent, "Aggiungi nuovo veicolo");
        this.setLocation(parent.getX()+50, parent.getY()+50);
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(10,10,10,10));
        mainPanel.setLayout(new GridLayout(5,2));
        this.add(mainPanel);
        colorField = new JComboBox<String>(new String[]{
            "White",
            "Black",
            "Red",
            "Blue",
            "Green",
            "Yellow",
            "Cyan",
            "Gray",
            "Rosa",
            "Orange"
        });
        registrationPlateField = new JTextField();
        maxSpeedField = new JTextField();
        horsepowerField = new JTextField();
        okButton = new JButton("OK");
        cancelButton = new JButton("Annulla");
        mainPanel.add(new JLabel("Colore: "));
        mainPanel.add(colorField);
        mainPanel.add(new JLabel("Matricola: "));
        mainPanel.add(registrationPlateField);
        mainPanel.add(new JLabel("Velocità massima (km/h): "));
        mainPanel.add(maxSpeedField);
        mainPanel.add(new JLabel("Potenza: "));
        mainPanel.add(horsepowerField);
        mainPanel.add(okButton);
        mainPanel.add(cancelButton);
        this.pack();
        this.setResizable(false);
        this.setVisible(true);
        this.okButton.addActionListener(
            e->{
                int status = evt.actionPerformed(colorField, registrationPlateField, maxSpeedField, horsepowerField);
                switch(status){
                    case PressedEvent.SUCCESS:
                        this.setVisible(false);
                        this.dispose();
                        break;
                    case PressedEvent.PLATE_INVALID:
                        JOptionPane.showMessageDialog(this, "Matricola non valida");
                        break;
                    case PressedEvent.SPEED_INVALID:
                        JOptionPane.showMessageDialog(this, "Velocità massima non valida");
                        break;
                    case PressedEvent.HORSEPOWER_INVALID:
                        JOptionPane.showMessageDialog(this, "Potenza non valida");
                        break;
                }
            }
        );
        this.cancelButton.addActionListener(
            e->{
                this.setVisible(false);
                this.dispose();
            }
        );
    }
}