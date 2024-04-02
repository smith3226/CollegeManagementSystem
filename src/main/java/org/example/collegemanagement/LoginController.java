package org.example.collegemanagement;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.collegemanagement.DatabaseConnector;
import org.example.collegemanagement.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    private Main main;

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;

    // Set reference to main application
    public void setMain(Main main) {
        this.main = main;
    }

    // Handle login button click
    @FXML
    public void loginButton() {
        String username = usernameTextField.getText();
        String password = passwordField.getText();

        //checking user credentials against the database
        if (validateLogin(username, password)) {
            System.out.println("Login Successfull");
        } else {
            System.out.println("Invalid username or password");
        }

    }

    //method to validate login
    private boolean validateLogin(String username, String password) {
        String query = "SELECT * FROM students WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if there is at least one row (i.e., username and password match)
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of any error
        }
    }


    // Handle register button click
    @FXML
    public void cancelBtn() {
        main.showRegisterPage();
    }
}











