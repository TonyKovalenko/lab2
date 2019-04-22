package com.group4.client.controller;

import com.group4.client.view.AdminPanelView;
import com.group4.client.view.DialogWindow;
import com.group4.client.view.EditProfileView;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.types.*;
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
        sendSetBanMessage(selectedUser, true);
    }

    public void deleteUser(User selectedUser) {
        if (selectedUser != null && DialogWindow.showConfirmationWindow("Are you sure to delete this user?", selectedUser.toString())) {
            DeleteUserRequest request = new DeleteUserRequest(selectedUser.getNickname());
            System.out.println(request);
            Controller.getInstance().getThread().sendMessage(request);
        }
    }

    public void editUser(User selectedUser) {
        if (selectedUser != null) {
            Controller.getInstance().showEditProfileDialog(selectedUser);
        }
    }

    public void unbanUser(User selectedUser) {
        sendSetBanMessage(selectedUser, false);
    }

    public void processMessage(MessageWrapper message) {
        switch (message.getMessageType()) {
            case ALL_USERS_RESPONSE:
                GetAllUsersResponse getAllUsersResponse = (GetAllUsersResponse) message.getEncapsulatedMessage();
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
                        if (!user.equals(Controller.getInstance().getCurrentUser())) {
                            DialogWindow.showInfoWindow("Credentials change was confirmed");
                            EditProfileView.getInstance().cancel();
                        }
                        view.setUsers(allUsers.values());
                    });
                } else {
                    Platform.runLater(() -> DialogWindow.showErrorWindow("Credentials change was denied"));
                }
                break;
            case SET_BAN_STATUS:
                SetBanStatusMessage setBanStatusMessage = (SetBanStatusMessage) message.getEncapsulatedMessage();
                allUsers.get(setBanStatusMessage.getUserNickname()).setBanned(setBanStatusMessage.isBanned());
                Platform.runLater(() -> view.setUsers(allUsers.values()));
                break;
            case DELETE_USER_RESPONSE:
                DeleteUserResponse deleteUserResponse = (DeleteUserResponse) message.getEncapsulatedMessage();
                allUsers.remove(deleteUserResponse.getUserNickname());
                Platform.runLater(() -> view.setUsers(allUsers.values()));
                break;
            default:
                break;
        }
    }

    public void sendAllUsersRequest() {
        GetAllUsersRequest request = new GetAllUsersRequest();
        Controller.getInstance().getThread().sendMessage(request);
    }

    private void sendSetBanMessage(User selectedUser, boolean isBanned) {
        SetBanStatusMessage message = new SetBanStatusMessage(selectedUser.getNickname(), isBanned);
        Controller.getInstance().getThread().sendMessage(message);
    }
}
