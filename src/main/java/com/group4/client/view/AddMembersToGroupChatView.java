package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.client.view.listcells.UsersListCellView;
import com.group4.server.model.entities.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class AddMembersToGroupChatView extends View{
    private static final Logger log = Logger.getLogger(AddMembersToGroupChatView.class);
    private static AddMembersToGroupChatView instance;
    private Controller controller;

    @FXML
    private ListView<User> usersListView;

    public static AddMembersToGroupChatView getInstance() {
        if (instance == null) {
            try {
                Stage dialogStage = View.newModalStage();
                instance = (AddMembersToGroupChatView) View.loadViewFromFxml(dialogStage, "/addMembersToGroupChatView.fxml", "Add members");
                Controller controller = Controller.getInstance();
                instance.setController(controller);
            } catch (IOException e) {
                log.error("Can't get instance of AddMembersToGroupChatView.", e);
                throw new RuntimeException("Can't get instance of AddMembersToGroupChatView.", e);
            }
        }
        return instance;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void initialize() {
        usersListView.setCellFactory(param -> new UsersListCellView());
        usersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    public void handleAddClick() {
        controller.addMembersToGroupChat();
    }

    public void setAvailableUsers(Collection<User> users) {
        usersListView.setItems(FXCollections.observableArrayList(users));
    }

    public List<User> getSelectedUsers() {
        return usersListView.getSelectionModel().getSelectedItems();
    }

    public void close() {
        this.getStage().close();
    }
}
