package com.group4.client.controller;

import com.group4.client.view.AdminPanelView;
import com.group4.client.view.DialogWindow;
import com.group4.client.view.EditProfileView;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.types.*;
import com.group4.server.model.message.wrappers.MessageWrapper;
import javafx.application.Platform;

import java.util.HashMap;

/**
 * Controller that is responsible for all admin actions.
 * This class realizes singleton design pattern
 */
public class AdminController {
    private AdminPanelView view;
    private static AdminController instance;
    private HashMap<String, User> allUsers;

    private AdminController() {
    }

    /**
     * Gets instance of the class
     *
     * @return instance of the class
     */
    public static AdminController getInstance() {
        if (instance == null) {
            instance = new AdminController();
        }
        return instance;
    }

    /**
     * Sets view for controller
     *
     * @param view view for controller
     */
    public void setView(AdminPanelView view) {
        this.view = view;
    }

    /**
     * Sends request to ban specified user
     * @param selectedUser user to be banned
     */
    public void banUser(User selectedUser) {
        sendSetBanMessage(selectedUser, true);
    }

    /**
     * Sends request to delete specified user
     * @param selectedUser user to be deleted
     */
    public void deleteUser(User selectedUser) {
        if (selectedUser != null && DialogWindow.showConfirmationWindow("Are you sure to delete this user?", selectedUser.toString())) {
            DeleteUserRequest request = new DeleteUserRequest(selectedUser.getNickname());
            Controller.getInstance().getThread().sendMessage(request);
        }
    }

    /**
     * Opens window for editing profile of selected user
     * @param selectedUser
     */
    public void editUser(User selectedUser) {
        if (selectedUser != null) {
            Controller.getInstance().showEditProfileDialog(selectedUser);
        }
    }

    /**
     * Sends request to unban specified user
     * @param selectedUser user to be unbanned
     */
    public void unbanUser(User selectedUser) {
        sendSetBanMessage(selectedUser, false);
    }

    /**
     * Processes incoming message.
     * <p>For ALL_USERS_RESPONSE displays all users info in the table inside view.</p>
     * <p>For CHANGE_CREDENTIALS_RESPONSE if change credentials request was confirmed,
     * changes info in the table for specified user.
     * Otherwise shows dialog window why action can't be done.</p>
     * <p>For SET_BAN_STATUS sets specified ban status.</p>
     * <p>For DELETE_USER_RESPONSE deletes user from table inside view</p>
     * @param message incoming message
     */
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

    /**
     * Sends request to get information about all users
     */
    public void sendAllUsersRequest() {
        GetAllUsersRequest request = new GetAllUsersRequest();
        Controller.getInstance().getThread().sendMessage(request);
    }

    /**
     * Sends request to set specified ban status
     * @param selectedUser user whose ban status has to be changed
     * @param isBanned new ban status
     */
    private void sendSetBanMessage(User selectedUser, boolean isBanned) {
        SetBanStatusMessage message = new SetBanStatusMessage(selectedUser.getNickname(), isBanned);
        Controller.getInstance().getThread().sendMessage(message);
    }
}
