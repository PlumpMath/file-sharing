package com.simotion.talk.UI;

import com.simotion.talk.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Date;

public class ChatWindowController {
    private ObservableList<ChatItem> chatItems = FXCollections.observableArrayList();
    @FXML private ListView chatListView;
    @FXML private TextArea chatMessage;
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
    }
    public void setPeer(Peer p) {
        this.myPeer = p;
    }

    public void close() {
        PeerListManager.removeWindowController(myPeer);
    }

    public void open() {
        ArrayList<ChatItem> prevItems = ChatSaveManager.getChatHistory(myPeer.UUID);
        System.out.println(chatItems.size());
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
