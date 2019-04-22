package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.client.view.listcells.ChatListCellView;
import com.group4.client.view.listcells.MessagesListCellView;
import com.group4.client.view.listcells.UsersListCellView;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.types.ChatMessage;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.Collection;

public class MainView extends View {
    private static MainView instance;
    private Controller controller;

    @FXML
    private ListView<ChatRoom> chatRoomsWithUser;

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

    @FXML
    private Menu adminMenu;

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
        chatRoomsWithUser.setCellFactory(param -> new ChatListCellView());
        onlineUsers.setCellFactory(param -> new UsersListCellView());
        chatMessageListView.setCellFactory(param -> new MessagesListCellView());
        chatRoomsWithUser.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
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
            } else {
                chatName.setText(null);
                infoImageButton.setVisible(false);
            }
        });
    }

    @FXML
    public void createNewChat() {
        controller.showCreateNewChatDialog();
    }

    @FXML
    public void editProfile() {
        controller.showEditProfileDialog(controller.getCurrentUser());
    }

    @FXML
    public void logout() {
        controller.logout();
    }

    @FXML
    public void exit() {
        controller.exit();
    }

    @FXML
    public void onSendButtonClick() {
        controller.sendMessageToChat();
    }

    @FXML
    public void showChatInfo() {
        controller.showChatInfo();
    }

    @FXML
    public void openAdminPanel() {
        controller.openAdminPanel();
    }

    public void setOnlineUsers(Collection<User> users) {
        onlineUsers.getItems().clear();
        onlineUsers.getItems().addAll(users);
        //onlineUsers.setItems(FXCollections.observableArrayList(users));
    }

    public void setChatRoomsWithUser(Collection<ChatRoom> chatRoomsWithUser) {
        this.chatRoomsWithUser.setItems(FXCollections.observableArrayList(chatRoomsWithUser));
    }

    public String getMessageInput() {
        return messageInput.getText();
    }

    public ChatRoom getSelectedChatRoom() {
        return chatRoomsWithUser.getSelectionModel().getSelectedItem();
    }

    public void updateAdminPanel(boolean isAdmin) {
        adminMenu.setVisible(isAdmin);
    }
}
