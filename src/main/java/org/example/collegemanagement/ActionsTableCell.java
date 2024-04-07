package org.example.collegemanagement;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class ActionsTableCell<S> extends TableCell<S, Void> {

    private final Button editButton;
    private final Button addButton;

    private final Button deleteButton;

    //private final String studentID;
    private final TableView<S> tableView;


    public ActionsTableCell(TableView<S> tableView) {
        this.tableView = tableView;
        this.editButton = new Button("Edit");
        this.addButton = new Button("Add");
        this.deleteButton = new Button("Delete");

        // Define actions for the buttons
        editButton.setOnAction(event -> handleEdit());
        addButton.setOnAction(event -> handleAdd());
        deleteButton.setOnAction(event -> handleDelete());
    }

    private void handleEdit() {
        int rowIndex = getIndex();
        if (rowIndex >= 0) {
            S rowData = tableView.getItems().get(rowIndex);
            if (rowData instanceof Map) {
                Map<?, ?> mapData = (Map<?, ?>) rowData;
                Object studentID = mapData.get("student_id"); // Assuming "student_id" is the key for student ID
                if (studentID != null) {
                    loadPage("StudentDashboard.fxml", studentID.toString());
                }
            }
        }
    }

    private void handleAdd() {
        int rowIndex = getIndex();
        if (rowIndex >= 0) {
            S rowData = tableView.getItems().get(rowIndex);
            if (rowData instanceof Map) {
                Map<?, ?> mapData = (Map<?, ?>) rowData;
                Object studentID = mapData.get("student_id"); // Assuming "student_id" is the key for student ID
                if (studentID != null) {
                    openProfilePage(studentID.toString());
                }
            }
        }
    }

    private void handleDelete(){
        int rowIndex = getIndex();
        if (rowIndex >= 0) {
            S rowData = tableView.getItems().get(rowIndex);
            if (rowData instanceof Map) {
                Map<?, ?> mapData = (Map<?, ?>) rowData;
                Object studentID = mapData.get("student_id"); // Assuming "student_id" is the key for student ID
                if (studentID != null) {
                    // Call the delete method in the controller
                    ((RegistrarDashboardController) tableView.getScene().getUserData()).handleDelete(studentID.toString());
                }
            }
        }
    }

    private void openProfilePage(String studentID) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));
            Parent root = loader.load();

            // Access the controller for the profile page
            ProfileController profileController = loader.getController();

            // Pass the student ID to the ProfileController
            profileController.setStudentId(studentID);

            // Create a new stage for the profile page
            Stage profileStage = new Stage();
            profileStage.setScene(new Scene(root));
            profileStage.setTitle("Profile Page");

            // Show the profile page stage
            profileStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception
            // showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading the profile page.");
        }
    }

    private void loadPage(String fxmlFileName, String studentID) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = loader.load();

            // Accessing the appropriate controller based on the loaded FXML file
            if ("StudentDashboard.fxml".equals(fxmlFileName)) {
                StudentDashboardController studentDashboardController = loader.getController();
                studentDashboardController.setStudentId(studentID);
            } else if ("profile.fxml".equals(fxmlFileName)) {
                ProfileController profileController = loader.getController();
                profileController.setStudentId(studentID);
            }

            // Load the details of the student with the given ID
            Map<String, String> studentDetails = DatabaseConnector.getStudentDetailsById(studentID);

            // Depending on the loaded FXML, set the student details accordingly
            if ("StudentDashboard.fxml".equals(fxmlFileName)) {
                // Handle setting student details in StudentDashboardController if needed
            } else if ("profile.fxml".equals(fxmlFileName)) {
                ProfileController profileController = loader.getController();
                profileController.setStudentDetails(studentDetails);
            }

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(new HBox(editButton, addButton , deleteButton));
        }
    }
}