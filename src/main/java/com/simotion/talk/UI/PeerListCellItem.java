package com.simotion.talk.UI;

import com.simotion.talk.Peer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class PeerListCellItem {
    @FXML
    private HBox hBox;
    @FXML
    private Label label1;
    @FXML
    private Label label2;

    public PeerListCellItem()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("PeerListCellItem.fxml"));
        fxmlLoader.setController(this);
        try
        {
            fxmlLoader.load();
        }
        catch (Exception e)
        {
            System.err.println("Shit");
            System.err.println(e.getMessage());
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
    }

    public void setInfo(Peer peer)
    {
        label1.setText(String.format("%s - %s (%s)",peer.name,peer.email,peer.ipAddress));
    }

    public HBox getBox()
    {
        return hBox;
    }
}
