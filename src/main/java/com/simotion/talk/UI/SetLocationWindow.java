package com.simotion.talk.UI;

import com.simotion.talk.Peer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

class SetLocationWindow {
    private Stage primaryStage;
    SetLocationWindow() {
        primaryStage = new Stage();
    }
    SetLocationWindowController showWindow(Peer peer) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SetLocationWindow.fxml"));
            Parent root = fxmlLoader.load();

            SetLocationWindowController controller = fxmlLoader.getController();
            controller.setPeer(peer);
            Scene scene = new Scene(root);

            primaryStage.setTitle(peer.name+"에게 위치 보내기");
            primaryStage.setScene(scene);
            primaryStage.show();

            return controller;
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
