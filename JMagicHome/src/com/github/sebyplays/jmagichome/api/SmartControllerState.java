package com.github.sebyplays.jmagichome.api;

import com.github.sebyplays.jmagichome.utils.Utilities;
import lombok.Getter;
import org.json.JSONObject;

public class SmartControllerState{

    @Getter private ISmartController iSmartController;

    @Getter private boolean powerState;

    @Getter private String name;
    @Getter private String ip;
    @Getter private String model;
    @Getter private int port = 5577;

    @Getter private int brightness = 0;
    @Getter private int speed = 0;
    @Getter private int red, green, blue;

    @Getter private Pattern pattern;

    @Getter private JSONObject deviceJSON = new JSONObject();

    public SmartControllerState(ISmartController iSmartController) {
        this.iSmartController = iSmartController;
        this.powerState = iSmartController.getState();
        this.name = iSmartController.getName();
        this.ip = iSmartController.getIp();
        this.model = iSmartController.getModel();
        this.brightness = iSmartController.getBrightness();
        this.speed = iSmartController.getSpeed();
        this.red = iSmartController.getRed();
        this.green = iSmartController.getGreen();
        this.blue = iSmartController.getBlue();
        this.pattern = iSmartController.getPattern();
        this.deviceJSON = iSmartController.getDeviceJSON();

    }

    public SmartControllerState(){}

    public void loadStateFromJSON(String name){
        JSONObject jsonObject = Utilities.loadJSON(name + "-state");
        this.name = jsonObject.getString("name");
        this.ip = jsonObject.getString("ip");
        this.model = jsonObject.getString("model");
        this.port = jsonObject.getInt("port");
        this.brightness = jsonObject.getInt("brightness");
        this.speed = jsonObject.getInt("speed");
        this.red = jsonObject.getInt("red");
        this.green = jsonObject.getInt("green");
        this.blue = jsonObject.getInt("blue");
        try {
            this.pattern = Pattern.valueOf(jsonObject.getString("pattern").toUpperCase());
        } catch (IllegalArgumentException e) {
            this.pattern = null;
        }
        this.deviceJSON = jsonObject.getJSONObject("deviceJSON");
    }

    public void saveJson(){
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("ip", ip);
        json.put("model", model);
        json.put("port", port);
        json.put("brightness", brightness);
        json.put("speed", speed);
        json.put("red", red);
        json.put("green", green);
        json.put("blue", blue);
        json.put("pattern", pattern.toString());
        json.put("deviceJSON", deviceJSON);
        Utilities.saveJSON(json, name + "-state");
    }


}
