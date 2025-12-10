package com.neonthread.ui;

import com.neonthread.GameConstants;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Botón personalizado con estética cyberpunk y efectos hover siguiendo DRY.
 */
public class CyberpunkButton extends JButton {
    private boolean isHovered = false;
    
    public CyberpunkButton(String text) {
        super(text);
        customizeUI();
    }
    
    private void customizeUI() {
        setFont(GameConstants.FONT_MENU);
        setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        setBackground(GameConstants.COLOR_PANEL);
        setBorder(new LineBorder(GameConstants.COLOR_CYAN_NEON, 2));
        setFocusPainted(false);
        setContentAreaFilled(true);
        setOpaque(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                setForeground(GameConstants.COLOR_BUTTON_HOVER);
                setBorder(new LineBorder(GameConstants.COLOR_BUTTON_HOVER, 3));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                setForeground(GameConstants.COLOR_TEXT_PRIMARY);
                setBorder(new LineBorder(GameConstants.COLOR_CYAN_NEON, 2));
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                setForeground(GameConstants.COLOR_BUTTON_ACTIVE);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isHovered) {
                    setForeground(GameConstants.COLOR_BUTTON_HOVER);
                } else {
                    setForeground(GameConstants.COLOR_TEXT_PRIMARY);
                }
            }
        });
    }
}
