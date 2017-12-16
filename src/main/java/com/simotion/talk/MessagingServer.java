package com.simotion.talk;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.prefs.Preferences;

public class MessagingServer implements Runnable {
    public static final int CHAT_PORT = 16912;

    private static String profileName, profileEmail, machineUUID;

    public MessagingServer() {
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
            server = new ServerSocket(CHAT_PORT);
            while (true) {
                socket = server.accept();
                System.out.println("Connection request from: " + socket.getInetAddress());

                is = socket.getInputStream();
                isr = new InputStreamReader(is);
                br = new BufferedReader(isr);


                String data = null;
                data = br.readLine();

                //Who is this from?
                for(int i=0;i<PeerListManager.peers.size();i++) {
                    if(PeerListManager.peers.get(i).ipAddress.equals(socket.getInetAddress().toString().substring(1))) {
                        PeerListManager.receiveChatMsg(PeerListManager.peers.get(i),DataParser.decrypt(data));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}


//출처: http://hunit.tistory.com/256 [HunIT Blog]
