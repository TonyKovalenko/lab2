package com.group4.client.controller;

import com.group4.client.view.AdminPanelView;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.wrappers.MessageWrapper;

public interface AdminController {
    /**
     * Sets view for controller
     *
     * @param view view for controller
     */
    void setView(AdminPanelView view);

    /**
     * Sends request to ban specified user
     * @param selectedUser user to be banned
     */
    void banUser(User selectedUser);

    /**
     * Sends request to delete specified user
     * @param selectedUser user to be deleted
     */
    void deleteUser(User selectedUser);

    /**
     * Opens window for editing profile of selected user
     * @param selectedUser
     */
    void editUser(User selectedUser);

    /**
     * Sends request to unban specified user
     * @param selectedUser user to be unbanned
     */
    void unbanUser(User selectedUser);

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
    void processMessage(MessageWrapper message);

    /**
     * Sends request to get information about all users
     */
    void sendAllUsersRequest();
}
