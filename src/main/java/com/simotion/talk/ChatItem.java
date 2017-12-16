package com.simotion.talk;

import java.util.Date;

// class ChatItem
// 채팅 메세지 하나에 대한 객체, 메세지 외 생성 시간 기록
// 채팅을 ArrayList에 저장, SQLite DB와의 통신에서 사용

public class ChatItem {
    public Date time;       // 보내거나 받은 시간
    public boolean sent;    // 보낸 것이면 True, 받은 것이면 False
    public String message;  // 메세지 내용
    public ChatItem(Date time, String msg, boolean sent) {
        this.time = time;
        message=new String(msg);
        this.sent=sent;
    }
}
