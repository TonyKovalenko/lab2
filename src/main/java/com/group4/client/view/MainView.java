package com.group4.client.view;
import com.group4.client.controller.Controller;
import com.group4.server.model.MessageTypes.ChatMessage;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class MainView extends View {
    private static MainView instance;
    private Controller controller;

    public static MainView getInstance() {
        try {
            instance = (MainView) View.loadViewFromFxml(Controller.getInstance().getStage(), "/mainWindow.fxml", "Messenger");
            Controller controller = Controller.getInstance();
            controller.setView(instance);
            instance.setController(controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @FXML
    private MenuItem newPrivateChatButton;

    @FXML
    private MenuItem newGroupChatButton;

    @FXML
    private MenuItem editProfileButton;

    @FXML
    private MenuItem exitButton;

    @FXML
    private ListView<ChatRoom> chatRooms;

    @FXML
    private TextArea messageInput;

    @FXML
    private Label chatName;

    @FXML
    private Button sendButton;

    @FXML
    private ListView <ChatMessage> chatMessageListView;

    @FXML
    private ListView<User> onlineUsers;

    public void initialize() {
        /*chatRooms.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ChatRoom>() {

            @Override
            public void changed(ObservableValue<? extends ChatRoom> observable, ChatRoom oldValue, ChatRoom newValue) {
                chatMessageListView.setItems(FXCollections.observableArrayList(newValue.getMessages()));
            }
        });*/
    }
    @FXML
    void createNewGroupChat(ActionEvent event) {

    }

    @FXML
    void createNewPrivateChat(ActionEvent event) {

    }

    @FXML
    void editProfile(ActionEvent event) {

    }

    @FXML
    void exit(ActionEvent event) {

    }

    @FXML
    void onSendButtonClick(ActionEvent event) {

    }

    public void setOnlineUsers(Collection<User> users) {
        onlineUsers.setItems(FXCollections.observableArrayList(users));
    }

    public void setChatRooms(Collection<ChatRoom> chatRooms) {
        System.out.println(this.chatRooms);
        System.out.println(chatRooms);
        this.chatRooms.setItems(FXCollections.observableArrayList(chatRooms));
    }
}
