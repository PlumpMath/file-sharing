package com.simotion.talk;

// public class DataParser
// 주고받는 데이터를 암/복호화한다.
public class DataParser {
    private static final String passKey = "QB!Z@MQ%B^3@gyNH";
    public static String encrypt(String text) {
        try {
            AES256Util util = new AES256Util(passKey);
            return util.encrypt(text);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String decrypt(String data) {
        try {
            AES256Util util = new AES256Util(passKey);
            return util.decrypt(data);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
