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

/**
 * Represents a view of the application window for adding new members to group chat
 */
public class AddMembersToGroupChatView extends View{
    private static final Logger log = Logger.getLogger(AddMembersToGroupChatView.class);
    private static AddMembersToGroupChatView instance;
    private Controller controller;

    @FXML
    private ListView<User> usersListView;

    /**
     * Gets instance of the class
     *
     * @return instance of the class
     */
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
        usersListView.setCellFactory(param -> new UsersListCellView());
        usersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Event handler for "Add" button click
     */
    @FXML
    public void handleAddClick() {
        controller.addMembersToGroupChat();
    }

    /**
     * Sets available online users whom the current user can add to the chat
     *
     * @param users online users whom the current user can add to the chat
     */
    public void setAvailableUsers(Collection<User> users) {
        usersListView.setItems(FXCollections.observableArrayList(users));
    }

    /**
     * Gets selected users for addition to chat
     *
     * @return selected users for addition to chat
     */
    public List<User> getSelectedUsers() {
        return usersListView.getSelectionModel().getSelectedItems();
    }

    /**
     * Closes the view
     */
    public void close() {
        this.getStage().close();
    }
}
