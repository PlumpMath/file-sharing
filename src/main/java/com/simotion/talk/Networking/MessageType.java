package com.simotion.talk.Networking;

// enum NetworkTypes
// MessagingServer / MessagingClient 프로토콜에서 사용하는 메세지의 첫번째 바이트를 정의한다.
// 사용 가능한 바이트의 종류는 다음과 같다:
// PLAIN_MESSAGE(0x25) - 사용자 간 전달하는 일반적인 메세지
// INFO_QUERY(0x09)     - 클라이언트 간 정보 요청
// FILE_SEND(0x12)      - 파일 전송
// LOCATION(0x51)       - 위치(호출) 전송

// MessagingServer / MessagingClient에서 요청을 주고 받을 때, 다음과 같이 MessageType를 이용한다.
// PLAIN_MESSAGE:
// [0x25 (int)][메세지 본문 (UTF)]
// INFO_QUERY:
// [0x09 (int)][요청 Parameter (UTF)]
// FILE_SEND:
// [0x12 (int)][파일명 (UTF)][파일 데이터 (FileStream)]
// LOCATION:
// [0x51 (int)][위치 (int)][x좌표 (double)][y좌표 (double)]

public enum MessageType {
    PLAIN_MESSAGE(0x25), INFO_QUERY(0x09), FILE_SEND(0x12), LOCATION(0x51);

    private int magicByte;

    MessageType(int magicByte) {
        this.magicByte = magicByte;
    }
    public int getMagicByte() {
        return this.magicByte;
    }
    public static int getType(int magicByte) {
        if(magicByte == PLAIN_MESSAGE.magicByte) return 0;
        if(magicByte == INFO_QUERY.magicByte) return 1;
        if(magicByte == FILE_SEND.magicByte) return 2;
        if(magicByte == LOCATION.magicByte) return 3;
        return -1;
    }
}
