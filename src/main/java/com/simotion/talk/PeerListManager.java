package com.simotion.talk;

import com.simotion.talk.UI.ChatWindow;
import com.simotion.talk.UI.ChatWindowController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

// public class PeerListManager
// 피어 목록과, 각 피어에 대한 채팅창을 관리한다.
// 채팅창이 하나만 뜨게 하는 역할을 한다.
public class PeerListManager {
    public static ObservableList<Peer> peers = FXCollections.observableArrayList(new ArrayList<Peer>());
    private static HashMap<Peer, ChatWindowController> chatControllerDictionary = new HashMap<>();

    // public static ChatWindowController getChatWindowController(Peer peer)
    // 피어에 대한 채팅창 Controller를 반환한다.
    // Controller가 없으면 (=창이 띄워져 있지 않으면) 직접 띄운다.
    // Thread-Safe하지 않으므로 주의 필요!
    public static ChatWindowController getChatWindowController(Peer peer) {
        if(chatControllerDictionary.containsKey(peer)) {
            // 창이 있으면 그냥 그 컨트롤러를 반환한다.
            return chatControllerDictionary.get(peer);
        } else {
            // 없으면 창을 만들고, 그 창에 대한 컨트롤러를 반환
            ChatWindow cw = new ChatWindow();
            ChatWindowController controller = cw.showWindow(peer);
            chatControllerDictionary.put(peer, controller);
            return controller;
        }
    }

    // public static void addMsg(Peer peer, String message, boolean isOutgoing)
    // 창에 메세지를 띄운다. 이건 Thread-Safe하니 괜찮다.
    public static void addMsg(Peer peer, String message, boolean isOutgoing) {
        Platform.runLater(() -> {
            if(chatControllerDictionary.containsKey(peer)) {
                chatControllerDictionary.get(peer).appendChatText(message, isOutgoing);
            } else {
                ChatWindow cw = new ChatWindow();
                ChatWindowController controller = cw.showWindow(peer);
                chatControllerDictionary.put(peer, controller);
                controller.appendChatText(message, isOutgoing);
            }
        });
    }
    public static void addMsg(Peer peer, String message) {
        addMsg(peer, message, false);
    }

    // public static void removeWindowController(Peer peer)
    // 창을 없앨 때 사용한다. 피어에 대핸 controller를 제거
    public static void removeWindowController(Peer peer) {
        if(chatControllerDictionary.containsKey(peer)) {
            chatControllerDictionary.remove(peer);
        }
    }

    // public static void addPeer(Peer peer)
    // 피어를 목록에 추가한다.
    public static void addPeer(Peer peer) {
        for (Object now : peers) {
            if (now.equals(peer)) {
                ((Peer)now).lastSeen = System.nanoTime();
                return;
            }
        }
        peers.add(peer);
    }
}
