package com.neonthread.settings;

import java.util.Properties;

public class AudioSettings {
    private int masterVolume = 70;

    public void load(Properties props) {
        try {
            this.masterVolume = Integer.parseInt(props.getProperty("masterVolume", "70"));
        } catch (NumberFormatException e) {
            this.masterVolume = 70;
        }
        validate();
    }

    public void save(Properties props) {
        props.setProperty("masterVolume", String.valueOf(masterVolume));
    }

    public void validate() {
        if (masterVolume < 0) masterVolume = 0;
        if (masterVolume > 100) masterVolume = 100;
    }

    public int getMasterVolume() { return masterVolume; }
    public void setMasterVolume(int masterVolume) { 
        this.masterVolume = masterVolume;
        validate();
    }
}
