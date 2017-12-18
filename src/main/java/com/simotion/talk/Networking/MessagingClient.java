package com.simotion.talk.Networking;


import com.simotion.talk.DataParser;
import com.simotion.talk.Peer;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

// class MessagingClient
// 피어를 찾은 후, 피어에게 보내는 모든 메세지를 처리한다.
// 한번에 하나의 작업만 수행하고, Thread-Safe하도록 하기 위해
// Singleton 패턴과 Synchronized를 적용하여 하나의 스레드만 점유할 수 있도록 한다.

public class MessagingClient {
    // Singleton 적용
    private static MessagingClient instance = new MessagingClient();
    public static MessagingClient getInstance() {
        return instance;
    }
    private MessagingClient() { }
    // Singleton 적용 끝

    // 소켓이 점유중인지 확인
    private boolean working = false;

    // public void sendMessage(Peer peer, String message)
    // 주어진 Peer에게 message의 내용으로 PLAIN_MESSAGE를 전달한다.
    public void sendMessage(Peer peer, String message) {
        sendString(peer, message, MessageType.PLAIN_MESSAGE, false);
    }

    // public String getStatusQuery(Peer peer, String query)
    // 주어진 Peer에게 query의 내용을 INFO_QUERY 하고, 결과를 반환한다.
    public String getStatusQuery(Peer peer, String query) {
        return sendString(peer, query, MessageType.INFO_QUERY, true);
    }

    // public synchronized void sendFile(Peer peer, File file)
    // 주어진 Peer에게 File을 전달한다.
    // 파일의 크기가 Integer.MAX_VALUE (2^31-1바이트, =2GB)보다 클 경우,
    // IllegalArgumentException을 throw한다.
    public synchronized void sendFile(Peer peer, File file) throws IllegalArgumentException {
        // 작업중일 경우 wait한다.
        if(working) {
            try { wait(); }
            catch(InterruptedException ignored) { }
        }
        // 작업중으로 표시한다.
        working = true;

        Socket socket = null;
        try {
            // 소켓 설정, OutputStream 생성
            socket = new Socket(peer.ipAddress, MessagingServer.CHAT_PORT);
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());
            // 파일이 너무 클 경우 예외를 throw한다.
            long length = file.length();
            if(length > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("파일이 너무 큽니다.");
            }
            // Magic Byte, 파일명, 파일 내용을 전달한다.
            try (DataOutputStream d = new DataOutputStream(out)) {
                d.writeInt(MessageType.FILE_SEND.getMagicByte());
                d.writeUTF(file.getName());
                Files.copy(file.toPath(), d);
                d.flush();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            // 소켓을 닫는다.
            try {
                if(socket!=null) socket.close();
            }
            catch(Exception ignored) { }
            // 작업을 마무리하고, 대기중인 Thread에게 notify한다.
            working = false;
            notify();
        }
    }

    // public synchronized void sendLocation(Peer peer, int map, double x, double y)
    // 주어진 Peer에게 현재 위치를 전달한다.
    public synchronized void sendLocation(Peer peer, int map, double x, double y) {
        // 작업중일 경우 wait한다.
        if(working) {
            try { wait(); }
            catch(InterruptedException ignored) { }
        }
        // 작업중인 것으로 표시한다.
        working = true;

        Socket socket = null;
        BufferedOutputStream os = null;

        try {
            // 소켓을 연결하고, OutputStream을 생성한다.
            socket = new Socket(peer.ipAddress, MessagingServer.CHAT_PORT);
            os = new BufferedOutputStream(socket.getOutputStream());

            try(DataOutputStream dos = new DataOutputStream(os)) {
                // 프로토콜에 맞게 데이터를 전송한다. (MessageType.java 참조)
                dos.writeInt(MessageType.LOCATION.getMagicByte());
                dos.writeInt(map);
                dos.writeDouble(x);
                dos.writeDouble(y);
                dos.flush();
            }
            return;

        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            // 소켓을 닫는다.
            try {
                if(socket!=null) socket.close();
                if(os!=null) os.close();
            } catch (Exception ignored) { }

            // 작업을 마무리하고, 대기중인 Thread에게 notify한다.
            working = false;
            notify();
        }
    }

    // private synchronized String sendString(Peer peer, String message, MessageType type, boolean recvData)
    // 주어진 Peer에게 명시된 MessageType type으로 message를 전달한다.
    // recvData를 설정함으로서 답변을 받을 것인지 설정한다.
    private synchronized String sendString(Peer peer, String message, MessageType type, boolean recvData) {
        // 작업중일 경우 wait한다.
        if(working) {
            try { wait(); }
            catch(InterruptedException ignored) { }
        }
        // 작업중인 것으로 표시한다.
        working = true;

        Socket socket = null;
        BufferedOutputStream os = null;
        DataOutputStream dos = null;
        InputStream is = null;
        DataInputStream dis = null;

        try {
            // 소켓을 연결하고, OutputStream을 생성한다.
            socket = new Socket(peer.ipAddress, MessagingServer.CHAT_PORT);
            os = new BufferedOutputStream(socket.getOutputStream());

            // 프로토콜에 맞게 데이터를 전송한다. (MessageType.java 참조)
            dos = new DataOutputStream(os);
            dos.writeInt(type.getMagicByte());
            dos.writeUTF(DataParser.encrypt(message));
            dos.flush();

            // 답변을 받아야 한다면, 받는다.
            if(recvData) {
                // InputStream을 생성한다.
                is = socket.getInputStream();
                dis = new DataInputStream(is);

                // 입력을 디코딩하여 반환한다.
                String receiveData;
                receiveData = dis.readUTF();
                return DataParser.decrypt(receiveData);
            }

            // true의 의미롤 "1"을 반환
            return "1";

        } catch (Exception e) {
            e.printStackTrace();
            // false의 의미로 "0"을 반환
            return "0";
        } finally {
            try {
                // 소켓과 각종 Stream을 닫는다.
                if(os!=null) os.close();
                if(dos!=null) dos.close();
                if(is!=null) is.close();
                if(dis!=null) dis.close();
                if(socket!=null) socket.close();
            }
            catch (Exception ignored) { }

            // 작업을 마무리하고, 대기중인 Thread에게 notify한다.
            working = false;
            notify();
        }
    }
}
