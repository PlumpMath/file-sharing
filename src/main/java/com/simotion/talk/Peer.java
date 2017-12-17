package com.simotion.talk;

// public class Peer
// 피어 객체이다.
public class Peer {
    public String ipAddress;   // ipAddress
    public long lastSeen = 0;  // 마지막 연결 시간

    public String email;       // 이메일
    public String name;        // 이름
    public String UUID;        // UUID

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
