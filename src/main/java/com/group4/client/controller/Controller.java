package com.group4.client.controller;

import com.group4.client.view.CreateChatView;
import com.group4.client.view.LoginView;
import com.group4.client.view.MainView;
import com.group4.client.view.View;
import com.group4.server.model.MessageTypes.AnswerMessage;
import com.group4.server.model.MessageTypes.ChatMessage;
import com.group4.server.model.MessageTypes.NewGroupChatMessage;
import com.group4.server.model.MessageTypes.UsersInChatMessage;
import com.group4.server.model.MessageWrappers.MessageWrapper;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Controller extends Application {
    private Stage stage;
    private MainView mainView;
    private MessageThread thread;
    private static Controller instance;
    private User currentUser;
    private HashMap<Integer, User> users;
    private HashMap<Integer, ChatRoom> chatRooms = new HashMap<>();

    public static Controller getInstance() {
        return instance;
    }

    public Stage getStage() {
        return stage;
    }

    public MessageThread getThread() {
        return thread;
    }

    public void setView(MainView view) {
        mainView = view;
    }

    public MainView getMainView() {
        return mainView;
    }

    public HashMap<Integer, ChatRoom> getChatRooms() {
        return chatRooms;
    }

    public void setChatRooms(HashMap<Integer, ChatRoom> chatRooms) {
        this.chatRooms = chatRooms;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getUserById(int id) {
        return users.get(id);
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
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        stage = primaryStage;
        thread = new MessageThread();
        thread.connect();
        thread.start();

        //test data
        currentUser = new User("qwe", "asd", "Dean Winchester");;
        User user2 = new User("azxc", "asd", "John Winchester");
        chatRooms.put(2, new ChatRoom(2, currentUser, user2));
        ArrayList<User> arrayList1 = new ArrayList<>();
        arrayList1.add(new User("marry", "1234", "Marry Winchester"));
        arrayList1.add(new User("sammy", "1234", "Sam Winchester"));
        arrayList1.add(currentUser);
        chatRooms.put(3, new ChatRoom(2, "Hunting things", arrayList1));

        LoginView.getInstance().showStage();
    }

    public void processMessage(MessageWrapper requestMessage, MessageWrapper responseMessage) {
        System.out.println("process message: " + responseMessage.getMessageType());

        if (mainView != null) {
            Platform.runLater(() -> mainView.setChatRooms(chatRooms.values()));
            switch (responseMessage.getMessageType()) {
                case USERS_IN_CHAT:
                    UsersInChatMessage usersInChatMessage = (UsersInChatMessage) responseMessage.getEncapsulatedMessage();
                    users = usersInChatMessage.getUsers();
                    Platform.runLater(() -> mainView.setOnlineUsers(users.values()));
                    break;
                case NEW_GROUPCHAT:
                case NEW_PRIVATECHAT:
                    NewGroupChatMessage newGroupChatMessage = (NewGroupChatMessage) responseMessage.getEncapsulatedMessage();
                    ChatRoom chatRoom = newGroupChatMessage.getChatRoom();
                    chatRooms.put(chatRoom.getId(), chatRoom);
                    break;
                case TO_CHAT:
                    ChatMessage chatMessage = (ChatMessage) responseMessage.getEncapsulatedMessage();
                    chatRooms.get(chatMessage.getChatId()).addMessage(chatMessage);
                    Platform.runLater(() -> mainView.setChatRooms(chatRooms.values()));
                    break;
                case PING:

                case ANSWER:
                    AnswerMessage innerMessage = (AnswerMessage) responseMessage.getEncapsulatedMessage();
                    switch (requestMessage.getMessageType()) {
                        case CHANGE_CREDENTIALS:

                            break;
                        case REGISTRATION_REQUEST:

                            break;
                        default:

                    }
            }
        }
    }

    public void exit() {
        thread.disconnect();
        System.exit(0);
    }

    public void sendMessageToChat() {
        ChatMessage message = new ChatMessage();
        message.setFromId(this.getCurrentUser().getId());
        message.setText(mainView.getMessageInput());

        thread.sendMessage(message);
    }

    public void showCreateNewChatDialog() {
        Stage dialogStage = new Stage();
        dialogStage.initOwner(stage);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        CreateChatView createChatView = CreateChatView.getInstance(dialogStage);
        createChatView.setOnlineUsers(users.values());
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
        NewGroupChatMessage message = new NewGroupChatMessage(chatRoom);
        thread.sendMessage(message);
        view.close();
    }
}
