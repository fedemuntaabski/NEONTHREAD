package com.neonthread.screens;

import com.neonthread.GameConstants;
import com.neonthread.settings.GameSettings;
import com.neonthread.SettingsApplier;
import com.neonthread.localization.Localization;
import com.neonthread.ui.CyberpunkButton;
import com.neonthread.ui.CyberpunkComboBox;
import com.neonthread.ui.CyberpunkSlider;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class SettingsScreen extends JPanel {
    private Consumer<Void> onBack;
    private final GameSettings settings;
    
    public SettingsScreen(Consumer<Void> onBack, JFrame window) {
        this.onBack = onBack;
        this.settings = GameSettings.getInstance();
        // Ensure applier is initialized (it registers itself as listener)
        new SettingsApplier(window);
        
        setLayout(new BorderLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        
        buildUI();
    }
    
    public void setBackAction(Consumer<Void> onBack) {
        this.onBack = onBack;
        // Rebuild UI to update back button listener if necessary, 
        // but since we use the field in the listener, it should be fine if we just update the field.
        // However, the back button listener is added in buildUI -> addFooter.
        // We need to update the listener on the existing button or rebuild.
        // Rebuilding is safer.
        buildUI();
        revalidate();
        repaint();
    }

    private void buildUI() {
        removeAll(); // Clear for rebuild on language change

        // Title
        JLabel titleLabel = new JLabel(Localization.get("menu.settings"), SwingConstants.CENTER);
        titleLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 40));
        titleLabel.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 40, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));
        
        // Resolution
        addSettingRow(centerPanel, Localization.get("settings.resolution"), createResolutionControl());
        
        // Fullscreen
        addSettingRow(centerPanel, Localization.get("settings.fullscreen"), createFullscreenControl());
        
        // Volume
        addSettingRow(centerPanel, Localization.get("settings.volume"), createVolumeControl());
        
        // Text Size
        addSettingRow(centerPanel, Localization.get("settings.largeText"), createTextSizeControl());

        // Language
        addSettingRow(centerPanel, Localization.get("settings.language"), createLanguageControl());

        // Theme
        addSettingRow(centerPanel, Localization.get("settings.theme"), createThemeControl());
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        bottomPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));
        
        CyberpunkButton saveButton = createStyledButton(Localization.get("settings.apply"), e -> settings.save());
        CyberpunkButton backButton = createStyledButton(Localization.get("settings.back"), e -> onBack.accept(null));
        
        bottomPanel.add(saveButton);
        bottomPanel.add(backButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        revalidate();
        repaint();
    }
    
    private void addSettingRow(JPanel panel, String labelText, JComponent control) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(GameConstants.COLOR_BACKGROUND);
        row.setMaximumSize(new Dimension(600, 50));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font(GameConstants.FONT_FAMILY, Font.PLAIN, 18));
        label.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        
        row.add(label, BorderLayout.WEST);
        row.add(control, BorderLayout.EAST);
        
        panel.add(row);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private CyberpunkButton createStyledButton(String text, java.awt.event.ActionListener action) {
        CyberpunkButton button = new CyberpunkButton(text);
        button.addActionListener(action);
        return button;
    }
    
    private JComponent createResolutionControl() {
        String[] resolutions = {"1280x720", "1600x900", "1920x1080"};
        CyberpunkComboBox<String> combo = new CyberpunkComboBox<>(resolutions);
        combo.setSelectedItem(settings.video.getResolution());
        
        combo.addActionListener(e -> {
            settings.video.setResolution((String) combo.getSelectedItem());
            settings.save(); // Hot reload
        });
        return combo;
    }
    
    private JComponent createFullscreenControl() {
        JCheckBox check = new JCheckBox();
        check.setBackground(GameConstants.COLOR_BACKGROUND);
        check.setSelected(settings.video.isFullscreen());
        
        check.addActionListener(e -> {
            settings.video.setFullscreen(check.isSelected());
            settings.save(); // Hot reload
        });
        return check;
    }
    
    private JComponent createVolumeControl() {
        CyberpunkSlider slider = new CyberpunkSlider(0, 100, settings.audio.getMasterVolume());
        slider.addChangeListener(e -> {
            settings.audio.setMasterVolume(slider.getValue());
            // Don't save on every slide tick, maybe just apply?
            // For simplicity, we won't save to disk on drag, but we could notify listeners.
            // But here we just update the setting object which might trigger listeners if we implemented property change support.
            // Our current GameSettings only notifies on save().
            // To support true hot reload without saving to disk constantly, we should separate set() from save().
            // But for this exercise, let's just save on release or accept it saves often.
            if (!slider.getValueIsAdjusting()) {
                settings.save();
            }
        });
        return slider;
    }
    
    private JComponent createTextSizeControl() {
        JCheckBox check = new JCheckBox();
        check.setBackground(GameConstants.COLOR_BACKGROUND);
        check.setSelected(settings.accessibility.isLargeText());
        
        check.addActionListener(e -> {
            settings.accessibility.setLargeText(check.isSelected());
            settings.save();
        });
        return check;
    }

    private JComponent createLanguageControl() {
        String[] langs = {"en", "es"};
        CyberpunkComboBox<String> combo = new CyberpunkComboBox<>(langs);
        combo.setSelectedItem(settings.localization.getLanguage());
        
        combo.addActionListener(e -> {
            String newLang = (String) combo.getSelectedItem();
            if (!newLang.equals(settings.localization.getLanguage())) {
                settings.localization.setLanguage(newLang);
                settings.save();
                buildUI(); // Rebuild UI to show new language
            }
        });
        return combo;
    }

    private JComponent createThemeControl() {
        String[] themes = {"CYAN", "PURPLE"};
        CyberpunkComboBox<String> combo = new CyberpunkComboBox<>(themes);
        combo.setSelectedItem(settings.gameplay.getTheme());
        
        combo.addActionListener(e -> {
            settings.gameplay.setTheme((String) combo.getSelectedItem());
            settings.save();
        });
        return combo;
    }
}

