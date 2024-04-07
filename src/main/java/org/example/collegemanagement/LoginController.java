package org.example.collegemanagement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.collegemanagement.DatabaseConnector;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField studentIDTextField;

    @FXML
    private PasswordField passwordField;

    private Main main;
    public String studentID;

    // Set reference to main application
    public void setMain(Main main) {
        this.main = main;
    }

    // Handle login button click
    @FXML
    public void loginButton() throws IOException, SQLException {
        String studentID = studentIDTextField.getText();
        String password = passwordField.getText();

        // Checking user credentials against the database
        if (validateLogin(studentID, password)) {
            System.out.println("Login Successful");
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome to the portal! " + studentID);
            // After the login is successful, check whether all fields are present
            if (allFieldsPresent(studentID)) { //if all fields are present show user this dashboard
                loadStudentDashboard(studentID);
            } else {
                loadProfilePage(studentID);
                System.out.println(studentID + "studentID hereeeee");
            }
        } else {
            System.out.println("Invalid username or password");
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid studentID or password.");
        }
    }

    // Method to check if all the fields are present for that studentID
    private boolean allFieldsPresent(String studentID) throws SQLException {
        return DatabaseConnector.allFieldsPresent(studentID);
    }

    // Method to show profile page
    public void loadProfilePage(String studentID) throws IOException {
        FXMLLoader profileLoader = new FXMLLoader(getClass().getResource("profile.fxml"));
        Parent root = profileLoader.load();

        // Getting profile controller instance
        ProfileController profileController = profileLoader.getController();

        // Passing studentID to profileController
        profileController.setStudentId(studentID);

        // Create a new stage for the profile page
        Stage profileStage = new Stage();
        profileStage.setScene(new Scene(root));
        profileStage.show();

        // Closing the login window
        Stage loginStage = (Stage) studentIDTextField.getScene().getWindow();
        loginStage.close();
    }

    // Method to load student dashboard
    private void loadStudentDashboard(String studentID) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("studentFinalDashboard.fxml"));
        Parent root = loader.load();
        studentFinalDashboard controller = loader.getController();
        controller.initData(studentID);
        controller.setStudentId(studentID);
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Student Dashboard");
        stage.show();
        // Close the current login window
        ((Stage) studentIDTextField.getScene().getWindow()).close();
    }

    // Method to validate login
    private boolean validateLogin(String studentID, String password) {
        String query = "SELECT * FROM students WHERE student_id = ? AND password = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, studentID);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if there is at least one row (i.e., username and password match)
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of any error
        }
    }

    // Method to show alert
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
