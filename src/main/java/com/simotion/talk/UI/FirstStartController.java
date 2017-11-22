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


public class FirstStartController {
    // https://stackoverflow.com/questions/8204680/java-regex-email
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @FXML private TextField nameInput;
    @FXML private TextField emailInput;

    Stage myStage;
    public void setStage(Stage stage) {
        myStage = stage;
    }

    // https://stackoverflow.com/questions/9722418/how-to-handle-listview-item-clicked-action
    @FXML public void firstStartNextClicked(MouseEvent e) throws Exception {
        // https://stackoverflow.com/questions/13003323/javafx-how-to-change-stage
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("FirstProfileSetup.fxml"));
        myStage.setScene(new Scene(root));

        myStage.show();
    }
    @FXML public void profileCompleteClicked(MouseEvent e) throws Exception {
        Stage myStage = (Stage)emailInput.getScene().getWindow();

        Preferences prefs = Preferences.userNodeForPackage(com.simotion.talk.Main.class);
        String name = nameInput.getText(), email = emailInput.getText();
        name = name.trim();
        email = email.trim();

        if(name.equals("") || email.equals("")) {
            //http://code.makery.ch/blog/javafx-dialogs-official/

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("입력 오류");
            alert.setHeaderText(null);
            alert.setContentText("모든 칸을 채워주세요.");

            alert.showAndWait();
            return;
        }
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);

        if(matcher.find() == false) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("입력 오류");
            alert.setHeaderText(null);
            alert.setContentText("올바른 이메일을 입력해 주세요.");

            alert.showAndWait();
            return;
        }

        prefs.put(Main.PROFILE_NAME, name);
        prefs.put(Main.PROFILE_EMAIL, email);
        prefs.put(Main.FIRST_START_TITLE, "1");

        myStage.close();
        new MainWindow().start();

    }
}
