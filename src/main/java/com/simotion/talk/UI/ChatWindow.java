package com.simotion.talk.UI;

import com.simotion.talk.Peer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatWindow {
    private Stage primaryStage;
    public ChatWindow() {
        primaryStage = new Stage();
    }
    public ChatWindowController showWindow(Peer peer) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ChatWindow.fxml"));
            Parent root = fxmlLoader.load();

            ChatWindowController cont = fxmlLoader.getController();
            cont.setPeer(peer);

            Scene scene = new Scene(root);

            primaryStage.setTitle(peer.name);
            primaryStage.setScene(scene);
            primaryStage.setOnShown(e -> cont.open());
            primaryStage.setOnHidden(e -> cont.close());
            primaryStage.setMinHeight(550);
            primaryStage.setMaxHeight(550);
            primaryStage.setMinWidth(400);
            primaryStage.setMaxWidth(400);

            primaryStage.show();

            return cont;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
