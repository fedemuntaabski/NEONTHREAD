package com.neonthread.settings;

import java.util.Properties;

public class AccessibilitySettings {
    private boolean largeText = false;

    public void load(Properties props) {
        this.largeText = Boolean.parseBoolean(props.getProperty("largeText", "false"));
    }

    public void save(Properties props) {
        props.setProperty("largeText", String.valueOf(largeText));
    }

    public boolean isLargeText() { return largeText; }
    public void setLargeText(boolean largeText) { this.largeText = largeText; }
}
