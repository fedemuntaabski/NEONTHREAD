package com.neonthread.settings;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GameSettings {
    private static GameSettings instance;
    private static final String SETTINGS_FILE = "config/settings.properties";
    private static final int CURRENT_VERSION = 1;

    public final VideoSettings video = new VideoSettings();
    public final AudioSettings audio = new AudioSettings();
    public final AccessibilitySettings accessibility = new AccessibilitySettings();
    public final LocalizationSettings localization = new LocalizationSettings();
    public final GameplaySettings gameplay = new GameplaySettings();

    private final List<SettingsListener> listeners = new ArrayList<>();

    private GameSettings() {
        load();
    }

    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }

    public void load() {
        Properties props = new Properties();
        File file = new File(SETTINGS_FILE);
        
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                props.load(in);
                
                // Version check and migration could go here
                int version = Integer.parseInt(props.getProperty("version", "0"));
                if (version < CURRENT_VERSION) {
                    migrate(props, version);
                }

                video.load(props);
                audio.load(props);
                accessibility.load(props);
                localization.load(props);
                gameplay.load(props);
                
            } catch (IOException e) {
                System.err.println("Error loading settings: " + e.getMessage());
            }
        }
    }

    private void migrate(Properties props, int oldVersion) {
        System.out.println("Migrating settings from version " + oldVersion + " to " + CURRENT_VERSION);
        // Migration logic would go here
    }

    public void save() {
        Properties props = new Properties();
        props.setProperty("version", String.valueOf(CURRENT_VERSION));
        
        video.save(props);
        audio.save(props);
        accessibility.save(props);
        localization.save(props);
        gameplay.save(props);

        try (FileOutputStream out = new FileOutputStream(SETTINGS_FILE)) {
            props.store(out, "Game Configuration");
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
        
        notifyListeners();
    }

    public void addListener(SettingsListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SettingsListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (SettingsListener listener : listeners) {
            listener.onSettingsChanged(this);
        }
    }
}
