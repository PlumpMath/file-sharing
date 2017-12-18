package com.simotion.talk.Networking;

import com.simotion.talk.DataParser;
import com.simotion.talk.Main;
import javafx.application.Platform;
import javafx.scene.control.Alert;

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

    // public void run()
    // Runnable를 implement하기 위한 메소드
    // 프로그램이 실행되는 동안 연결 요청을 받이들이고, 요청을 처리하는 스레드를 생성한다.
    @Override
    public void run() {
        ServerSocket server;
        Socket socket;
        try {
            server = new ServerSocket(HANDSHAKE_PORT);
            while (true) {
                try {
                    // 연결 요청을 기다림
                    socket = server.accept();
                    // 연결이 수립되면 처리 스레드를 만들어 소켓을 전달
                    new HandshakeServerThread(socket).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch(java.net.BindException e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("오류");
                alert.setHeaderText("네트워크 오류");
                alert.setContentText("연결 서버를 만들 수 없습니다. 같은 프로그램이 이미 실행중인지 확인해 주세요.");
                alert.showAndWait();
                Platform.exit();
                System.exit(0);
            });
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
        // 내부 멤버 변수에 소켓을 저장
        this.socket = socket;
        // 설정에서 장치의 이름, 이메일, 장치 UUID를 불러온다.
        Preferences prefs = Preferences.userNodeForPackage(com.simotion.talk.Main.class);
        profileName = prefs.get(Main.PROFILE_NAME, "DefaultName");
        profileEmail = prefs.get(Main.PROFILE_EMAIL, "DefaultEmail");
        machineUUID = prefs.get(Main.UUID_KEY, "-1");
    }

    // public void run()
    // Runnable를 implement하기 위한 메소드
    // 연결을 처리하는 메소드
    @Override
    public void run() {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            System.out.println("Connection request from: " + socket.getInetAddress());

            is = socket.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            //데이터를 읽고, respondData에 전달해서 파싱후 답변한다.
            String data = br.readLine();
            respondData(data);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if(is!=null) is.close();
                if(isr!=null) isr.close();
                if(br!=null) br.close();
            }
            catch(Exception ignore) {}
        }
    }
    private void respondData(String data) {
        //Base64 디코딩
        data = DataParser.decrypt(data);

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        String resp;

        //요청 파싱
        if (data.equals("info")) {
            resp = String.format("%s %s %s", profileName, profileEmail, machineUUID);
        } else resp = "no info";

        //resp의 내용을 보낸다.
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
            } catch (Exception ignore) { }
        }
    }
}


// 참고 출처
// http://hunit.tistory.com/256
