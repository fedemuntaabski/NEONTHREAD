package com.neonthread.localization;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Localization {
    private static Localization instance;
    private Properties translations;
    private String currentLanguage;

    private Localization() {
        translations = new Properties();
        currentLanguage = "en";
        loadLanguage("en");
    }

    public static Localization getInstance() {
        if (instance == null) {
            instance = new Localization();
        }
        return instance;
    }

    public void loadLanguage(String langCode) {
        this.currentLanguage = langCode;
        translations.clear();
        String filename = "config/lang/lang_" + langCode + ".properties";
        
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8)) {
            translations.load(reader);
        } catch (IOException e) {
            System.err.println("Could not load language file: " + filename + ". Falling back to English.");
            if (!langCode.equals("en")) {
                loadLanguage("en");
            }
        }
    }

    public static String get(String key) {
        return getInstance().translations.getProperty(key, key);
    }
    
    public static String get(String key, String defaultValue) {
        return getInstance().translations.getProperty(key, defaultValue);
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }
}
