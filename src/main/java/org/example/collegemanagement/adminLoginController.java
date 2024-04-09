package org.example.collegemanagement;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class adminLoginController {

    @FXML
    public TextField adminIDTextField;

    @FXML
    public PasswordField passwordField;

    @FXML
    public Button loginBtn;

    @FXML
    public Button cancelBtn;

    //method to handle login
    @FXML
    public void handleLogin() throws SQLException {
        String adminID = adminIDTextField.getText();
        String password = passwordField.getText();

        //validate Login
        if (validateAdminCredentials(adminID, password)) {
            showAlert(Alert.AlertType.CONFIRMATION, "Login Successful", "Welcome to Admin Dashboard");

            //close the login widnow
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.close();

            //if login is successfull open the admin dasbboard
            showAdminDashboard();
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid adminID or password.");
        }
    }


    //method to validate admin credentials
    private boolean validateAdminCredentials(String adminID, String password) throws SQLException {
        String query = "SELECT * FROM admins WHERE admin_id= ? AND password = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, adminID);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if there is at least one row (i.e., adminID and password match)
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of any error
        }
    }


    //method to display Alert
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    //method to show adminDashboard
    public void showAdminDashboard() {
        // Show loading animation
        StackPane root = new StackPane();
        // Create a ProgressIndicator as a loading animation
        ProgressIndicator loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(100, 100);

        // Add the loading animation to the StackPane
        root.getChildren().add(loadingIndicator);

        Stage loadingStage = new Stage();
        loadingStage.setScene(new Scene(root, 1000, 1000));
        loadingStage.setTitle("Loading");
        loadingStage.show();

        // Load the admin dashboard in a separate thread to prevent freezing UI
        new Thread(() -> {
            try {
                // Simulate a delay for demonstration purposes (replace with actual loading code)
                Thread.sleep(1000);

                // Load the admin dashboard FXML file
                FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("adminDashboardStudentView.fxml"));
                Parent adminRoot = adminLoader.load();

                // Update UI on JavaFX Application Thread
                Platform.runLater(() -> {
                    // Create a new stage for the admin dashboard
                    Stage adminStage = new Stage();
                    adminStage.setScene(new Scene(adminRoot));
                    adminStage.setTitle("Admin Dashboard");

                    // Show the admin dashboard
                    adminStage.show();

                    // Close the loading overlay
                    loadingStage.close();
                });
            } catch (IOException | InterruptedException e) {
                // Handle exceptions
                e.printStackTrace();
            }
        }).start();

    }
}




