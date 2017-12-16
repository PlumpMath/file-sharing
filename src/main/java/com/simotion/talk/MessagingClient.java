package com.simotion.talk;

import sun.plugin2.message.Message;

import java.io.*;
import java.net.Socket;

public class MessagingClient {
    public static boolean sendMessage(Peer peer, String message) {
        Socket socket;
        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            socket = new Socket(peer.ipAddress, MessagingServer.CHAT_PORT);
            os = socket.getOutputStream();
            osw = new OutputStreamWriter(os);
            bw = new BufferedWriter(osw);

            is = socket.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            bw.write(DataParser.encrypt(message));
            bw.newLine();
            bw.flush();

            // receiveData = br.readLine();        // 서버로부터 데이터 한줄 읽음
            // receiveData = DataParser.decrypt(receiveData);
            // return receiveData.equals("ok");
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
        return true;
    }
}
