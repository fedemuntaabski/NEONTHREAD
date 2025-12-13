package com.neonthread.settings;

import java.util.Properties;

public class VideoSettings {
    private String resolution = "1600x900";
    private boolean fullscreen = false;

    public void load(Properties props) {
        this.resolution = props.getProperty("resolution", "1600x900");
        this.fullscreen = Boolean.parseBoolean(props.getProperty("fullscreen", "false"));
        validate();
    }

    public void save(Properties props) {
        props.setProperty("resolution", resolution);
        props.setProperty("fullscreen", String.valueOf(fullscreen));
    }

    public void validate() {
        if (!resolution.matches("\\d+x\\d+")) {
            resolution = "1600x900";
        }
    }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public boolean isFullscreen() { return fullscreen; }
    public void setFullscreen(boolean fullscreen) { this.fullscreen = fullscreen; }
}
