package com.simotion.talk.Networking;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.net.InetAddress;

// public class MulticastEmitter
// 네트워크 상의 mDNS 쿼리에 답변한다.

public class MulticastEmitter implements Runnable {
    private JmDNS jmdns;
    private static final String TYPE_STRING = "_transfer._http._tcp.local.";

    public MulticastEmitter() {
    }
    public void run() {
        try {
            // JmDNS 객체를 생성
            InetAddress address = InetAddress.getLocalHost();
            String hostname = InetAddress.getByName(address.getHostName()).toString();
            jmdns = JmDNS.create(address, hostname);

            // 서비스 정보 생성
            ServiceInfo serviceInfo = ServiceInfo.create(TYPE_STRING, "example", 1234, "");
            // 서비스 등록, 요청 대기
            while(true) {
                jmdns.registerService(serviceInfo);
                Thread.sleep(5000);
                jmdns.unregisterService(serviceInfo);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
