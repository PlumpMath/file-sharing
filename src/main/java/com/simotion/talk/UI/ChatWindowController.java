package com.simotion.talk.UI;

import com.simotion.talk.*;
import com.simotion.talk.Networking.MessagingClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

// public class ChatWindowController
// 채팅창의 Controller
public class ChatWindowController {
    private ObservableList<ChatItem> chatItems = FXCollections.observableArrayList();
    @FXML public FlowPane modesFlowPane;
    @FXML private ListView chatListView;
    @FXML private TextArea chatMessage;
    @FXML private ImageView btn_clear;
    @FXML private ImageView btn_filebox;
    @FXML private ImageView btn_location;
    @FXML private ImageView btn_emote;
    private Peer myPeer;

    // public void appendChatText(String text, boolean isOutgoing)
    // 주어진 메세지를 창에 띄운다.
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
        // 엔터 키를 눌렀을 때, 메세지를 전송
        chatMessage.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                String text = chatMessage.getText().trim();
                if (text.equals("")) return;
                MessagingClient.getInstance().sendMessage(myPeer, text);
                chatMessage.clear();
                appendChatText(text, true);
            }
        });
        // chatListView의 아이템 리스트 설정
        chatListView.setItems(chatItems);

        // Custom ListCell 설정
        chatListView.setCellFactory(
            (Callback<ListView<ChatItem>, ListCell<ChatItem>>) list -> new ChatItemCell()
        );
        // 리스트 항목을 선택하지 못하도록 강제 (파란 배경 못생기게 방지)
        chatListView.setOnMouseClicked(event -> chatListView.getSelectionModel().select(-1));

        // 이미지의 투명 영역을 눌러도 클릭 되도록 설정
        btn_clear.setPickOnBounds(true);
        btn_filebox.setPickOnBounds(true);
        btn_location.setPickOnBounds(true);
        btn_emote.setPickOnBounds(true);
        // 채팅창 지우기 버튼
        btn_clear.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("ㄹㅇ?");
            alert.setHeaderText("ㄹㅇ?");
            alert.setContentText("정말 채팅 기록을 삭제하시겠습니까?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                ChatSaveManager.clear(myPeer.UUID);
                chatListView.getItems().clear();
                ((Stage)chatListView.getScene().getWindow()).close();
                PeerListManager.getChatWindowController(myPeer);
            }
        });
        // 파일 전송 버튼
        btn_filebox.setOnMouseClicked(e -> FileSend.chooseAndSend((Stage)chatListView.getScene().getWindow(), myPeer));
        // 위치 전송 버튼
        btn_location.setOnMouseClicked(e -> new SetLocationWindow().showWindow(myPeer));
        btn_emote.setOnMouseClicked(e -> {
            int selection = selectEmote();
            if(selection != -1) {
                String text = "\\\\emote "+selection;
                MessagingClient.getInstance().sendMessage(myPeer, text);
                chatMessage.clear();
                appendChatText(text, true);
            }
        });
    }
    public void setPeer(Peer p) {
        this.myPeer = p;
    }

    @FXML
    void close() {
        // PeerListManager 목록에서 현재 창을 제거
        PeerListManager.removeWindowController(myPeer);
    }

    @FXML
    void open() {
        // 채팅 기록을 가져옴
        ArrayList<ChatItem> prevItems = ChatSaveManager.getChatHistory(myPeer.UUID);
        chatItems.addAll(prevItems);
        chatListView.scrollTo(chatItems.size() - 1);
    }

    private int selectEmote() {
        // 이모티콘 선택 창 생성
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("이모티콘 선택");
        dialog.setHeaderText("이모티콘을 선택해주세요.");

        // 버튼 종류 생성 (
        ButtonType submitButtonType = new ButtonType("전송", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);
        Node submitButton = dialog.getDialogPane().lookupButton(submitButtonType);
        submitButton.setDisable(true);

        // 이모티콘 목록을 담을 GridPane 생성
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 20, 10));

        // 이모티콘 ListView 생성, CellFactory 설정
        ListView lv = new ListView();
        lv.setCellFactory(
            (Callback<ListView<Integer>, ListCell<Integer>>) list -> new EmoteListItem()
        );

        // 이모티콘 채우기
        ObservableList<Integer> items = FXCollections.observableArrayList();
        lv.setItems(items);
        for(int i=1;i<=Main.EMOTE_COUNT;i++) {
            items.add(i);
        }
        // ListView를 GridView 생성
        grid.add(lv, 0, 0);

        // Dialog에 Grid를 담음
        dialog.getDialogPane().setContent(grid);

        // ListView 항목을 선택하면 submitButton을 활성화
        lv.getSelectionModel().selectedIndexProperty().addListener(e -> {
           submitButton.setDisable(false);
        });
        // Dialog의 Result를 ListView의 selectedIndex+1이 되도록 설정
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                return lv.getSelectionModel().getSelectedIndex()+1;
            }
            return null;
        });

        // Dialog를 띄우고, 사용자가 선택했으면 그 Index, 선택하지 않았으면 -1를 반환한다.
        Optional<Integer> result = dialog.showAndWait();
        return result.isPresent()?result.get():-1;
    }

    // 이모티콘 선택 Dialog의 Custom ListCell
    class EmoteListItem extends ListCell<Integer>  {
        @Override
        public void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);
            if(item != null) {
                TextFlow flow = new TextFlow();
                ImageView img = new ImageView();
                img.setImage(new Image(getClass().getResource("/emote/"+item+".png").toExternalForm()));
                img.setFitHeight(90);
                img.setFitWidth(90);
                Text t1 = new Text("  이모티콘 "+item);
                flow.getChildren().addAll(img, t1);
                setGraphic(flow);
            }
        }
    }

    // 채팅창 메세지 하나에 대한 Custom ListCell
    class ChatItemCell extends ListCell<ChatItem> {
        @Override
        public void updateItem(ChatItem item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                TextFlow flow = new TextFlow();

                // 내가 보낸 것인지, 상대가 보낸 것인지 확인하여 이름 Label을 붙임
                Text text1=new Text(item.sent?"나: ":(myPeer.name+": "));
                if(item.sent) text1.setStyle("-fx-font-weight: bold; -fx-fill: crimson;");
                else text1.setStyle("-fx-font-weight: bold; -fx-fill: cornflowerblue;");

                // 위치 메시지인지 확인
                if(checkMapData(item.message)) {
                    // 위치 메세지이면 ViewLocationWindow를 띄우는 버튼 생성
                    Button locBtn = new Button();
                    locBtn.setText("위치 보기");
                    // 버튼 MouseClicked 핸들러 연결
                    locBtn.setOnMouseClicked(e ->
                    {
                        // 메세지 파싱, 정보 전달하여 창 띄우기
                        String[] msgs = item.message.split(" ");
                        int map = Integer.valueOf(msgs[1]);
                        double px = Double.valueOf(msgs[2]);
                        double py = Double.valueOf(msgs[3]);
                        new ViewLocationWindow().showWindow(map, px, py);
                    });
                    // TextFlow에 요소 추가
                    flow.getChildren().addAll(text1, locBtn);
                } else if(checkEmote(item.message)) {
                    String imageID = Integer.valueOf(item.message.split(" ")[1]).toString();
                    ImageView img = new ImageView();
                    img.setImage(new Image(getClass().getResource("/emote/"+imageID+".png").toExternalForm()));
                    img.setFitHeight(90);
                    img.setFitWidth(90);
                    flow.getChildren().addAll(text1, img);
                } else {
                    // 메세지 본문 Text 추가
                    Text text2=new Text(item.message);
                    text2.setStyle("-fx-font-weight: normal;");

                    // TextFlow에 요소 추가
                    flow.getChildren().addAll(text1, text2);
                }
                // Text Warp 설정하여 글이 밖으로 나가는 현상 방지
                flow.setStyle("-fx-wrap-text: true");
                // 너비를 명시적으로 설정
                flow.setPrefWidth(360.0);
                // TextFlow를 리스트에 추가
                setGraphic(flow);
            }
        }
        // private boolean checkMapData(String msg)
        // 주어진 메세지가 위치 정보 메세지인지 확인
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
        private boolean checkEmote(String msg) {
            try {
                String[] msgs = msg.split(" ");
                if(msgs.length != 2) return false;
                if(!msgs[0].equals("\\\\emote")) return false;
                int val = Integer.valueOf(msgs[1]);
                return (val>=1 && val<= Main.EMOTE_COUNT);
            } catch(Exception ignore) {
                return false;
            }
        }
    }
}

// 참고 출처
// http://code.makery.ch/blog/javafx-dialogs-official/