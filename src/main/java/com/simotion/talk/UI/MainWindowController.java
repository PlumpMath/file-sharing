package com.simotion.talk.UI;

import com.simotion.talk.Peer;
import com.simotion.talk.PeerListManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;
import com.simotion.talk.Main;
import javafx.util.Callback;

// public class MainWindowController
// MainWindow의 Controller
public class MainWindowController {
    @FXML private Ellipse profileImg;
    @FXML private Label profileName;
    @FXML private Label profileEmail;
    @FXML private Label onlineCountText;
    @FXML private ListView peerListView;
    @FXML private FlowPane mainPane;
    @FXML private ImageView btn_filebox;
    @FXML private ImageView btn_filemode;
    private Preferences prefs;

    @FXML
    protected void initialize() {
        // 프로필 이미지 설정
        String url = getClass().getResource("/img/default_profile.png").toExternalForm();
        profileImg.setFill(new ImagePattern(new Image(url, false)));

        // 사용자의 이름, 이메일을 가져온 후 Label에 표시
        prefs = Preferences.userNodeForPackage(com.simotion.talk.Main.class);
        profileName.setText(prefs.get(Main.PROFILE_NAME, "DefaultName"));
        profileEmail.setText(prefs.get(Main.PROFILE_EMAIL, "DefaultEmail"));

        // 피어 목록 ListView의 크기를 화면에 맞도록 설정
        peerListView.prefWidthProperty().bind(mainPane.widthProperty());
        peerListView.prefHeightProperty().bind(mainPane.heightProperty());

        // 피어 목록 ListView와 피어 목록 ObservableList를 연결
        peerListView.setItems(PeerListManager.peers);

        // 피어 목록의 Custom ListCell 설정
        peerListView.setCellFactory((Callback<ListView<Peer>, ListCell<Peer>>) listView -> new PeerListViewCell());

        // 리스트 항목을 선택하지 못하도록 강제 (파란 배경 못생기게 방지)
        peerListView.setOnMouseClicked(event -> peerListView.getSelectionModel().select(-1));

        // 온라인 사용자 수 업데이트 타이머 설정
        Timer timer = new Timer();
        TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> onlineCountText.setText("온라인 사용자: "+PeerListManager.peers.size()));
            }
        };
        timer.scheduleAtFixedRate(updateTask, 0, 1000);

        // 이미지의 투명 영역을 눌러도 클릭 되도록 설정
        btn_filebox.setPickOnBounds(true);
        btn_filemode.setPickOnBounds(true);

        // 파일 다운로드 폴더 보기 버튼
        btn_filebox.setOnMouseClicked(e -> {
            Desktop desktop = Desktop.getDesktop();
            try {
                Files.createDirectories(Paths.get("incoming/a").getParent());
                desktop.open(new File("incoming"));
            }
            catch(IOException ioe) {
                ioe.printStackTrace();
            }
        });
        // 설정을 불러와서 파일 수신모드 버튼을 변경
        updateFileModeBtn(prefs.getBoolean(Main.ALLOW_FILES, false));
        btn_filemode.setOnMouseClicked(e -> {
            boolean value = prefs.getBoolean(Main.ALLOW_FILES, false);
            prefs.putBoolean(Main.ALLOW_FILES, !value);
            setFileMode(!value);
        });
    }
    // 파일 수신모드 변경
    private void setFileMode(boolean value) {
        prefs.putBoolean(Main.ALLOW_FILES, value);
        updateFileModeBtn(value);
    }
    // 파일 수신모드 버튼 형태 업데이트
    private void updateFileModeBtn(boolean value) {
        String imgURL;
        if(value) imgURL = getClass().getResource("/img/ico_file_allow.png").toExternalForm();
        else imgURL = getClass().getResource("/img/ico_file_block.png").toExternalForm();
        btn_filemode.setImage(new Image(imgURL));
    }
}

// 참고 출처
// https://stackoverflow.com/questions/29847703/nullpointer-exception-during-initialize-fxml-injection-not-working-as-expected
// https://stackoverflow.com/questions/42116313/how-to-set-an-image-in-a-circle
// https://stackoverflow.com/questions/6174299/javafx-2-0-set-component-to-full-width-and-height-of-immediate-parent