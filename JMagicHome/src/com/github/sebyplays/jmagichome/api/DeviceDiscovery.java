package com.github.sebyplays.jmagichome.api;

import com.github.sebyplays.jmagichome.JMagicHome;
import com.github.sebyplays.jmagichome.utils.Utilities;
import lombok.Getter;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;

public class DeviceDiscovery {

    public static final int DISCOVERY_PORT = 48899;
    public static final String DISCOVERY_MESSAGE = "HF-A11ASSISTHREAD";
    public static DatagramSocket udpClient;

    static {
        try {
            udpClient = new DatagramSocket(DISCOVERY_PORT);
        } catch (SocketException e) {
        }
    }

    public static ArrayList<SmartLight> asyncDiscovery(int timeout){
        final ArrayList<SmartLight>[] devices = new ArrayList[]{new ArrayList<>()};
        new Thread(() -> {
            devices[0] = discoverDevices(timeout);
        }).start();
        return devices[0];
    }

    @SneakyThrows
    public static ArrayList<SmartLight> discoverDevices(int timeout){
        ArrayList<SmartLight> devices = new ArrayList<>();
        udpClient.setBroadcast(true);
        udpClient.setSoTimeout(1000);
        boolean receive = true;
        for(String ip : Utilities.getAllAddresses())
            udpClient.send(new DatagramPacket(DISCOVERY_MESSAGE.getBytes(), DISCOVERY_MESSAGE.length(), InetAddress.getByName(ip), DISCOVERY_PORT));

        while (receive){
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

            try{
                udpClient.receive(packet);
            } catch (Exception e){
                receive = false;
                break;
            }

            String message = new String(packet.getData(), 0, packet.getLength());
            if(!message.equals(DISCOVERY_MESSAGE)){
                String[] parts = message.split(",");
                devices.add(new SmartLight(parts[0], parts[1], parts[2]));
            }
        }
        return devices;
    }

    public static boolean deviceJSONAvailable(String name){
        return new File(JMagicHome.dataDir.getAbsolutePath() + "/" + name + ".json").exists();
    }

    public static void saveDeviceToJSON(SmartLight device){
        JSONObject json = new JSONObject();
        json.put("ip", device.getIp());
        json.put("id", device.getName());
        json.put("model", device.getModel());
        Utilities.saveJSON(json, device.getName());
    }

    @SneakyThrows
    public static SmartLight loadDeviceFromJSON(File file){
        JSONObject json = new JSONObject(new BufferedReader(new FileReader(file)).readLine());
        String ip = (String) json.get("ip");
        String id = (String) json.get("id");
        String model = (String) json.get("model");
        return new SmartLight(ip, id, model);
    }

}
