package com.group4.client.view.listcells;

import com.group4.server.model.message.types.ChatMessage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Represents a view of the message ListCell
 */
public class MessagesListCellView extends ListCell<ChatMessage> {
    private static final Logger log = Logger.getLogger(MessagesListCellView.class);

    @FXML
    private VBox vbox;

    @FXML
    private Label nameLabel;

    @FXML
    private Label messageTextLabel;

    private FXMLLoader mLLoader;

    /**
     * Constructs new message ListCell and loads its view from appropriate resource file
     */
    public MessagesListCellView() {
        if (mLLoader == null) {
            mLLoader = new FXMLLoader(getClass().getResource("/messagesListCellView.fxml"));
            mLLoader.setController(this);
            try {
                mLLoader.load();
            } catch (IOException e) {
                log.error("Can't load MessagesListCellView from resources.", e);
                throw new RuntimeException("Can't load MessagesListCellView from resources.", e);
            }
        }
        prefWidthProperty().bind(widthProperty());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateItem(ChatMessage message, boolean empty) {
        super.updateItem(message, empty);

        if(empty || message == null) {
            setText(null);
            setGraphic(null);
        } else {
            nameLabel.setText(message.getSender());
            messageTextLabel.setText(message.getText());

            messageTextLabel.setWrapText(true);
            setText(null);
            setGraphic(vbox);
        }

    }
}
