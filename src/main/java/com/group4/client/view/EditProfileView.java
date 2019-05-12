package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.server.model.entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Represents a view of the application window for editing user profile
 */
public class EditProfileView extends View {
    private static final Logger log = Logger.getLogger(EditProfileView.class);
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

    /**
     * Gets instance of the class
     *
     * @return instance of the class
     */
    public static EditProfileView getInstance() {
        if (instance == null) {
            try {
                Stage dialogStage = View.newModalStage();
                instance = (EditProfileView) View.loadViewFromFxml(dialogStage, "/editProfile.fxml", "Edit profile");
                Controller controller = Controller.getInstance();
                instance.setController(controller);
                instance.getStage().setOnCloseRequest(windowEvent -> {
                    instance = null;
                });
            } catch (IOException e) {
                log.error("Can't get instance of EditProfileView.", e);
                throw new RuntimeException("Can't get instance of EditProfileView.", e);
            }
        }
        return instance;
    }

    /**
     * Sets controller for the view
     *
     * @param controller controller for the view
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Event handler for "Cancel" button click. Cancels current view
     */
    @FXML
    public void cancel() {
        this.getStage().close();
        instance = null;
    }

    /**
     * Event handler for "Save changes" button click
     */
    @FXML
    public void saveChanges() {
        controller.saveProfileChanges(instance);
    }

    /**
     * Sets user info to be displayed in current view
     *
     * @param currentUser
     */
    public void setUserInfo(User currentUser) {
        user = currentUser;
        nicknameTextField.setText(user.getNickname());
        fullNameTextField.setText(user.getFullName());
    }

    /**
     * Gets entered full name
     *
     * @return entered full name
     */
    public String getFullName() {
        return fullNameTextField.getText();
    }

    /**
     * Gets entered password
     *
     * @return entered password
     */
    public String getPassword() {
        return passwordTextField.getText();
    }

    /**
     * Returns {@code true}  if password is confirmed, otherwise {@code false} .
     *
     * @return {@code true}  if password is confirmed
     */
    public boolean isPasswordConfirmed() {
        return passwordTextField.getText().equals(confirmPasswordTextField.getText());
    }

    /**
     * Gets current user whose info is displayed view
     *
     * @return current user whose info is displayed view
     */
    public User getUser() {
        return user;
    }
}
