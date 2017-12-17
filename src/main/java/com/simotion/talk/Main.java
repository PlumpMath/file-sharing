package com.simotion.talk;

import com.simotion.talk.UI.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.UUID;
import java.util.prefs.Preferences;


public class Main extends Application {
    public static final String FIRST_START_TITLE = "FIRST_START";
    public static final String PROFILE_NAME      = "PROFILE_NAME";
    public static final String PROFILE_EMAIL     = "PROFILE_EMAIL";
    public static final String APP_NAME          = "File Transfer";
    public static final String UUID_KEY          = "MachineUUID";
    public static final String ALLOW_FILES       = "ALLOW_FILES";

    // 모든것을 시작한다
    @Override
    public void start(Stage primaryStage) throws Exception {
        Preferences prefs = Preferences.userNodeForPackage(com.simotion.talk.Main.class);

        // 장치 UUID가 없으면 생성한다.
        if(prefs.get(UUID_KEY, "-1").equals("-1")) {
            UUID idOne = UUID.randomUUID();
            prefs.put(UUID_KEY, idOne.toString());
        }

        // 프로그램을 처음 시작하는 것이면 프로필 설정 창을 띄운다.
        if(prefs.get(FIRST_START_TITLE, "0").equals("0")) {
            new FirstStartWindow().CreateWindow();
        } else {
            // 그게 아니라면 메인 화면을 띄운다.
            new MainWindow().start();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}

// 참고 출처
// https://stackoverflow.com/questions/4017137/how-do-i-save-preference-user-settings-in-java
// https://stackoverflow.com/questions/2982748/create-a-guid-in-java
