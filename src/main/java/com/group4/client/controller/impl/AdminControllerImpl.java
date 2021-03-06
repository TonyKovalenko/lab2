package com.group4.client.controller.impl;

import com.group4.client.controller.AdminController;
import com.group4.client.view.AdminPanelView;
import com.group4.client.view.DialogWindow;
import com.group4.client.view.EditProfileView;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.types.*;
import com.group4.server.model.message.wrappers.MessageWrapper;
import javafx.application.Platform;

import java.util.HashMap;

/**
 * ControllerImpl that is responsible for all admin actions.
 * This class realizes singleton design pattern
 */
public class AdminControllerImpl implements AdminController {
    private AdminPanelView view;
    private static AdminControllerImpl instance;
    private HashMap<String, User> allUsers;

    private AdminControllerImpl() {
    }

    /**
     * Gets instance of the class
     *
     * @return instance of the class
     */
    public static AdminControllerImpl getInstance() {
        if (instance == null) {
            instance = new AdminControllerImpl();
        }
        return instance;
    }

    @Override
    public void setView(AdminPanelView view) {
        this.view = view;
    }

    @Override
    public void banUser(User selectedUser) {
        sendSetBanMessage(selectedUser, true);
    }

    @Override
    public void deleteUser(User selectedUser) {
        if (selectedUser != null && DialogWindow.showConfirmationWindow("Are you sure to delete this user?", selectedUser.toString())) {
            DeleteUserRequest request = new DeleteUserRequest(selectedUser.getNickname());
            ControllerImpl.getInstance().getThread().sendMessage(request);
        }
    }

    @Override
    public void editUser(User selectedUser) {
        if (selectedUser != null) {
            ControllerImpl.getInstance().showEditProfileDialog(selectedUser);
        }
    }

    @Override
    public void unbanUser(User selectedUser) {
        sendSetBanMessage(selectedUser, false);
    }

    @Override
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
                        if (!user.equals(ControllerImpl.getInstance().getCurrentUser())) {
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

    @Override
    public void sendAllUsersRequest() {
        GetAllUsersRequest request = new GetAllUsersRequest();
        ControllerImpl.getInstance().getThread().sendMessage(request);
    }

    /**
     * Sends request to set specified ban status
     * @param selectedUser user whose ban status has to be changed
     * @param isBanned new ban status
     */
    private void sendSetBanMessage(User selectedUser, boolean isBanned) {
        SetBanStatusMessage message = new SetBanStatusMessage(selectedUser.getNickname(), isBanned);
        ControllerImpl.getInstance().getThread().sendMessage(message);
    }
}
