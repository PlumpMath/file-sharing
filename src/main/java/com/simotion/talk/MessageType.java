package com.simotion.talk;

// class NetworkMagicBytes
//
public enum MessageType {
    NORMAL_MESSAGE(0x25), INFO_QUERY(0x09), FILE_SEND(0x12);

    private int magicByte;

    MessageType(int magicByte) {
        this.magicByte = magicByte;
    }
    public int getMagicByte() {
        return this.magicByte;
    }
    public static int getType(int magicByte) {
        if(magicByte == NORMAL_MESSAGE.magicByte) return 0;
        if(magicByte == INFO_QUERY.magicByte) return 1;
        if(magicByte == FILE_SEND.magicByte) return 2;
        return -1;
    }
}
