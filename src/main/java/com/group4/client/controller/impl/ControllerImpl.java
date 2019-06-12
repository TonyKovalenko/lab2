package com.group4.client.controller.impl;

import com.group4.client.controller.Controller;
import com.group4.client.controller.MessageThread;
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
public class ControllerImpl extends Application implements Controller {
    private static final Logger log = Logger.getLogger(ControllerImpl.class);
    private Stage stage;
    private MainView mainView;
    private MessageThread thread;
    private static ControllerImpl instance;
    private User currentUser;
    private HashMap<String, User> users = new HashMap<>();
    private HashMap<Long, ChatRoom> chatRooms = new HashMap<>();
    private CreateChatView view;

    /**
     * Gets instance of the class
     *
     * @return instance of the class
     */
    public static ControllerImpl getInstance() {
        return instance;
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public MessageThread getThread() {
        return thread;
    }

    @Override
    public void setThread(MessageThread messageThread) {
        thread = messageThread;
    }

    @Override
    public void setView(MainView view) {
        mainView = view;
    }

    @Override
    public MainView getMainView() {
        return mainView;
    }

    @Override
    public Map<Long, ChatRoom> getChatRooms() {
        return chatRooms;
    }

    @Override
    public ChatRoom getChatRoomById(long id) {
        return chatRooms.get(id);
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        Platform.runLater(() -> {
            mainView.updateAdminPanel(currentUser.isAdmin());
            if (currentUser.isBanned()) {
                DialogWindow.showWarningWindow(null, "You have been banned");
            }
        });
    }

    @Override
    public User getUserByNickname(String nickname) {
        return users.get(nickname);
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public void updateChatRoomsView() {
        Platform.runLater(() -> mainView.setChatRoomsWithUser(chatRooms.values()));
    }

    @Override
    public void updateOnlineUsersView() {
        Platform.runLater(() -> mainView.setOnlineUsers(getUsers()));
    }

    @Override
    public List<User> getUsersWithoutPrivateChat() {
        List<User> usersWithoutPrivateChat = new ArrayList<>(getUsers());
        ControllerImpl.getInstance().getChatRooms().values()
                .stream()
                .filter(ChatRoom::isPrivate)
                .forEach((item) -> usersWithoutPrivateChat.remove(item.getOtherMember(ControllerImpl.getInstance().getCurrentUser())));
        return usersWithoutPrivateChat;
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        stage = primaryStage;
        ControllerImpl.getInstance().getStage().setOnCloseRequest(windowEvent -> exit());
        thread = new MessageThreadImpl();
        LoginView.getInstance().showStage();
        try {
            thread.connect();
            thread.start();
        } catch (IOException e) {
            log.info("Can't connect to server. Start reconnecting");
            thread.reconnect();
        }
    }

    @Override
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
                    Platform.runLater(() -> {
                        CreateChatView.getInstance().setOnlineUsers(users.values());
                        CreateChatView.getInstance().setUsersWithoutPrivateChat(getUsersWithoutPrivateChat());
                    });
                }
                break;
            case CHAT_CREATION_RESPONSE:
                ChatRoomCreationResponse chatRoomCreationResponse = (ChatRoomCreationResponse) responseMessage.getEncapsulatedMessage();
                if (chatRoomCreationResponse.isSuccessful()) {
                    ChatRoom chatRoom = chatRoomCreationResponse.getChatRoom();
                    chatRooms.put(chatRoom.getId(), chatRoom);
                    updateChatRoomsView();
                }
                break;
            case CHAT_CREATION_REQUEST:
                ChatRoom requestChatRoom = ((ChatRoomCreationRequest) responseMessage.getEncapsulatedMessage()).getChatRoom();
                String username = requestChatRoom.getAdminNickname();
                ChatRoomCreationResponse response = new ChatRoomCreationResponse();
                Platform.runLater(() -> {
                    boolean isConfirmed = DialogWindow.showConfirmationWindow(
                            "Invitation to chat from " + username,
                            "Do you want to start chat with " + username);
                    response.setSuccessful(isConfirmed);
                    response.setChatRoom(requestChatRoom);
                    thread.sendMessage(response);

                });
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
                        AdminControllerImpl.getInstance().processMessage(responseMessage);
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
                    AdminControllerImpl.getInstance().processMessage(responseMessage);
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
                    AdminControllerImpl.getInstance().processMessage(responseMessage);
                }
                break;
            case SERVER_RESTART:
            case SERVER_SHUTDOWN:
                thread.disconnect();
                thread.reconnect();
                break;
            default:
                break;
        }
        //}
    }

    @Override
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

    @Override
    public void logout() {
        if (currentUser != null) {
            thread.sendMessage(new UserLogoutMessage(currentUser.getNickname()));
            log.info("Log out: " + currentUser);
            currentUser = null;
        }
        LoginView.getInstance().showStage();
        users = new HashMap<>();
        chatRooms = new HashMap<>();
    }

    @Override
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

    @Override
    public void showCreateNewChatDialog() {
        CreateChatView createChatView = CreateChatView.getInstance();
        createChatView.setOnlineUsers(getUsers());
        createChatView.setUsersWithoutPrivateChat(getUsersWithoutPrivateChat());
        createChatView.getStage().showAndWait();
    }

    @Override
    public void handleCreateChatClick(CreateChatView view) {
        this.view = view;
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
        }
        chatRoom.setAdminNickname(currentUser.getNickname());
        ChatRoomCreationRequest message = new ChatRoomCreationRequest(chatRoom);
        thread.sendMessage(message);
        view.close();
    }

    @Override
    public void showEditProfileDialog(User user) {
        EditProfileView editProfileView = EditProfileView.getInstance();
        editProfileView.setUserInfo(user);
        editProfileView.getStage().showAndWait();
    }

    @Override
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

    @Override
    public void showChatInfo() {
        ChatInfoView chatInfoView = ChatInfoView.getInstance();
        chatInfoView.setChatRoom(mainView.getSelectedChatRoom());
        chatInfoView.getStage().showAndWait();
    }

    @Override
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

    @Override
    public void showAddMemberToGroupChatView() {
        AddMembersToGroupChatView view = AddMembersToGroupChatView.getInstance();
        Collection<User> members = ChatInfoView.getInstance().getUsersList();
        List<User> availableUsers = getUsers().stream().filter(item -> !members.contains(item)).collect(Collectors.toList());
        view.setAvailableUsers(availableUsers);
        view.getStage().showAndWait();
    }

    @Override
    public void addMembersToGroupChat() {
        AddMembersToGroupChatView view = AddMembersToGroupChatView.getInstance();
        ChatInfoView.getInstance().addMembersToListView(AddMembersToGroupChatView.getInstance().getSelectedUsers());
        view.close();
    }

    @Override
    public void openAdminPanel() {
        AdminPanelView adminPanelView = AdminPanelView.getInstance();
        AdminControllerImpl.getInstance().sendAllUsersRequest();
        adminPanelView.getStage().showAndWait();
    }

    @Override
    public void leaveChatRoom() {
        ChatInfoView view = ChatInfoView.getInstance();
        ChatRoom room = mainView.getSelectedChatRoom();
        List<User> membersToDelete = new ArrayList<>();
        membersToDelete.add(currentUser);
        ChatUpdateMessage message = new ChatUpdateMessage(room.getId(), null, null, membersToDelete);
        thread.sendMessage(message);
        view.close();
    }

    @Override
    public void openMainChatRoom() {
        List<ChatRoom> list = chatRooms.values().stream()
                .filter(item -> !item.isPrivate())
                .filter(item -> item.getName().equals("mainChatRoom")).collect(Collectors.toList());
        if (list.size() == 1) {
            Platform.runLater(() -> mainView.selectChatRoom(list.get(0)));
        }
    }
}
