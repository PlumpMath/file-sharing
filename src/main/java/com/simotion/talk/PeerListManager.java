package com.simotion.talk;

import com.simotion.talk.UI.ChatWindow;
import com.simotion.talk.UI.ChatWindowController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class PeerListManager {
    public static ObservableList<Peer> peers = FXCollections.observableArrayList(new ArrayList<Peer>());
    private static HashMap<Peer, ChatWindowController> chatControllerDictionary = new HashMap<>();

    public static ChatWindowController getChatWindowController(Peer peer) {
        if(chatControllerDictionary.containsKey(peer)) {
            return chatControllerDictionary.get(peer);
        } else {
            ChatWindow cw = new ChatWindow();
            ChatWindowController controller = cw.showWindow(peer);
            chatControllerDictionary.put(peer, controller);
            return controller;
        }
    }

    public static void receiveChatMsg(Peer peer, String message) {
        Platform.runLater(() -> {
            getChatWindowController(peer).appendChatText(message, false);
        });
    }

    public static void removeWindowController(Peer peer) {
        if(chatControllerDictionary.containsKey(peer)) {
            chatControllerDictionary.remove(peer);
        }
    }

    public static boolean addPeer(Peer peer) {
        Iterator it = peers.iterator();
        while(it.hasNext()) {
            Peer now = (Peer)it.next();
            if(now.equals(peer)) {
                now.lastSeen = System.nanoTime();
                return false;
            }
        }
        peers.add(peer);
        return true;
    }
}
