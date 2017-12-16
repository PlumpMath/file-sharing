package com.simotion.talk;

public class NetworkManager {

    public static void enableMulticast() {
        MulticastEmitter me = new MulticastEmitter();
        Thread t = new Thread(me);
        t.start();
        Thread t2 = new Thread(new MulticastReceiver());
        t2.start();
        Thread t3 = new Thread(new HandshakeServer());
        t3.start();
        Thread t4 = new Thread(new MessagingServer());
        t4.start();
    }

}
