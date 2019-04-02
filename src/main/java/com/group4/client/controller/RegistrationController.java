package com.group4.client.controller;

import com.group4.client.view.DialogWindow;
import com.group4.client.view.LoginView;
import com.group4.client.view.RegistrationView;
import com.group4.server.model.MessageTypes.RegistrationRequest;
import com.group4.server.model.MessageWrappers.MessageWrapper;

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
                MessageWrapper messageWrapper = new MessageWrapper(message);

                Controller.getInstance().getThread().sendMessage(messageWrapper);
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
        System.out.println("registered successfully");
        cancel();
    }
}
