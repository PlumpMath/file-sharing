package com.simotion.talk.UI;

import com.simotion.talk.Main;
import com.simotion.talk.NetworkManager;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// public class MainWindow
// 메인 화면의 창
public class MainWindow extends Stage {
    private Stage primaryStage;
    public void start() throws Exception {
        primaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/MainWindow.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setTitle(Main.APP_NAME);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(100);
        primaryStage.setMinWidth(480);
        primaryStage.setMaxWidth(480);
        primaryStage.show();

        NetworkManager.enableServers();
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
