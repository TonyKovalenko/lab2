package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.server.model.entities.ChatRoom;
import com.group4.server.model.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatInfoView extends View {
    public static ChatInfoView instance;
    private Controller controller;

    @FXML
    private ListView<User> usersListView;

    @FXML
    private TextField groupNameTextField;

    public static ChatInfoView getInstance() {
        if (instance == null) {
            try {
                Stage dialogStage = new Stage();
                dialogStage.initOwner(Controller.getInstance().getStage());
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                instance = (ChatInfoView) View.loadViewFromFxml(dialogStage, "/chatInfoView.fxml", "Chat info");
                Controller controller = Controller.getInstance();
                instance.setController(controller);
            } catch (IOException e) {
                e.printStackTrace();
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
            cell.getUserImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                usersListView.getItems().remove(cell.getItem());
            });
            return cell;
        });
    }

    @FXML
    private void handleAddMemberClick() {

    }

    @FXML
    private void handleSaveClick() {

    }

    public void setChatRoom(ChatRoom chatRoom) {
        groupNameTextField.setText(chatRoom.getName());
        ObservableList<User> users = FXCollections.observableArrayList(chatRoom.getMembers().values());
        usersListView.setItems(users);
    }
}
