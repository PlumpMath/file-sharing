package com.simotion.talk.UI;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class FirstStart extends Stage {
    Stage myStage;
    public FirstStart() {
        myStage = new Stage();
    }
    public void CreateWindow() throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL location = getClass().getClassLoader().getResource("FirstStart.fxml");
        fxmlLoader.setLocation(location);
        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = fxmlLoader.load(location.openStream());
        Scene scene = new Scene(root);

        myStage.setTitle("처음 시작하기");
        myStage.setScene(scene);

        ((FirstStartController)fxmlLoader.getController()).setStage(myStage);
        myStage.show();
    }
}

// 참고 출처
// https://stackoverflow.com/questions/13003323/javafx-how-to-change-stage