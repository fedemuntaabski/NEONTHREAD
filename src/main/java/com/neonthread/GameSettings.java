package com.neonthread;

import java.io.*;

/**
 * Configuración simple del juego (KISS).
 * Solo las opciones esenciales: resolución, pantalla completa, volumen y tamaño de texto.
 */
public class GameSettings {
    private static GameSettings instance;
    private static final String SETTINGS_FILE = "settings.txt";
    
    // Configuración mínima
    private String resolution = "1600x900";
    private boolean fullscreen = false;
    private int masterVolume = 70; // 0-100
    private boolean largeText = false; // false = Normal, true = Grande

    // Extensibilidad (KISS): localización y tema
    private String language = "en"; // en|es
    private String theme = "CYAN"; // CYAN|PURPLE
    
    private GameSettings() {
        loadSettings();
    }
    
    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }
    
    public void resetToDefaults() {
        resolution = "1600x900";
        fullscreen = false;
        masterVolume = 70;
        largeText = false;

        language = "en";
        theme = "CYAN";
    }
    
    // Getters y Setters
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    
    public boolean isFullscreen() { return fullscreen; }
    public void setFullscreen(boolean fullscreen) { this.fullscreen = fullscreen; }
    
    public int getMasterVolume() { return masterVolume; }
    public void setMasterVolume(int masterVolume) { 
        this.masterVolume = Math.max(0, Math.min(100, masterVolume)); 
    }
    
    public boolean isLargeText() { return largeText; }
    public void setLargeText(boolean largeText) { this.largeText = largeText; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) {
        if (language == null) return;
        String normalized = language.trim().toLowerCase();
        this.language = normalized.equals("es") ? "es" : "en";
    }

    public String getTheme() { return theme; }
    public void setTheme(String theme) {
        if (theme == null) return;
        String normalized = theme.trim().toUpperCase();
        this.theme = normalized.equals("PURPLE") ? "PURPLE" : "CYAN";
    }
    
    // Persistencia simple (KISS)
    public void saveSettings() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SETTINGS_FILE))) {
            writer.println("resolution=" + resolution);
            writer.println("fullscreen=" + fullscreen);
            writer.println("masterVolume=" + masterVolume);
            writer.println("largeText=" + largeText);
            writer.println("language=" + language);
            writer.println("theme=" + theme);
        } catch (IOException e) {
            System.err.println("Error al guardar configuración: " + e.getMessage());
        }
    }
    
    private void loadSettings() {
        File file = new File(SETTINGS_FILE);
        if (!file.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length != 2) continue;
                
                String key = parts[0].trim();
                String value = parts[1].trim();
                
                switch (key) {
                    case "resolution":
                        resolution = value;
                        break;
                    case "fullscreen":
                        fullscreen = Boolean.parseBoolean(value);
                        break;
                    case "masterVolume":
                        masterVolume = Integer.parseInt(value);
                        break;
                    case "largeText":
                        largeText = Boolean.parseBoolean(value);
                        break;
                    case "language":
                        setLanguage(value);
                        break;
                    case "theme":
                        setTheme(value);
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar configuración: " + e.getMessage());
        }
    }
}
