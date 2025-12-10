package com.neonthread.ui;

import com.neonthread.GameConstants;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ComboBox/Dropdown personalizado con estética cyberpunk siguiendo DRY.
 */
public class CyberpunkComboBox<T> extends JComboBox<T> {
    
    public CyberpunkComboBox(T[] items) {
        super(items);
        customizeUI();
    }
    
    private void customizeUI() {
        setBackground(GameConstants.COLOR_BACKGROUND);
        setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        setFont(GameConstants.FONT_TEXT);
        setBorder(new LineBorder(GameConstants.COLOR_CYAN_NEON, 2));
        setFocusable(false);
        
        // Renderer personalizado
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                
                label.setBackground(isSelected ? GameConstants.COLOR_PANEL : GameConstants.COLOR_BACKGROUND);
                label.setForeground(isSelected ? GameConstants.COLOR_CYAN_NEON : GameConstants.COLOR_TEXT_PRIMARY);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
                return label;
            }
        });
        
        // Hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(new LineBorder(GameConstants.COLOR_BUTTON_HOVER, 2));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(new LineBorder(GameConstants.COLOR_CYAN_NEON, 2));
            }
        });
    }
}
