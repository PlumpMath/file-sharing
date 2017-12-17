package com.simotion.talk;

import java.util.Base64;

// public class DataParser
// 주고받는 데이터의 Base64 디코딩, 인코딩
// 원래 보안까지 추가하려 했으나...
public class DataParser {
    public static String encrypt(String text) {
        Base64.Encoder encoder = Base64.getEncoder();
        try {
            byte[] targetBytes = text.getBytes("UTF-8");
            byte[] encodedBytes = encoder.encode(targetBytes);
            return new String(encodedBytes);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
    public static String decrypt(String data) {
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            byte[] encodedBytes = decoder.decode(data);
            return new String(encodedBytes, "UTF-8");
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
}
