package com.group4.client.controller;

import com.group4.client.view.LoginView;
import com.group4.client.view.MainView;
import com.group4.server.model.MessageTypes.*;
import com.group4.server.model.MessageWrappers.MessageWrapper;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.sun.jmx.remote.internal.Unmarshal;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Controller extends Application {
    private Stage stage;
    private MainView mainView;
    private MessageThread thread;
    private static Controller instance;
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

        LoginView.getInstance().showStage();
    }

    public void processMessage(MessageWrapper requestMessage, MessageWrapper responseMessage) {
        //test data
        System.out.println("process message");
        ArrayList<User> arrayList = new ArrayList<>();
        arrayList.add(new User("qwe", "asd", "fullname"));
        chatRooms.put(2, new ChatRoom(2, true, arrayList));
        mainView.setChatRooms(chatRooms.values());

        switch (responseMessage.getMessageType()) {
            case USERS_IN_CHAT:
                UsersInChatMessage usersInChatMessage = (UsersInChatMessage) responseMessage.getEncapsulatedMessage();
                users = usersInChatMessage.getUsers();
                mainView.setOnlineUsers(users.values());
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
                mainView.setChatRooms(chatRooms.values());
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
