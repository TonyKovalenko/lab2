package com.group4.client.controller;

import com.group4.client.view.CreateChatView;
import com.group4.client.view.EditProfileView;
import com.group4.client.view.MainView;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.wrappers.MessageWrapper;
import javafx.stage.Stage;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Controller {
    /**
     * Gets main stage of the application
     *
     * @return main stage of the application
     */
    public Stage getStage();

    /**
     * Gets main message thread of the application
     *
     * @return message thread of the application
     */
    public MessageThread getThread();

    /**
     * Sets main message thread of the application
     *
     * @param messageThread main message thread of the application
     */
    public void setThread(MessageThread messageThread);

    /**
     * Sets view for controller
     *
     * @param view view for controller
     */
    public void setView(MainView view);

    /**
     * Gets view of controller
     * @return main view
     */
    public MainView getMainView();

    /**
     * Gets all user's chat rooms
     * @return all user's chat rooms
     */
    public Map<Long, ChatRoom> getChatRooms();

    /**
     * Gets chat room from all chat rooms by id
     * @param id id of chat room to be found
     * @return found chat room
     */
    public ChatRoom getChatRoomById(long id);

    /**
     * Gets current user of the application
     * @return current user
     */
    public User getCurrentUser();

    /**
     * Sets current user of application
     * @param currentUser current user of application
     */
    public void setCurrentUser(User currentUser);

    /**
     * Gets user from online users list by specified nickname
     * @param nickname specified nickname of user
     * @return found user
     */
    public User getUserByNickname(String nickname);

    /**
     * Gets collection of online users
     * @return collection of online users
     */
    public Collection<User> getUsers();

    /**
     * Updates chat rooms view
     */
    public void updateChatRoomsView();

    /**
     * Updates online users view
     */
    public void updateOnlineUsersView();

    /**
     * Gets all users with whom the current user hasn't have chat yet
     * @return all users with whom the current user hasn't have chat yet
     */
    public List<User> getUsersWithoutPrivateChat();

    /**
     * Processes incoming message.
     * <p>For ONLINE_LIST updates online users in view.</p>
     * <p>For CHAT_CREATION_RESPONSE if create chat request was confirmed,
     * adds chat room to chat rooms list.</p>
     * <p>For NEW_CHATS adds new chat rooms to chat rooms list</p>
     * <p>For TO_CHAT adds new message to chat room</p>
     * <p>For CHANGE_CREDENTIALS_RESPONSE if change credentials request was confirmed,
     * changes user credentials and closes EditProfileView.
     * Otherwise shows dialog window why action can't be done.</p>
     * <p>For CHAT_UPDATE updates specified chat room</p>
     * <p>For CHAT_SUSPENSION deletes specified chat room from chat rooms list</p>
     * <p>For SET_BAN_STATUS sets specified ban status.</p>
     * <p>For DELETE_USER_RESPONSE makes force log out/p>
     * @param responseMessage incoming message
     */
    public void processMessage(MessageWrapper responseMessage);

    /**
     * Exits the application
     */
    public void exit();

    /**
     * Logs out from current account
     */
    public void logout();

    /**
     * Sends entered message to corresponding chat
     */
    public void sendMessageToChat();

    /**
     * Shows window for creating new chat
     */
    public void showCreateNewChatDialog();

    /**
     * Event handler for "Create" button click from CreateChatView.
     * Gets information from CreateChatView and sends request to create new chat.
     *
     * @param view view where event handler was called
     */
    public void handleCreateChatClick(CreateChatView view);

    /**
     * Shows window for editing user profile
     */
    public void showEditProfileDialog(User user);

    /**
     * Event handler for "Save changes" button click from EditProfileView.
     * Gets information from EditProfileView and sends request to edit user profile.
     *
     * @param view view where event handler was called
     */
    public void saveProfileChanges(EditProfileView view);

    /**
     * Shows window with chat info
     */
    public void showChatInfo();

    /**
     * Event handler for "Save" button click from ChatInfoView.
     * Gets information from ChatInfoView and sends request update chat room.
     */
    public void saveGroupChatChanges();

    /**
     * Shows window for adding new members to chat room
     */
    public void showAddMemberToGroupChatView();

    /**
     * Event handler for "Add" button click from AddMembersToGroupChatView.
     * Gets list of selected users abd adds them to users in ChatInfoView
     */
    public void addMembersToGroupChat();

    /**
     * Shows admin panel window
     */
    public void openAdminPanel();

    /**
     * Event handler for "Leave chat" link click from ChatInfoView.
     * Send request to leave shown chat
     */
    void leaveChatRoom();

    /**
     * Opens main chat room
     */
    public void openMainChatRoom();
}
