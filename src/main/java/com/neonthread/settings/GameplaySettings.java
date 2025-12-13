package com.neonthread.settings;

import java.util.Properties;

public class GameplaySettings {
    private String theme = "CYAN";

    public void load(Properties props) {
        this.theme = props.getProperty("theme", "CYAN");
        validate();
    }

    public void save(Properties props) {
        props.setProperty("theme", theme);
    }

    public void validate() {
        if (!theme.equals("CYAN") && !theme.equals("PURPLE")) {
            theme = "CYAN";
        }
    }

    public String getTheme() { return theme; }
    public void setTheme(String theme) { 
        this.theme = theme;
        validate();
    }
}
