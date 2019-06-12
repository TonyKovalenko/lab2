package com.group4.client.controller.impl;

import com.group4.client.controller.RegistrationController;
import com.group4.client.view.DialogWindow;
import com.group4.client.view.LoginView;
import com.group4.client.view.RegistrationView;
import com.group4.server.model.message.types.RegistrationRequest;
import com.group4.server.model.message.types.RegistrationResponse;
import com.group4.server.model.message.wrappers.MessageWrapper;
import javafx.application.Platform;

/**
 * ControllerImpl that is responsible for all registration actions.
 * This class realizes singleton design pattern
 */
public class RegistrationControllerImpl implements RegistrationController {
    private RegistrationView view;
    private static RegistrationControllerImpl instance;

    private RegistrationControllerImpl() {
    }

    /**
     * Gets instance of the class
     *
     * @return instance of the class
     */
    public static RegistrationControllerImpl getInstance() {
        if (instance == null) {
            instance = new RegistrationControllerImpl();
        }
        return instance;
    }

    @Override
    public void setView(RegistrationView view) {
        this.view = view;
    }

    @Override
    public void cancel() {
        LoginView.getInstance().showStage();
    }

    @Override
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

                ControllerImpl.getInstance().getThread().sendMessage(message);
            } else {
                DialogWindow.showWarningWindow("Passwords don't match", "The password and confirm password fields do not match.");
            }
        } else {
            DialogWindow.showWarningWindow("Fill the fields", "Fields can't be empty");
        }
    }

    @Override
    public void processMessage(MessageWrapper responseMessage) {
        RegistrationResponse innerMessage = (RegistrationResponse) responseMessage.getEncapsulatedMessage();
        if (innerMessage.isRegistrationSuccessful()) {
            Platform.runLater(() -> cancel());
        } else {
            Platform.runLater(() -> DialogWindow.showWarningWindow("Registration failed", "Registration was denied"));
        }
    }
}
