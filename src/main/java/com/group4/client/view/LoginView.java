package com.group4.client.view;

import com.group4.client.controller.LoginController;
import com.group4.client.controller.impl.ControllerImpl;
import com.group4.client.controller.impl.LoginControllerImpl;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Represents a view of the application login window
 */
public class LoginView extends View {
    private static final Logger log = Logger.getLogger(LoginView.class);
    private static LoginView instance;
    private LoginController controller;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    /**
     * Gets instance of the class
     *
     * @return instance of the class
     */
    public static LoginView getInstance() {
        try {
            instance = (LoginView) View.loadViewFromFxml(ControllerImpl.getInstance().getStage(), "/loginView.fxml", "Login");
            LoginController loginController = LoginControllerImpl.getInstance();
            loginController.setView(instance);
            instance.setController(loginController);
        } catch (IOException e) {
            log.error("Can't get instance of LoginView.", e);
            throw new RuntimeException("Can't get instance of LoginView.", e);
        }
        return instance;
    }

    /**
     * Sets controller for the view
     *
     * @param controller controller for the view
     */
    public void setController(LoginController controller) {
        this.controller = controller;
    }

    /**
     * Event handler for "Login" button click
     */
    @FXML
    public void onLoginClick() {
        controller.login();
    }

    /**
     * Event handler for "Register" link click
     */
    @FXML
    public void onRegisterClick() {
        controller.register();
    }

    /**
     * Gets entered username
     * @return entered username
     */
    public String getUsername() {
        return usernameTextField.getText();
    }

    /**
     * Gets entered password
     * @return entered password
     */
    public String getPassword() {
        return passwordTextField.getText();
    }
}
