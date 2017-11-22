package com.simotion.talk;

public class Peer {
    public String ipAddress;
    public long lastSeen = 0;

    public String email;
    public String name;
    public String UUID;

    public Peer() {
        lastSeen = System.nanoTime();
    }
    public Peer(String ip, String name, String email, String UUID) {
        this.ipAddress = ip;
        this.name = name;
        this.email = email;
        this.UUID = UUID;
        this.lastSeen = System.nanoTime();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        Peer diffPeer = (Peer)obj;
        return ipAddress.equals(diffPeer.ipAddress);
    }
}
