package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.server.model.entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class CreateGroupView extends View {
    Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @FXML
    private ListView<User> usersListView;

    @FXML
    private TextField groupNameTextField;

    @FXML
    private Button createGroupButton;

    @FXML
    void handleCreateClick(ActionEvent event) {

    }
}

