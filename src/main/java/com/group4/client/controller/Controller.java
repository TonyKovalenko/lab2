package com.group4.client.controller;

import com.group4.client.view.*;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.types.*;
import com.group4.server.model.message.wrappers.MessageWrapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Controller extends Application {
    private Stage stage;
    private MainView mainView;
    private MessageThread thread;
    private static Controller instance;
    private User currentUser;
    private HashMap<String, User> users = new HashMap<>();
    private HashMap<Long, ChatRoom> chatRooms = new HashMap<>();

    public static Controller getInstance() {
        return instance;
    }

    public Stage getStage() {
        return stage;
    }

    public MessageThread getThread() {
        return thread;
    }

    public void setThread(MessageThread messageThread) {
        thread = messageThread;
    }

    public void setView(MainView view) {
        mainView = view;
    }

    public MainView getMainView() {
        return mainView;
    }

    public Map<Long, ChatRoom> getChatRooms() {
        return chatRooms;
    }

    public ChatRoom getChatRoomById(long id) {
        return chatRooms.get(id);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        Platform.runLater(() -> {
            mainView.updateAdminPanel(currentUser.isAdmin());
            if (currentUser.isBanned()) {
                DialogWindow.showWarningWindow(null, "You have been banned");
            }
        });
    }

    public User getUserByNickname(String nickname) {
        return users.get(nickname);
    }

    public Collection<User> getUsers() {
        return users.values();
    }

    public void updateChatRoomsView() {
        Platform.runLater(() -> mainView.setChatRoomsWithUser(chatRooms.values()));
    }

    public void updateOnlineUsersView() {
        Platform.runLater(() -> mainView.setOnlineUsers(getUsers()));
    }

    public List<User> getUsersWithoutPrivateChat() {
        List<User> usersWithoutPrivateChat = new ArrayList<>(getUsers());
        Controller.getInstance().getChatRooms().values()
                .stream()
                .filter(ChatRoom::isPrivate)
                .forEach((item) -> usersWithoutPrivateChat.remove(item.getOtherMember(Controller.getInstance().getCurrentUser())));
        return usersWithoutPrivateChat;
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
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
            thread.reconnect();
        }
    }

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
                break;
            case CHAT_CREATION_RESPONSE:
                ChatRoomCreationResponse chatRoomCreationResponse = (ChatRoomCreationResponse) responseMessage.getEncapsulatedMessage();
                if (chatRoomCreationResponse.isSuccessful()) {
                    ChatRoom chatRoom = chatRoomCreationResponse.getChatRoom();
                    chatRooms.put(chatRoom.getId(), chatRoom);
                    updateChatRoomsView();
                }
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

    public void logout() {
        if (currentUser != null) {
            thread.sendMessage(new UserLogoutMessage(currentUser.getNickname()));
            currentUser = null;
        }
        LoginView.getInstance().showStage();
        users = new HashMap<>();
        chatRooms = new HashMap<>();
    }

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

    public void showCreateNewChatDialog() {
        CreateChatView createChatView = CreateChatView.getInstance();
        createChatView.setOnlineUsers(getUsers());
        createChatView.setUsersWithoutPrivateChat(getUsersWithoutPrivateChat());
        createChatView.getStage().showAndWait();
    }

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

    public void showEditProfileDialog(User user) {
        EditProfileView editProfileView = EditProfileView.getInstance();
        editProfileView.setUserInfo(user);
        editProfileView.getStage().showAndWait();
    }

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

    public void showChatInfo() {
        ChatInfoView chatInfoView = ChatInfoView.getInstance();
        chatInfoView.setChatRoom(mainView.getSelectedChatRoom());
        chatInfoView.getStage().showAndWait();
    }

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

    public void showAddMemberToGroupChatView() {
        AddMembersToGroupChatView view = AddMembersToGroupChatView.getInstance();
        Collection<User> members = ChatInfoView.getInstance().getUsersList();
        List<User> availableUsers = getUsers().stream().filter(item -> !members.contains(item)).collect(Collectors.toList());
        view.setAvailableUsers(availableUsers);
        view.getStage().showAndWait();
    }

    public void addMembersToGroupChat() {
        AddMembersToGroupChatView view = AddMembersToGroupChatView.getInstance();
        ChatInfoView.getInstance().addMembersToListView(AddMembersToGroupChatView.getInstance().getSelectedUsers());
        view.close();
    }

    public void openAdminPanel() {
        AdminPanelView adminPanelView = AdminPanelView.getInstance();
        AdminController.getInstance().sendAllUsersRequest();
        adminPanelView.getStage().showAndWait();
    }

    public void leaveChatRoom() {
        ChatInfoView view = ChatInfoView.getInstance();
        ChatRoom room = mainView.getSelectedChatRoom();
        List<User> membersToDelete = new ArrayList<>();
        membersToDelete.add(currentUser);
        ChatUpdateMessage message = new ChatUpdateMessage(room.getId(), null, null, membersToDelete);
        thread.sendMessage(message);
        view.close();
    }

    public void openMainChatRoom() {
        List<ChatRoom> list = chatRooms.values().stream()
                .filter(item -> !item.isPrivate())
                .filter(item -> item.getName().equals("mainChatRoom")).collect(Collectors.toList());
        if (list.size() == 1) {
            Platform.runLater(() -> mainView.selectChatRoom(list.get(0)));
        }
    }
}
