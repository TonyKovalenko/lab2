package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.server.model.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class CreateChatView extends View {
    private static CreateChatView instance;
    private Controller controller;
    private ObservableList<User> onlineUsers;
    private ObservableList<User> usersWithoutPrivateChat;

    @FXML
    private ListView<User> usersListView;

    @FXML
    private CheckBox isPrivateCheckbox;

    @FXML
    private TextField groupNameTextField;

    @FXML
    private GridPane groupFieldsPane;

    public static CreateChatView getInstance() {
        try {
            instance = (CreateChatView) View.loadViewFromFxml(Controller.getInstance().getStage(), "/createGroupView.fxml", "Create chat");
            Controller controller = Controller.getInstance();
            instance.setController(controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public static CreateChatView getInstance(Stage stage) {
        try {
            instance = (CreateChatView) View.loadViewFromFxml(stage, "/createGroupView.fxml", "Create chat");
            Controller controller = Controller.getInstance();
            instance.setController(controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setOnlineUsers(Collection<User> onlineUsers) {
        this.onlineUsers = FXCollections.observableArrayList(onlineUsers);
        usersListView.setItems(this.onlineUsers);
    }

    public void setUsersWithoutPrivateChat(Collection<User> usersWithoutPrivateChat) {
        this.usersWithoutPrivateChat = FXCollections.observableArrayList(usersWithoutPrivateChat);
    }

    public List<User> getUsersList() {
        return usersListView.getSelectionModel().getSelectedItems();
    }

    public User getSelectedUser() {
        return usersListView.getSelectionModel().getSelectedItem();
    }

    public boolean isPrivate() {
        return isPrivateCheckbox.isSelected();
    }

    public String getGroupName() {
        return groupNameTextField.getText();
    }

    public void initialize() {
        usersListView.setCellFactory(param -> new UsersListCellView());
        usersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void changeFields() {
        if (isPrivateCheckbox.isSelected()) {
            groupFieldsPane.setVisible(false);
            usersListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            usersListView.setItems(usersWithoutPrivateChat);

        } else {
            groupFieldsPane.setVisible(true);
            usersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            usersListView.setItems(onlineUsers);
        }
    }

    @FXML
    private void handleCreateClick() {
        controller.handleCreateChatClick(instance);
    }

    public void close() {
        this.getStage().close();
    }
}

