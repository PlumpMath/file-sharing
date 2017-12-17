package com.simotion.talk.Networking;

import com.simotion.talk.DataParser;
import com.simotion.talk.Main;
import com.simotion.talk.PeerListManager;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class MessagingServer implements Runnable {
    static final int CHAT_PORT = 16912;

    @Override
    public void run() {
        ServerSocket server;
        Socket socket;

        try {
            server = new ServerSocket(CHAT_PORT);
            while (true) {
                socket = server.accept();
                new MessagingServerThread(socket).start();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
}

class MessagingServerThread extends Thread {
    private Socket socket;
    private Preferences prefs;
    // private static String profileName, profileEmail, machineUUID;
    MessagingServerThread(Socket socket) {
        this.socket = socket;
        prefs = Preferences.userNodeForPackage(com.simotion.talk.Main.class);
        // profileName = prefs.get(Main.PROFILE_NAME, "DefaultName");
        // profileEmail = prefs.get(Main.PROFILE_EMAIL, "DefaultEmail");
        // machineUUID = prefs.get(Main.UUID_KEY, "-1");
    }
    @Override
    public void run() {
        BufferedInputStream bis = null;
        DataInputStream in = null;
        try {
            System.out.println("Connection request from: " + socket.getInetAddress());

            bis = new BufferedInputStream(socket.getInputStream());
            in = new DataInputStream(bis);

            // 메세지 종류를 찾아서, 종류에 맞게 핸들링
            int messageType = in.readInt();
            switch (MessageType.getType(messageType)) {
                case 0:
                    manageNormalMessage(socket, in);
                    break;
                case 1:
                    manageInfoQuery(socket, in);
                    break;
                case 2:
                    manageFileRecv(in);
                    break;
                case 3:
                    manageLocationSend(socket, in);
                    break;
                default:
                    System.err.println("Malformed message.");
                    break;
            }
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            try {
                if(bis!=null) bis.close();
                if(in!=null) in.close();
            }
            catch(Exception ignored) {}
        }
    }

    private void manageLocationSend(Socket socket, DataInputStream in) {
        boolean senderFound = false;
        try {
            int map = in.readInt();
            double locX = in.readDouble();
            double locY = in.readDouble();

            String message = "\\\\comehere "+map+" "+locX+" "+locY;

            //Who is this from?
            for(int i = 0; i< PeerListManager.peers.size(); i++) {
                if(PeerListManager.peers.get(i).ipAddress.equals(socket.getInetAddress().toString().substring(1))) {
                    senderFound = true;
                    PeerListManager.receiveChatMsg(PeerListManager.peers.get(i), message);
                }
            }
            if(!senderFound) {
                System.err.println("Message sender not found.");
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void manageNormalMessage(Socket socket, DataInputStream br) {
        boolean senderFound = false;
        try {
            String data = br.readUTF();

            //Who is this from?
            for(int i = 0; i< PeerListManager.peers.size(); i++) {
                if(PeerListManager.peers.get(i).ipAddress.equals(socket.getInetAddress().toString().substring(1))) {
                    senderFound = true;
                    PeerListManager.receiveChatMsg(PeerListManager.peers.get(i), DataParser.decrypt(data));
                }
            }
            if(!senderFound) {
                System.err.println("Message sender not found.");
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    private void manageInfoQuery(Socket socket, DataInputStream br) {
        BufferedOutputStream bos = null;
        DataOutputStream dos = null;
        try {
            String[] query = DataParser.decrypt(br.readUTF()).trim().split(" ");
            String response = queryRespond(query);

            bos = new BufferedOutputStream(socket.getOutputStream());
            dos = new DataOutputStream(bos);

            dos.writeUTF(DataParser.encrypt(response));
            dos.flush();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(bos!=null) bos.close();
                if(dos!=null) dos.close();
            } catch (Exception ignored) {}
        }

    }
    private void manageFileRecv(DataInputStream d) {
        try {
            String fileName = d.readUTF();

            String name = fileName.substring(0,fileName.lastIndexOf('.'));
            String ext = fileName.substring(fileName.lastIndexOf('.'));

            String nameAdd = "";
            while(new File("incoming/"+name+nameAdd+ext).exists()) {
                nameAdd = "-"+randomString();
            }
            Path finalDir = Paths.get("incoming/"+name+nameAdd+ext);
            Files.createDirectories(finalDir.getParent());
            Files.copy(d, finalDir);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    private String queryRespond(String[] query) {
        if(query[0].equals("allowFileTransfer")) {
            return prefs.getBoolean(Main.ALLOW_FILES, false)?"1":"0";
        }
        return "";
    }
    private String randomString() {
        StringBuilder ret = new StringBuilder();
        for(int i=0;i<8;i++) {
            int rnd = (int)(Math.random()*32);
            ret.append((char) (rnd < 10 ? (rnd + '0') : ((rnd - 10) + 'a')));
        }
        return ret.toString();
    }
}

// 참고 출처
// http://hunit.tistory.com/256
// https://stackoverflow.com/questions/15649972/how-do-i-send-file-name-with-file-using-sockets-in-java
// https://stackoverflow.com/questions/2833853/create-whole-path-automatically-when-writing-to-a-new-file
// https://stackoverflow.com/questions/10131377/socket-programming-multiple-client-to-one-server