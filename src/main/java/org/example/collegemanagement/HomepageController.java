package org.example.collegemanagement;

import org.example.collegemanagement.Main;
import javafx.fxml.FXML;

public class HomepageController {

    private Main main;

    public void setMain(Main main) {
        this.main = main;
    }

    @FXML
    private void registerButtonClicked() {
      main.showStudentRegisterPage();
    }

    @FXML
    private void loginButtonClicked() {
        main.showStudentLoginPage();
    }
}



