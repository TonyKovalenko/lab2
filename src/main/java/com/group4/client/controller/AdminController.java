package com.group4.client.controller;

import com.group4.client.view.AdminPanelView;
import com.group4.client.view.DialogWindow;
import com.group4.client.view.EditProfileView;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.types.AllUsersRequest;
import com.group4.server.model.message.types.AllUsersResponse;
import com.group4.server.model.message.types.ChangeCredentialsResponse;
import com.group4.server.model.message.wrappers.MessageWrapper;
import javafx.application.Platform;

import java.util.HashMap;

public class AdminController {
    private AdminPanelView view;
    private static AdminController instance;
    private HashMap<String, User> allUsers;

    private AdminController() {
    }

    public static AdminController getInstance() {
        if (instance == null) {
            instance = new AdminController();
        }
        return instance;
    }

    public void setView(AdminPanelView view) {
        this.view = view;
    }

    public void banUser(User selectedUser) {

    }

    public void deleteUser(User selectedUser) {
        if (selectedUser != null) {
            if (DialogWindow.showConfirmationWindow("Are you sure to delete this user?", selectedUser.toString())) {

                allUsers.remove(selectedUser.getId());
            }
        }
    }

    public void editUser(User selectedUser) {
        if (selectedUser != null) {
            Controller.getInstance().showEditProfileDialog(selectedUser);
        }
    }

    public void unbanUser(User selectedUser) {

    }

    public void processMessage(MessageWrapper message) {
        switch (message.getMessageType()) {
            case ALL_USERS_RESPONSE:
                AllUsersResponse getAllUsersResponse = (AllUsersResponse) message.getEncapsulatedMessage();
                allUsers = new HashMap<>();
                for (User user : getAllUsersResponse.getUsers()) {
                    allUsers.put(user.getNickname(), user);
                }
                Platform.runLater(() -> view.setUsers(allUsers.values()));
                break;
            case CHANGE_CREDENTIALS_RESPONSE:
                ChangeCredentialsResponse changeCredentialsResponse = (ChangeCredentialsResponse) message.getEncapsulatedMessage();
                if (changeCredentialsResponse.isConfirmed()) {
                    User user = changeCredentialsResponse.getUser();
                    allUsers.put(user.getNickname(), user);
                    Platform.runLater(() -> {
                        DialogWindow.showInfoWindow("Credentials change was confirmed");
                        EditProfileView.getInstance().cancel();
                        view.setUsers(allUsers.values());
                    });
                } else {
                    Platform.runLater(() -> DialogWindow.showErrorWindow("Credentials change was denied"));
                }
            default:
                break;
        }
    }

    public void sendAllUsersRequest() {
        AllUsersRequest request = new AllUsersRequest();
        Controller.getInstance().getThread().sendMessage(request);
    }
}
