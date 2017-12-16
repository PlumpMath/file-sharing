package com.simotion.talk;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.prefs.Preferences;

public class HandshakeServer implements Runnable {
    public static final int HANDSHAKE_PORT = 25252;

    private static String profileName, profileEmail, machineUUID;

    public HandshakeServer() {
        Preferences prefs = Preferences.userNodeForPackage(com.simotion.talk.Main.class);
        profileName = prefs.get(Main.PROFILE_NAME, "DefaultName");
        profileEmail = prefs.get(Main.PROFILE_EMAIL, "DefaultEmail");
        machineUUID = prefs.get(Main.UUID_KEY, "-1");
    }

    @Override
    public void run() {
        ServerSocket server;
        Socket socket;
        InputStream is;
        InputStreamReader isr;
        BufferedReader br;

        try {
            server = new ServerSocket(HANDSHAKE_PORT);
            while (true) {
                socket = server.accept();
                System.out.println("Connection request from: " + socket.getInetAddress());

                is = socket.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);

                String data = null;
                data = br.readLine();
                System.out.println(data);
                System.out.println(DataParser.decrypt(data));

                respondData(data, socket);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void respondData(String data, Socket socket) {
        data = DataParser.decrypt(data);

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        String resp;

        if (data.equals("info")) {
            resp = String.format("%s %s %s",profileName,profileEmail,machineUUID);
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
                bw.close();
                osw.close();
                os.close();
                socket.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}


// 참고 출처
// http://hunit.tistory.com/256
