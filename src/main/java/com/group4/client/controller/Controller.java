package com.group4.client.controller;

import com.group4.client.view.*;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.types.*;
import com.group4.server.model.message.wrappers.MessageWrapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main controller og the application.
 * This class realizes singleton design pattern
 */
public class Controller extends Application {
    private static final Logger log = Logger.getLogger(Controller.class);
    private Stage stage;
    private MainView mainView;
    private MessageThread thread;
    private static Controller instance;
    private User currentUser;
    private HashMap<String, User> users = new HashMap<>();
    private HashMap<Long, ChatRoom> chatRooms = new HashMap<>();

    /**
     * Gets instance of the class
     *
     * @return instance of the class
     */
    public static Controller getInstance() {
        return instance;
    }

    /**
     * Gets main stage of the application
     *
     * @return main stage of the application
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Gets main message thread of the application
     *
     * @return message thread of the application
     */
    public MessageThread getThread() {
        return thread;
    }

    /**
     * Sets main message thread of the application
     *
     * @param messageThread main message thread of the application
     */
    public void setThread(MessageThread messageThread) {
        thread = messageThread;
    }

    /**
     * Sets view for controller
     *
     * @param view view for controller
     */
    public void setView(MainView view) {
        mainView = view;
    }

    /**
     * Gets view of controller
     * @return main view
     */
    public MainView getMainView() {
        return mainView;
    }

    /**
     * Gets all user's chat rooms
     * @return all user's chat rooms
     */
    public Map<Long, ChatRoom> getChatRooms() {
        return chatRooms;
    }

    /**
     * Gets chat room from all chat rooms by id
     * @param id id of chat room to be found
     * @return found chat room
     */
    public ChatRoom getChatRoomById(long id) {
        return chatRooms.get(id);
    }

    /**
     * Gets current user of the application
     * @return current user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets current user of application
     * @param currentUser current user of application
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        Platform.runLater(() -> {
            mainView.updateAdminPanel(currentUser.isAdmin());
            if (currentUser.isBanned()) {
                DialogWindow.showWarningWindow(null, "You have been banned");
            }
        });
    }

    /**
     * Gets user from online users list by specified nickname
     * @param nickname specified nickname of user
     * @return found user
     */
    public User getUserByNickname(String nickname) {
        return users.get(nickname);
    }

    /**
     * Gets collection of online users
     * @return collection of online users
     */
    public Collection<User> getUsers() {
        return users.values();
    }

    /**
     * Updates chat rooms view
     */
    public void updateChatRoomsView() {
        Platform.runLater(() -> mainView.setChatRoomsWithUser(chatRooms.values()));
    }

    /**
     * Updates online users view
     */
    public void updateOnlineUsersView() {
        Platform.runLater(() -> mainView.setOnlineUsers(getUsers()));
    }

    /**
     * Gets all users with whom the current user hasn't have chat yet
     * @return all users with whom the current user hasn't have chat yet
     */
    public List<User> getUsersWithoutPrivateChat() {
        List<User> usersWithoutPrivateChat = new ArrayList<>(getUsers());
        Controller.getInstance().getChatRooms().values()
                .stream()
                .filter(ChatRoom::isPrivate)
                .forEach((item) -> usersWithoutPrivateChat.remove(item.getOtherMember(Controller.getInstance().getCurrentUser())));
        return usersWithoutPrivateChat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) {
        instance = this;
        stage = primaryStage;
        Controller.getInstance().getStage().setOnCloseRequest(windowEvent -> exit());
        thread = new MessageThread();
        LoginView.getInstance().showStage();
        try {
            thread.connect();
            thread.start();
        } catch (IOException e) {
            log.info("Can't connect to server. Start reconnecting");
            thread.reconnect();
        }
    }

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
    public void processMessage(MessageWrapper responseMessage) {
        //if (mainView != null) {
        switch (responseMessage.getMessageType()) {
            case ONLINE_LIST:
                OnlineListMessage onlineListMessage = (OnlineListMessage) responseMessage.getEncapsulatedMessage();
                if (onlineListMessage.getUsers().stream().anyMatch(item -> item.isAdmin() && !currentUser.isAdmin())
                        && users.values().stream().noneMatch(item -> item.isAdmin())) {
                    DialogWindow.showInfoWindow("Admin had entered the chat");
                } else if (!onlineListMessage.getUsers().stream().anyMatch(item -> item.isAdmin())
                            && users.values().stream().anyMatch(item -> item.isAdmin())) {
                    DialogWindow.showInfoWindow("Admin had left the chat");
                }
                users = new HashMap<>();
                for (User user : onlineListMessage.getUsers()) {
                    users.put(user.getNickname(), user);
                }
                users.remove(currentUser.getNickname());
                updateOnlineUsersView();
                if (CreateChatView.isOpened()) {
                    Platform.runLater(() -> CreateChatView.getInstance().setOnlineUsers(users.values()));
                }
                break;
            case CHAT_CREATION_RESPONSE:
                ChatRoomCreationResponse chatRoomCreationResponse = (ChatRoomCreationResponse) responseMessage.getEncapsulatedMessage();
                if (chatRoomCreationResponse.isSuccessful()) {
                    ChatRoom chatRoom = chatRoomCreationResponse.getChatRoom();
                    chatRooms.put(chatRoom.getId(), chatRoom);
                    updateChatRoomsView();
                }
                System.out.println("CHAT_CREATION_RESPONSE");
                break;
            case NEW_CHATS:
                ChatInvitationMessage chatInvitationMessage = (ChatInvitationMessage) responseMessage.getEncapsulatedMessage();
                Set<ChatRoom> chatRoom = chatInvitationMessage.getChatRooms();
                chatRoom.forEach(room -> chatRooms.put(room.getId(), room));
                updateChatRoomsView();
                break;
            case TO_CHAT:
                ChatMessage chatMessage = (ChatMessage) responseMessage.getEncapsulatedMessage();
                chatRooms.get(chatMessage.getChatId()).addMessage(chatMessage);
                updateChatRoomsView();
                break;
            case CHANGE_CREDENTIALS_RESPONSE:
                ChangeCredentialsResponse changeCredentialsResponse = (ChangeCredentialsResponse) responseMessage.getEncapsulatedMessage();
                if (changeCredentialsResponse.isConfirmed()) {
                    User updatedUser = changeCredentialsResponse.getUser();
                    if (updatedUser.getNickname().equals(currentUser.getNickname())) {
                        setCurrentUser(changeCredentialsResponse.getUser());
                        Platform.runLater(() -> {
                            DialogWindow.showInfoWindow("Credentials change was confirmed");
                            EditProfileView.getInstance().cancel();
                        });
                    } else {
                        if (users.containsKey(updatedUser.getNickname())) {
                            users.put(updatedUser.getNickname(), updatedUser);
                            updateOnlineUsersView();
                        }
                    }

                    if (AdminPanelView.isOpened()) {
                        AdminController.getInstance().processMessage(responseMessage);
                    }
                } else {
                    DialogWindow.showErrorWindow("Credentials change was denied");
                }
                break;
            case CHAT_UPDATE:
                ChatUpdateMessage chatUpdateMessageRequest = (ChatUpdateMessage) responseMessage.getEncapsulatedMessage();
                ChatRoom updatedRoom = chatRooms.get(chatUpdateMessageRequest.getChatRoomId());
                List<User> membersToAdd = chatUpdateMessageRequest.getMembersToAdd();
                List<User> membersToDelete = chatUpdateMessageRequest.getMembersToDelete();
                if (membersToAdd != null) {
                    updatedRoom.getMembers().addAll(membersToAdd);
                }
                if (membersToDelete != null) {
                    updatedRoom.getMembers().removeAll(chatUpdateMessageRequest.getMembersToDelete());
                }
                updatedRoom.setName(chatUpdateMessageRequest.getNewName());
                updateChatRoomsView();
                break;
            case CHAT_SUSPENSION:
                ChatSuspensionMessage chatSuspensionMessage = (ChatSuspensionMessage) responseMessage.getEncapsulatedMessage();
                chatRooms.remove(chatSuspensionMessage.getChatId());
                updateChatRoomsView();
                break;
            case SET_BAN_STATUS:
                SetBanStatusMessage setBanStatusMessage = (SetBanStatusMessage) responseMessage.getEncapsulatedMessage();
                if (setBanStatusMessage.getUserNickname().equals(currentUser.getNickname())) {
                    if (!currentUser.isBanned() && setBanStatusMessage.isBanned()) {
                        DialogWindow.showWarningWindow(null, "You have been banned");
                    } else if (currentUser.isBanned() && !setBanStatusMessage.isBanned()) {
                        DialogWindow.showInfoWindow("You have been unbanned");
                    }
                    currentUser.setBanned(setBanStatusMessage.isBanned());
                }
                if (AdminPanelView.isOpened()) {
                    AdminController.getInstance().processMessage(responseMessage);
                }
                break;
            case DELETE_USER_RESPONSE:
                DeleteUserResponse deleteUserResponse = (DeleteUserResponse) responseMessage.getEncapsulatedMessage();
                if (deleteUserResponse.getUserNickname().equals(currentUser.getNickname())) {
                    currentUser = null;
                    Platform.runLater(() -> {
                        DialogWindow.showWarningWindow("Your profile was deleted", null);
                        logout();
                    });
                }
                if (AdminPanelView.isOpened()) {
                    AdminController.getInstance().processMessage(responseMessage);
                }
                break;
            default:
                break;
        }
        //}
    }

    /**
     * Exits the application
     */
    public void exit() {
        if (thread.isConnected()) {
            if (currentUser != null) {
                thread.sendMessage(new UserLogoutMessage(currentUser.getNickname()));
            }
            thread.sendMessage(new UserDisconnectMessage());
        }
        thread.disconnect();
        stage.close();
    }

    /**
     * Logs out from current account
     */
    public void logout() {
        if (currentUser != null) {
            thread.sendMessage(new UserLogoutMessage(currentUser.getNickname()));
            currentUser = null;
        }
        LoginView.getInstance().showStage();
        users = new HashMap<>();
        chatRooms = new HashMap<>();
        System.out.println("Log out");
    }

    /**
     * Sends entered message to corresponding chat
     */
    public void sendMessageToChat() {
        if (mainView.getSelectedChatRoom() == null) {
            return;
        }
        if (mainView.getSelectedChatRoom().getId() == 1 && currentUser.isBanned()) {
            DialogWindow.showWarningWindow("You are banned by admin", null);
            return;
        }
        String text = mainView.getMessageInput().trim();
        if (text.isEmpty()) {
            return;
        }
        ChatMessage message = new ChatMessage();
        message.setSender(this.getCurrentUser().getNickname());
        message.setText(text);
        message.setChatId(mainView.getSelectedChatRoom().getId());

        thread.sendMessage(message);
        mainView.clearMessageInput();
    }

    /**
     * Shows window for creating new chat
     */
    public void showCreateNewChatDialog() {
        CreateChatView createChatView = CreateChatView.getInstance();
        createChatView.setOnlineUsers(getUsers());
        createChatView.setUsersWithoutPrivateChat(getUsersWithoutPrivateChat());
        createChatView.getStage().showAndWait();
    }

    /**
     * Event handler for "Create" button click from CreateChatView.
     * Gets information from CreateChatView and sends request to create new chat.
     *
     * @param view view where event handler was called
     */
    public void handleCreateChatClick(CreateChatView view) {
        ChatRoom chatRoom;
        boolean isPrivate = view.isPrivate();
        if (isPrivate) {
            User selectedUser = view.getSelectedUser();
            chatRoom = new ChatRoom(selectedUser, currentUser);
        } else {
            Set<User> users = new HashSet<>(view.getUsersList());
            String chatName = view.getGroupName();
            users.add(currentUser);
            chatRoom = new ChatRoom(chatName, users);
            chatRoom.setAdminNickname(currentUser.getNickname());
        }
        ChatRoomCreationRequest message = new ChatRoomCreationRequest(chatRoom);
        thread.sendMessage(message);
        view.close();
    }

    /**
     * Shows window for editing user profile
     */
    public void showEditProfileDialog(User user) {
        EditProfileView editProfileView = EditProfileView.getInstance();
        editProfileView.setUserInfo(user);
        editProfileView.getStage().showAndWait();
    }

    /**
     * Event handler for "Save changes" button click from EditProfileView.
     * Gets information from EditProfileView and sends request to edit user profile.
     *
     * @param view view where event handler was called
     */
    public void saveProfileChanges(EditProfileView view) {
        String newFullName = view.getFullName();
        String newPassword = view.getPassword();
        boolean isUpdated = false;
        if (!newFullName.equals(view.getUser().getFullName())) {
            isUpdated = true;
        }

        if (view.getPassword() != null && !view.getPassword().isEmpty()) {
            if (view.isPasswordConfirmed()) {
                isUpdated = true;
            } else {
                DialogWindow.showWarningWindow("Passwords don't match", "The password and confirm password fields do not match.");
                isUpdated = false;
            }
        }
        if (isUpdated) {
            thread.sendMessage(new ChangeCredentialsRequest(view.getUser().getNickname(), newFullName, newPassword));
        }
    }

    /**
     * Shows window with chat info
     */
    public void showChatInfo() {
        ChatInfoView chatInfoView = ChatInfoView.getInstance();
        chatInfoView.setChatRoom(mainView.getSelectedChatRoom());
        chatInfoView.getStage().showAndWait();
    }

    /**
     * Event handler for "Save" button click from ChatInfoView.
     * Gets information from ChatInfoView and sends request update chat room.
     */
    public void saveGroupChatChanges() {
        ChatInfoView view = ChatInfoView.getInstance();
        List<User> newUsersList = view.getUsersList();
        String newName = view.getName();
        if (newName.isEmpty()) {
            DialogWindow.showWarningWindow("Invalid name", "Group chat name cannot be empty");
            return;
        }
        if (newUsersList.size() < 1) {
            DialogWindow.showWarningWindow(null, "Group chat has to have at least 1 member");
            return;
        }

        ChatRoom room = mainView.getSelectedChatRoom();
        List<User> oldMembers = new ArrayList<>(room.getMembers());
        if (!newName.equals(room.getName()) || !oldMembers.equals(newUsersList)) {
            List<User> membersToAdd = new ArrayList<>(newUsersList);
            membersToAdd.removeAll(oldMembers);
            List<User> membersToDelete = new ArrayList<>(oldMembers);
            membersToDelete.removeAll(newUsersList);

            ChatUpdateMessage message = new ChatUpdateMessage(room.getId(), newName, membersToAdd, membersToDelete);
            thread.sendMessage(message);
        }
        view.close();
    }

    /**
     * Shows window for adding new members to chat room
     */
    public void showAddMemberToGroupChatView() {
        AddMembersToGroupChatView view = AddMembersToGroupChatView.getInstance();
        Collection<User> members = ChatInfoView.getInstance().getUsersList();
        List<User> availableUsers = getUsers().stream().filter(item -> !members.contains(item)).collect(Collectors.toList());
        view.setAvailableUsers(availableUsers);
        view.getStage().showAndWait();
    }

    /**
     * Event handler for "Add" button click from AddMembersToGroupChatView.
     * Gets list of selected users abd adds them to users in ChatInfoView
     */
    public void addMembersToGroupChat() {
        AddMembersToGroupChatView view = AddMembersToGroupChatView.getInstance();
        ChatInfoView.getInstance().addMembersToListView(AddMembersToGroupChatView.getInstance().getSelectedUsers());
        view.close();
    }

    /**
     * Shows admin panel window
     */
    public void openAdminPanel() {
        AdminPanelView adminPanelView = AdminPanelView.getInstance();
        AdminController.getInstance().sendAllUsersRequest();
        adminPanelView.getStage().showAndWait();
    }

    /**
     * Event handler for "Leave chat" link click from ChatInfoView.
     * Send request to leave shown chat
     */
    public void leaveChatRoom() {
        ChatInfoView view = ChatInfoView.getInstance();
        ChatRoom room = mainView.getSelectedChatRoom();
        List<User> membersToDelete = new ArrayList<>();
        membersToDelete.add(currentUser);
        ChatUpdateMessage message = new ChatUpdateMessage(room.getId(), null, null, membersToDelete);
        thread.sendMessage(message);
        view.close();
    }

    /**
     * Opens main chat room
     */
    public void openMainChatRoom() {
        List<ChatRoom> list = chatRooms.values().stream()
                .filter(item -> !item.isPrivate())
                .filter(item -> item.getName().equals("mainChatRoom")).collect(Collectors.toList());
        if (list.size() == 1) {
            Platform.runLater(() -> mainView.selectChatRoom(list.get(0)));
        }
    }
}
