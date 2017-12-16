package com.simotion.talk;


import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class MessagingClient {
    public static void sendMessage(Peer peer, String message) {
        sendString(peer, message, MessageType.NORMAL_MESSAGE, false).equals("1");
    }
    public static String getStatusQuery(Peer peer, String query) {
        return sendString(peer, query, MessageType.INFO_QUERY, true);
    }
    public static void sendFile(Peer peer, File file) {
        try {
            Socket socket = new Socket(peer.ipAddress, MessagingServer.CHAT_PORT);
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
    }
    private static String sendString(Peer peer, String message, MessageType type, boolean recvData) {
        Socket socket;
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
        } finally {
            try {
                if(os!=null) os.close();
                if(dos!=null) dos.close();
                if(is!=null) is.close();
                if(dis!=null) dis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "1";
    }
}
