package org.example.collegemanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class CombineSignInController {

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private Button signInButton;

    @FXML
    private void initialize() {
        comboBox.setOnAction(this::handleComboBoxAction);
        signInButton.setDisable(true);
    }

    private void handleComboBoxAction(ActionEvent event) {
        String selectedOption = comboBox.getValue();
        signInButton.setDisable(selectedOption == null); // Enable the sign-in button if an option is selected
    }

    //method for signin button
    @FXML
    private void handleSignIn(ActionEvent event) {
        String selectedOption = comboBox.getValue();
        String loginPage = null;

        switch (selectedOption) {
            case "Student":
                loginPage = "home.fxml";
                break;
            case "Admin":
                loginPage = "adminLogin.fxml";
                break;
            case "Registrar":
                loginPage = "registrarLogin.fxml";
                break;
        }

        //selected the option from the user
        if (loginPage != null) {
            loadLoginPage(loginPage);
        }
    }

    //method to load FXML file

    private void loadLoginPage(String fxmlFile) {
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
    }
}
