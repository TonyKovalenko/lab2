package com.group4.client.controller.impl;

import com.group4.client.controller.LoginController;
import com.group4.client.view.DialogWindow;
import com.group4.client.view.LoginView;
import com.group4.client.view.MainView;
import com.group4.client.view.RegistrationView;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.message.types.AuthorizationRequest;
import com.group4.server.model.message.types.AuthorizationResponse;
import com.group4.server.model.message.wrappers.MessageWrapper;
import javafx.application.Platform;

import java.util.Set;

/**
 * ControllerImpl that is responsible for all authorization actions.
 * This class realizes singleton design pattern
 */
public class LoginControllerImpl implements LoginController {
    private LoginView view;
    private static LoginControllerImpl instance;

    private LoginControllerImpl() {
    }

    /**
     * Gets instance of the class
     *
     * @return instance of the class
     */
    public static LoginControllerImpl getInstance() {
        if (instance == null) {
            instance = new LoginControllerImpl();
        }
        return instance;
    }

    @Override
    public void setView(LoginView view) {
        this.view = view;
    }

    @Override
    public void login() {
        String nickname = view.getUsername();
        String password = view.getPassword();
        if (!nickname.isEmpty() && !password.isEmpty()) {
            sendAuthorizationRequest(nickname, password);
        } else {
            DialogWindow.showWarningWindow("Fill the fields", "Fields can't be empty");
        }
    }

    @Override
    public void register() {
        RegistrationView.getInstance().showStage();
    }

    @Override
    public void processMessage(MessageWrapper responseMessage) {
        AuthorizationResponse innerMessage = (AuthorizationResponse) responseMessage.getEncapsulatedMessage();
        if (innerMessage.isConfirmed()) {
            Platform.runLater(() -> MainView.getInstance().showStage());
            ControllerImpl.getInstance().setCurrentUser(innerMessage.getUser());
            Set<ChatRoom> chatRooms = innerMessage.getChatRoomsWithUser();
            for (ChatRoom room : chatRooms) {
                ControllerImpl.getInstance().getChatRooms().put(room.getId(), room);
            }
            ControllerImpl.getInstance().updateChatRoomsView();
            ControllerImpl.getInstance().openMainChatRoom();
        } else {
            Platform.runLater(() -> {
                DialogWindow.showWarningWindow("Authorization failed", "Authorization was denied");
                ControllerImpl.getInstance().logout();
            });
        }
    }

    @Override
    public void sendAuthorizationRequest(String nickname, String password) {
        AuthorizationRequest message = new AuthorizationRequest();
        message.setUserNickname(nickname);
        message.setPassword(password);

        ControllerImpl.getInstance().getThread().sendMessage(message);
    }
}
