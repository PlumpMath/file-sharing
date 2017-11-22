package com.simotion.talk;

public class NetworkManager {
    public static boolean isMulticastOn() {
        return multicastOn;
    }
    private static boolean multicastOn;

    public static void enableMulticast() {
        if(multicastOn) return;
        MulticastEmitter me = new MulticastEmitter();
        Thread t = new Thread(me);
        t.start();
        Thread t2 = new Thread(new MulticastReceiver());
        t2.start();
        Thread t3 = new Thread(new HandshakeServer());
        t3.start();
    }

}
