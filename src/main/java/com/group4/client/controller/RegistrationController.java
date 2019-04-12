package com.group4.client.controller;

import com.group4.client.view.DialogWindow;
import com.group4.client.view.LoginView;
import com.group4.client.view.RegistrationView;
import com.group4.server.model.message.types.RegistrationRequest;
import com.group4.server.model.message.types.RegistrationResponse;
import com.group4.server.model.message.wrappers.MessageWrapper;
import javafx.application.Platform;

public class RegistrationController {
    private RegistrationView view;
    private static RegistrationController instance;

    private RegistrationController() {
    }

    public static RegistrationController getInstance() {
        if (instance == null) {
            instance = new RegistrationController();
        }
        return instance;
    }

    public void setView(RegistrationView view) {
        this.view = view;
    }

    public void cancel() {
        System.out.println("cancel reg");
        LoginView.getInstance().showStage();
    }

    public void register() {
        System.out.println("register");
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
                System.out.println("password doesn't match");
            }
        } else {
            DialogWindow.showWarningWindow("Fill the fields", "Fields can't be empty");
            System.out.println("Fill the fields");
        }
    }

    public void processMessage(MessageWrapper requestMessage, MessageWrapper responseMessage) {
        RegistrationResponse innerMessage = (RegistrationResponse) responseMessage.getEncapsulatedMessage();
        if (innerMessage.isRegistrationSuccessful()) {
            System.out.println("registered successfully");
            Platform.runLater(() -> cancel());
        } else {
            System.out.println("authorization was denied");
            DialogWindow.showWarningWindow("Registration failed", "Registration was denied");
        }
    }
}
