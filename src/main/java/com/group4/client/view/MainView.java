package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.types.ChatMessage;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.Collection;

public class MainView extends View {
    private static MainView instance;
    private Controller controller;

    @FXML
    private ListView<ChatRoom> chatRooms;

    @FXML
    private TextArea messageInput;

    @FXML
    private Label chatName;

    @FXML
    private ImageView infoImageButton;

    @FXML
    private ListView<ChatMessage> chatMessageListView;

    @FXML
    private ListView<User> onlineUsers;

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

    public void initialize() {
        chatRooms.setCellFactory(param -> new ChatListCellView());
        onlineUsers.setCellFactory(param -> new UsersListCellView());
        chatMessageListView.setCellFactory(param -> new MessagesListCellView());
        chatRooms.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("selection changed to : " + newValue);
            if (newValue != null) {
                chatMessageListView.setItems(FXCollections.observableArrayList(newValue.getMessages()));
                if (newValue.isPrivate()) {
                    if (Controller.getInstance().getCurrentUser() != null) {
                        chatName.setText(newValue.getOtherMember(Controller.getInstance().getCurrentUser()).getNickname());
                    }
                    infoImageButton.setVisible(false);
                } else {
                    chatName.setText(newValue.getName());
                    infoImageButton.setVisible(true);
                }
                chatMessageListView.scrollTo(chatMessageListView.getItems().size()-1);
            }
        });
    }

    @FXML
    private void createNewChat() {
        controller.showCreateNewChatDialog();
    }

    @FXML
    private void editProfile() {
        controller.showEditProfileDialog();
    }

    @FXML
    private void exit() {
        controller.exit();
    }

    @FXML
    private void onSendButtonClick() {
        controller.sendMessageToChat();
    }

    @FXML
    private void showChatInfo() {
        controller.showChatInfo();
    }

    public void setOnlineUsers(Collection<User> users) {
        System.out.println("users: " + users.size());
        onlineUsers.setItems(FXCollections.observableArrayList(users));
    }

    public void setChatRooms(Collection<ChatRoom> chatRooms) {
        //System.out.println(chatRooms);
        for (ChatRoom room : chatRooms) {
            System.out.println(room.getId() + " - " + room.getMessages().size());
        }
        this.chatRooms.setItems(FXCollections.observableArrayList(chatRooms));
    }

    public String getMessageInput() {
        return messageInput.getText();
    }

    public ChatRoom getSelectedChatRoom() {
        return chatRooms.getSelectionModel().getSelectedItem();
    }
}
