package com.group4.client.view.listcells;

import com.group4.client.controller.Controller;
import com.group4.server.model.entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class GroupMemberListCellView extends ListCell<User> {
    @FXML
    private StackPane stackPane;

    @FXML
    private Label nicknameLabel;

    @FXML
    private Label fullNameLabel;

    @FXML
    private ImageView imageView;

    private FXMLLoader mLLoader;

    public GroupMemberListCellView() {
        if (mLLoader == null) {
            mLLoader = new FXMLLoader(getClass().getResource("/groupMembersListCellView.fxml"));
            mLLoader.setController(this);
            try {
                mLLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);

        if(empty || user == null) {
            setText(null);
            setGraphic(null);
        } else {
            nicknameLabel.setText(user.getNickname());
            fullNameLabel.setText(user.getFullName());
            if (user.equals(Controller.getInstance().getCurrentUser()) || !Controller.getInstance().getCurrentUser().isAdmin()) {
                stackPane.getChildren().remove(imageView);
            }
            setText(null);
            setGraphic(stackPane);
        }
    }

    public ImageView getImageView() {
        return imageView;
    }
}