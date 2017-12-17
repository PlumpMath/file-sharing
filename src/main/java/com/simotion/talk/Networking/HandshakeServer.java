package com.simotion.talk.Networking;

import com.simotion.talk.DataParser;
import com.simotion.talk.Main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.prefs.Preferences;


// Class HandshakeServer
// 피어 정보에 대한 요청이 들어왔을 때, 이름, 이메일 등 기본적인 프로필을 반환하는 역할을 한다.
// 별도의 스레드에서 포트 25252에서 TCP 소켓 요청을 받아들이고,
// 요청이 들어오면 HandshakeServerThread를 생성하여 요청을 처리한다.

public class HandshakeServer implements Runnable {
    static final int HANDSHAKE_PORT = 25252;

    @Override
    public void run() {
        ServerSocket server;
        Socket socket;
        try {
            server = new ServerSocket(HANDSHAKE_PORT);
            while (true) {
                try {
                    socket = server.accept();
                    new HandshakeServerThread(socket).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}

// class HandshakeServerThread extends Thread
// 요청 하나에 대한 처리를 한다.
// 기본적인 피어 정보를 인코딩하여 전송한다.
class HandshakeServerThread extends Thread {
    private Socket socket;
    private String profileName, profileEmail, machineUUID;
    HandshakeServerThread(Socket socket) {
        this.socket = socket;
        Preferences prefs = Preferences.userNodeForPackage(com.simotion.talk.Main.class);
        profileName = prefs.get(Main.PROFILE_NAME, "DefaultName");
        profileEmail = prefs.get(Main.PROFILE_EMAIL, "DefaultEmail");
        machineUUID = prefs.get(Main.UUID_KEY, "-1");
    }

    @Override
    public void run() {
        InputStream is;
        InputStreamReader isr;
        BufferedReader br;
        try {
            System.out.println("Connection request from: " + socket.getInetAddress());

            is = socket.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            String data = br.readLine();
            respondData(data);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    private void respondData(String data) {
        data = DataParser.decrypt(data);

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        String resp;

        if (data.equals("info")) {
            resp = String.format("%s %s %s", profileName, profileEmail, machineUUID);
        } else resp = "no info";

        try {
            os = socket.getOutputStream();
            osw = new OutputStreamWriter(os);
            bw = new BufferedWriter(osw);

            bw.write(DataParser.encrypt(resp));
            bw.flush();
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            try {
                if(bw!=null) bw.close();
                if(osw!=null) osw.close();
                if(os!=null) os.close();
                if(socket!=null) socket.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}


// 참고 출처
// http://hunit.tistory.com/256
