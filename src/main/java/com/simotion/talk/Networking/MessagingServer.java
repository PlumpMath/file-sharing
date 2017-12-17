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

// class MessagingServer
// Handshake 후 들어오는 모든 요청에 대해 처리한다.
// 요청이 들어올 때 까지 기다린 후, 요청이 들어오면 MessagingServerThread를 생성해 요청을 처리한다.

public class MessagingServer implements Runnable {
    // 포트를 상수로 정의
    static final int CHAT_PORT = 16912;

    @Override
    public void run() {
        ServerSocket server;
        Socket socket;

        try {
            server = new ServerSocket(CHAT_PORT);

            while (true) {
                // 연결을 대기
                socket = server.accept();
                // 연결이 들어오면 MessagingServerThread를 생성하여 요쳥을 처리
                new MessagingServerThread(socket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class MessagingServerThread extends Thread {
    private Socket socket;
    private Preferences prefs;

    MessagingServerThread(Socket socket) {
        this.socket = socket;
        prefs = Preferences.userNodeForPackage(com.simotion.talk.Main.class);
    }
    @Override
    public void run() {
        BufferedInputStream bis = null;
        DataInputStream in = null;
        try {
            System.out.println("연결 요청: " + socket.getInetAddress());

            // InputStream을 생성
            bis = new BufferedInputStream(socket.getInputStream());
            in = new DataInputStream(bis);

            // 메세지 종류를 찾아서, 종류에 맞게 처리
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
                    // 펑-터
                    System.err.println("Malformed message.");
                    break;
            }
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            // 소켓, Stream을 닫음
            try {
                if(socket!=null) socket.close();
                if(bis!=null) bis.close();
                if(in!=null) in.close();
            }
            catch(Exception ignored) {}
        }
    }

    // private void manageLocationSend(Socket socket, DataInputStream in)
    // LOCATION 요청을 처리
    // 발신자를 찾고, 메세지를 인코딩하여 채팅창으로 전달한다.
    private void manageLocationSend(Socket socket, DataInputStream in) {
        boolean senderFound = false;
        try {
            int map = in.readInt();
            double locX = in.readDouble();
            double locY = in.readDouble();

            String message = "\\\\comehere "+map+" "+locX+" "+locY;

            // 피어 중 IP 주소를 검색
            for(int i = 0; i< PeerListManager.peers.size(); i++) {
                if(PeerListManager.peers.get(i).ipAddress.equals(socket.getInetAddress().toString().substring(1))) {
                    senderFound = true;
                    PeerListManager.addMsg(PeerListManager.peers.get(i), message);
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

    // private void manageLocationSend(Socket socket, DataInputStream in)
    // PLAIN_MESSAGE 요청을 처리
    // 발신자를 찾고, 채팅창으로 전달한다.
    private void manageNormalMessage(Socket socket, DataInputStream br) {
        boolean senderFound = false;
        try {
            String data = br.readUTF();

            // 피어 중 IP 주소를 검색
            for(int i = 0; i< PeerListManager.peers.size(); i++) {
                if(PeerListManager.peers.get(i).ipAddress.equals(socket.getInetAddress().toString().substring(1))) {
                    senderFound = true;
                    PeerListManager.addMsg(PeerListManager.peers.get(i), DataParser.decrypt(data));
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

    // private void manageInfoQuery(Socket socket, DataInputStream br)
    // INFO_QUERY 요청을 처리
    // 요청을 파싱한 후, 해당하는 답변을 전송한다.
    private void manageInfoQuery(Socket socket, DataInputStream br) {
        BufferedOutputStream bos = null;
        DataOutputStream dos = null;
        try {
            String[] query = DataParser.decrypt(br.readUTF()).trim().split(" ");
            // 요청을 파싱, 답변 생성
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
            // Stream을 닫음
            try {
                if(bos!=null) bos.close();
                if(dos!=null) dos.close();
            } catch (Exception ignored) {}
        }

    }

    // private void manageFileRecv(DataInputStream d)
    // FILE_TRANSFER 요청을 처리한다.
    // 만약 파일 수신을 거부한 상태라면, return한다.
    private void manageFileRecv(DataInputStream d) {
        try {
            // 파일 수신을 거부한 상태라면 return
            if(!prefs.getBoolean(Main.ALLOW_FILES, false)) {
                return;
            }

            // 파일명, 확장자 추출
            String fileName = d.readUTF();

            String name = fileName.substring(0,fileName.lastIndexOf('.'));
            String ext = fileName.substring(fileName.lastIndexOf('.'));

            // 파일이 겹치지 않도록 임의의 문자열 붙이기
            String nameAdd = "";
            while(new File("incoming/"+name+nameAdd+ext).exists()) {
                nameAdd = "-"+randomString();
            }

            // 파일을 저장
            Path finalDir = Paths.get("incoming/"+name+nameAdd+ext);
            Files.createDirectories(finalDir.getParent());
            Files.copy(d, finalDir);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    // private String queryRespond(String[] query)
    // 주어진 쿼리 문자열을 분석해 답변을 반환한다.
    private String queryRespond(String[] query) {
        if(query[0].equals("allowFileTransfer")) {
            return prefs.getBoolean(Main.ALLOW_FILES, false)?"1":"0";
        }
        return "";
    }

    // private String randomString()
    // 임의의 8글자 문자열을 반환한다.
    // manageFileRecv에서 이용
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