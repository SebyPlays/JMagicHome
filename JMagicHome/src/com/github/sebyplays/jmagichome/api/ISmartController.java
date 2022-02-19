package com.github.sebyplays.jmagichome.api;

import org.json.JSONObject;

public interface ISmartController {

    void setState(boolean state);
    void powerOn();
    void powerOff();

    void setBrightness(int brightness);
    void reduceBrightness(int amount);
    void IncreaseBrightness(int amount);

    void setHexColor(String hexColor);
    void setRed(int red);

    void setGreen(int green);

    void setBlue(int blue);
    void setRGB(int red, int green, int blue);
    void fadeOutColor(String hexColor, int duration);

    void fadeInColor(String hexColor, int duration);

    void transitionToColor(String hexColor, int duration);

    void setPattern(Pattern pattern, int speed);
    void turnWhite();
    String getColor();

    int getBrightness();

    boolean getState();
    byte[] applyColors();

    SmartControllerState getSmartControllerState();

    void setSmartControllerState(SmartControllerState smartControllerState);

    int getRed();
    int getGreen();
    int getBlue();

    int getSpeed();

    JSONObject getDeviceJSON();

    Pattern getPattern();

    String getModel();
    String getName();
    String getIp();

    byte[] sendCommand(byte[] bytes, boolean isResponse);
}
