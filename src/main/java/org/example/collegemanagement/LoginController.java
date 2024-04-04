package org.example.collegemanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.collegemanagement.DatabaseConnector;
import org.example.collegemanagement.Main;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    private Main main;
    public String studentID;

    @FXML
    private TextField studentIDTextField;
    @FXML
    private PasswordField passwordField;

    // Set reference to main application
    public void setMain(Main main) {
        this.main = main;
    }

    // Handle login button click
    @FXML
    public void loginButton() throws IOException {
        String studentID = studentIDTextField.getText();
        String password = passwordField.getText();

        //checking user credentials against the database
        if (validateLogin(studentID, password)) {
            System.out.println("Login Successfull");
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome to the portal! " + studentID);
            //after the login is successfull show the student profilepage
            this.studentID = studentID;
            loadProfilePage(studentID);
            System.out.println(studentID + "studentID hereeeee");
        } else {
            System.out.println("Invalid username or password");
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid studentID or password.");
        }

    }

    //method to show profilepage
    public void loadProfilePage(String studentID) throws IOException {
        FXMLLoader profileLoader = new FXMLLoader(getClass().getResource("profile.fxml"));
        Parent root = profileLoader.load();

        //getting profilecontroller instance
        ProfileController profileController = profileLoader.getController();

        //passing studentID to profileController
        profileController.setStudentId(studentID);


        // Create a new stage for the profile page
        Stage profileStage = new Stage();
        profileStage.setScene(new Scene(root));
        profileStage.show();

        //closing the login window
        Stage loginStage = (Stage) studentIDTextField.getScene().getWindow();
         loginStage.close();

    }

    //method to validate login
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


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



   // @FXML
//    public void cancelBtn() {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
//            Parent root = loader.load();
//           // Stage stage = (Stage) studentIDTextField.getScene().getWindow();
//            //stage.setScene(new Scene(root));
//            //stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
  //  }
}




