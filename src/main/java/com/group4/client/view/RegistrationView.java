package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.client.controller.RegistrationController;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Represents a view of the application registration window
 */
public class RegistrationView extends View {
    private static final Logger log = Logger.getLogger(RegistrationView.class);
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

    /**
     * Gets instance of the class
     *
     * @return instance of the class
     */
    public static RegistrationView getInstance() {
        try {
            instance = (RegistrationView) View.loadViewFromFxml(Controller.getInstance().getStage(), "/registerView.fxml", "Register");
            RegistrationController registrationController = RegistrationController.getInstance();
            registrationController.setView(instance);
            instance.setController(registrationController);
        } catch (IOException e) {
            log.error("Can't get instance of RegistrationView.", e);
            throw new RuntimeException("Can't get instance of RegistrationView.", e);
        }
        return instance;
    }

    /**
     * Sets controller for the view
     *
     * @param controller controller for the view
     */
    public void setController(RegistrationController controller) {
        this.controller = controller;
    }

    /**
     * Event handler for "Cancel" button click
     */
    @FXML
    public void cancel() {
        controller.cancel();
    }

    /**
     * Event handler for "Register" link click
     */
    @FXML
    public void register() {
        controller.register();
    }

    /**
     * Gets the text value of username text field
     *
     * @return the text value of username text field
     */
    public String getUsername() {
        return nicknameTextField.getText();
    }

    /**
     * Gets the text value of password text field
     *
     * @return the text value of password text field
     */
    public String getPassword() {
        return passwordTextField.getText();
    }

    /**
     * Gets the text value of fullname text field
     *
     * @return the text value of fullname text field
     */
    public String getFullName() {
        return fullnameTextField.getText();
    }

    /**
     * Gets the text value of confirmed password text field
     *
     * @return the text value of confirmed password text field
     */
    public String getConfirmPassword() {
        return confirmPasswordTextField.getText();
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
     * Returns {@code true}  if all fields are filled, otherwise {@code false} .
     *
     * @return {@code true}  if all fields are filled
     */
    public boolean isFieldsFilled() {
        return !(getUsername().isEmpty() || getFullName().isEmpty() || getPassword().isEmpty() || getConfirmPassword().isEmpty());
    }
}
