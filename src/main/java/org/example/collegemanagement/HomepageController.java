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
    private void registerButtonClicked() {
        loadFXML("register.fxml", "Student Registration");
    }

    @FXML
    private void loginButtonClicked() {
        loadFXML("login.fxml", "Student Login");
    }

    //method to loadFXML file
    private void loadFXML( String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 800, 500));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMain(Main main) {
        this.main = main;
    }
}



