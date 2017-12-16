package com.simotion.talk;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.prefs.Preferences;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;


public class MulticastEmitter implements Runnable {
    public JmDNS jmdns;
    public static final String TYPE_STRING = "_transfer._http._tcp.local.";

    String profileName, profileEmail, machineUUID;

    public MulticastEmitter() {
        Preferences prefs = Preferences.userNodeForPackage(com.simotion.talk.Main.class);
        profileName = prefs.get(Main.PROFILE_NAME, "DefaultName");
        profileEmail = prefs.get(Main.PROFILE_EMAIL, "DefaultEmail");
        machineUUID = prefs.get(Main.UUID_KEY, "-1");
    }
    public void run() {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String hostname = InetAddress.getByName(addr.getHostName()).toString();
            jmdns = JmDNS.create(addr, hostname);

            ServiceInfo serviceInfo = ServiceInfo.create(TYPE_STRING, "example", 1234, "");
            System.out.println("Start response to _transfer._http._tcp.local.");
            while(true) {
                jmdns.registerService(serviceInfo);
                Thread.sleep(5000);
                jmdns.unregisterService(serviceInfo);
            }
        }
        catch(Exception e) {

        }
    }
}
