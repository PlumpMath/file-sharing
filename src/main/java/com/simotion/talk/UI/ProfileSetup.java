package com.simotion.talk.UI;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProfileSetup extends Stage {
    public ProfileSetup() throws Exception {
        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("ProfileSetup.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setTitle("AppTitle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
