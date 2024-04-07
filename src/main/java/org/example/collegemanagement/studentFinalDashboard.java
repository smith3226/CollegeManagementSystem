package org.example.collegemanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class studentFinalDashboard {

    public String studentId;

    @FXML
    private Label headerName;
    @FXML
    private TextField dashEmail;
    @FXML
    private TextField dashPhone;
    @FXML
    private TextField dashPassport;
    @FXML
    private TextField dashAddress;
    @FXML
    private TextField dashDOB;
    @FXML
    private Label dashCourses;
    @FXML
    private Label dashStatus;

    // Declare fields to store initial values
    private String initialEmail;
    private String initialPhone;
    private String initialAddress;
    private String initialPassport;
    private String initialDOB;
    private String initialCourses;
    private String initialStatus;

    public void setStudentId(String studentId) throws SQLException {
        this.studentId = studentId;
        loadStudentData(studentId);
    }

    // Method to load student data from the database based on student ID
    private void loadStudentData(String studentId) throws SQLException {
        try (Connection connection = DatabaseConnector.getConnection()) {
            String query = "SELECT * FROM students WHERE student_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, studentId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        headerName.setText("Welcome " + resultSet.getString("full_name") + " - (" + resultSet.getString("student_id") + ")");
                        dashEmail.setText(resultSet.getString("email"));
                        dashPhone.setText(resultSet.getString("phone"));
                        dashPassport.setText(resultSet.getString("passport_number"));
                        dashAddress.setText(resultSet.getString("address"));
                        dashDOB.setText(resultSet.getString("dob"));
                        dashCourses.setText(resultSet.getString("selected_courses"));
                        dashStatus.setText(resultSet.getString("status"));

                        // Store initial values for comparison
                        initialEmail = resultSet.getString("email");
                        initialPhone = resultSet.getString("phone");
                        initialAddress = resultSet.getString("address");
                        initialPassport = resultSet.getString("passport_number");
                        initialDOB = resultSet.getString("dob");
                        initialCourses = resultSet.getString("selected_courses");
                        initialStatus = resultSet.getString("status");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading student data.");
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


    public void initData(String studentID) {
        this.studentId = studentID;
        System.out.println("I am inside studentdashboard controller");
    }
}


