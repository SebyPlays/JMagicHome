package com.github.sebyplays.jmagichome.utils;

import com.github.sebyplays.jmagichome.JMagicHome;
import com.github.sebyplays.jmagichome.api.Pattern;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

public class Utilities {

    @SneakyThrows
    public static ArrayList<String> getAllAddresses(){
        ArrayList<String> addresses = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while(interfaces.hasMoreElements()){
            NetworkInterface networkInterface = interfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

            while(inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                String[] address = inetAddress.getHostAddress().split("\\.");
                if(address.length != 4) continue;
                address[3] = "255";
                addresses.add(String.join(".", address));
            }
        }
        if(addresses.size() == 0) addresses.add("255.255.255.255");
        return addresses;
    }

    public static int[] hexToRgb(String hex) {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return rgb;
    }

    public static String rgbToHex(int[] rgb) {
        return String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
    }

    public static int delayToSpeed(int delay){
        delay = clamp(delay, 1, 31);
        delay -= 1;
        return 100 - (delay / 30  * 100);
    }

    public static int speedToDelay(int speed){
        speed = clamp(speed, 0, 100);
        return (30 - ((speed / 100) * 30)) + 1;
    }

    public static int clamp(int value, int min, int max){
        return value > max ? max : Math.max(value, min);
    }

    @SneakyThrows
    public static void saveJSON(JSONObject json, String fileName) {
        FileWriter fileWriter = new FileWriter(new File(JMagicHome.dataDir.getAbsolutePath() + "/" + fileName + ".json"));
        fileWriter.write(json.toString());
        fileWriter.flush();
        fileWriter.close();
    }

    @SneakyThrows
    public static JSONObject loadJSON(String fileName) {
        return new JSONObject(new BufferedReader(new FileReader(JMagicHome.dataDir.getAbsolutePath() + "/" + fileName + ".json")).readLine());
    }

}
