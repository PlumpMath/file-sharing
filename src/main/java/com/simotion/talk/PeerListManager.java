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

    public static void receiveChatMsg(Peer peer, String message) {
        Platform.runLater(() -> getChatWindowController(peer).appendChatText(message, false));
    }

    public static void removeWindowController(Peer peer) {
        if(chatControllerDictionary.containsKey(peer)) {
            chatControllerDictionary.remove(peer);
        }
    }

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
