package com.group4.client.view.listcells;

import com.group4.client.controller.Controller;
import com.group4.server.model.entities.ChatRoom;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Represents a view of the chat ListCell
 */
public class ChatListCellView extends ListCell<ChatRoom> {
    private static final Logger log = Logger.getLogger(ChatListCellView.class);
    @FXML
    private ImageView chatImageView;

    @FXML
    private Label chatNameLabel;

    @FXML
    private HBox hbox;

    private FXMLLoader mLLoader;

    /**
     * Constructs new chat ListCell and loads its view from appropriate resource file
     */
    public ChatListCellView() {
        if (mLLoader == null) {
            mLLoader = new FXMLLoader(getClass().getResource("/chatListCellView.fxml"));
            mLLoader.setController(this);
            try {
                mLLoader.load();
            } catch (IOException e) {
                log.error("Can't load ChatListCellView from resources.", e);
                throw new RuntimeException("Can't load ChatListCellView from resources.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateItem(ChatRoom chatRoom, boolean empty) {
        super.updateItem(chatRoom, empty);

        if(empty || chatRoom == null) {
            setText(null);
            setGraphic(null);
        } else {
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
