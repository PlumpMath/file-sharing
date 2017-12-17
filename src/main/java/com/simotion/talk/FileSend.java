package com.simotion.talk;

import com.simotion.talk.Networking.MessagingClient;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

// public class FileSend
// 파일 전송을 하는 클래스
public class FileSend {
    public static void chooseAndSend(Stage stage, Peer peer) {
        // 상대가 파일 전송을 허용하는지 확인
        boolean peerAllowsFile =  MessagingClient.getInstance().getStatusQuery(peer, "allowFileTransfer").equals("1");
        if(!peerAllowsFile) {
            // 허옹하지 않는다면 안내창을 띄우고 끝낸다.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("상대가 거부함");
            alert.setHeaderText("망했어요ㅜㅜ");
            alert.setContentText("상대가 파일 전송을 거부한 상태입니다.");
            alert.show();
            return;
        }
        // 파일을 선택한다. 여러개도 된다!
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("파일 선택");
        List<File> selectedFile = fileChooser.showOpenMultipleDialog(stage);

        // 별도의 스레드를 생성하여, 각각의 파일을 대해 전송한다.
        if(selectedFile != null) {
            new Thread(() -> {
                for(File file : selectedFile) {
                    // 보낸다~
                    MessagingClient.getInstance().sendFile(peer, file);
                    // 보냈다는 메세지 생성
                    String fileMsg = "파일을 전송했습니다: "+file.getName();
                    // 내가 보낸걸로 메세지를 추가
                    PeerListManager.addMsg(peer, fileMsg, true);
                    // 상대에게 전송 알림 메세지 보내기
                    MessagingClient.getInstance().sendMessage(peer, fileMsg);
                }
            }).start();
        }
    }
}
