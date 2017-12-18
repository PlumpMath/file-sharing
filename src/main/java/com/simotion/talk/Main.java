package com.simotion.talk;

import com.simotion.talk.UI.*;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.UUID;
import java.util.prefs.Preferences;

/*

도움을 받은 사람))
조하연 - 프로그램 로고 제작

도와을 준 사람))
박상훈 - 타워 디펜스 게임에서 적의 자료구조 관련 조언
오은수 - JavaFX Thread 관련 오류 해결 (Platform.runLater)
김태현 - Swing에서 Thread를 이용한 화면 전환 (Setter를 활용한 스레드 전환)
조하연 - 게임의 배경화면 (게임 판 등) 그리기, 플레이어 말 자료구조 및 그리기
권순현 - 게임 플레이 시 반응 속도가 계속 느려지는 문제 해결 (지속적인 ImageView 추가로 인한 메모리 누수)
이현민 - 네트워킹 관련 도움 (소켓 프로그래밍 등)

*/

public class Main extends Application {
    public static final String FIRST_START_TITLE = "FIRST_START";
    public static final String PROFILE_NAME      = "PROFILE_NAME";
    public static final String PROFILE_EMAIL     = "PROFILE_EMAIL";
    public static final String APP_NAME          = "File Transfer";
    public static final String UUID_KEY          = "MachineUUID";
    public static final String ALLOW_FILES       = "ALLOW_FILES";

    public static final int EMOTE_COUNT          = 30;

    // 모든것을 시작한다
    @Override
    public void start(Stage primaryStage) throws Exception {
        // 폰트를 로드
        Font.loadFont(
            getClass().getResource("/NanumGothic.ttf").toExternalForm(),
            10
        );

        Preferences prefs = Preferences.userNodeForPackage(com.simotion.talk.Main.class);

        // 장치 UUID가 없으면 생성한다.
        if(prefs.get(UUID_KEY, "-1").equals("-1")) {
            UUID idOne = UUID.randomUUID();
            prefs.put(UUID_KEY, idOne.toString());
        }

        if(prefs.get(FIRST_START_TITLE, "0").equals("0")) {
            // 프로그램을 처음 시작하는 것이면 프로필 설정 창을 띄운다.
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
