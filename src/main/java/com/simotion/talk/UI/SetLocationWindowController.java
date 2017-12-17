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

// public class SetLocationWindowController
// SetLocationWindow의 Controller
public class SetLocationWindowController {
    @FXML private ChoiceBox locationChoice;
    @FXML private ImageView mapImageView;
    @FXML private Circle pinCircle;
    @FXML private Button submitBtn;
    @FXML private Button cancelBtn;

    private int currentFloor = 0;
    private double locX, locY, ratioX, ratioY;

    private Peer myPeer;
    public void setPeer(Peer peer) {
        this.myPeer = peer;
    }
    @FXML
    public void initialize() {
        // 층 선택 ChoiceBox의 아이템 채우기
        for(int i=1;i<=5;i++) {
            locationChoice.getItems().add("본관/기숙사 "+i+"층");
        }
        // ChoiceBox의 선택사항이 바뀌었을 때 핸들러 설정
        locationChoice.getSelectionModel().selectedIndexProperty().addListener(e -> changeFloor(locationChoice.getSelectionModel().getSelectedIndex()+1));
        // 맨 처음에는 1층으로
        locationChoice.getSelectionModel().select(0);
        // 처음에는 핀이 보이지 않게
        pinCircle.setVisible(false);
        // 처음에는 전송 버튼을 비활성화
        submitBtn.setDisable(true);
        // 지도를 클릭했을 때의 작업 설정
        mapImageView.setOnMouseClicked(e ->
        {
            // 마우스 클릭 위치 계산 (전체 캔버스 위치 - 이미지의 위치)
            Bounds imageBounds = mapImageView.localToScene(mapImageView.getBoundsInLocal());
            double clickX = e.getSceneX() - imageBounds.getMinX();
            double clickY = e.getSceneY() - imageBounds.getMinY();
            locX = clickX;
            locY = clickY;
            // 전체 이미지에 대한 위치 비율 계산
            ratioX = locX / mapImageView.getFitWidth();
            ratioY = locY / mapImageView.getFitHeight();
            // 핀이 클릭한 위치에 보이도록 설정
            pinCircle.setVisible(true);
            pinCircle.setCenterX(clickX);
            pinCircle.setCenterY(clickY);
            // 전송 버튼을 활성화
            submitBtn.setDisable(false);
        });
        // 전송 버튼을 눌렀을 때 데이터 전송
        submitBtn.setOnMousePressed(e -> {
            // 내가 보낸 메세지를 추가
            PeerListManager.addMsg(myPeer,
                    "\\\\comehere "+currentFloor+" "+ratioX+" "+ratioY,
                    true
            );
            // 메세지를 실제로 전송
            MessagingClient.getInstance().sendLocation(myPeer, currentFloor, ratioX, ratioY);
            // 창 닫기
            ((Stage)submitBtn.getScene().getWindow()).close();
        });
        // 취소 버튼을 누르면 창 닫기
        cancelBtn.setOnMousePressed(e -> ((Stage)cancelBtn.getScene().getWindow()).close());
    }
    // private void changeFloor(int floor)
    // 층을 바꿔었을 떄의 작업 설정
    private void changeFloor(int floor) {
        // 지도 이미지 변경
        String location = getClass().getResource("/map/"+floor+"f.png").toExternalForm();
        mapImageView.setImage(new Image(location));

        // 층 저장
        currentFloor = floor;
        // 핀 사라지고, 전송버튼 비활성화
        pinCircle.setVisible(false);
        submitBtn.setDisable(true);
    }
}
