package com.simotion.talk.UI;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

// public class ViewLocationWindowController
// ViewLocationWindow의 Controller
public class ViewLocationWindowController {
    @FXML private ImageView mapImageView;
    @FXML private Circle pinCircle;

    // 주어진 데이터로 위치를 설정
    void setLocation(int map, double ratioX, double ratioY) {
        String location = getClass().getResource("/map/"+map+"f.png").toExternalForm();
        mapImageView.setImage(new Image(location));

        pinCircle.setCenterX(ratioX*mapImageView.getFitWidth());
        pinCircle.setCenterY(ratioY*mapImageView.getFitHeight());
    }
}
