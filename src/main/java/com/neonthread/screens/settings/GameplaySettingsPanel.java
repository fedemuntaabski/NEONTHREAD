package com.neonthread.screens.settings;

import com.neonthread.GameConstants;
import com.neonthread.GameSettings;
import com.neonthread.ui.CyberpunkComboBox;
import com.neonthread.ui.CyberpunkSlider;
import com.neonthread.ui.CyberpunkToggle;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de configuración de gameplay siguiendo KISS y DRY.
 */
public class GameplaySettingsPanel extends JPanel {
    
    public GameplaySettingsPanel(GameSettings settings) {
        setLayout(new GridBagLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Text Speed
        CyberpunkComboBox<String> textSpeedCombo = new CyberpunkComboBox<>(
            new String[]{"Slow", "Normal", "Fast"});
        textSpeedCombo.setSelectedIndex(settings.getTextSpeed());
        textSpeedCombo.addActionListener(e -> settings.setTextSpeed(textSpeedCombo.getSelectedIndex()));
        addSetting(gbc, "Text Speed:", textSpeedCombo);
        
        // Auto-Advance
        CyberpunkToggle autoAdvanceToggle = new CyberpunkToggle(settings.isAutoAdvance(), 
            () -> settings.setAutoAdvance(!settings.isAutoAdvance()));
        addSetting(gbc, "Auto-Advance Dialogues:", autoAdvanceToggle);
        
        // Show Confirmed Choices
        CyberpunkToggle choicesToggle = new CyberpunkToggle(settings.isShowConfirmedChoices(), 
            () -> settings.setShowConfirmedChoices(!settings.isShowConfirmedChoices()));
        addSetting(gbc, "Show Confirmed Choices:", choicesToggle);
        
        // Difficulty
        CyberpunkComboBox<String> difficultyCombo = new CyberpunkComboBox<>(
            new String[]{"Story", "Balanced", "Hardcore"});
        difficultyCombo.setSelectedItem(settings.getDifficulty());
        difficultyCombo.addActionListener(e -> 
            settings.setDifficulty((String)difficultyCombo.getSelectedItem()));
        addSetting(gbc, "Narrative Difficulty:", difficultyCombo);
        
        // Permadeath
        CyberpunkToggle permaToggle = new CyberpunkToggle(settings.isPermadeath(), 
            () -> settings.setPermadeath(!settings.isPermadeath()));
        addSetting(gbc, "Permadeath Mode:", permaToggle);
        
        // Glitch Intensity
        CyberpunkSlider glitchSlider = new CyberpunkSlider(0, 100, settings.getGlitchIntensity());
        glitchSlider.addChangeListener(e -> settings.setGlitchIntensity(glitchSlider.getValue()));
        addSlider(gbc, "Glitch Effect Intensity:", glitchSlider);
    }
    
    private void addSetting(GridBagConstraints gbc, String label, Component component) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(GameConstants.FONT_TEXT);
        labelComp.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        gbc.gridx = 0;
        add(labelComp, gbc);
        
        gbc.gridx = 1;
        add(component, gbc);
        gbc.gridy++;
    }
    
    private void addSlider(GridBagConstraints gbc, String label, CyberpunkSlider slider) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(GameConstants.FONT_TEXT);
        labelComp.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        gbc.gridx = 0;
        add(labelComp, gbc);
        
        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        sliderPanel.add(slider, BorderLayout.CENTER);
        
        JLabel valueLabel = new JLabel(slider.getValue() + "%");
        valueLabel.setFont(GameConstants.FONT_TEXT);
        valueLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        slider.addChangeListener(e -> valueLabel.setText(slider.getValue() + "%"));
        sliderPanel.add(valueLabel, BorderLayout.EAST);
        
        gbc.gridx = 1;
        add(sliderPanel, gbc);
        gbc.gridy++;
    }
}
