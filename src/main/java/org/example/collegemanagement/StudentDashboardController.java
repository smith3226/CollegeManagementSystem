package org.example.collegemanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.collegemanagement.DatabaseConnector;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class StudentDashboardController {


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
        loadStudentData();
    }

    // Method to load student data from the database based on student ID
    private void loadStudentData() throws SQLException {
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

    // Method to handle the action event when the user clicks on the "Edit" button
    @FXML
    public void handleEditEmail(ActionEvent event) {
        // Enable editing of text fields
        dashEmail.setEditable(true);
    }
    @FXML
    private void handleEditPhone(ActionEvent event) {
        // Enable editing of text fields
        dashPhone.setEditable(true);
    }
    @FXML
    private void handleEditAddress(ActionEvent event) {
        // Enable editing of text fields
        dashAddress.setEditable(true);
    }
    @FXML
    private void handleEditPassport(ActionEvent event) {
        // Enable editing of text fields
        dashPassport.setEditable(true);
    }
    @FXML
    private void handleEditDOB(ActionEvent event) {
        // Enable editing of text fields
        dashDOB.setEditable(true);
    }








      //  dashCourses.setEditable(true);


    // Method to handle the action event when the user clicks on the "Save" button
    @FXML
    private void handleSubmitBtn(ActionEvent event) {
        Dialog<ButtonType> confirmationDialog = new Dialog<>();
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Submit Application");
        confirmationDialog.setContentText("Are you sure you want to submit the application?");

        // Add buttons to the dialog
        ButtonType confirmButton = new ButtonType("Submit");
        ButtonType cancelButton = new ButtonType("Cancel");
        confirmationDialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);

        // Show the dialog and wait for user input
        Optional<ButtonType> result = confirmationDialog.showAndWait();

        // Process user input
        if (result.isPresent() && result.get() == confirmButton) {
            try (Connection connection = DatabaseConnector.getConnection()) {
                String query = "UPDATE students SET email = ?, phone = ?, address = ?, passport_number = ?, dob = ?, selected_courses = ? WHERE student_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    // Check if data has changed
                    if (!isDataChanged()) {
                        showAlert(Alert.AlertType.ERROR, "Error", "No changes detected. Please update the data first.");
                        return;
                    }

                    // Set parameter values for the prepared statement
                    preparedStatement.setString(1, dashEmail.getText());
                    preparedStatement.setString(2, dashPhone.getText());
                    preparedStatement.setString(3, dashAddress.getText());
                    preparedStatement.setString(4, dashPassport.getText());
                    preparedStatement.setString(5, dashDOB.getText());
                    preparedStatement.setString(6, dashCourses.getText());
                    preparedStatement.setString(7, studentId);

                    // Execute the update query
                    preparedStatement.executeUpdate();
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Data updated successfully!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating student data.");
            }
        }
    }

    // Method to check if data has changed
    private boolean isDataChanged() {
        return !initialEmail.equals(dashEmail.getText()) ||
                !initialPhone.equals(dashPhone.getText()) ||
                !initialAddress.equals(dashAddress.getText()) ||
                !initialPassport.equals(dashPassport.getText()) ||
                !initialDOB.equals(dashDOB.getText()) ||
                !initialCourses.equals(dashCourses.getText());
    }

    // Method to handle the action event when the user clicks on the "Back to Courses" button
    @FXML
    private void handleBackToCourses(ActionEvent event) throws IOException {
        // Load the FXML file for the courses page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("courses.fxml"));
        Parent root = loader.load();

        // Create a new stage for the courses page
        Stage coursesStage = new Stage();
        coursesStage.setScene(new Scene(root));
        coursesStage.setTitle("Courses Page");

        // Close the current dashboard stage
        ((Node) event.getSource()).getScene().getWindow().hide();

        // Show the courses page stage
        coursesStage.show();
    }

    public void initData(String studentID) {
        this.studentId = studentID;
    }
}
