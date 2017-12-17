package com.simotion.talk.UI;

import com.simotion.talk.FileSend;
import com.simotion.talk.Peer;
import com.simotion.talk.PeerListManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class PeerListCellItemController {
    @FXML private HBox hBox;
    @FXML private Label label1;
    @FXML private ImageView btn_info;
    @FXML private ImageView btn_chat;
    @FXML private ImageView btn_filebox;
    @FXML private ImageView btn_location;

    private Peer myPeer;

    PeerListCellItemController()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("PeerListCellItem.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void initialize() {
        try
        {
            btn_info.setPickOnBounds(true);
            btn_chat.setPickOnBounds(true);
            btn_filebox.setPickOnBounds(true);
            btn_location.setPickOnBounds(true);
            btn_info.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> new PeerInformationWindow().showWindow(myPeer));
            btn_chat.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> PeerListManager.getChatWindowController(myPeer));
            btn_filebox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> FileSend.chooseAndSend((Stage)hBox.getScene().getWindow(), myPeer));
            btn_location.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> new SetLocationWindow().showWindow(myPeer));
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
    void setInfo(Peer peer)
    {
        this.myPeer = peer;
        label1.setText(String.format("%s (%s)",peer.name,peer.email));
    }

    HBox getBox()
    {
        return hBox;
    }
}
