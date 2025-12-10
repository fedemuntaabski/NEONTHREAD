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
    private Color backgroundColor;
    
    public CyberpunkButton(String text) {
        super(text);
        this.backgroundColor = GameConstants.COLOR_PANEL;
        customizeUI();
    }
    
    public CyberpunkButton(String text, Color backgroundColor) {
        super(text);
        this.backgroundColor = backgroundColor;
        customizeUI();
    }
    
    private void customizeUI() {
        setFont(GameConstants.FONT_MENU);
        setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        setBackground(this.backgroundColor);
        setBorder(new LineBorder(GameConstants.COLOR_CYAN_NEON, 2));
        setFocusPainted(false);
        setContentAreaFilled(true);
        setOpaque(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // No hover effect
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // No hover effect
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                // No press effect
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                // No release effect
            }
        });
    }
}
