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

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void initialize() {
        usersListView.setCellFactory(param -> {
            GroupMemberListCellView cell = new GroupMemberListCellView();
            cell.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                usersListView.getItems().remove(cell.getItem());
            });
            return cell;
        });
    }

    @FXML
    public void handleAddMemberClick() {
        controller.showAddMemberToGroupChatView();
    }

    @FXML
    public void handleSaveClick() {
        controller.saveGroupChatChanges();
    }

    @FXML
    public void leaveChat() {
        controller.leaveChatRoom();
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
        groupNameTextField.setText(chatRoom.getName());
        ObservableList<User> users = FXCollections.observableArrayList(chatRoom.getMembers());
        users.sort(Comparator.comparing(User::getNickname));
        usersListView.setItems(users);
        addMemberButton.setVisible(chatRoom.getId() != 1);
        leaveChatLink.setVisible(chatRoom.getId() != 1);
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void addMembersToListView(List<User> selectedUsers) {
        usersListView.getItems().addAll(selectedUsers);
    }

    public List<User> getUsersList() {
        return usersListView.getItems();
    }

    public String getName() {
        return groupNameTextField.getText();
    }

    public void close() {
        this.getStage().close();
    }
}
