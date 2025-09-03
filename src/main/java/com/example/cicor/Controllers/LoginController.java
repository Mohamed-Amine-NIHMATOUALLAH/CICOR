package com.example.cicor.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class LoginController {
    @FXML
    private TextField Username;
    @FXML
    private PasswordField passwd;

    @FXML
    public void login(ActionEvent event) throws IOException {
        String user = Username.getText();
        String pass = passwd.getText();

        if (user.equals("admin") && pass.equals("admin")) {
            openDashboard("views/Categories.fxml", "Espace Administrateur");
            showAlert(Alert.AlertType.INFORMATION,"login success","Welcome to the Admin Dashboard in the CICOR Application ");

        } else if (user.equals("user") && pass.equals("user")) {
            openDashboard("views/UserDashboard.fxml", "Espace Utilisateur");
            showAlert(Alert.AlertType.INFORMATION,"login success","Welcome to the user Dashboard in the CICOR Application ");

        } else {
            Alert errorLogin = new Alert(Alert.AlertType.ERROR);
            errorLogin.setTitle("Login error");
            errorLogin.setContentText("Invalid password or username, try again");
            errorLogin.setHeaderText(null);
            errorLogin.show();
        }
    }

    private void openDashboard(String fxmlFile, String title) throws IOException {
        Stage stage = (Stage) Username.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/cicor/" + fxmlFile));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    public void CloseProgram(){
        Alert closeAlert = new Alert(Alert.AlertType.CONFIRMATION);
        closeAlert.setTitle("Close Confirmation");
        closeAlert.setContentText("Are you sure you want to close the program?");
        Optional<ButtonType> option = closeAlert.showAndWait();
        if (option.get() == ButtonType.OK){
            System.exit(0);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
