import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

/**
 * Car
 */
public class Car extends JPanel {

    private String color, plate;
    private int maxSpeed, horsePower;
    private boolean editable = false; // Added editable flag

    // JComboBox for color selection
    private JComboBox<String> colorComboBox;

    // JTextFields for other attributes
    private JTextField plateTextField;
    private JTextField maxSpeedTextField;
    private JTextField horsePowerTextField;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int getHorsePower() {
        return horsePower;
    }

    public void setHorsePower(int horsePower) {
        this.horsePower = horsePower;
    }
    public void setColorBg(){
        switch (color) {
            case "White":
                this.setBackground(Color.WHITE);
                break;
            case "Red":
                this.setBackground(Color.RED);
                break;
            case "Blue":
                this.setBackground(Color.BLUE);
                break;
            case "Green":
                this.setBackground(Color.GREEN);
                break;
            case "Yellow":
                this.setBackground(Color.YELLOW);
                break;
            case "Cyan":
                this.setBackground(Color.CYAN);
                break;
            case "Gray":
                this.setBackground(Color.GRAY);
                break;
            case "Pink":
                this.setBackground(Color.PINK);
                break;
            case "Orange":
                this.setBackground(Color.ORANGE);
                break;
            default:
                this.setBackground(Color.WHITE);
                break;
        }
    }
    public Car(String color, String plate, int maxSpeed, int horsePower, JPanel parent) {
        if (!plate.matches("[A-Z]{2}[\\-|][0-9]{3}[\\-|][A-Z]{2}")) {
            throw new IllegalArgumentException("Invalid plate");
        }
        this.color = color;
        this.plate = plate;
        this.maxSpeed = maxSpeed;
        this.horsePower = horsePower;

        colorComboBox = new JComboBox<>(new String[]{"White", "Red", "Blue", "Green", "Yellow", "Cyan", "Gray", "Pink", "Orange"});
        colorComboBox.setSelectedItem(color);
        colorComboBox.setEnabled(false);

        plateTextField = new JTextField(plate);
        plateTextField.setEditable(false);

        maxSpeedTextField = new JTextField(String.valueOf(maxSpeed));
        maxSpeedTextField.setEditable(false);

        horsePowerTextField = new JTextField(String.valueOf(horsePower));
        horsePowerTextField.setEditable(false);

        this.setColorBg();
        this.setOpaque(true);
        this.setBorder(javax.swing.BorderFactory.createLineBorder(Color.BLACK));
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.add(colorComboBox);
        this.add(new JSeparator(JSeparator.VERTICAL));
        this.add(plateTextField);
        this.add(new JSeparator(JSeparator.VERTICAL));
        this.add(maxSpeedTextField);
        this.add(new JSeparator(JSeparator.VERTICAL));
        this.add(horsePowerTextField);
        this.add(new JSeparator(JSeparator.VERTICAL));

        JButton deleteBtn = new JButton("X");
        deleteBtn.addActionListener(e -> {
            parent.remove(this);
            parent.revalidate();
            parent.repaint();
        });
        this.add(deleteBtn);

        JButton editBtn = new JButton("Edit");
        editBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getActionCommand().equals("Edit")){
                    setEditableFields(true);
                    editBtn.setText("Confirm");
                }
                else if(e.getActionCommand().equals("Confirm")){
                    // Validate and update the GUI
                    setEditableFields(false);
                    updateAttributes();
                    editBtn.setText("Edit");
                }
                editBtn.revalidate();
                editBtn.repaint();
            }
        });
        this.add(editBtn);

        parent.add(this);
        // update parent
        parent.revalidate();
        parent.repaint();
    }

    // Helper method to set the editable state of fields
    private void setEditableFields(boolean editable) {
        this.editable = editable;
        colorComboBox.setEnabled(editable);
        plateTextField.setEditable(editable);
        maxSpeedTextField.setEditable(editable);
        horsePowerTextField.setEditable(editable);
        this.revalidate();
        this.repaint();
    }

    // Helper method to update attributes based on the edited fields
    private void updateAttributes() {
        // parse integers and plate, if error show a jdialog and do not update attributes
        if (!plate.matches("[A-Z]{2}[\\-|][0-9]{3}[\\-|][A-Z]{2}")) {
            JOptionPane.showMessageDialog(null, "Invalid plate", "Error", JOptionPane.ERROR_MESSAGE);
            // reset plate text field to old value
            plateTextField.setText(plate);
            plateTextField.revalidate();
            plateTextField.repaint();
            return;
        }
        if (!maxSpeedTextField.getText().matches("[0-9]+")) {
            JOptionPane.showMessageDialog(null, "Invalid max speed", "Error", JOptionPane.ERROR_MESSAGE);
            maxSpeedTextField.setText(String.valueOf(maxSpeed));
            maxSpeedTextField.revalidate();
            maxSpeedTextField.repaint();
            return;
        }
        if (!horsePowerTextField.getText().matches("[0-9]+")) {
            JOptionPane.showMessageDialog(null, "Invalid horse power", "Error", JOptionPane.ERROR_MESSAGE);
            horsePowerTextField.setText(String.valueOf(horsePower));
            horsePowerTextField.revalidate();
            horsePowerTextField.repaint();
            return;
        }
        color = (String) colorComboBox.getSelectedItem();
        plate = plateTextField.getText();
        maxSpeed = Integer.parseInt(maxSpeedTextField.getText());
        horsePower = Integer.parseInt(horsePowerTextField.getText());
        setColorBg();
    }
}
