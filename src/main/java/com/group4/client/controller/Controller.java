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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Collection<User> getUsersWithoutCurrent() {
        return users.values();
    }

    public void updateChatRoomsView() {
        Platform.runLater(() -> mainView.setChatRooms(chatRooms.values()));
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
                    users = usersInChatMessage.getUsers();
                    users.remove(currentUser.getId());
                    if (chatRooms.get(2L) == null) {
                        chatRooms.put(2L, new ChatRoom(2, currentUser, getUserById((currentUser.getId()==10000)?10001:10000)));
                    }
                    Platform.runLater(() -> mainView.setOnlineUsers(getUsersWithoutCurrent()));
                    break;
                case NEW_GROUPCHAT:
                case NEW_PRIVATECHAT:
                    NewGroupChatMessage newGroupChatMessage = (NewGroupChatMessage) responseMessage.getEncapsulatedMessage();
                    ChatRoom chatRoom = newGroupChatMessage.getChatRoom();
                    chatRooms.put(chatRoom.getId(), chatRoom);
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
        createChatView.setOnlineUsers(getUsersWithoutCurrent());
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
            Map<Long, User> usersMap = new HashMap<>();
            for (User user : users) {
                usersMap.put(user.getId(), user);
            }
            usersMap.put(currentUser.getId(), currentUser);
            String chatName = view.getGroupName();
            chatRoom = new ChatRoom(chatName, usersMap);
        }
        NewGroupChatMessage message = new NewGroupChatMessage(chatRoom);
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
}
