package com.group4.client.controller;

import com.group4.client.view.DialogWindow;
import com.group4.client.view.LoginView;
import com.group4.client.view.MainView;
import com.group4.client.view.RegistrationView;
import com.group4.server.model.message.types.AuthorizationRequest;
import com.group4.server.model.message.types.AuthorizationResponse;
import com.group4.server.model.message.wrappers.MessageWrapper;
import com.group4.server.model.entities.ChatRoom;
import javafx.application.Platform;

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
            AuthorizationRequest message = new AuthorizationRequest();
            message.setUserNickname(nickname);
            message.setPassword(password);

            Controller.getInstance().getThread().sendMessage(message);
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
        AuthorizationResponse innerMessage = (AuthorizationResponse) responseMessage.getEncapsulatedMessage();
        if (innerMessage.isConfirmed()) {
            System.out.println("authorization was confirmed");
            Platform.runLater(() -> MainView.getInstance().showStage());
            Controller.getInstance().setCurrentUser(innerMessage.getUser());
            ChatRoom chatRoom = innerMessage.getMainChatRoom();
            Controller.getInstance().getChatRooms().put(chatRoom.getId(), chatRoom);
        } else {
            System.out.println("authorization was denied");
        }
    }
}
