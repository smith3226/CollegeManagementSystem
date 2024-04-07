package org.example.collegemanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class registrarSignupController {
    @FXML
    private TextField registerIdField;

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
    void registerButtonClicked(ActionEvent event) {

        //generate Registrar id
        String registrarId = generateRegisterId();
        //saving the registrar_id into registerField
        registerIdField.setText(registrarId);
        if (validateInput()) {
            saveRegistrarDetails();
        }
    }


    // Method to generat register_id for the registrar
    private String generateRegisterId() {
        // Initialize a Random object
        Random random = new Random();

        // Generate a random 3-digit number
        int randomNumber = random.nextInt(900) + 100;
        String registrarId = "R01" + randomNumber;

        return registrarId;
    }


    //method to validate Input

    private boolean validateInput() {
        String registerId = registerIdField.getText();
        String name = nameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String department = departmentField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();

        if (registerId.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || department.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Fields", "Please fill in all the fields.");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Password Mismatch", "Passwords do not match.");
            return false;
        }

        // Additional validation can be added for email format, phone number format, etc.

        return true;
    }

    private void saveRegistrarDetails() {
        try (Connection connection = DatabaseConnector.getConnection()) {
            String query = "INSERT INTO registrar_details (register_id, name, password, department, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, registerIdField.getText());
            statement.setString(2, nameField.getText());
            statement.setString(3, passwordField.getText());
            statement.setString(4, departmentField.getText());
            statement.setString(5, emailField.getText());
            statement.setString(6, phoneField.getText());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Registrar registered successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to register registrar.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
