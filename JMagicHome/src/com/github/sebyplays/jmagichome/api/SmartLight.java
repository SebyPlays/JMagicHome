package com.github.sebyplays.jmagichome.api;

import com.github.sebyplays.jmagichome.utils.Utilities;
import lombok.Getter;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SmartLight implements ISmartController{

    @Getter private String name;
    @Getter private String ip;
    @Getter private String model;
    @Getter private int port = 5577;

    @Getter private int brightness = 0;
    @Getter private int speed = 0;
    @Getter private int red, green, blue;

    @Getter private Pattern pattern;

    @Getter private JSONObject deviceJSON = new JSONObject();

    @Getter private boolean state;

    public SmartLight(String ip, String id, String model) {
        this.name = id;
        this.ip = ip;
        this.model = model;

        this.getLightJSON();
        this.red = (int) this.deviceJSON.get("red");
        this.green = (int) this.deviceJSON.get("green");
        this.blue = (int) this.deviceJSON.get("blue");
    }


    @Override
    public void setState(boolean state) {
        if(!state)
            powerOn();
        else
            powerOff();
    }

    @Override
    public void powerOn() {
        this.sendCommand(new byte[]{0x71, 0x24, 0x0f}, false);
        this.state = true;
    }

    @Override
    public void powerOff() {
        this.sendCommand(new byte[]{0x71, 0x23, 0x0f}, false);
        this.state = false;
    }

    @Override
    public void setBrightness(int brightness) {
        setRGB(this.red*(brightness/this.brightness),
                this.green*(brightness/this.brightness),
                this.blue*(brightness/this.brightness));
        this.brightness = getBrightness();

    }

    @Override
    public void reduceBrightness(int amount) {
        setBrightness(getBrightness() - amount);
    }

    @Override
    public void IncreaseBrightness(int amount) {
        setBrightness(getBrightness() + amount);
    }

    @Override
    public void setHexColor(String hexColor) {
        int[] rgb = Utilities.hexToRgb(hexColor);
        this.setRed(rgb[0]);
        this.setGreen(rgb[1]);
        this.setBlue(rgb[2]);
    }

    @Override
    public void setRed(int red) {
        this.red = red;
    }

    @Override
    public void setGreen(int green) {
        this.green = green;
    }

    @Override
    public void setBlue(int blue) {
        this.blue = blue;
    }

    @Override
    public void setRGB(int red, int green, int blue) {
        setGreen(green);
        setBlue(blue);
        setRed(red);
        applyColors();
    }

    @Override
    public void fadeOutColor(String hexColor, int duration) {
        for(int i = 0; i < duration; i--) {
            setRGB(Utilities.hexToRgb(hexColor)[0], Utilities.hexToRgb(hexColor)[1], Utilities.hexToRgb(hexColor)[2]);
        }
    }

    @Override
    public void fadeInColor(String hexColor, int duration) {

    }

    @Override
    public void transitionToColor(String hexColor, int duration) {

    }

    @Override
    public void setPattern(Pattern pattern, int speed) {
        this.pattern = pattern;
        sendAsyncCommand(new byte[]{0x61, (byte) pattern.getId(), (byte) Utilities.speedToDelay(speed), 0x0f});
    }

    @Override
    public void turnWhite() {
        setRGB(255, 255, 255);
        applyColors();
    }

    @Override
    public String getColor() {
        return null;
    }

    @Override
    public int getBrightness() {
        getLightJSON();
        int brightness = 0;
        if(red > brightness) brightness = red;
        if(green > brightness) brightness = green;
        if(blue > brightness) brightness = blue;

        this.brightness = brightness*100/255;
        return this.brightness;
    }

    @Override
    public boolean getState() {
        this.state = Boolean.parseBoolean(String.valueOf(this.getLightJSON().get("power")));
        return this.state;
    }

    @Override
    public byte[] applyColors() {
        byte[] command = new byte[]{0x41,
                (byte) Utilities.clamp(red, 0, 255),
                (byte) Utilities.clamp(green, 0, 255),
                (byte) Utilities.clamp(blue, 0, 255),
                (byte) 0x00, 0x0F, 0x00};
        return sendCommand(command, false);
    }

    @Override
    public SmartControllerState getSmartControllerState() {
        getLightJSON();
        return new SmartControllerState(this);
    }

    @Override
    public void setSmartControllerState(SmartControllerState smartControllerState) {
        this.red = smartControllerState.getRed();
        this.green = smartControllerState.getGreen();
        this.blue = smartControllerState.getBlue();
        this.brightness = smartControllerState.getBrightness();
        this.pattern = smartControllerState.getPattern();
        this.name = smartControllerState.getName();
        this.ip = smartControllerState.getIp();
        this.model = smartControllerState.getModel();
        this.state = smartControllerState.isPowerState();
        this.port = smartControllerState.getPort();
        this.deviceJSON = smartControllerState.getDeviceJSON();
        this.speed = smartControllerState.getSpeed();

        if(brightness > 0)
            setBrightness(this.brightness);

        if(pattern != null)
            setPattern(this.pattern, this.speed);

        applyColors();
    }

    public void sendAsyncCommand(byte[] command) {
        new Thread(() -> {
            sendCommand(command, false);
        }).start();
    }

    @Override
    public byte[] sendCommand(byte[] bytes, boolean expectResponse) {
        try {
            Socket socket = new Socket(this.ip, this.port);
            socket.setSoTimeout(10000);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            byte[] command = new byte[bytes.length + 1];
            int checksum = 0;

            for(int i = 0; i < bytes.length; i++){
                checksum += bytes[i];
                command[i] = bytes[i];
            }

            command[command.length - 1] = (byte) checksum;
            dataOutputStream.write(command);
            byte[] response = new byte[64];
            if(expectResponse){

                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                dataInputStream.read(response);
            }
            socket.close();
            return response;
        } catch (Exception e) {
        }
        return null;
    }


    public JSONObject getLightJSON(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("success", false);
        byte[] response = this.sendCommand(new byte[]{(byte) 0x81, (byte) 0x8a, (byte) 0x8b}, true);
        if (response == null) return jsonObject;
        //if (response[13] == 0x0) return jsonObject;

        jsonObject.put("power", response[2] == 0x23);
       // jsonObject.put("pattern", Pattern.fromId(response[3]));
        jsonObject.put("red", response[6]);
        jsonObject.put("green", response[7]);
        jsonObject.put("blue", response[8]);
        jsonObject.put("success", true);

        this.deviceJSON = jsonObject;
        return jsonObject;
    }

    public String toString(){
        JSONObject jsonObject = this.getLightJSON();
        jsonObject.put("brightness", this.getBrightness());
        jsonObject.put("id", this.name);
        jsonObject.put("model", this.model);
        jsonObject.put("ip", this.ip);
        return jsonObject.toString();
    }

}
