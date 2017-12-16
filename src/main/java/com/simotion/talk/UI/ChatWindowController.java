package com.simotion.talk.UI;

import com.simotion.talk.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class ChatWindowController {
    private ObservableList<ChatItem> chatItems = FXCollections.observableArrayList();
    @FXML private ListView chatListView;
    @FXML private TextArea chatMessage;
    @FXML private ImageView btn_clear;
    private Peer myPeer;
    public void appendChatText(String text, boolean isOutgoing) {
        Platform.runLater(() -> {
            if(chatListView == null) {
                System.err.println("WARNING: appendChatText() called when TextArea is uninitialized.");
                return;
            }
            ChatItem thisItem = new ChatItem(new Date(), text, isOutgoing);
            chatItems.add(thisItem);
            chatListView.scrollTo(chatItems.size()-1);
            ChatSaveManager.append(myPeer.UUID, thisItem);
        });
    }
    @FXML
    protected void initialize() {
        chatMessage.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                String text = chatMessage.getText().trim();
                if (text.equals("")) return;
                MessagingClient.sendMessage(myPeer, text);
                chatMessage.clear();
                appendChatText(text, true);
            }
        });
        chatListView.setItems(chatItems);

        chatListView.setCellFactory(
            (Callback<ListView<ChatItem>, ListCell<ChatItem>>) list -> new ChatItemCell()
        );
        chatListView.setOnMouseClicked(event -> {
            chatListView.getSelectionModel().select(-1);
        });
        btn_clear.setPickOnBounds(true);
        btn_clear.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("ㄹㅇ?");
            alert.setContentText("정말 채팅 기록을 삭제하시겠습니까?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                ChatSaveManager.clear(myPeer.UUID);
                chatItems.clear();
            }
        });
    }
    public void setPeer(Peer p) {
        this.myPeer = p;
    }

    public void close() {
        PeerListManager.removeWindowController(myPeer);
    }

    public void open() {
        ArrayList<ChatItem> prevItems = ChatSaveManager.getChatHistory(myPeer.UUID);
        for(ChatItem item : prevItems) {
            chatItems.add(item);
        }
        chatListView.scrollTo(chatItems.size() - 1);
    }

    class ChatItemCell extends ListCell<ChatItem> {
        @Override
        public void updateItem(ChatItem item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                TextFlow flow = new TextFlow();

                Text text1=new Text(item.sent?"나: ":(myPeer.name+": "));
                if(item.sent) text1.setStyle("-fx-font-weight: bold; -fx-fill: crimson;");
                else text1.setStyle("-fx-font-weight: bold; -fx-fill: cornflowerblue;");

                Text text2=new Text(item.message);
                text2.setStyle("-fx-font-weight: normal;");

                flow.getChildren().addAll(text1, text2);
                flow.setStyle("-fx-wrap-text: true");
                flow.setPrefWidth(360.0);
                setGraphic(flow);
            }
        }
    }
}
