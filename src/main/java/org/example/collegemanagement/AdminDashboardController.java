package org.example.collegemanagement;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminDashboardController {

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
        updateEntriesLabel();
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

    private void updateEntriesLabel() {
        try {
            int totalEntries = getTotalEntries();
            entries.setText("Showing " + totalEntries + " entries.");
        } catch (SQLException e) {
            e.printStackTrace();
            entries.setText("Showing 1 to 10 of ? entries.");
        }
    }

    //method to get total entries from the database
    private int getTotalEntries() throws SQLException {
        int count = 0;
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM students");
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        }
        return count;
    }


    //showing dashboard views
    @FXML
    private ComboBox selectedView;
    private Stage studentDashboardStage;
    private Stage registrarDashboardStage;
    @FXML
    public void handleComboBoxSelection(ActionEvent event) {
        String selectedItem =(String)selectedView.getValue();
        if (selectedItem != null) {
            switch (selectedItem) {
                case "Student Info":
                    if (studentDashboardStage == null) {
                        redirectToStudentDashboard();
                    } else {
                    if (!studentDashboardStage.isShowing()) {
                        studentDashboardStage.show();
                    } else {
                        studentDashboardStage.toFront();
                    }
                }

                    break;
                case "Registrar Info":
                    if (registrarDashboardStage == null) {
                        redirectToRegistrarDashboard();
                    }else {
                    if (!registrarDashboardStage.isShowing()) {
                        registrarDashboardStage.show();
                    } else {
                        registrarDashboardStage.toFront();
                    }
                }
                    break;
                default:
                    break;
            }
        }
    }


    private void redirectToStudentDashboard() {
        try {
            System.out.println("Redirecting to student dashboard...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("adminDashboardStudentView.fxml"));
            Parent root = loader.load();
            studentDashboardStage = new Stage();
            studentDashboardStage.setScene(new Scene(root));
            studentDashboardStage.setTitle("Admin Dashboard - Student View");
            studentDashboardStage.show();
            studentDashboardStage.setOnCloseRequest(event -> studentDashboardStage = null);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading the student dashboard.");
        }
    }

    private void redirectToRegistrarDashboard() {
        try {
            System.out.println("Redirecting to registrar dashboard...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("adminDashboardRegistrarView.fxml"));
            Parent root = loader.load();
            registrarDashboardStage = new Stage();
            registrarDashboardStage.setScene(new Scene(root));
            registrarDashboardStage.setTitle("Admin Dashboard - Registrar View");
            registrarDashboardStage.show();
            // Listener to set registrarDashboardStage to null when closed
            registrarDashboardStage.setOnCloseRequest(event -> registrarDashboardStage = null);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading the registrar dashboard.");
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
