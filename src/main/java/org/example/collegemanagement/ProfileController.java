package org.example.collegemanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ProfileController {

    //reference to keep trakc of curent page
    private Stage profileStage;

    private Stage coursesStage;


    // Method to set the profile page stage
    public void setProfileStage(Stage profileStage) {
        this.profileStage = profileStage;
    }

    public String studentId;

    // Take fields from profile fxml to dynamically display details

    @FXML
    public Text bannerName;

    @FXML
    public Label displayName;

    @FXML
    public Label emailDisplay;

    @FXML
    public Label phoneDisplay;

    //submitting details
    //1.get the details from student fxml
    @FXML
    private TextField addressDisplay;
    @FXML
    private TextField passportDisplay;
    @FXML
    private DatePicker dobDisplay;
    @FXML
    private TextField emergDisplay;
    @FXML
    private ComboBox<String> degreeDisplay;
    @FXML
    private TextField gpaDisplay;
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

    //intake
    @FXML
    private CheckBox fall;

    @FXML
    private CheckBox winter;

    @FXML
    private CheckBox summer;

    @FXML
    private CheckBox summ25;

    // Setting userId
    public void setStudentId(String studentId) {
        this.studentId = studentId;
        System.out.println("Student ID is " + studentId);
        initialize();
    }

    // Fetch student data from DB
    @FXML
    private void initialize() {
        if (studentId != null && !studentId.isEmpty()) {
            // Fetch student data from the database based on the ID
            try (Connection connection = DatabaseConnector.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT student_id, full_name, email , phone FROM students WHERE student_id  = ?")) {
                preparedStatement.setString(1, studentId); // Set the student Id
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Populate the UI elements with the fetched data
                        String fullName = resultSet.getString("full_name");
                        String email = resultSet.getString("email");
                        String phone = resultSet.getString("phone");
                        bannerName.setText("Student Profile " + "- " + fullName + " (" + studentId + ")");
                        displayName.setText(fullName);
                        emailDisplay.setText(email);
                        phoneDisplay.setText(phone);

                        System.out.println(bannerName.getText());
                        System.out.println(displayName.getText());
                        System.out.println(phoneDisplay.getText());
                        System.out.println("Email Display: " + emailDisplay.getText());

                    } else {
                        // Handle the case where no student data is found
                        System.out.println("Student not found");
                    }
                }
            } catch (SQLException e) {
                // Handle SQL exception
                e.printStackTrace();
            }
        } else {
            System.out.println("Student ID is null or empty");
        }
    }

    //validations
    //1.phonenumber  should be 10 digit
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{10}");
    }

    // Validation method for passport number
    private boolean isValidPassportNumber(String passportNumber) {
        return passportNumber.matches("[a-zA-Z0-9]{8}");
    }

    // Validation method for emergency contact
    private boolean isValidEmergencyContact(String emergencyContact, String phoneNumber) {
        return !emergencyContact.equals(phoneNumber);
    }

    // Validation method for date of birth
    private boolean isValidDateOfBirth(LocalDate dob) {
        return dob.plusYears(50).isAfter(LocalDate.now());
    }

    // Validation method for GPA
    private boolean isValidGPA(String gpa) {
        try {
            double gpaValue = Double.parseDouble(gpa);
            return gpaValue >= 0 && gpaValue <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //Handling the students details to the dashboard
    @FXML
    private void handleNext(ActionEvent event) {
        if (studentId == null || studentId.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Student ID is null or empty.");
            return;
        }

        // Get details and selected courses from UI components
        String address = addressDisplay.getText();
        String phoneNumber = phoneDisplay.getText();
        String passportNumber = passportDisplay.getText();
        LocalDate dob = dobDisplay.getValue();
        String emergencyContact = emergDisplay.getText();
        String degree = degreeDisplay.getValue();
        String gpa = gpaDisplay.getText();

        // Validate input fields
        if (!isValidPhoneNumber(phoneNumber)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Phone Number", "Phone number must be a 10-digit number.");
            return;
        }

        if (!isValidPassportNumber(passportNumber)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Passport Number", "Passport number must be 8 characters long and alphanumeric.");
            return;
        }

        if (!isValidEmergencyContact(emergencyContact, phoneNumber)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Emergency Contact", "Emergency contact cannot be the same as phone number.");
            return;
        }

        if (!isValidDateOfBirth(dob)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Date of Birth", "Date of birth cannot be more than 50 years ago.");
            return;
        }

        if (!isValidGPA(gpa)) {
            showAlert(Alert.AlertType.ERROR, "Invalid GPA", "GPA must be a number between 0 and 100.");
            return;
        }

        //convert date to LocalDate
        java.sql.Date sqlDate = java.sql.Date.valueOf(dob);

        // Save details and selected courses to the database
        try (
            Connection connection = DatabaseConnector.getConnection()){
            // Save details to the database
            String updateQuery = "UPDATE students SET address = ?, phone = ?, " +
                    "passport_number = ?, dob = ?, emergency_contact = ?, degree = ?, gpa = ? WHERE student_id = ?";
            try(PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

                //setting parameters to preparedstatement
                preparedStatement.setString(1, address);
                preparedStatement.setString(2, phoneNumber);
                preparedStatement.setString(3, passportNumber);
                preparedStatement.setDate(4, sqlDate);
                preparedStatement.setString(5, emergencyContact);
                preparedStatement.setString(6, degree);
                preparedStatement.setString(7, gpa);
                preparedStatement.setString(8, studentId);

                // Execute the SQL statement
                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Data inserted successfully!");
                    // Add code here to display the dashboard or perform other actions
                } else {
                    System.out.println("Failed to insert data.");
                }

                showAlert(Alert.AlertType.INFORMATION, "Success", "Details saved successfully");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while saving details and courses.");

        }

        String selectedIntake = null;
        LocalDate startDate = null;
        if (fall.isSelected()) {
            selectedIntake = fall.getText();
            startDate = LocalDate.of(2024, 9, 5);
        } else if (summer.isSelected()) {
            selectedIntake = summer.getText();
            startDate = LocalDate.of(2024, 5, 7);
        } else if (winter.isSelected()) {
            selectedIntake = winter.getText();
            startDate = LocalDate.of(2025, 1, 8);
        } else if (summ25.isSelected()) {
            selectedIntake = summ25.getText();
            startDate = LocalDate.of(2025, 5, 5);
        }

        if (selectedIntake != null && startDate != null) {
            try {
                Connection connection = DatabaseConnector.getConnection();
                saveIntake(connection, selectedIntake, startDate,studentId);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while saving courses intake.");
            }
        }

        //load courses page
        loadCoursesPage(event);
    }

    //method to saveIntake
    private void saveIntake(Connection connection, String intakeName, LocalDate startDate , String studentId) throws SQLException {
        String intakeQuery = "INSERT INTO courses_intake (intake_name, start_date , student_id) VALUES (?, ?,?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(intakeQuery)) {
            preparedStatement.setString(1, intakeName);
            preparedStatement.setDate(2, java.sql.Date.valueOf(startDate));
            preparedStatement.setString(3,studentId);
            preparedStatement.executeUpdate();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    //method to load courses page
    public void loadCoursesPage(ActionEvent event){
            try {
                // Load the FXML file for the courses page
                FXMLLoader loader = new FXMLLoader(getClass().getResource("courses.fxml"));
                Parent root = loader.load();

                // Access the controller for the courses page
                CourseController courseController = loader.getController();

                // Pass the student ID to the CourseController
                courseController.setStudentId(studentId);

                // Create a new stage for the courses page
                Stage coursesStage = new Stage();
                coursesStage.setScene(new Scene(root));
                coursesStage.setTitle("Courses Page");

                // Set the profile page stage as the owner of the courses page stage
                coursesStage.initOwner(((Node) event.getSource()).getScene().getWindow());

                // Close the profile page stage
                ((Node) event.getSource()).getScene().getWindow().hide();

                // Show the courses page stage
                coursesStage.show();

            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading the courses page.");
            }
        }



    }

