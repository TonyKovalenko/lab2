package com.group4.client.view.listcells;

import com.group4.server.model.entities.User;
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
 * Represents a view of the online user ListCell
 */
public class UsersListCellView extends ListCell<User> {
    private static final Logger log = Logger.getLogger(UsersListCellView.class);

    @FXML
    private HBox hbox;

    @FXML
    private ImageView userImageView;

    @FXML
    private Label nicknameLabel;

    @FXML
    private Label fullnameLabel;

    private FXMLLoader mLLoader;

    /**
     * Constructs new user ListCell and loads its view from appropriate resource file
     */
    public UsersListCellView() {
        if (mLLoader == null) {
            mLLoader = new FXMLLoader(getClass().getResource("/usersListCellView.fxml"));
            mLLoader.setController(this);
            try {
                mLLoader.load();
            } catch (IOException e) {
                log.error("Can't load UsersListCellView from resources.", e);
                throw new RuntimeException("Can't load UsersListCellView from resources.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        if(empty || user == null) {
            setText(null);
            setGraphic(null);
        } else {
            userImageView.setImage(new Image("/user0.png"));
            nicknameLabel.setText(user.getNickname());
            fullnameLabel.setText(user.getFullName());

            setText(null);
            setGraphic(hbox);
        }

    }
}
