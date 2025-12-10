package com.neonthread;

/**
 * Modelo de datos para configuraciones del juego siguiendo DRY.
 * Singleton pattern para acceso global.
 */
public class GameSettings {
    private static GameSettings instance;
    
    // Video settings
    private String resolution = "1024x768";
    private String windowMode = "Windowed"; // Windowed, Borderless, Fullscreen
    private boolean vsync = true;
    private int brightness = GameConstants.DEFAULT_BRIGHTNESS;
    private int uiScale = GameConstants.DEFAULT_UI_SCALE;
    
    // Audio settings
    private int masterVolume = GameConstants.DEFAULT_MASTER_VOLUME;
    private int musicVolume = GameConstants.DEFAULT_MUSIC_VOLUME;
    private int sfxVolume = GameConstants.DEFAULT_SFX_VOLUME;
    private int voiceVolume = GameConstants.DEFAULT_VOICE_VOLUME;
    private boolean dynamicMix = true;
    
    // Gameplay settings
    private int textSpeed = GameConstants.DEFAULT_TEXT_SPEED; // 0=slow, 1=normal, 2=fast
    private boolean autoAdvance = false;
    private boolean showConfirmedChoices = true;
    private String difficulty = "Balanced"; // Story, Balanced, Hardcore
    private boolean permadeath = false;
    private int glitchIntensity = GameConstants.DEFAULT_GLITCH_INTENSITY;
    
    // Controls settings
    private String keyInteract = "E";
    private String keyInventory = "I";
    private String keyMap = "M";
    private String keyAdvanceDialogue = "Space";
    private int cursorSensitivity = 50;
    private boolean keyboardOnly = false;
    
    // Accessibility settings
    private boolean highContrast = false;
    private int fontSize = 100; // percentage
    private boolean disableGlitchEffects = false;
    private boolean wideSubtitles = false;
    private boolean textGuideLines = false;
    
    private GameSettings() {}
    
    public static GameSettings getInstance() {
        if (instance == null) {
            instance = new GameSettings();
        }
        return instance;
    }
    
    public void resetToDefaults() {
        // Video
        resolution = "1024x768";
        windowMode = "Windowed";
        vsync = true;
        brightness = GameConstants.DEFAULT_BRIGHTNESS;
        uiScale = GameConstants.DEFAULT_UI_SCALE;
        
        // Audio
        masterVolume = GameConstants.DEFAULT_MASTER_VOLUME;
        musicVolume = GameConstants.DEFAULT_MUSIC_VOLUME;
        sfxVolume = GameConstants.DEFAULT_SFX_VOLUME;
        voiceVolume = GameConstants.DEFAULT_VOICE_VOLUME;
        dynamicMix = true;
        
        // Gameplay
        textSpeed = GameConstants.DEFAULT_TEXT_SPEED;
        autoAdvance = false;
        showConfirmedChoices = true;
        difficulty = "Balanced";
        permadeath = false;
        glitchIntensity = GameConstants.DEFAULT_GLITCH_INTENSITY;
        
        // Controls
        keyInteract = "E";
        keyInventory = "I";
        keyMap = "M";
        keyAdvanceDialogue = "Space";
        cursorSensitivity = 50;
        keyboardOnly = false;
        
        // Accessibility
        highContrast = false;
        fontSize = 100;
        disableGlitchEffects = false;
        wideSubtitles = false;
        textGuideLines = false;
    }
    
    // Getters and Setters
    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }
    
    public String getWindowMode() { return windowMode; }
    public void setWindowMode(String windowMode) { this.windowMode = windowMode; }
    
    public boolean isVsync() { return vsync; }
    public void setVsync(boolean vsync) { this.vsync = vsync; }
    
    public int getBrightness() { return brightness; }
    public void setBrightness(int brightness) { this.brightness = brightness; }
    
    public int getUiScale() { return uiScale; }
    public void setUiScale(int uiScale) { this.uiScale = uiScale; }
    
    public int getMasterVolume() { return masterVolume; }
    public void setMasterVolume(int masterVolume) { this.masterVolume = masterVolume; }
    
    public int getMusicVolume() { return musicVolume; }
    public void setMusicVolume(int musicVolume) { this.musicVolume = musicVolume; }
    
    public int getSfxVolume() { return sfxVolume; }
    public void setSfxVolume(int sfxVolume) { this.sfxVolume = sfxVolume; }
    
    public int getVoiceVolume() { return voiceVolume; }
    public void setVoiceVolume(int voiceVolume) { this.voiceVolume = voiceVolume; }
    
    public boolean isDynamicMix() { return dynamicMix; }
    public void setDynamicMix(boolean dynamicMix) { this.dynamicMix = dynamicMix; }
    
    public int getTextSpeed() { return textSpeed; }
    public void setTextSpeed(int textSpeed) { this.textSpeed = textSpeed; }
    
    public boolean isAutoAdvance() { return autoAdvance; }
    public void setAutoAdvance(boolean autoAdvance) { this.autoAdvance = autoAdvance; }
    
    public boolean isShowConfirmedChoices() { return showConfirmedChoices; }
    public void setShowConfirmedChoices(boolean showConfirmedChoices) { this.showConfirmedChoices = showConfirmedChoices; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public boolean isPermadeath() { return permadeath; }
    public void setPermadeath(boolean permadeath) { this.permadeath = permadeath; }
    
    public int getGlitchIntensity() { return glitchIntensity; }
    public void setGlitchIntensity(int glitchIntensity) { this.glitchIntensity = glitchIntensity; }
    
    public String getKeyInteract() { return keyInteract; }
    public void setKeyInteract(String keyInteract) { this.keyInteract = keyInteract; }
    
    public String getKeyInventory() { return keyInventory; }
    public void setKeyInventory(String keyInventory) { this.keyInventory = keyInventory; }
    
    public String getKeyMap() { return keyMap; }
    public void setKeyMap(String keyMap) { this.keyMap = keyMap; }
    
    public String getKeyAdvanceDialogue() { return keyAdvanceDialogue; }
    public void setKeyAdvanceDialogue(String keyAdvanceDialogue) { this.keyAdvanceDialogue = keyAdvanceDialogue; }
    
    public int getCursorSensitivity() { return cursorSensitivity; }
    public void setCursorSensitivity(int cursorSensitivity) { this.cursorSensitivity = cursorSensitivity; }
    
    public boolean isKeyboardOnly() { return keyboardOnly; }
    public void setKeyboardOnly(boolean keyboardOnly) { this.keyboardOnly = keyboardOnly; }
    
    public boolean isHighContrast() { return highContrast; }
    public void setHighContrast(boolean highContrast) { this.highContrast = highContrast; }
    
    public int getFontSize() { return fontSize; }
    public void setFontSize(int fontSize) { this.fontSize = fontSize; }
    
    public boolean isDisableGlitchEffects() { return disableGlitchEffects; }
    public void setDisableGlitchEffects(boolean disableGlitchEffects) { this.disableGlitchEffects = disableGlitchEffects; }
    
    public boolean isWideSubtitles() { return wideSubtitles; }
    public void setWideSubtitles(boolean wideSubtitles) { this.wideSubtitles = wideSubtitles; }
    
    public boolean isTextGuideLines() { return textGuideLines; }
    public void setTextGuideLines(boolean textGuideLines) { this.textGuideLines = textGuideLines; }
}
