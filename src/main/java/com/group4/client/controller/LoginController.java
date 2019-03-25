package com.group4.client.controller;

import com.group4.client.view.DialogWindow;
import com.group4.client.view.LoginView;
import com.group4.client.view.MainView;
import com.group4.client.view.RegistrationView;
import com.group4.server.model.MessageTypes.AnswerMessage;
import com.group4.server.model.MessageTypes.AnswerType;
import com.group4.server.model.MessageTypes.AuthorizationMessage;
import com.group4.server.model.MessageWrappers.MessageWrapper;

public class LoginController {
    private LoginView view;
    private static LoginController instance;

    private LoginController() {
    }

    public static LoginController getInstance() {
        if (instance == null) {
            instance = new LoginController();
        }
        return instance;
    }

    public void setView(LoginView view) {
        this.view = view;
    }

    public void login() {
        String nickname = view.getUsername();
        String password = view.getPassword();
        if (!nickname.isEmpty() && !password.isEmpty()) {
            AuthorizationMessage message = new AuthorizationMessage();
            message.setUserNickname(nickname);
            message.setPassword(password);
            MessageWrapper messageWrapper = new MessageWrapper(message);

            Controller.getInstance().getThread().sendMessage(messageWrapper);
        } else {
            DialogWindow.showWarningWindow("Fill the fields", "Fields can't be empty");
            System.out.println("Fill the fields");
        }
    }

    public void register() {
        System.out.println("register");
        RegistrationView.getInstance().showStage();
    }

    public void processMessage(MessageWrapper requestMessage, MessageWrapper responseMessage) {
        AnswerMessage innerMessage = (AnswerMessage) responseMessage.getEncapsulatedMessage();
        if (innerMessage.getAnswerType() == AnswerType.CONFIRMED) {
            System.out.println("authorization was confirmed");
            MainView.getInstance().showStage();
        } else {
            System.out.println("authorization was denied");
        }
    }
}
