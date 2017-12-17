package com.simotion.talk.UI;

import com.simotion.talk.*;
import com.simotion.talk.Networking.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class ChatWindowController {
    public FlowPane modesFlowPane;
    private ObservableList<ChatItem> chatItems = FXCollections.observableArrayList();
    @FXML private ListView chatListView;
    @FXML private TextArea chatMessage;
    @FXML private ImageView btn_clear;
    @FXML private ImageView btn_filebox;
    @FXML private ImageView btn_location;
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
                MessagingClient.getInstance().sendMessage(myPeer, text);
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
        btn_filebox.setPickOnBounds(true);
        btn_location.setPickOnBounds(true);
        btn_clear.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("ㄹㅇ?");
            alert.setContentText("정말 채팅 기록을 삭제하시겠습니까?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                ChatSaveManager.clear(myPeer.UUID);
                chatListView.getItems().clear();
                ((Stage)chatListView.getScene().getWindow()).close();
                PeerListManager.getChatWindowController(myPeer);
            }
        });
        btn_filebox.setOnMouseClicked(e -> FileSend.chooseAndSend((Stage)chatListView.getScene().getWindow(), myPeer));
        btn_location.setOnMouseClicked(e -> new SetLocationWindow().showWindow(myPeer));
    }
    public void setPeer(Peer p) {
        this.myPeer = p;
    }

    void close() {
        PeerListManager.removeWindowController(myPeer);
    }

    void open() {
        ArrayList<ChatItem> prevItems = ChatSaveManager.getChatHistory(myPeer.UUID);
        chatItems.addAll(prevItems);
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

                if(checkMapData(item.message)) {
                    Button locBtn=new Button();
                    locBtn.setText("위치 보기");
                    locBtn.setOnMouseClicked(e ->
                    {
                        String[] msgs = item.message.split(" ");
                        int map = Integer.valueOf(msgs[1]);
                        double px = Double.valueOf(msgs[2]);
                        double py = Double.valueOf(msgs[3]);
                        new ViewLocationWindow().showWindow(map, px, py);
                    });
                    flow.getChildren().addAll(text1, locBtn);
                } else {
                    Text text2=new Text(item.message);
                    text2.setStyle("-fx-font-weight: normal;");

                    flow.getChildren().addAll(text1, text2);
                }
                flow.setStyle("-fx-wrap-text: true");
                flow.setPrefWidth(360.0);
                setGraphic(flow);
            }
        }
        private boolean checkMapData(String msg) {
            try {
                String[] msgs = msg.split(" ");
                if (msgs.length != 4) return false;
                if (!msgs[0].equals("\\\\comehere")) return false;
                int map = Integer.valueOf(msgs[1]);
                double px = Double.valueOf(msgs[2]);
                double py = Double.valueOf(msgs[3]);
                return (map>=1 && map<=5 && px>=0 && px<=1 && py>=0 && py<=1);
            } catch(Exception e) {
                return false;
            }
        }
    }
}
