package org.example.collegemanagement;


import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Parent;


import java.io.IOException;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        showCombinedSignInPage();

    }


    //first the user will be shown combine sign in page so that he can select his role
    public void showCombinedSignInPage() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("combinesignin.fxml"));
            Parent root = loader.load();
            CombineSignInController controller = loader.getController();
            controller.setMain(this);
            primaryStage.setTitle("Welcome");
            primaryStage.setScene(new Scene(root, 800, 500));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //function to show the homepage
    public void showHomePageForStudent(){
        try {
            FXMLLoader homePageLoader = new FXMLLoader(Main.class.getResource("home.fxml"));
            Parent root = homePageLoader.load();
            HomepageController homepageController = homePageLoader.getController();
            homepageController.setMain(this);
            primaryStage.setTitle("Homepage - Student");
            primaryStage.setScene(new Scene(root, 800, 500));
            primaryStage.show();
            primaryStage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //this login page is for student
    //function to show login page
    public void showStudentLoginPage() {
        try {
            FXMLLoader loginLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
            Parent root = loginLoader.load();
            LoginController loginController = loginLoader.getController();
            loginController.setMain(this);
            primaryStage.setTitle("Login");
            primaryStage.setScene(new Scene(root, 800, 500));
            primaryStage.show();
            primaryStage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //loading and showing register page
    public void showStudentRegisterPage() {
        try {
            FXMLLoader registerloader = new FXMLLoader(Main.class.getResource("register.fxml"));
            Parent root = registerloader.load();
            RegisterController registerController = registerloader.getController();
            registerController.setMain(this);
            primaryStage.setTitle("Register");
            primaryStage.setScene(new Scene(root, 800, 500));
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("The error is " + e.getMessage());
        }
    }

    //loading profile page
    //method to show profilepage
    public void showProfilePage(String studentId) {
        try {
            FXMLLoader profileLoader = new FXMLLoader(getClass().getResource("profile.fxml"));
            Parent root = profileLoader.load();

            //passing user ID or username to the profile controller
            ProfileController profileController = profileLoader.getController();
            profileController.setStudentId(studentId);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }







    public static void main(String[] args) {
        launch();
    }
}