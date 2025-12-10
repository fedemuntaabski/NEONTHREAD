package com.neonthread.screens.settings;

import com.neonthread.GameConstants;
import com.neonthread.GameSettings;
import com.neonthread.ui.CyberpunkComboBox;
import com.neonthread.ui.CyberpunkToggle;

import javax.swing.*;
import java.awt.*;

/**
 * Panel de configuración de accesibilidad siguiendo KISS y DRY.
 */
public class AccessibilitySettingsPanel extends JPanel {
    
    public AccessibilitySettingsPanel(GameSettings settings) {
        setLayout(new GridBagLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // High Contrast Mode
        CyberpunkToggle highContrastToggle = new CyberpunkToggle(settings.isHighContrast(), 
            () -> settings.setHighContrast(!settings.isHighContrast()));
        addToggle(gbc, "High Contrast Mode:", highContrastToggle);
        
        // Font Size
        CyberpunkComboBox<String> fontSizeCombo = new CyberpunkComboBox<>(
            new String[]{"100%", "120%", "150%"});
        int index = settings.getFontSize() == 100 ? 0 : (settings.getFontSize() == 120 ? 1 : 2);
        fontSizeCombo.setSelectedIndex(index);
        fontSizeCombo.addActionListener(e -> {
            String selected = (String)fontSizeCombo.getSelectedItem();
            int size = Integer.parseInt(selected.replace("%", ""));
            settings.setFontSize(size);
        });
        addSetting(gbc, "Font Size:", fontSizeCombo);
        
        // Disable Glitch Effects
        CyberpunkToggle disableGlitchToggle = new CyberpunkToggle(settings.isDisableGlitchEffects(), 
            () -> settings.setDisableGlitchEffects(!settings.isDisableGlitchEffects()));
        addToggle(gbc, "Disable Glitch Effects:", disableGlitchToggle);
        
        // Wide Subtitles
        CyberpunkToggle wideSubsToggle = new CyberpunkToggle(settings.isWideSubtitles(), 
            () -> settings.setWideSubtitles(!settings.isWideSubtitles()));
        addToggle(gbc, "Wide Subtitles:", wideSubsToggle);
        
        // Text Guide Lines
        CyberpunkToggle guideLinesToggle = new CyberpunkToggle(settings.isTextGuideLines(), 
            () -> settings.setTextGuideLines(!settings.isTextGuideLines()));
        addToggle(gbc, "Text Guide Lines:", guideLinesToggle);
        
        // Info text
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 10, 20);
        JTextArea infoText = new JTextArea(
            "Accessibility features help make the game more comfortable for all players. " +
            "High contrast mode uses enhanced colors and borders for better visibility.");
        infoText.setFont(new Font(GameConstants.FONT_FAMILY, Font.ITALIC, 12));
        infoText.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        infoText.setBackground(GameConstants.COLOR_BACKGROUND);
        infoText.setLineWrap(true);
        infoText.setWrapStyleWord(true);
        infoText.setEditable(false);
        add(infoText, gbc);
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
    
    private void addSetting(GridBagConstraints gbc, String label, Component component) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(GameConstants.FONT_TEXT);
        labelComp.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        add(labelComp, gbc);
        
        gbc.gridx = 1;
        add(component, gbc);
        gbc.gridy++;
    }
}
