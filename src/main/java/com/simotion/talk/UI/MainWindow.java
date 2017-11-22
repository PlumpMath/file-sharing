package com.simotion.talk.UI;

import com.simotion.talk.Main;
import com.simotion.talk.NetworkManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class MainWindow extends Stage {
    @FXML private FlowPane profileBar;
    public void start() throws Exception {
        Stage primaryStage = new Stage();
        System.out.println(getClass().getResource("/MainWindow.fxml"));
        Parent root = FXMLLoader.load(getClass().getResource("/MainWindow.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setTitle(Main.APP_NAME);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(100);
        primaryStage.setMinWidth(480);
        primaryStage.setMaxWidth(480);
        primaryStage.show();

        NetworkManager.enableMulticast();
    }
}
