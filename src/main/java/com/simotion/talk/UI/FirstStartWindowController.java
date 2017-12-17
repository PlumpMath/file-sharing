package com.simotion.talk.UI;

import com.simotion.talk.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// public class FirstStartWindowController
// FirstStartWindow의 Controller
public class FirstStartWindowController {
    // 이메일 확인용 Regex
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );

    @FXML private TextField nameInput;
    @FXML private TextField emailInput;
    Stage myStage;

    public void setStage(Stage stage) {
        myStage = stage;
    }

    // 처음 화면에서 다음 화면으로 넘어가는 버튼 클릭
    @FXML
    public void firstStartNextClicked(MouseEvent e) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("FirstProfileSetup.fxml"));
        myStage.setScene(new Scene(root));

        myStage.show();
    }
    // 프로필 설정 완료 버튼 클릭
    @FXML public void profileCompleteClicked(MouseEvent e) throws Exception {
        Stage myStage = (Stage)emailInput.getScene().getWindow();

        // 설정 객체 가져오기
        Preferences prefs = Preferences.userNodeForPackage(com.simotion.talk.Main.class);

        // 이메일, 이름 가져오기
        String name = nameInput.getText(), email = emailInput.getText();
        name = name.trim();
        email = email.trim();

        // 둘 중 하나가 비어있으면 오류 반환 및 return
        if(name.equals("") || email.equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("입력 오류");
            alert.setHeaderText(null);
            alert.setContentText("모든 칸을 채워주세요.");

            alert.showAndWait();
            return;
        }

        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);

        // 이메일이 올비르지 않으면 오류 반환 및 return
        if(matcher.find() == false) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("입력 오류");
            alert.setHeaderText(null);
            alert.setContentText("올바른 이메일을 입력해 주세요.");

            alert.showAndWait();
            return;
        }

        // 설정 저장 및 프로그램 시작
        prefs.put(Main.PROFILE_NAME, name);
        prefs.put(Main.PROFILE_EMAIL, email);
        prefs.put(Main.FIRST_START_TITLE, "1");

        myStage.close();
        new MainWindow().start();

    }
}

// 참고 출처
// http://code.makery.ch/blog/javafx-dialogs-official/
// https://stackoverflow.com/questions/8204680/java-regex-email
// https://stackoverflow.com/questions/9722418/how-to-handle-listview-item-clicked-action
// https://stackoverflow.com/questions/13003323/javafx-how-to-change-stage