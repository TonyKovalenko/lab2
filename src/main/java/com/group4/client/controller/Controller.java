package com.group4.client.controller;

import com.group4.client.view.*;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.types.*;
import com.group4.server.model.message.wrappers.MessageWrapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.stage.Modality;
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
    private HashMap<Long, User> users = new HashMap<>();
    private ObservableMap<Long, ChatRoom> chatRooms = FXCollections.observableHashMap();

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
    }

    public User getUserById(long id) {
        return users.get(id);
    }

    public Collection<User> getUsers() {
        return users.values();
    }

    public void updateChatRoomsView() {
        Platform.runLater(() -> mainView.setChatRoomsWithUser(chatRooms.values()));
    }

    public List<User> getUsersWithoutPrivateChat() {
        List<User> usersWithoutPrivateChat = new ArrayList<>(getUsers());
        Controller.getInstance().getChatRooms().values()
                .stream()
                .filter(item -> item.isPrivate())
                .forEach((item) -> {
                    usersWithoutPrivateChat.remove(item.getOtherMember(Controller.getInstance().getCurrentUser()));
                });
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
        chatRooms.addListener((MapChangeListener) change -> updateChatRoomsView());
        thread = new MessageThread();
        LoginView.getInstance().showStage();
        try {
            thread.connect();
            thread.start();
        } catch (IOException e) {
            thread.reconnect();
        }
    }

    public void processMessage(MessageWrapper requestMessage, MessageWrapper responseMessage) {
        System.out.println("process message: " + responseMessage.getMessageType());

        System.out.println("mainView: " + mainView);
        //if (mainView != null) {
            switch (responseMessage.getMessageType()) {
                case USERS_IN_CHAT:
                    UsersInChatMessage usersInChatMessage = (UsersInChatMessage) responseMessage.getEncapsulatedMessage();
                    users = new HashMap<>();
                    for (User user : usersInChatMessage.getUsers()) {
                        users.put(user.getId(), user);
                    }
                    users.remove(currentUser.getId());
                    if (chatRooms.get(2L) == null) {
                        chatRooms.put(2L, new ChatRoom(2, currentUser, getUserById((currentUser.getId()==10000)?10001:10000)));
                    }
                    Platform.runLater(() -> mainView.setOnlineUsers(getUsers()));
                    break;
                case NEW_GROUPCHAT:
                case NEW_PRIVATECHAT:
                    ChatInvitationMessage chatInvitationMessage = (ChatInvitationMessage) responseMessage.getEncapsulatedMessage();
                    Set<ChatRoom> chatRoom = chatInvitationMessage.getChatRooms();
                    chatRoom.forEach(room -> chatRooms.put(room.getId(), room));
                    //updateChatRoomsView();
                    break;
                case TO_CHAT:
                    ChatMessage chatMessage = (ChatMessage) responseMessage.getEncapsulatedMessage();
                    chatRooms.get(chatMessage.getChatId()).addMessage(chatMessage);
                    updateChatRoomsView();
                    break;
                case CHANGE_CREDENTIALS_RESPONSE:
                    ChangeCredentialsResponse changeCredentialsResponse = (ChangeCredentialsResponse) responseMessage.getEncapsulatedMessage();
                    if (changeCredentialsResponse.isConfirmed()) {
                        currentUser = changeCredentialsResponse.getUser();
                        Platform.runLater(() -> {
                            DialogWindow.showInfoWindow("Credentials change was confirmed");
                            EditProfileView.getInstance().cancel();
                        });
                    } else {
                        Platform.runLater(() -> DialogWindow.showErrorWindow("Credentials change was denied"));
                    }
                    break;
                default:
                    break;
            }
        //}
    }

    public void exit() {
        thread.disconnect();
        stage.close();
    }

    public void sendMessageToChat() {
        if (mainView.getSelectedChatRoom() == null) {
            return;
        }
        String text = mainView.getMessageInput().trim();
        if (text.isEmpty()) {
            return;
        }
        ChatMessage message = new ChatMessage();
        message.setFromId(this.getCurrentUser().getId());
        message.setText(text);
        message.setChatId(mainView.getSelectedChatRoom().getId());

        thread.sendMessage(message);
    }

    public void showCreateNewChatDialog() {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(stage);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        CreateChatView createChatView = CreateChatView.getInstance(dialogStage);
        createChatView.setOnlineUsers(getUsers());
        createChatView.setUsersWithoutPrivateChat(getUsersWithoutPrivateChat());
        dialogStage.showAndWait();
    }

    public void handleCreateChatClick(CreateChatView view) {
        ChatRoom chatRoom;
        boolean isPrivate = view.isPrivate();
        if (isPrivate) {
            User selectedUser = view.getSelectedUser();
            chatRoom = new ChatRoom(selectedUser, currentUser);
        } else {
            List<User> users = view.getUsersList();
            String chatName = view.getGroupName();
            chatRoom = new ChatRoom(chatName, users);
        }
        ChatInvitationMessage message = new ChatInvitationMessage(chatRoom);
        thread.sendMessage(message);
        view.close();
    }

    public void showEditProfileDialog() {
        EditProfileView editProfileView = EditProfileView.getInstance();
        editProfileView.setUserInfo(currentUser);
        System.out.println("showEditProfileDialog");
        editProfileView.getStage().showAndWait();
    }

    public void saveProfileChanges(EditProfileView view) {
        String newFullName = view.getFullName();
        String newPassword = view.getPassword();
        boolean isUpdated = false;
        if (!newFullName.equals(currentUser.getFullName())) {
            isUpdated = true;
        }

        if (view.getPassword() != null && !view.getPassword().isEmpty()) {
            if (view.isPasswordConfirmed()) {
                isUpdated = true;
            } else {
                DialogWindow.showWarningWindow("Passwords don't match", "The password and confirm password fields do not match.");
                System.out.println("password doesn't match");
                isUpdated = false;
            }
        }

        if (isUpdated) {
            thread.sendMessage(new ChangeCredentialsRequest(currentUser.getId(), newFullName, newPassword));
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

            UpdateGroupChatMessage message = new UpdateGroupChatMessage(room.getId(), newName, membersToAdd, membersToDelete);
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
}
