package com.group4.client.view;
import com.group4.client.controller.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.text.TextFlow;

import java.io.IOException;

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
    private ListView<?> chatRooms;

    @FXML
    private TextArea messageInput;

    @FXML
    private Label chatName;

    @FXML
    private Button sendButton;

    @FXML
    private TextFlow messagesTextFlow;

    @FXML
    private ListView<?> onlineUsers;

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

}
