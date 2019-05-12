package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.client.view.listcells.GroupMemberListCellView;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a view of the application window for displaying and editing chat info
 */
public class ChatInfoView extends View {
    private static final Logger log = Logger.getLogger(ChatInfoView.class);
    public static ChatInfoView instance;
    private Controller controller;
    private ChatRoom chatRoom;

    @FXML
    private ListView<User> usersListView;

    @FXML
    private TextField groupNameTextField;

    @FXML
    private Button addMemberButton;

    @FXML
    private Hyperlink leaveChatLink;

    /**
     * Gets instance of the class
     *
     * @return instance of the class
     */
    public static ChatInfoView getInstance() {
        if (instance == null) {
            try {
                Stage dialogStage = View.newModalStage();
                instance = (ChatInfoView) View.loadViewFromFxml(dialogStage, "/chatInfoView.fxml", "Chat info");
                Controller controller = Controller.getInstance();
                instance.setController(controller);
            } catch (IOException e) {
                log.error("Can't get instance of ChatInfoView.", e);
                throw new RuntimeException("Can't get instance of ChatInfoView.", e);
            }
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
        usersListView.setCellFactory(param -> {
            GroupMemberListCellView cell = new GroupMemberListCellView();
            cell.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                usersListView.getItems().remove(cell.getItem());
            });
            return cell;
        });
    }

    /**
     * Event handler for "Add" button click
     */
    @FXML
    public void handleAddMemberClick() {
        controller.showAddMemberToGroupChatView();
    }

    /**
     * Event handler for "Save" button click
     */
    @FXML
    public void handleSaveClick() {
        controller.saveGroupChatChanges();
    }

    /**
     * Event handler for "Leave chat" link click
     */
    @FXML
    public void leaveChat() {
        controller.leaveChatRoom();
    }

    /**
     * Sets chat room that is displayed in current view
     *
     * @param chatRoom
     */
    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        groupNameTextField.setText(chatRoom.getName());
        ObservableList<User> users = FXCollections.observableArrayList(chatRoom.getMembers());
        users.sort(Comparator.comparing(User::getNickname));
        usersListView.setItems(users);
        addMemberButton.setVisible(chatRoom.getId() != 1);
        leaveChatLink.setVisible(chatRoom.getId() != 1);
    }

    /**
     * Gets chat room that is displayed in current view
     *
     * @return
     */
    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    /**
     * Adds selected users to users ListView
     *
     * @param selectedUsers selected users to be add to users ListView
     */
    public void addMembersToListView(List<User> selectedUsers) {
        usersListView.getItems().addAll(selectedUsers);
    }

    /**
     * Returns users from users ListView
     *
     * @return users from users ListView
     */
    public List<User> getUsersList() {
        return usersListView.getItems();
    }

    /**
     * Gets entered name from groupNameTextField
     *
     * @return entered group name
     */
    public String getName() {
        return groupNameTextField.getText();
    }

    /**
     * Closes the view
     */
    public void close() {
        this.getStage().close();
    }
}
