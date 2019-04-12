package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.client.controller.RegistrationController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegistrationView extends View {
    private static RegistrationView instance;
    private RegistrationController controller;

    @FXML
    private TextField nicknameTextField;

    @FXML
    private TextField fullnameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private PasswordField confirmPasswordTextField;

    public static RegistrationView getInstance() {
        try {
            instance = (RegistrationView) View.loadViewFromFxml(Controller.getInstance().getStage(), "/registerView.fxml", "Register");
            RegistrationController registrationController = RegistrationController.getInstance();
            registrationController.setView(instance);
            instance.setController(registrationController);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public void setController(RegistrationController controller) {
        this.controller = controller;
    }

    @FXML
    private void cancel(ActionEvent event) {
        controller.cancel();
    }

    @FXML
    private void register(ActionEvent event) {
        controller.register();
    }

    public String getUsername() {
        return nicknameTextField.getText();
    }

    public String getPassword() {
        return passwordTextField.getText();
    }

    public String getFullName() {
        return fullnameTextField.getText();
    }

    public String getConfirmPassword() {
        return confirmPasswordTextField.getText();
    }

    public boolean isPasswordConfirmed() {
        return passwordTextField.getText().equals(confirmPasswordTextField.getText());
    }

    public boolean isFieldsFilled() {
        return !(getUsername().isEmpty() || getFullName().isEmpty() || getPassword().isEmpty() || getConfirmPassword().isEmpty());
    }
}
