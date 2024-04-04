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
import java.sql.SQLException;

public class CourseController {


    @FXML
    private CheckBox DBMS;

    @FXML
    private CheckBox JAVA;

    @FXML
    private CheckBox dsa;

    @FXML
    private CheckBox requirement;

    @FXML
    private CheckBox dataanalytics;

    @FXML
    private CheckBox OS;

    @FXML
    private Button submitBtn;

    public CourseController() throws IOException {
    }

    // Handler for submitBtn button click event
    @FXML
    private void handleButtonClick(ActionEvent event) {
        // Check if at least three courses are selected
        if (!isThreeCoursesSelected()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select at least three courses.");
            return;
        }

        // Proceed to save selected courses to the database
        saveSelectedCourses();

        //close the window after selecting the course

    }

    // Method to check if at least three courses are selected
    private boolean isThreeCoursesSelected() {
        int count = 0;
        if (DBMS.isSelected()) count++;
        if (JAVA.isSelected()) count++;
        if (dsa.isSelected()) count++;
        if (requirement.isSelected()) count++;
        if (dataanalytics.isSelected()) count++;
        if (OS.isSelected()) count++;
        return count >= 3;
    }

    // Method to save selected courses to the database
    private void saveSelectedCourses() {
        String selectedCourses = "";
        if (DBMS.isSelected()) selectedCourses += "DBMS, ";
        if (JAVA.isSelected()) selectedCourses += "JAVA, ";
        if (dsa.isSelected()) selectedCourses += "DSA, ";
        if (requirement.isSelected()) selectedCourses += "Requirement, ";
        if (dataanalytics.isSelected()) selectedCourses += "Data Analytics, ";
        if (OS.isSelected()) selectedCourses += "OS, ";

        // Remove the trailing comma and space
        if (!selectedCourses.isEmpty()) {
            selectedCourses = selectedCourses.substring(0, selectedCourses.length() - 2);
        }

        // Display selected courses in console
        System.out.println("Selected Courses: " + selectedCourses);

        // Save selected courses to the database
        try (Connection connection = DatabaseConnector.getConnection()) {
            String query = "UPDATE students SET selected_courses = ? WHERE student_id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, selectedCourses);
                preparedStatement.setString(2, studentId);
                preparedStatement.executeUpdate();
                System.out.println("Data updated in DB");

                //show the dashboard screen
                loadStudentDashboard();




            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save selected courses to the database.");
        }


    }

    // Method to show an alert dialog
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public String studentId;

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }


    //method to load the dashboard
    private void loadStudentDashboard() {
        try {
            // Load the FXML file for the dashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StudentDashboard.fxml"));
            Parent root = loader.load();

            // Access the controller for the dashboard
            StudentDashboardController studentdashboardController = loader.getController();

            // Pass the student ID to the DashboardController
            studentdashboardController.setStudentId(studentId);

            // Create a new stage for the dashboard
            Stage dashboardStage = new Stage();
            dashboardStage.setScene(new Scene(root));
            dashboardStage.setTitle("Student Dashboard");

            //close the current courses page


            // Show the dashboard stage
            dashboardStage.show();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading the dashboard.");
        }
    }
}

