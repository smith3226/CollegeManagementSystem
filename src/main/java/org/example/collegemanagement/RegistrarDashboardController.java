package org.example.collegemanagement;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RegistrarDashboardController  {
    @FXML
    private TableView<Map<String, String>> studentData;

    @FXML
    private TableColumn<Map<String, String>, String> studentID;
    @FXML
    private TableColumn<Map<String, String>, String> fullName;
    @FXML
    private TableColumn<Map<String, String>, String> email;
    @FXML
    private TableColumn<Map<String, String>, String> address;
    @FXML
    private TableColumn<Map<String, String>, String> phone;
    @FXML
    private TableColumn<Map<String, String>, String> emergencyContact;
    @FXML
    private TableColumn<Map<String, String>, String> passportNo;
    @FXML
    private TableColumn<Map<String, String>, String> dob;
    @FXML
    private TableColumn<Map<String, String>, String> previousDegree;
    @FXML
    private TableColumn<Map<String, String>, String> coursesApplied;
    @FXML
    private TableColumn<Map<String, String>, String> status;

    // Getting registerStudent button
    @FXML
    private Button addNewStudent;

    @FXML
    private TableColumn<Map<String, String>, Void> actionsColumn;

    @FXML
    private TextField studentIdSearch;

    @FXML
    private Label entries;

    @FXML
    public void initialize() {
        actionsColumn.setCellFactory(col -> new ActionsTableCell<>(studentData));
        initializeColumns();
        refreshTable();
        setSceneUserData();
       // updateEntriesLabel();
    }

    private void setSceneUserData() {
        Scene scene = studentData.getScene();
        if (scene != null) {
            scene.setUserData(this); // Set the controller instance as UserData
        } else {
            System.err.println("Scene is null. TableView is not yet added to a Scene.");
        }
    }
    private void initializeColumns() {
        studentID.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("student_id")));
        fullName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("full_name")));
        email.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("email")));
        address.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("address")));
        phone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("phone")));
        emergencyContact.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("emergencyContact")));
        passportNo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("passportNo")));
        dob.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("dob")));
        previousDegree.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("previousDegree")));
        coursesApplied.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("selected_courses")));
        status.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("status")));

        status.setCellFactory(tc -> {
            ComboBox<String> comboBox = new ComboBox<>();
            comboBox.getItems().addAll("Accepted", "Rejected", "Conditionally Accepted");

            TableCell<Map<String, String>, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        comboBox.setValue(item);
                        comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                            Map<String, String> student = getTableView().getItems().get(getIndex());
                            student.put("status", newVal);
                            DatabaseConnector.updateStudentStatus(student.get("student_id"), newVal);
                        });
                        setGraphic(comboBox);
                    }
                }
            };

            return cell;
        });
    }

    private void refreshTable() {
        List<Map<String, String>> students = DatabaseConnector.getAllStudents();
        studentData.getItems().addAll(students);
    }

    public void refreshDashboard() {
        studentData.getItems().clear();
        refreshTable();
    }

    @FXML
    public void addNewStudent() throws IOException {
        FXMLLoader profileLoader = new FXMLLoader(getClass().getResource("register.fxml"));
        Parent root = profileLoader.load();
        // Create a new stage for the profile page
        Stage profileStage = new Stage();
        profileStage.setScene(new Scene(root));
        profileStage.show();

    }

    @FXML
    public void searchStudentByID() {
        String studentID = studentIdSearch.getText();
        if (!studentID.isEmpty()) {
            studentData.getItems().clear();
            List<Map<String, String>> allStudents = DatabaseConnector.getAllStudents();
            List<Map<String, String>> matchedStudents = allStudents.stream()
                    .filter(student -> student.get("student_id").startsWith(studentID))
                    .collect(Collectors.toList());
            if (!matchedStudents.isEmpty()) {
                studentData.getItems().addAll(matchedStudents);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Student Not Found", "No student found with ID starting with " + studentID);
                System.out.println("No student found with ID starting with " + studentID);
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Empty Field", "Please enter student ID");
            System.out.println("Please enter Student ID");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleDelete(String studentID) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this student?");
        Optional<ButtonType> confirmationResult = alert.showAndWait();

        if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
            // Attempt to delete the student from the database
            boolean deleted = DatabaseConnector.deleteStudent(studentID);

            if (deleted) {
                // Remove the student from the table
                studentData.getItems().removeIf(row -> row.get("student_id").equals(studentID));
                showAlert(Alert.AlertType.INFORMATION, "Student Deleted", "The student has been successfully deleted.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete student.");
            }
        }
    }
}
