package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.client.controller.impl.ControllerImpl;
import com.group4.client.view.listcells.UsersListCellView;
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
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Represents a view of the application window for creation new chat
 */
public class CreateChatView extends View {
    private static final Logger log = Logger.getLogger(CreateChatView.class);
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

    /**
     * Gets instance of the class
     * @return instance of the class
     */
    public static CreateChatView getInstance() {
        if (instance == null) {
            try {
                Stage dialogStage = View.newModalStage();
                instance = (CreateChatView) View.loadViewFromFxml(dialogStage, "/createChatView.fxml", "Create chat");
                Controller controller = ControllerImpl.getInstance();
                instance.setController(controller);
                instance.getStage().setOnCloseRequest(windowEvent -> {
                    instance = null;
                });
            } catch (IOException e) {
                log.error("Can't get instance of CreateChatView.", e);
                throw new RuntimeException("Can't get instance of CreateChatView.", e);
            }
        }
        return instance;
    }

    /**
     * Returns {@code true}  if the view is opened, otherwise {@code false}.
     *
     * @return {@code true}  if the view is opened
     */
    public static boolean isOpened() {
        return instance != null;
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
     * Sets online users to usersListView
     * @param onlineUsers online users for usersListView
     */
    public void setOnlineUsers(Collection<User> onlineUsers) {
        this.onlineUsers = FXCollections.observableArrayList(onlineUsers);
        usersListView.getItems().clear();
        usersListView.getItems().addAll(this.onlineUsers);
    }

    /**
     * Sets users without private chat
     * @param usersWithoutPrivateChat users without private chat
     */
    public void setUsersWithoutPrivateChat(Collection<User> usersWithoutPrivateChat) {
        this.usersWithoutPrivateChat = FXCollections.observableArrayList(usersWithoutPrivateChat);
    }

    /**
     * Gets selected users for creation new chat
     * @return selected users for creation new chat
     */
    public List<User> getUsersList() {
        return usersListView.getSelectionModel().getSelectedItems();
    }

    /**
     * Gets selected user from users list view
     * @return selected user from users list view
     */
    public User getSelectedUser() {
        return usersListView.getSelectionModel().getSelectedItem();
    }

    /**
     * Returns {@code true}  if the 'isPrivateCheckBox' is selected, otherwise {@code false} .
     * @return {@code true}  if the 'isPrivateCheckBox' is selected
     */
    public boolean isPrivate() {
        return isPrivateCheckbox.isSelected();
    }

    /**
     * Gets entered group name from text field
     * @return entered group name
     */
    public String getGroupName() {
        return groupNameTextField.getText();
    }

    /**
     * Initializes state of the view
     */
    public void initialize() {
        usersListView.setCellFactory(param -> new UsersListCellView());
        usersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Event handler for 'isPrivateCheckbox' selection
     */
    @FXML
    public void changeFields() {
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

    /**
     * Event handler for "Create" button click
     */
    @FXML
    public void handleCreateClick() {
        controller.handleCreateChatClick(instance);
    }

    /**
     * Closes the view
     */
    public void close() {
        this.getStage().close();
    }
}

