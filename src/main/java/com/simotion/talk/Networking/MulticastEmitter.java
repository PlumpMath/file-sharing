package com.simotion.talk.Networking;

import com.simotion.talk.Main;

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
    private JmDNS jmdns;
    private static final String TYPE_STRING = "_transfer._http._tcp.local.";

    private String profileName, profileEmail, machineUUID;

    public MulticastEmitter() {
        Preferences prefs = Preferences.userNodeForPackage(com.simotion.talk.Main.class);
        profileName = prefs.get(Main.PROFILE_NAME, "DefaultName");
        profileEmail = prefs.get(Main.PROFILE_EMAIL, "DefaultEmail");
        machineUUID = prefs.get(Main.UUID_KEY, "-1");
    }
    public void run() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            String hostname = InetAddress.getByName(address.getHostName()).toString();
            jmdns = JmDNS.create(address, hostname);

            ServiceInfo serviceInfo = ServiceInfo.create(TYPE_STRING, "example", 1234, "");
            while(true) {
                jmdns.registerService(serviceInfo);
                Thread.sleep(5000);
                jmdns.unregisterService(serviceInfo);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
