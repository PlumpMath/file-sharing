package com.simotion.talk.UI;

import com.simotion.talk.Peer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

// public class PeerInformationWindowController
// 피어 정보 창에 대한 Controller
public class PeerInformationWindowController {
    @FXML private Label bodyText;

    private Peer myPeer;
    public void setPeer(Peer peer) {
        this.myPeer = peer;
        if(bodyText == null) return;
        // 피어 정보를 읽어와 정보를 Label에 띄움
        bodyText.setText(
                String.format(
                        "이름: %s\n이메일: %s\nIP: %s\nUUID: %s",
                        myPeer.name,
                        myPeer.email,
                        myPeer.ipAddress,
                        myPeer.UUID
                )
        );
    }
}
