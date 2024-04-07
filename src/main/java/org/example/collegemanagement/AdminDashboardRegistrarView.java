package org.example.collegemanagement;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminDashboardRegistrarView {
    @FXML
    private TableView<Map<String, String>> registrarData;

    @FXML
    private TableColumn<Map<String, String>, String> registrarID;

    @FXML
    private TableColumn<Map<String, String>, String> fullName;

    @FXML
    private TableColumn<Map<String, String>, String> email;
    @FXML
    private TableColumn<Map<String, String>, String> phone;
    @FXML
    private TableColumn<Map<String, String>, String> department;

    @FXML
    private Button addNewRegistrar;

    @FXML
    private TextField registrarIdSearch;

    @FXML
    private Label entries;

    @FXML
    private Button showStudentViewButton;


    @FXML
    public void initialize() {
        initializeColumns();
        refreshTable();
        updateEntriesLabel();
    }

    private void initializeColumns() {
        registrarID.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("register_id")));
        fullName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("name")));
        email.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("email")));
        phone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("phone")));
        department.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("department")));

    }

    //method to refresh the DB
    private void refreshTable() {
        List<Map<String, String>> registrars = DatabaseConnector.getAllRegistrars();
        registrarData.getItems().addAll(registrars);
    }

    //method to refresh the dashboard
    public void refreshDashboard() {
        registrarData.getItems().clear();
        refreshTable();
    }


    //method to add new Registrar into system
    @FXML
    public void addNewRegistrar(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("registrarSignUpPage.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Signup Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error","An error occurred while loading the student view");
        }
    }

    @FXML
    public void redirectToStudentView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("adminDashboardStudentView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Admin Dashboard - Student View");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading the student view");
        }
    }


    //method to show total entries of registrars
    private void updateEntriesLabel() {
        try {
            int totalEntries = getTotalEntries();
            entries.setText("Showing " + totalEntries + " entries.");
        } catch (Exception e) {
            entries.setText("Showing 1 to 10 of ? entries.");
        }
    }

    private int getTotalEntries() {
        // Fetch total number of registrars from the database
        return DatabaseConnector.getTotalRegistrarEntries();
    }


    //method to search the registrar by ID
    @FXML
    public void searchRegistrarByID(ActionEvent event) {
        String registrarID = registrarIdSearch.getText();
        System.out.println("Inside Registrar ID" + registrarID);
        if (!registrarID.isEmpty()) {
            registrarData.getItems().clear();
            List<Map<String, String>> allRegistrars = DatabaseConnector.getAllRegistrars();
            List<Map<String, String>> matchedRegistrars = allRegistrars.stream()
                    .filter(registrar -> registrar.get("register_id").startsWith(registrarID))
                    .collect(Collectors.toList());
            if (!matchedRegistrars.isEmpty()) {
                registrarData.getItems().addAll(matchedRegistrars);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Registrar Not Found", "No registrar found with ID starting with " + registrarID);
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Empty Field", "Please enter registrar ID");
        }
    }


    //method to delete registrar
    @FXML
    private void deleteSelectedRow() {
        Map<String, String> selectedRow = registrarData.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            String registrarID = selectedRow.get("register_id");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this student?");
            Optional<ButtonType> confirmationResult = alert.showAndWait();

            if (confirmationResult.isPresent() && confirmationResult.get() == ButtonType.OK) {
                // Attempt to delete the student from the database
                boolean deleted = DatabaseConnector.deleteRegistrar(registrarID);

                if (deleted) {
                    // Remove the selected row from the table
                    ObservableList<Map<String, String>> items = registrarData.getItems();
                    items.remove(selectedRow);
                    showAlert(Alert.AlertType.INFORMATION, "Registrar Deleted", "The registrar has been successfully deleted.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete registrar.");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a registrar to delete.");
        }
    }


    //method to show alert
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
