package com.neonthread.ui;

import com.neonthread.GameConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Toggle switch personalizado con estética cyberpunk siguiendo DRY.
 */
public class CyberpunkToggle extends JPanel {
    private boolean enabled = false;
    private final Runnable onChange;
    private Color currentColor;
    
    public CyberpunkToggle(boolean initialValue, Runnable onChange) {
        this.enabled = initialValue;
        this.onChange = onChange;
        this.currentColor = enabled ? GameConstants.COLOR_CYAN_NEON : GameConstants.COLOR_PANEL;
        
        setPreferredSize(new Dimension(50, 25));
        setBackground(GameConstants.COLOR_BACKGROUND);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggle();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });
    }
    
    public void toggle() {
        enabled = !enabled;
        currentColor = enabled ? GameConstants.COLOR_CYAN_NEON : GameConstants.COLOR_PANEL;
        if (onChange != null) {
            onChange.run();
        }
        repaint();
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.currentColor = enabled ? GameConstants.COLOR_CYAN_NEON : GameConstants.COLOR_PANEL;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Track
        g2d.setColor(currentColor);
        g2d.fillRoundRect(0, 0, width, height, height, height);
        
        // Border
        g2d.setColor(GameConstants.COLOR_CYAN_NEON);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(1, 1, width - 2, height - 2, height, height);
        
        // Thumb
        int thumbSize = height - 6;
        int thumbX = enabled ? width - thumbSize - 3 : 3;
        g2d.setColor(GameConstants.COLOR_TEXT_PRIMARY);
        g2d.fillOval(thumbX, 3, thumbSize, thumbSize);
    }
}
