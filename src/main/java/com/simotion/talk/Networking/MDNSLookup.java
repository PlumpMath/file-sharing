package com.simotion.talk.Networking;

import com.simotion.talk.DataParser;
import com.simotion.talk.Peer;
import com.simotion.talk.PeerListManager;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

// public class MDNSLookup
// 네트워크 상의 피어를 탐색하고, 찾았을 때 피어 정보를 가져온다.
public class MDNSLookup implements Runnable {
    private JmDNS jmdns;
    private static final String TYPE_STRING = "_transfer._http._tcp.local.";

    public void run() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String hostname = InetAddress.getByName(addr.getHostName()).toString();
            // JmDNS 객체 생성
            jmdns = JmDNS.create(addr, hostname);

            // 네트위크 상의 서비스를 탐색
            jmdns.addServiceListener(TYPE_STRING, new ServiceListener() {
                public void serviceAdded(ServiceEvent event) {
                    System.out.println("Service added: " + event.getName() +
                            " of type " + event.getType());
                    ServiceInfo info = jmdns.getServiceInfo(event.getType(), event.getName());
                    System.out.println("Service IP: " + info.getHostAddresses()[0]); // --> null
                    String[] resp = ClientRun(info.getHostAddresses()[0], "info");
                    System.out.println("서버로부터 받은 데이터 : " + Arrays.toString(resp));
                    PeerListManager.addPeer(new Peer(
                            info.getHostAddresses()[0],
                            resp[0],
                            resp[1],
                            resp[2]
                    ));
                }

                public void serviceRemoved(ServiceEvent event) {
                    System.out.println("Service removed: " + event.getInfo());
                }

                public void serviceResolved(ServiceEvent event) {
                    System.out.println("Service resolved: " + event.getInfo());
                }
            });
            while(true) {
                Thread.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // private static String[] ClientRun(String ip, String data)
    // 피어를 찾았을 때, 기본적인 피어 정보를 요청한다.
    // 상대의 HandshakeServer에서 답장한다.
    private static String[] ClientRun(String ip, String data) {
        Socket socket;
        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            socket = new Socket(ip, HandshakeServer.HANDSHAKE_PORT);
            os = socket.getOutputStream();
            osw = new OutputStreamWriter(os);
            bw = new BufferedWriter(osw);

            is = socket.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            bw.write(DataParser.encrypt(data));
            bw.newLine();
            bw.flush();

            String receiveData = br.readLine();
            receiveData = DataParser.decrypt(receiveData);
            return receiveData.split(" ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(bw!=null) bw.close();
                if(osw!=null) osw.close();
                if(os!=null) os.close();
                if(br!=null) br.close();
                if(isr!=null) isr.close();
                if(is!=null) is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new String[0];
    }
}
