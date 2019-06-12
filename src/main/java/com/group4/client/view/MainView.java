package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.client.controller.impl.ControllerImpl;
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
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;

/**
 * Represents a view of the application main window
 */
public class MainView extends View {
    private static final Logger log = Logger.getLogger(MainView.class);
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

    /**
     * Gets instance of the class
     *
     * @return instance of the class
     */
    public static MainView getInstance() {
        try {
            instance = (MainView) View.loadViewFromFxml(ControllerImpl.getInstance().getStage(), "/mainWindow.fxml", "Messenger");
            Controller controller = ControllerImpl.getInstance();
            controller.setView(instance);
            instance.setController(controller);
        } catch (IOException e) {
            log.error("Can't get instance of MainView.", e);
            throw new RuntimeException("Can't get instance of MainView.", e);
        }
        return instance;
    }

    /**
     * Sets controller for the view
     *
     * @param controller controller for the view
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * Initializes state of the view
     */
    public void initialize() {
        chatRoomsWithUser.setCellFactory(param -> new ChatListCellView());
        onlineUsers.setCellFactory(param -> new UsersListCellView());
        chatMessageListView.setCellFactory(param -> new MessagesListCellView());
        chatRoomsWithUser.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                chatMessageListView.setItems(FXCollections.observableArrayList(newValue.getMessages()));
                if (newValue.isPrivate()) {
                    if (ControllerImpl.getInstance().getCurrentUser() != null) {
                        chatName.setText(newValue.getOtherMember(ControllerImpl.getInstance().getCurrentUser()).getNickname());
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

    /**
     * Event handler for "New chat" menu button click
     */
    @FXML
    public void createNewChat() {
        controller.showCreateNewChatDialog();
    }

    /**
     * Event handler for "Edit profile" menu button click
     */
    @FXML
    public void editProfile() {
        controller.showEditProfileDialog(controller.getCurrentUser());
    }

    /**
     * Event handler for "Log out" menu button click
     */
    @FXML
    public void logout() {
        controller.logout();
    }

    /**
     * Event handler for "Exit" menu button click
     */
    @FXML
    public void exit() {
        controller.exit();
    }

    /**
     * Event handler for "Send" button click
     */
    @FXML
    public void onSendButtonClick() {
        controller.sendMessageToChat();
    }

    /**
     * Event handler for chat info button click
     */
    @FXML
    public void showChatInfo() {
        controller.showChatInfo();
    }

    /**
     * Event handler for "Open admin panel" menu button click
     */
    @FXML
    public void openAdminPanel() {
        controller.openAdminPanel();
    }

    /**
     * Sets online users
     *
     * @param users online users
     */
    public void setOnlineUsers(Collection<User> users) {
        onlineUsers.getItems().clear();
        onlineUsers.getItems().addAll(users);
        //onlineUsers.setItems(FXCollections.observableArrayList(users));
    }

    /**
     * Sets chat rooms with current user
     *
     * @param chatRoomsWithUser chat rooms with current user
     */
    public void setChatRoomsWithUser(Collection<ChatRoom> chatRoomsWithUser) {
        this.chatRoomsWithUser.setItems(FXCollections.observableArrayList(chatRoomsWithUser));
    }

    /**
     * Gets message input
     *
     * @return message input
     */
    public String getMessageInput() {
        return messageInput.getText();
    }

    /**
     * Gets selected chat room
     *
     * @return selected chat room
     */
    public ChatRoom getSelectedChatRoom() {
        return chatRoomsWithUser.getSelectionModel().getSelectedItem();
    }

    /**
     * Selects specified chat room
     *
     * @param chatRoom selected chat room to be selected
     */
    public void selectChatRoom(ChatRoom chatRoom) {
        chatRoomsWithUser.getSelectionModel().select(chatRoom);
    }

    /**
     * Updates visibility of admin panel menu according to user rights
     *
     * @param isAdmin indicates if the user is admin
     */
    public void updateAdminPanel(boolean isAdmin) {
        adminMenu.setVisible(isAdmin);
    }

    /**
     * Clears message input
     */
    public void clearMessageInput() {
        messageInput.clear();
    }
}
