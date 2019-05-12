package com.group4.client.view;

import com.group4.client.controller.AdminController;
import com.group4.server.model.entities.User;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Collection;

/**
 * Represents a view of the application admin panel window
 */
public class AdminPanelView extends View {
    private static final Logger log = Logger.getLogger(AdminPanelView.class);
    public static AdminPanelView instance;
    private AdminController controller;

    @FXML
    private TableView<User> usersTableView;

    @FXML
    private TableColumn<User, String> nicknameColumn;

    @FXML
    private TableColumn<User, String> fullNameColumn;

    @FXML
    private TableColumn<User, Boolean> isAdminColumn;

    @FXML
    private TableColumn<User, Boolean> isBannedColumn;

    /**
     * Gets instance of the class
     *
     * @return instance of the class
     */
    public static AdminPanelView getInstance() {
        if (instance == null) {
            try {
                Stage dialogStage = View.newModalStage();
                instance = (AdminPanelView) View.loadViewFromFxml(dialogStage, "/adminPanelView.fxml", "Admin panel");
                AdminController controller = AdminController.getInstance();
                controller.setView(instance);
                instance.setController(controller);
                instance.getStage().setOnCloseRequest(windowEvent -> {
                    instance = null;
                });
            } catch (IOException e) {
                log.error("Can't get instance of AdminPanelView.", e);
                throw new RuntimeException("Can't get instance of AdminPanelView.", e);
            }
        }
        return instance;
    }

    /**
     * Returns {@code true}  if the view is opened, otherwise {@code false} .
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
    public void setController(AdminController controller) {
        this.controller = controller;
    }

    /**
     * Sets users list items for the ListView
     *
     * @param users users list items
     */
    public void setUsers(Collection<User> users) {
        usersTableView.getItems().clear();
        usersTableView.getItems().addAll(users);
    }

    /**
     * Returns selected user from the list
     *
     * @return selected user from the list
     */
    public User getSelectedUser() {
        return usersTableView.getSelectionModel().getSelectedItem();
    }

    /**
     * Initializes state of the view
     */
    public void initialize() {
        nicknameColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        isBannedColumn.setCellValueFactory(new PropertyValueFactory<>("isBanned"));

        isAdminColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper((p.getValue().isAdmin())? "+" : "-"));
        isBannedColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper((p.getValue().isBanned())? "+" : "-"));
    }

    /**
     * Event handler for "Ban user" button click
     */
    @FXML
    public void banUser() {
        controller.banUser(getSelectedUser());
    }

    /**
     * Event handler for "Delete user" button click
     */
    @FXML
    public void deleteUser() {
        controller.deleteUser(getSelectedUser());
    }

    /**
     * Event handler for "Edit user" button click
     */
    @FXML
    public void editUser() {
        controller.editUser(getSelectedUser());
    }

    /**
     * Event handler for "Unban user" button click
     */
    @FXML
    public void unbanUser() {
        controller.unbanUser(getSelectedUser());
    }

    /**
     * Event handler for "Refresh table" button click
     */
    @FXML
    public void refreshTable() {
        controller.sendAllUsersRequest();
    }
}
