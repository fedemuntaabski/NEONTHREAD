package com.neonthread;

import com.neonthread.settings.GameSettings;
import com.neonthread.settings.SettingsListener;
import com.neonthread.settings.appliers.*;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class SettingsApplier implements SettingsListener {
    private final VideoSettingsApplier videoApplier;
    private final AudioSettingsApplier audioApplier;
    private final AccessibilitySettingsApplier accessibilityApplier;
    private final LocalizationSettingsApplier localizationApplier;
    private final ThemeSettingsApplier themeApplier;
    private final JFrame window;

    public SettingsApplier(JFrame window) {
        this.window = window;
        this.videoApplier = new VideoSettingsApplier(window);
        this.audioApplier = new AudioSettingsApplier();
        this.accessibilityApplier = new AccessibilitySettingsApplier();
        this.localizationApplier = new LocalizationSettingsApplier();
        this.themeApplier = new ThemeSettingsApplier();
        
        // Register as listener
        GameSettings.getInstance().addListener(this);
    }

    public void applyAll() {
        GameSettings settings = GameSettings.getInstance();
        
        // Order matters: Localization -> Theme -> Accessibility -> Video -> Audio
        localizationApplier.apply(settings.localization);
        themeApplier.apply(settings.gameplay);
        accessibilityApplier.apply(settings.accessibility);
        videoApplier.apply(settings.video);
        audioApplier.apply(settings.audio);
        
        // Refresh UI to show changes immediately
        SwingUtilities.updateComponentTreeUI(window);
    }

    @Override
    public void onSettingsChanged(GameSettings settings) {
        applyAll();
    }
}
