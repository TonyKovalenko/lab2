package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.server.model.entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class EditProfileView extends View {
    private static EditProfileView instance;
    private Controller controller;

    @FXML
    private TextField nicknameTextField;

    @FXML
    private TextField fullNameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private PasswordField confirmPasswordTextField;

    public static EditProfileView getInstance(Stage stage) {
        try {
            instance = (EditProfileView) View.loadViewFromFxml(stage, "/editProfile.fxml", "Edit profile");
            Controller controller = Controller.getInstance();
            instance.setController(controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @FXML
    public void cancel() {
        this.getStage().close();
    }

    @FXML
    void saveChanges() {
        controller.saveProfileChanges(instance);
    }

    public void setUserInfo(User currentUser) {
        nicknameTextField.setText(currentUser.getNickname());
        fullNameTextField.setText(currentUser.getFullName());
        passwordTextField.setText(currentUser.getPassword());
    }

    public String getFullName() {
        return fullNameTextField.getText();
    }

    public String getPassword() {
        return passwordTextField.getText();
    }

    public boolean isPasswordConfirmed() {
        return passwordTextField.getText().equals(confirmPasswordTextField.getText());
    }
}
