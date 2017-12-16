package com.simotion.talk.UI;

import com.simotion.talk.Main;
import com.simotion.talk.NetworkManager;
import com.simotion.talk.Peer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PeerInformationWindow extends Stage {
    private Stage primaryStage;
    public PeerInformationWindow() {
        primaryStage = new Stage();
    }
    public void showWindow(Peer peer) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/PeerInformationWindow.fxml"));
            Parent root = fxmlLoader.load();

            ((PeerInformationWindowController)fxmlLoader.getController()).setPeer(peer);
            Scene scene = new Scene(root);

            primaryStage.setTitle(Main.APP_NAME);
            primaryStage.setScene(scene);
            primaryStage.setMinHeight(160);
            primaryStage.setMaxHeight(160);
            primaryStage.setMinWidth(450);
            primaryStage.setMaxWidth(450);

            primaryStage.show();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
