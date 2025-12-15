package com.neonthread.settings;

import java.util.Properties;

public class LocalizationSettings {
    private String language = "en";

    public void load(Properties props) {
        this.language = props.getProperty("language", "en");
        validate();
    }

    public void save(Properties props) {
        props.setProperty("language", language);
    }

    public void validate() {
        if (!language.equals("en") && !language.equals("es")) {
            language = "en";
        }
    }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { 
        this.language = language;
        validate();
    }
}
