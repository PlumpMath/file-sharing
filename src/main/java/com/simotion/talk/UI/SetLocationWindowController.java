package com.simotion.talk.UI;

import com.simotion.talk.ChatSaveManager;
import com.simotion.talk.Networking.MessagingClient;
import com.simotion.talk.Peer;
import com.simotion.talk.PeerListManager;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class SetLocationWindowController {
    @FXML private ChoiceBox locationChoice;
    @FXML private ImageView mapImageView;
    @FXML private Circle pinCircle;
    @FXML private Button submitBtn;
    @FXML private Button cancelBtn;

    private int currentFloor = 0;
    private double locX, locY, ratioX, ratioY;
    private boolean locSelected = false;

    private Peer myPeer;
    public void setPeer(Peer peer) {
        this.myPeer = peer;
    }
    @FXML
    public void initialize() {
        for(int i=1;i<=5;i++) {
            locationChoice.getItems().add("본관/기숙사 "+i+"층");
        }
        locationChoice.getSelectionModel().selectedIndexProperty().addListener(e -> changeFloor(locationChoice.getSelectionModel().getSelectedIndex()+1));
        locationChoice.getSelectionModel().select(0);
        pinCircle.setVisible(false);
        mapImageView.setOnMouseClicked(e ->
        {
            Bounds imageBounds = mapImageView.localToScene(mapImageView.getBoundsInLocal());
            double clickX = e.getSceneX() - imageBounds.getMinX();
            double clickY = e.getSceneY() - imageBounds.getMinY();
            locX = clickX;
            locY = clickY;
            ratioX = locX / mapImageView.getFitWidth();
            ratioY = locY / mapImageView.getFitHeight();
            pinCircle.setVisible(true);
            pinCircle.setCenterX(clickX);
            pinCircle.setCenterY(clickY);
            locSelected = true;
            submitBtn.setDisable(false);
        });
        submitBtn.setDisable(true);
        submitBtn.setOnMousePressed(e -> {
            PeerListManager.addMsg(myPeer,
                    "\\\\comehere "+currentFloor+" "+ratioX+" "+ratioY,
                    true
            );
            MessagingClient.getInstance().sendLocation(myPeer, currentFloor, ratioX, ratioY);
            ((Stage)submitBtn.getScene().getWindow()).close();
        });
        cancelBtn.setOnMousePressed(e -> ((Stage)cancelBtn.getScene().getWindow()).close());
    }
    private void changeFloor(int floor) {
        String location = getClass().getResource("/map/"+floor+"f.png").toExternalForm();
        mapImageView.setImage(new Image(location));

        currentFloor = floor;
        pinCircle.setVisible(false);
        submitBtn.setDisable(true);
        locSelected = false;
        locX = locY = 0;
    }
}
