package com.simotion.talk.UI;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class ViewLocationWindowController {
    @FXML private ImageView mapImageView;
    @FXML private Circle pinCircle;

    private int floor;
    private double ratioX, ratioY;
    void setLocation(int map, double ratioX, double ratioY) {
        this.floor = map;
        this.ratioX = ratioX;
        this.ratioY = ratioY;

        String location = getClass().getResource("/map/"+floor+"f.png").toExternalForm();
        mapImageView.setImage(new Image(location));

        pinCircle.setCenterX(ratioX*mapImageView.getFitWidth());
        pinCircle.setCenterY(ratioY*mapImageView.getFitHeight());
    }
    @FXML
    public void initialize() {
    }
}
