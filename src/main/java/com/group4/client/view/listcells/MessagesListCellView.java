package com.group4.client.view.listcells;

import com.group4.client.controller.Controller;
import com.group4.server.model.entities.User;
import com.group4.server.model.message.types.ChatMessage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MessagesListCellView extends ListCell<ChatMessage> {

    @FXML
    private VBox vbox;

    @FXML
    private Label nameLabel;

    @FXML
    private Label messageTextLabel;

    private FXMLLoader mLLoader;

    public MessagesListCellView() {
        prefWidthProperty().bind(widthProperty());
    }

    @Override
    protected void updateItem(ChatMessage message, boolean empty) {
        super.updateItem(message, empty);

        if(empty || message == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/messagesListCellView.fxml"));
                mLLoader.setController(this);
                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            User user = Controller.getInstance().getChatRoomById(message.getChatId()).getMembers().get(message.getFromId());
            nameLabel.setText(user.getNickname());
            messageTextLabel.setText(message.getText());

            messageTextLabel.setWrapText(true);
            setText(null);
            setGraphic(vbox);
        }

    }
}
