package com.group4.client.view;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;

public class MainView {


    @FXML
    private MenuItem newPrivateChatButton;

    @FXML
    private MenuItem newGroupChatButton;

    @FXML
    private MenuItem editProfileButton;

    @FXML
    private MenuItem exitButton;

    @FXML
    private ListView<?> chatRooms;

    @FXML
    private TextArea messageInput;

    @FXML
    private Label chatName;

    @FXML
    private Button sendBotton;

    @FXML
    private TextFlow messagesTextFlow;

    @FXML
    private ListView<?> onlineUsers;

    @FXML
    private Font x3;

    @FXML
    private Color x4;

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
    void onSendBittonClick(ActionEvent event) {

    }

}
