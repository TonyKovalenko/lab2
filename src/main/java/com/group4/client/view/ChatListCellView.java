package com.group4.client.view;

import com.group4.client.controller.Controller;
import com.group4.server.model.entities.ChatRoom;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class ChatListCellView extends ListCell<ChatRoom> {
    @FXML
    private ImageView chatImageView;

    @FXML
    private Label chatNameLabel;

    @FXML
    private HBox hbox;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(ChatRoom chatRoom, boolean empty) {
        super.updateItem(chatRoom, empty);

        if(empty || chatRoom == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/chatListCellView.fxml"));
                mLLoader.setController(this);
                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            if (chatRoom.isPrivate()) {
                chatNameLabel.setText(chatRoom.getOtherMember(Controller.getInstance().getCurrentUser()).getFullName());
                chatImageView.setImage(new Image("/user0.png"));
            } else {
                chatNameLabel.setText(chatRoom.getName());
                chatImageView.setImage(new Image("/users0.png"));
            }

            setText(null);
            setGraphic(hbox);
        }

    }
}
