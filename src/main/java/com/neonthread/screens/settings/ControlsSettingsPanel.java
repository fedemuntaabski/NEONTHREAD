package com.neonthread.screens.settings;

import com.neonthread.GameConstants;
import com.neonthread.GameSettings;
import com.neonthread.ui.CyberpunkButton;
import com.neonthread.ui.CyberpunkSlider;
import com.neonthread.ui.CyberpunkToggle;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de configuración de controles siguiendo KISS y DRY.
 */
public class ControlsSettingsPanel extends JPanel {
    
    public ControlsSettingsPanel(GameSettings settings) {
        setLayout(new GridBagLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Key bindings
        addKeyBinding(gbc, "Interact:", settings.getKeyInteract(), 
            key -> settings.setKeyInteract(key));
        addKeyBinding(gbc, "Inventory:", settings.getKeyInventory(), 
            key -> settings.setKeyInventory(key));
        addKeyBinding(gbc, "Map:", settings.getKeyMap(), 
            key -> settings.setKeyMap(key));
        addKeyBinding(gbc, "Advance Dialogue:", settings.getKeyAdvanceDialogue(), 
            key -> settings.setKeyAdvanceDialogue(key));
        
        // Cursor Sensitivity
        CyberpunkSlider sensitivitySlider = new CyberpunkSlider(0, 100, settings.getCursorSensitivity());
        sensitivitySlider.addChangeListener(e -> settings.setCursorSensitivity(sensitivitySlider.getValue()));
        addSlider(gbc, "Cursor Sensitivity:", sensitivitySlider);
        
        // Keyboard Only Mode
        CyberpunkToggle keyboardOnlyToggle = new CyberpunkToggle(settings.isKeyboardOnly(), 
            () -> settings.setKeyboardOnly(!settings.isKeyboardOnly()));
        addToggle(gbc, "Keyboard-Only Mode:", keyboardOnlyToggle);
    }
    
    private void addKeyBinding(GridBagConstraints gbc, String label, String currentKey, 
            java.util.function.Consumer<String> onChange) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(GameConstants.FONT_TEXT);
        labelComp.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        gbc.gridx = 0;
        add(labelComp, gbc);
        
        CyberpunkButton keyButton = new CyberpunkButton("[ " + currentKey + " ]");
        keyButton.addActionListener(e -> {
            String newKey = JOptionPane.showInputDialog(this, "Press new key for " + label, currentKey);
            if (newKey != null && !newKey.trim().isEmpty()) {
                onChange.accept(newKey.trim());
                keyButton.setText("[ " + newKey.trim() + " ]");
            }
        });
        
        gbc.gridx = 1;
        add(keyButton, gbc);
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
        
        JLabel valueLabel = new JLabel(String.valueOf(slider.getValue()));
        valueLabel.setFont(GameConstants.FONT_TEXT);
        valueLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        slider.addChangeListener(e -> valueLabel.setText(String.valueOf(slider.getValue())));
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
        add(labelComp, gbc);
        
        gbc.gridx = 1;
        add(toggle, gbc);
        gbc.gridy++;
    }
}
