package com.simotion.talk;

// class NetworkMagicBytes
//
public enum MessageType {
    NORMAL_MESSAGE((byte)0x25), INFO_QUERY((byte)0x09), FILE_SEND((byte)0x12);

    private byte magicByte;

    MessageType(byte magicByte) {
        this.magicByte = magicByte;
    }
    public byte getMagicByte() {
        return this.magicByte;
    }
    public static MessageType getType(byte magicByte) {
        if(magicByte == NORMAL_MESSAGE.magicByte) return NORMAL_MESSAGE;
        if(magicByte == INFO_QUERY.magicByte)     return INFO_QUERY;
        if(magicByte == FILE_SEND.magicByte)      return FILE_SEND;
        return null;
    }
}
