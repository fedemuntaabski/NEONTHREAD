package com.neonthread.screens.settings;

import com.neonthread.GameConstants;
import com.neonthread.GameSettings;
import com.neonthread.ui.CyberpunkComboBox;
import com.neonthread.ui.CyberpunkSlider;
import com.neonthread.ui.CyberpunkToggle;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de configuración de video siguiendo KISS y DRY.
 */
public class VideoSettingsPanel extends JPanel {
    
    public VideoSettingsPanel(GameSettings settings) {
        setLayout(new GridBagLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Resolution
        CyberpunkComboBox<String> resolutionCombo = new CyberpunkComboBox<>(
            new String[]{"1024x768", "1280x720", "1920x1080", "2560x1440"});
        resolutionCombo.setSelectedItem(settings.getResolution());
        resolutionCombo.addActionListener(e -> 
            settings.setResolution((String)resolutionCombo.getSelectedItem()));
        addSetting(gbc, "Resolution:", resolutionCombo, null);
        
        // Window Mode
        CyberpunkComboBox<String> windowModeCombo = new CyberpunkComboBox<>(
            new String[]{"Windowed", "Borderless", "Fullscreen"});
        windowModeCombo.setSelectedItem(settings.getWindowMode());
        windowModeCombo.addActionListener(e -> 
            settings.setWindowMode((String)windowModeCombo.getSelectedItem()));
        addSetting(gbc, "Window Mode:", windowModeCombo, null);
        
        // VSync
        CyberpunkToggle vsyncToggle = new CyberpunkToggle(settings.isVsync(), 
            () -> settings.setVsync(!settings.isVsync()));
        addSetting(gbc, "VSync:", vsyncToggle, null);
        
        // Brightness
        CyberpunkSlider brightnessSlider = new CyberpunkSlider(0, 100, settings.getBrightness());
        brightnessSlider.addChangeListener(e -> settings.setBrightness(brightnessSlider.getValue()));
        addSettingWithSlider(gbc, "Brightness:", brightnessSlider);
        
        // UI Scale
        CyberpunkSlider uiScaleSlider = new CyberpunkSlider(80, 200, settings.getUiScale());
        uiScaleSlider.addChangeListener(e -> settings.setUiScale(uiScaleSlider.getValue()));
        addSettingWithSlider(gbc, "UI Scale:", uiScaleSlider);
        
        // Brightness preview
        gbc.gridy++;
        JPanel previewPanel = new JPanel();
        previewPanel.setPreferredSize(new Dimension(200, 50));
        previewPanel.setBackground(new Color(settings.getBrightness() * 255 / 100, 
                                            settings.getBrightness() * 255 / 100, 
                                            settings.getBrightness() * 255 / 100));
        brightnessSlider.addChangeListener(e -> {
            int brightness = brightnessSlider.getValue();
            previewPanel.setBackground(new Color(brightness * 255 / 100, brightness * 255 / 100, brightness * 255 / 100));
        });
        
        JLabel previewLabel = new JLabel("Brightness Preview:");
        previewLabel.setFont(GameConstants.FONT_TEXT);
        previewLabel.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        gbc.gridx = 0;
        add(previewLabel, gbc);
        
        gbc.gridx = 1;
        add(previewPanel, gbc);
    }
    
    private void addSetting(GridBagConstraints gbc, String label, Component component, Object value) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(GameConstants.FONT_TEXT);
        labelComp.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        gbc.gridx = 0;
        add(labelComp, gbc);
        
        gbc.gridx = 1;
        add(component, gbc);
        gbc.gridy++;
    }
    
    private void addSettingWithSlider(GridBagConstraints gbc, String label, CyberpunkSlider slider) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(GameConstants.FONT_TEXT);
        labelComp.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        gbc.gridx = 0;
        add(labelComp, gbc);
        
        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        sliderPanel.add(slider, BorderLayout.CENTER);
        
        JLabel valueLabel = new JLabel(String.valueOf(slider.getValue()));
        valueLabel.setFont(GameConstants.FONT_TEXT);
        valueLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        slider.addChangeListener(e -> valueLabel.setText(String.valueOf(slider.getValue())));
        sliderPanel.add(valueLabel, BorderLayout.EAST);
        
        gbc.gridx = 1;
        add(sliderPanel, gbc);
        gbc.gridy++;
    }
}
