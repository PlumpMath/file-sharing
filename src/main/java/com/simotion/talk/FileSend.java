package com.simotion.talk;

import com.simotion.talk.Networking.MessagingClient;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class FileSend {
    public static void chooseAndSend(Stage stage, Peer peer) {
        // 상대가 파일 전송을 허용하는지 확인
        boolean peerAllowsFile =  MessagingClient.getInstance().getStatusQuery(peer, "allowFileTransfer").equals("1");
        if(!peerAllowsFile) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("상대가 거부함");
            alert.setHeaderText("망했어요ㅜㅜ");
            alert.setContentText("상대가 파일 전송을 거부한 상태입니다.");
            alert.show();
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("파일 선택");
        List<File> selectedFile = fileChooser.showOpenMultipleDialog(stage);

        if(selectedFile != null) {
            new Thread(() -> {
                for(File file : selectedFile) {
                    MessagingClient.getInstance().sendFile(peer, file);
                    String fileMsg = "파일을 전송했습니다: "+file.getName();
                    PeerListManager.addMsg(peer, fileMsg, true);
                    MessagingClient.getInstance().sendMessage(peer, fileMsg);
                }
            }).start();
        }
    }
}
