package com.simotion.talk.UI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

class ViewLocationWindow {
    private Stage primaryStage;
    ViewLocationWindow() {
        primaryStage = new Stage();
    }
    void showWindow(int map, double x, double y) {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ViewLocationWindow.fxml"));
            Parent root = fxmlLoader.load();

            ViewLocationWindowController controller = fxmlLoader.getController();
            controller.setLocation(map, x, y);
            Scene scene = new Scene(root);

            primaryStage.setTitle("위치 보기");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
