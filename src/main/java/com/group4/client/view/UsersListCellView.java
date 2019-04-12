package com.group4.client.view;

import com.group4.server.model.entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class UsersListCellView extends ListCell<User> {

    @FXML
    private HBox hbox;

    @FXML
    private ImageView userImageView;

    @FXML
    private Label nicknameLabel;

    @FXML
    private Label fullnameLabel;

    private FXMLLoader mLLoader;

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        if(empty || user == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("/usersListCellView.fxml"));
                mLLoader.setController(this);
                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            userImageView.setImage(new Image("/user0.png"));
            nicknameLabel.setText(user.getNickname());
            fullnameLabel.setText(user.getFullName());

            setText(null);
            setGraphic(hbox);
        }

    }
}
