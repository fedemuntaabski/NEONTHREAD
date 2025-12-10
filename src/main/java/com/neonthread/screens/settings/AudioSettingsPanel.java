package com.neonthread.screens.settings;

import com.neonthread.GameConstants;
import com.neonthread.GameSettings;
import com.neonthread.ui.CyberpunkButton;
import com.neonthread.ui.CyberpunkSlider;
import com.neonthread.ui.CyberpunkToggle;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de configuración de audio siguiendo KISS y DRY.
 */
public class AudioSettingsPanel extends JPanel {
    
    public AudioSettingsPanel(GameSettings settings) {
        setLayout(new GridBagLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Master Volume
        addVolumeSlider(gbc, "Master Volume:", settings.getMasterVolume(), 
            value -> settings.setMasterVolume(value));
        
        // Music Volume
        addVolumeSlider(gbc, "Music Volume:", settings.getMusicVolume(), 
            value -> settings.setMusicVolume(value));
        
        // SFX Volume
        addVolumeSlider(gbc, "SFX Volume:", settings.getSfxVolume(), 
            value -> settings.setSfxVolume(value));
        
        // Voice Volume
        addVolumeSlider(gbc, "Voice Volume:", settings.getVoiceVolume(), 
            value -> settings.setVoiceVolume(value));
        
        // Dynamic Mix
        CyberpunkToggle dynamicMixToggle = new CyberpunkToggle(settings.isDynamicMix(), 
            () -> settings.setDynamicMix(!settings.isDynamicMix()));
        addToggle(gbc, "Dynamic Mix:", dynamicMixToggle);
        
        // Test Sound Button
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        CyberpunkButton testButton = new CyberpunkButton("TEST SOUND");
        testButton.addActionListener(e -> Toolkit.getDefaultToolkit().beep());
        add(testButton, gbc);
    }
    
    private void addVolumeSlider(GridBagConstraints gbc, String label, int value, 
            java.util.function.IntConsumer onChange) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(GameConstants.FONT_TEXT);
        labelComp.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        add(labelComp, gbc);
        
        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        
        CyberpunkSlider slider = new CyberpunkSlider(0, 100, value);
        slider.addChangeListener(e -> onChange.accept(slider.getValue()));
        sliderPanel.add(slider, BorderLayout.CENTER);
        
        JLabel valueLabel = new JLabel(value + "%");
        valueLabel.setFont(GameConstants.FONT_TEXT);
        valueLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        slider.addChangeListener(e -> valueLabel.setText(slider.getValue() + "%"));
        sliderPanel.add(valueLabel, BorderLayout.EAST);
        
        gbc.gridx = 1;
        add(sliderPanel, gbc);
        gbc.gridy++;
    }
    
    private void addToggle(GridBagConstraints gbc, String label, CyberpunkToggle toggle) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(GameConstants.FONT_TEXT);
        labelComp.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        add(labelComp, gbc);
        
        gbc.gridx = 1;
        add(toggle, gbc);
        gbc.gridy++;
    }
}
