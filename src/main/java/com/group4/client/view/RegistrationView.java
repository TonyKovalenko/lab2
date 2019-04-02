package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.client.controller.RegistrationController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RegistrationView extends View {
    private static RegistrationView instance;
    private RegistrationController controller;

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
    private TextField nicknameTextField;

    @FXML
    private TextField fullnameTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private TextField confirmPasswordTextField;

    @FXML
    private Button registerButton;

    @FXML
    private Button cancelButton;

    @FXML
    void cancel(ActionEvent event) {
        controller.cancel();
    }

    @FXML
    void register(ActionEvent event) {
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