package com.group4.client.view;

import com.group4.client.controller.AdminController;
import com.group4.client.controller.Controller;
import com.group4.server.model.entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Collection;

public class AdminPanelView extends View {
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

    public static AdminPanelView getInstance() {
        if (instance == null) {
            try {
                Stage dialogStage = new Stage();
                dialogStage.initOwner(Controller.getInstance().getStage());
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                instance = (AdminPanelView) View.loadViewFromFxml(dialogStage, "/adminPanelView.fxml", "Admin panel");
                AdminController controller = AdminController.getInstance();
                controller.setView(instance);
                instance.setController(controller);
                instance.getStage().setOnCloseRequest(windowEvent -> {
                    instance = null;
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public static boolean isOpened() {
        return instance != null;
    }

    public void setController(AdminController controller) {
        this.controller = controller;
    }

    public void setUsers(Collection<User> users) {
        usersTableView.getItems().clear();
        usersTableView.getItems().addAll(users);
    }

    public User getSelectedUser() {
        return usersTableView.getSelectionModel().getSelectedItem();
    }

    public void initialize() {
        nicknameColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        isAdminColumn.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));
        isBannedColumn.setCellValueFactory(new PropertyValueFactory<>("isBanned"));
    }

    @FXML
    public void banUser() {
        controller.banUser(getSelectedUser());
    }

    @FXML
    public void deleteUser() {
        controller.deleteUser(getSelectedUser());
    }

    @FXML
    public void editUser() {
        controller.editUser(getSelectedUser());
    }

    @FXML
    public void unbanUser() {
        controller.unbanUser(getSelectedUser());
    }

    @FXML
    public void refreshTable() {
        controller.sendAllUsersRequest();
    }
}
