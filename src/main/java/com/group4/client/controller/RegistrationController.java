package com.group4.client.controller;

import com.group4.client.view.DialogWindow;
import com.group4.client.view.LoginView;
import com.group4.client.view.RegistrationView;
import com.group4.server.model.message.types.RegistrationRequest;
import com.group4.server.model.message.types.RegistrationResponse;
import com.group4.server.model.message.wrappers.MessageWrapper;
import javafx.application.Platform;

/**
 * Controller that is responsible for all registration actions.
 * This class realizes singleton design pattern
 */
public class RegistrationController {
    private RegistrationView view;
    private static RegistrationController instance;

    private RegistrationController() {
    }

    /**
     * Gets instance of the class
     *
     * @return instance of the class
     */
    public static RegistrationController getInstance() {
        if (instance == null) {
            instance = new RegistrationController();
        }
        return instance;
    }

    /**
     * Sets view for controller
     *
     * @param view view for controller
     */
    public void setView(RegistrationView view) {
        this.view = view;
    }

    /**
     * Cancels registration and returns to login page
     */
    public void cancel() {
        LoginView.getInstance().showStage();
    }

    /**
     * Sends registration request if all information is filled correctly,
     * otherwise shows dialog window why action can't be done
     */
    public void register() {
        if (view.isFieldsFilled()) {
            if (view.isPasswordConfirmed()) {
                String nickname = view.getUsername();
                String password = view.getPassword();
                String fullName = view.getFullName();
                RegistrationRequest message = new RegistrationRequest();
                message.setUserNickname(nickname);
                message.setPassword(password);
                message.setFullName(fullName);

                Controller.getInstance().getThread().sendMessage(message);
            } else {
                DialogWindow.showWarningWindow("Passwords don't match", "The password and confirm password fields do not match.");
            }
        } else {
            DialogWindow.showWarningWindow("Fill the fields", "Fields can't be empty");
        }
    }

    /**
     * Processes incoming message. If registration was confirmed, then login page is shown.
     * Otherwise dialog window with explanation why action can't be done is shown
     * @param responseMessage incoming message
     */
    public void processMessage(MessageWrapper responseMessage) {
        RegistrationResponse innerMessage = (RegistrationResponse) responseMessage.getEncapsulatedMessage();
        if (innerMessage.isRegistrationSuccessful()) {
            Platform.runLater(() -> cancel());
        } else {
            Platform.runLater(() -> DialogWindow.showWarningWindow("Registration failed", "Registration was denied"));
        }
    }
}
