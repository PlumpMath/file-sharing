package com.simotion.talk.Networking;


import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import com.simotion.talk.*;

// class MessagingClient
// 피어를 찾은 후, 피어에게 보내는 모든 메세지를 처리한다.
// 한번에 하나의 작업만 수행하기 위해, Singleton 패턴을 적용하고
// Synchronized를 적용하여 하나의 스레드만 점유할 수 있도록 한다.
public class MessagingClient {
    // Singleton 적용
    private static MessagingClient instance = new MessagingClient();
    public static MessagingClient getInstance() {
        return instance;
    }
    private MessagingClient() { }
    // Singleton 적용 끝

    private boolean working = false;

    public void sendMessage(Peer peer, String message) {
        sendString(peer, message, MessageType.NORMAL_MESSAGE, false);
    }
    public String getStatusQuery(Peer peer, String query) {
        return sendString(peer, query, MessageType.INFO_QUERY, true);
    }
    public synchronized void sendFile(Peer peer, File file) {
        if(working) {
            try { wait(); }
            catch(InterruptedException e) { e.printStackTrace(); }
        }
        working = true;

        Socket socket = null;
        try {
            socket = new Socket(peer.ipAddress, MessagingServer.CHAT_PORT);
            long length = file.length();
            if(length > Integer.MAX_VALUE) {
                System.err.println("File is too large.");
            }
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

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
            try {
                if(socket!=null) socket.close();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            working = false;
            notify();
        }
    }
    public synchronized void sendLocation(Peer peer, int map, double x, double y) {
        if(working) {
            try { wait(); }
            catch(InterruptedException e) { e.printStackTrace(); }
        }
        working = true;

        Socket socket = null;
        BufferedOutputStream os = null;
        DataOutputStream dos = null;

        try {
            socket = new Socket(peer.ipAddress, MessagingServer.CHAT_PORT);

            os = new BufferedOutputStream(socket.getOutputStream());
            dos = new DataOutputStream(os);
            dos.writeInt(MessageType.LOCATION.getMagicByte());
            dos.writeInt(map);
            dos.writeDouble(x);
            dos.writeDouble(y);
            dos.flush();
            return;
            // return true;

        } catch (Exception e) {
            e.printStackTrace();
            return;
            // return false;
        } finally {
            working = false;
            notify();
            try {
                if(os!=null) os.close();
                if(dos!=null) dos.close();
                if(socket!=null) socket.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
    private synchronized String sendString(Peer peer, String message, MessageType type, boolean recvData) {
        if(working) {
            try { wait(); }
            catch(InterruptedException e) { e.printStackTrace(); }
        }
        working = true;

        Socket socket = null;
        BufferedOutputStream os = null;
        DataOutputStream dos = null;
        InputStream is = null;
        DataInputStream dis = null;

        try {
            socket = new Socket(peer.ipAddress, MessagingServer.CHAT_PORT);
            os = new BufferedOutputStream(socket.getOutputStream());

            dos = new DataOutputStream(os);
            dos.writeInt(type.getMagicByte());
            dos.writeUTF(DataParser.encrypt(message));
            dos.flush();

            is = socket.getInputStream();
            dis = new DataInputStream(is);

            if(recvData) {
                String receiveData;
                receiveData = dis.readUTF();
                return DataParser.decrypt(receiveData);
            }
            return "1";

        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        } finally {
            working = false;
            notify();
            try {
                if(os!=null) os.close();
                if(dos!=null) dos.close();
                if(is!=null) is.close();
                if(dis!=null) dis.close();
                if(socket!=null) socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
