package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.client.controller.LoginController;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginView extends View {
    private static LoginView instance;
    private LoginController controller;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    public static LoginView getInstance() {
        try {
            instance = (LoginView) View.loadViewFromFxml(Controller.getInstance().getStage(), "/loginView.fxml", "Login");
            LoginController loginController = LoginController.getInstance();
            loginController.setView(instance);
            instance.setController(loginController);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public void setController(LoginController controller) {
        this.controller = controller;
    }

    @FXML
    public void onLoginClick() {
        controller.login();
    }

    @FXML
    public void onRegisterClick() {
        System.out.println(controller);
        controller.register();
    }

    public String getUsername() {
        return usernameTextField.getText();
    }

    public String getPassword() {
        return passwordTextField.getText();
    }
}
