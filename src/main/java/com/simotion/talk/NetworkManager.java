package com.simotion.talk;

import com.simotion.talk.Networking.*;

// public class NetworkManager
// 네트워크 클래스들을 실행하기 위해 이용
public class NetworkManager {
    // 네트워킹 서버/클라이언트들을 시작한다.
    // 각각 스레드를 만들어 실행
    public static void enableServers() {
        // mDNS 답장 클래스
        Thread t = new Thread(new MDNSResponse());
        t.start();
        // mDNS 검색 스레드
        Thread t2 = new Thread(new MDNSLookup());
        t2.start();
        // HandshakeServer
        Thread t3 = new Thread(new HandshakeServer());
        t3.start();
        // MessagingServer
        Thread t4 = new Thread(new MessagingServer());
        t4.start();
    }
}
