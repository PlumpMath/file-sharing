package com.simotion.talk;

import com.sun.org.apache.xpath.internal.operations.Mult;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.prefs.Preferences;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class MulticastReceiver implements Runnable {
    public JmDNS jmdns;
    public static final String TYPE_STRING = "_transfer._http._tcp.local.";

    public String machineUUID;

    public MulticastReceiver() {
        Preferences prefs = Preferences.userNodeForPackage(com.simotion.talk.Main.class);
        machineUUID = prefs.get(Main.UUID_KEY, "-1");
    }

    public void run() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String hostname = InetAddress.getByName(addr.getHostName()).toString();
            // Create a JmDNS instance
            jmdns = JmDNS.create(addr, hostname);

            // Add a service listener
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
            while(true) {}
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static String[] ClientRun(String ip, String data) {

        Socket socket = null;
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
            bw = new BufferedWriter(osw);            //서버로 전송을 위한 OutputStream

            is = socket.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);        // 서버로부터 Data를 받음

            bw.write(DataParser.encrypt(data));
            bw.newLine();
            bw.flush();

            String receiveData = "";
            receiveData = br.readLine();        // 서버로부터 데이터 한줄 읽음
            receiveData = DataParser.decrypt(receiveData);
            return receiveData.split(" ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                osw.close();
                os.close();
                br.close();
                isr.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new String[0];
    }
}
