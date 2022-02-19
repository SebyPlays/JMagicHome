package com.github.sebyplays.jmagichome;

import com.github.sebyplays.jmagichome.api.DeviceDiscovery;
import com.github.sebyplays.jmagichome.api.Pattern;
import com.github.sebyplays.jmagichome.api.SmartControllerState;
import com.github.sebyplays.jmagichome.api.SmartLight;
import lombok.SneakyThrows;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class JMagicHome {

    public static final File dataDir = new File(System.getProperty("user.dir") + "/data/json/");

    public JMagicHome(){
    }

    @SneakyThrows
    public static void main(String[] args) {
        dataDir.mkdirs();
        ArrayList<SmartLight> devices = DeviceDiscovery.discoverDevices(20000);
        for(SmartLight device : devices){
            if(!DeviceDiscovery.deviceJSONAvailable(device.getName()))
                DeviceDiscovery.saveDeviceToJSON(device);
        }

        for(File file : dataDir.listFiles()){
            if(file.getName().endsWith(".json") && !file.getName().contains("-state")){
                SmartLight device = DeviceDiscovery.loadDeviceFromJSON(file);
                devices.add(device);
            }
        }

        SmartLight light = devices.get(0);
        light.turnWhite();
        TimeUnit.SECONDS.sleep(2);
      /*  if(!new File(dataDir.getAbsolutePath() + "/" + light.getName() + "-state.json").exists()){

            light.setPattern(Pattern.SEVEN_COLOR_JUMPING, 10000);
            SmartControllerState state = light.getSmartControllerState();
            System.out.println(state.getRed());
            System.out.println(state.getGreen());
            System.out.println(state.getBlue());
            state.saveJson();
        } else {
            SmartControllerState state = new SmartControllerState();
            state.loadStateFromJSON(light.getName());
            System.out.println(state.getRed());
            System.out.println(state.getGreen());
            System.out.println(state.getBlue());
            System.out.println("LOADED FROM CACHE");
            light.setSmartControllerState(state);
            System.out.println("SET");
        }*/

        System.out.println(light);

        Robot robot = new Robot();
        new Thread(() -> {
            int r, g, b;
            while(true){

                try {
                    TimeUnit.MILLISECONDS.sleep(4);
                    Color color = robot.getPixelColor(getCenterPixel()[0], getCenterPixel()[1]);
                    r = color.getRed();
                    g = color.getGreen();
                    b = color.getBlue();
                    light.setRGB(r, g, b);
                    System.out.println(r + " " + g + " " + b);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static int[] getCenterPixel(){
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        GraphicsDevice gd = gs[0];
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        Rectangle rect = gc.getBounds();
        int width = rect.width;
        int height = rect.height;
        int[] centerPixel = new int[2];
        centerPixel[0] = width / 2;
        centerPixel[1] = height / 2;
        return centerPixel;
    }

}
