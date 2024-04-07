package org.example.collegemanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class registrarSignupController {
    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField departmentField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private Button registerButton;

    @FXML
    public void registerRegistrar() {
        String name = nameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String department = departmentField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        // Validation
        if (name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || department.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Error", "Please fill in all the fields.");
            return; // Stop registration process
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Error", "Passwords do not match.");
            return; // Stop registration process
        }

        // Check if phone number is ten digits
        if (phone.length() != 10) {
            showAlert(Alert.AlertType.WARNING, "Error", "Phone number must be 10 digits.");
            return; // Stop registration process
        }

        //chcecking email validation
        if (!email.endsWith("@humber.ca")) {
            showAlert(Alert.AlertType.WARNING, "Error", "Email must end with @humber.ca.");
            return; // Stop registration process
        }

        // Call function to save the details if registration is successfully
        if (saveRegistrarDetails()) {
            // Registration successful
            clearFields();


        }
    }

    // Method to generate register_id for the registrar
    private String generateRegisterId() {
        // Initialize a Random object
        Random random = new Random();

        // Generate a random 3-digit number
        int randomNumber = random.nextInt(9000) + 1000;
        return "R01" + randomNumber;
    }

    // Method to save registrar details into the database
    private boolean saveRegistrarDetails() {
        try (Connection connection = DatabaseConnector.getConnection()) {
            String registrarId = generateRegisterId();
            String query = "INSERT INTO registrar_details (register_id, name, password, department, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, registrarId);
            statement.setString(2, nameField.getText());
            statement.setString(3, passwordField.getText());
            statement.setString(4, departmentField.getText());
            statement.setString(5, emailField.getText());
            statement.setString(6, phoneField.getText());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Registrar registered successfully.");
                return true;
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to register registrar.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + e.getMessage());
        }
        return false;
    }

    // Method to show an alert dialog
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to clear input fields after successful registration
    private void clearFields() {
        nameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        departmentField.clear();
        emailField.clear();
        phoneField.clear();
    }
}
