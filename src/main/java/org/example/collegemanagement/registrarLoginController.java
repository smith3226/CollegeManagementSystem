package org.example.collegemanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class registrarLoginController {

    @FXML
    private TextField registrarIDTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void loginButton(ActionEvent event) {
        String registrarID = registrarIDTextField.getText();
        String password = passwordField.getText();

        // Check if the fields are empty
        if (registrarID.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Fields", "Please enter both Registrar ID and Password.");
            return;
        }

        // Validate the login credentials
        if (validateLogin(registrarID, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, Registrar!");
            //show regirstrar admin dashboard
            redirectToRegistrarDashboard();

        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid Registrar ID or Password.");
        }
    }

    // Method to validate login credentials
    private boolean validateLogin(String registrarID, String password) {
        try (Connection connection = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM registrar_details WHERE register_id = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, registrarID);
                preparedStatement.setString(2, password);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // Returns true if a matching record is found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while validating login credentials.");
            return false;
        }
    }


    //method to show Registrar Admin Dashboard

    private void redirectToRegistrarDashboard() {
        try {
            // Load the FXML file for the registrar dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("registrarDashboard.fxml"));
            Parent root = loader.load();

            // Create a new stage for the registrar dashboard
            Stage registrarDashboardStage = new Stage();
            registrarDashboardStage.setScene(new Scene(root));
            registrarDashboardStage.setTitle("Registrar Dashboard");

            // Hide the login window
            Stage loginStage = (Stage) registrarIDTextField.getScene().getWindow();
            loginStage.close();

            // Show the registrar dashboard stage
            registrarDashboardStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading the registrar dashboard.");
        }
    }


    // Method to display an alert dialog
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
