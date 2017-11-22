package com.simotion.talk;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Iterator;

public class PeerListManager {
    public static ObservableList<Peer> peers = FXCollections.observableArrayList(new ArrayList<Peer>());
    public static boolean addPeer(Peer peer) {
        Iterator it = peers.iterator();
        while(it.hasNext()) {
            Peer now = (Peer)it.next();
            if(now.equals(peer)) {
                now.lastSeen = System.nanoTime();
                return false;
            }
        }
        System.out.println("ADD!!");
        peers.add(peer);
        return true;
    }
    public static void cleanOld() {
        long now = System.nanoTime();
        for(int i=peers.size()-1;i>=0;i--) {
            if(peers.get(i).lastSeen - now > 60*1000000000) {
                peers.remove(i);
            }
        }
    }
}
