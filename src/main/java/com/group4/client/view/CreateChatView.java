package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.server.model.entities.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class CreateChatView extends View {
    private static CreateChatView instance;
    private Controller controller;

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

    @FXML
    private ListView<User> usersListView;

    @FXML
    private CheckBox isPrivateCheckbox;

    @FXML
    private Button createButton;

    @FXML
    private TextField groupNameTextField;

    @FXML
    private GridPane groupFieldsPane;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setOnlineUsers(Collection<User> onlineUsers) {
        usersListView.setItems(FXCollections.observableArrayList(onlineUsers));
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
    }

    @FXML
    void changeFields(ActionEvent event) {
        if (isPrivateCheckbox.isSelected()) {
            groupFieldsPane.setVisible(false);
            usersListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        } else {
            groupFieldsPane.setVisible(true);
            usersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
    }

    @FXML
    void handleCreateClick(ActionEvent event) {
        controller.handleCreateChatClick(instance);
    }

    public void close() {
        this.getStage().close();
    }
}

