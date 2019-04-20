package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.server.model.entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class EditProfileView extends View {
    private static EditProfileView instance;
    private Controller controller;
    private User user;

    @FXML
    private TextField nicknameTextField;

    @FXML
    private TextField fullNameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private PasswordField confirmPasswordTextField;

    public static EditProfileView getInstance() {
        if (instance == null) {
            try {
                System.out.println("EditProfileView getInstance");
                Stage dialogStage = new Stage();
                dialogStage.initOwner(Controller.getInstance().getStage());
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                instance = (EditProfileView) View.loadViewFromFxml(dialogStage, "/editProfile.fxml", "Edit profile");
                Controller controller = Controller.getInstance();
                instance.setController(controller);
                instance.getStage().setOnCloseRequest(windowEvent -> {
                    instance = null;
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("EditProfileView getInstance return");
        return instance;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @FXML
    public void cancel() {
        this.getStage().close();
        instance = null;
    }

    @FXML
    private void saveChanges() {
        controller.saveProfileChanges(instance);
    }

    public void setUserInfo(User currentUser) {
        user = currentUser;
        nicknameTextField.setText(user.getNickname());
        fullNameTextField.setText(user.getFullName());
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

    public User getUser() {
        return user;
    }
}
