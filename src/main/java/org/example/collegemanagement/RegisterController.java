package org.example.collegemanagement;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import org.example.collegemanagement.DatabaseConnector;
import org.example.collegemanagement.Main;
import java.util.Random;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class RegisterController {


    @FXML
    private TextField fullNameField;

    @FXML
    private TextField userNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    private Main main;


    //method to handle register logic
    @FXML
    private void registerButtonClicked() {
        //getting the data from the fields
        String fullName = fullNameField.getText();
        String userName = userNameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();

        //validation
        // Validate input fields
        if (fullName.isEmpty() || userName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            System.out.println("Please fill in all fields.");
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match.");
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
            return;

        }

        // Register the student
        if (registerStudent(fullName, userName, password, phone, email)) {
            System.out.println("Student registered successfully.");
            showAlert(Alert.AlertType.INFORMATION, "Success", "Student registered successfully.");
        } else {
            System.out.println("Failed to register student.");
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to register student.");
        }
    }

    // Method to register a new student
    private boolean registerStudent(String fullName, String userName, String password, String phone, String email) {
        String studentId = generateStudentId(); // Generate a unique six-digit student ID
        String query = "INSERT INTO students (student_id, full_name, username, password, phone, email) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, studentId);
            preparedStatement.setString(2, fullName);
            preparedStatement.setString(3, userName);
            preparedStatement.setString(4, password);
            preparedStatement.setString(5, phone);
            preparedStatement.setString(6, email);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                main.showLoginPage(); //showing login page
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    // Method to generate a unique six-digit student ID
    private String generateStudentId() {
        // Generate a random six-digit number
        Random random = new Random();
        int student_id = random.nextInt(900000) + 100000; // Range from 100000 to 999999
        String studentId = "N" + student_id;
        return studentId;
    }

    // Method to show an alert dialog
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setMain(Main main) {
        this.main = main;
    }
}







