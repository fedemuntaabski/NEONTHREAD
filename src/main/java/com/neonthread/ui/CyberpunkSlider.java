package com.neonthread.ui;

import com.neonthread.GameConstants;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;

/**
 * Slider personalizado con estética cyberpunk siguiendo DRY.
 */
public class CyberpunkSlider extends JSlider {
    
    public CyberpunkSlider(int min, int max, int value) {
        super(min, max, value);
        customizeUI();
    }
    
    private void customizeUI() {
        setUI(new CyberpunkSliderUI(this));
        setBackground(GameConstants.COLOR_BACKGROUND);
        setForeground(GameConstants.COLOR_CYAN_NEON);
        setFocusable(false);
    }
    
    private static class CyberpunkSliderUI extends BasicSliderUI {
        
        public CyberpunkSliderUI(JSlider slider) {
            super(slider);
        }
        
        @Override
        public void paintTrack(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Rectangle trackBounds = trackRect;
            int cy = (trackBounds.height / 2) - 2;
            int cw = trackBounds.width;
            
            // Track background
            g2d.setColor(GameConstants.COLOR_PANEL);
            g2d.fillRoundRect(trackBounds.x, trackBounds.y + cy, cw, 4, 2, 2);
            
            // Filled portion
            int filledWidth = (int) ((double) (slider.getValue() - slider.getMinimum()) / 
                                    (slider.getMaximum() - slider.getMinimum()) * cw);
            g2d.setColor(GameConstants.COLOR_CYAN_NEON);
            g2d.fillRoundRect(trackBounds.x, trackBounds.y + cy, filledWidth, 4, 2, 2);
        }
        
        @Override
        public void paintThumb(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Rectangle knobBounds = thumbRect;
            int w = knobBounds.width;
            int h = knobBounds.height;
            
            // Outer glow
            g2d.setColor(new Color(5, 217, 232, 100));
            g2d.fillOval(knobBounds.x - 2, knobBounds.y - 2, w + 4, h + 4);
            
            // Thumb
            g2d.setColor(GameConstants.COLOR_CYAN_NEON);
            g2d.fillOval(knobBounds.x, knobBounds.y, w, h);
            
            // Inner highlight
            g2d.setColor(GameConstants.COLOR_TEXT_PRIMARY);
            g2d.fillOval(knobBounds.x + 3, knobBounds.y + 3, w - 6, h - 6);
        }
    }
}
