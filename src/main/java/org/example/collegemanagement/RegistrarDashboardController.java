package org.example.collegemanagement;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.PngImage;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        updateEntriesLabel();
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

    //method to delete the student selected Data
    @FXML
    private Button deleteSelectedButton;

    @FXML
    private void deleteSelectedRow() {
        Map<String, String> selectedRow = studentData.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            String studentID = selectedRow.get("student_id");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this student?");
            Optional<ButtonType> confirmationResult = alert.showAndWait();

            if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
                // Attempt to delete the student from the database
                boolean deleted = DatabaseConnector.deleteStudent(studentID);

                if (deleted) {
                    // Remove the selected row from the table
                    ObservableList<Map<String, String>> items = studentData.getItems();
                    items.remove(selectedRow);
                    showAlert(Alert.AlertType.INFORMATION, "Student Deleted", "The student has been successfully deleted.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete student.");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a student to delete.");
        }
    }


    //methdod to show total number of entries in the database
    private void updateEntriesLabel() {
        try {
            int totalEntries = getTotalEntries();
            entries.setText("Showing " + totalEntries + " entries.");
        } catch (SQLException e) {
            e.printStackTrace();
            entries.setText("Showing 1 to 10 of ? entries.");
        }
    }

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


    @FXML
    private void generateReport(ActionEvent event) {
        // Create a new document
        Document document = new Document();

        // Show file chooser dialog to choose where to save the PDF
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        // Provide an initial file name
        fileChooser.setInitialFileName("Student_Report_Registrar.pdf");

        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                Image logo = PngImage.getImage("C:/Users/smith/JavaProgramming/CollegeManagement/src/main/java/org/example/collegemanagement/Humber_Logo.png");
                logo.scaleToFit(100, 100);

                // Add logo to pdf
                document.add(logo);

                // Header
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
                Paragraph header = new Paragraph("HUMBER COLLEGE", headerFont);
                header.setAlignment(Element.ALIGN_CENTER);
                header.setSpacingAfter(10);
                document.add(header);

                // Title
                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
                Paragraph title = new Paragraph("Registrar Report -List of all Students Registered", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(10);
                document.add(title);

                // Student List
                for (Map<String, String> student : studentData.getItems()) {
                    for (Map.Entry<String, String> entry : student.entrySet()) {
                        // Capitalize field names
                        String fieldName = entry.getKey().toUpperCase();
                        // Add field name and value
                        document.add(new Chunk(fieldName + ": ", FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
                        document.add(new Chunk(entry.getValue() + "\n", FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK)));
                    }
                    // Add separator between students
                    document.add(new Chunk("\n---------------------------------------------------\n"));
                }

                // Footer
                Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
                Paragraph footer = new Paragraph("@CopyRight 2024 Humber College\nIntended to use by College Staff only", footerFont);
                footer.setAlignment(Element.ALIGN_CENTER);
                footer.setSpacingBefore(10);
                document.add(footer);

                showAlert(Alert.AlertType.INFORMATION, "Report Generated", "PDF report generated successfully.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate PDF report: " + e.getMessage());
            } finally {
                if (document != null) {
                    document.close();
                }
            }
        }
    }




}
