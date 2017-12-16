package com.simotion.talk;
import org.sqlite.SQLiteException;

import java.sql.*;
import java.util.ArrayList;


// class ChatSaveManager
// SQLite DB의 채팅 기록 저장을 관리하는 데 사용.
// 창을 껐다가 켜도 채팅 기록이 사라지지 않도록 하기 위함

public class ChatSaveManager {
    // DB의 JDBC URL
    private static final String DB_URL="jdbc:sqlite:chat.db";

    // void setupTable(String name)
    // 주어진 테이블명을 가진 테이블이 없으면 그 테이블을 생성한다.
    private static void setupTable(String name) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS `" + name + "` ("
                    + "`time` DATETIME NOT NULL, "
                    + "`message` TEXT NOT NULL,"
                    + "`sent` BOOLEAN NOT NULL"
                    + ")";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }

    // ArrayList<ChatItem> getChatHistory(String uuid)
    // 주어진 사용자 UUID로, 사용자와의 모든 채팅 기록을 저장한다.

    public static ArrayList<ChatItem> getChatHistory(String uuid) {
        setupTable(uuid);
        ArrayList<ChatItem> ret = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("SELECT * FROM `"+uuid+"`");
            while(set.next()) {
                ret.add(
                    new ChatItem(set.getTime("time"),
                            set.getString("message"),
                            set.getBoolean("sent")
                ));
            }
            set.close();
            stmt.close();
            conn.close();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    // void append(String uuid, ChatItem item)
    // 주어진 사용자 UUID에 대해 ChatItem을 DB에 추가한다.
    public static void append(String uuid, ChatItem item) {
        setupTable(uuid);
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(false);

            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO `"+uuid+"`"
                        + " (`time`,`message`,`sent`) "
                        + "VALUES (?,?,?)");

            stmt.setLong(1, item.time.getTime());
            stmt.setString(2, item.message);
            stmt.setBoolean(3, item.sent);
            stmt.executeUpdate();

            stmt.close();
            conn.commit();
            conn.close();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }

    // void clear(String UUID)
    // 주어진 사용자 UUID에 대한 메세지 기록을 모두 지운다.
    public static void clear(String uuid) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DROP TABLE `"+uuid+"`;");
            stmt.close();
            conn.commit();
            conn.close();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }
    }
}

// 참고 출처
// http://www.sqlitetutorial.net/sqlite-java/
// https://www.tutorialspoint.com/sqlite/sqlite_java.htm
// https://www.tutorialspoint.com/sqlite/sqlite_insert_query.htm
