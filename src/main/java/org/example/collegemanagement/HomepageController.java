package org.example.collegemanagement;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
public class HomepageController {


    private Main main;

    @FXML
    private void registerButtonClicked(ActionEvent event) {
        loadFXML("register.fxml", "Student Registration", event);
    }

    @FXML
    private void loginButtonClicked(ActionEvent event) {
        loadFXML("login.fxml", "Student Login", event);
    }

    // Method to load FXML file
    private void loadFXML(String fxmlFile, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 800, 500));
            stage.setResizable(false);
            stage.setTitle(title);
            stage.show();

            // Close the current stage (homepage)
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMain(Main main) {
        this.main = main;
    }
}



