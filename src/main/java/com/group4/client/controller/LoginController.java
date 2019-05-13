package com.group4.client.controller;

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
 * Controller that is responsible for all authorization actions.
 * This class realizes singleton design pattern
 */
public class LoginController {
    private LoginView view;
    private static LoginController instance;

    private LoginController() {
    }

    /**
     * Gets instance of the class
     *
     * @return instance of the class
     */
    public static LoginController getInstance() {
        if (instance == null) {
            instance = new LoginController();
        }
        return instance;
    }

    /**
     * Sets view for controller
     *
     * @param view view for controller
     */
    public void setView(LoginView view) {
        this.view = view;
    }

    /**
     * Event handler for "Login" button.
     * Sends login request if all information is filled correctly,
     * otherwise shows dialog window why action can't be done
     */
    public void login() {
        String nickname = view.getUsername();
        String password = view.getPassword();
        if (!nickname.isEmpty() && !password.isEmpty()) {
            sendAuthorizationRequest(nickname, password);
        } else {
            DialogWindow.showWarningWindow("Fill the fields", "Fields can't be empty");
        }
    }

    /**
     * Shows registration window
     */
    public void register() {
        RegistrationView.getInstance().showStage();
    }

    /**
     * Processes incoming message. If authorization was confirmed,
     * then data form AuthorizationResponse is initialized and main page is shown.
     * Otherwise dialog window with explanation why action can't be done is shown
     *
     * @param responseMessage incoming message
     */
    public void processMessage(MessageWrapper responseMessage) {
        AuthorizationResponse innerMessage = (AuthorizationResponse) responseMessage.getEncapsulatedMessage();
        if (innerMessage.isConfirmed()) {
            Platform.runLater(() -> MainView.getInstance().showStage());
            Controller.getInstance().setCurrentUser(innerMessage.getUser());
            Set<ChatRoom> chatRooms = innerMessage.getChatRoomsWithUser();
            for (ChatRoom room : chatRooms) {
                Controller.getInstance().getChatRooms().put(room.getId(), room);
            }
            Controller.getInstance().updateChatRoomsView();
            Controller.getInstance().openMainChatRoom();
        } else {
            Platform.runLater(() -> {
                DialogWindow.showWarningWindow("Authorization failed", "Authorization was denied");
                Controller.getInstance().logout();
            });
        }
    }

    /**
     * Sends authorization request
     *
     * @param nickname nickname for authorization request
     * @param password password for authorization request
     */
    public void sendAuthorizationRequest(String nickname, String password) {
        AuthorizationRequest message = new AuthorizationRequest();
        message.setUserNickname(nickname);
        message.setPassword(password);

        Controller.getInstance().getThread().sendMessage(message);
    }
}
