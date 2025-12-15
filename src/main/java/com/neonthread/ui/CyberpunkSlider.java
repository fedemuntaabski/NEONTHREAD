package com.neonthread.ui;

import com.neonthread.GameConstants;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;

/**
 * Slider personalizado con estética cyberpunk.
 * UI personalizado usando Strategy pattern con BasicSliderUI.
 */
public class CyberpunkSlider extends JSlider {
    
    public CyberpunkSlider(int min, int max, int value) {
        super(min, max, value);
        setUI(new CyberpunkSliderUI(this));
        setBackground(GameConstants.COLOR_BACKGROUND);
        setForeground(GameConstants.COLOR_CYAN_NEON);
        setFocusable(false);
        setOpaque(false);
    }
    
    /**
     * UI personalizado para el slider con renderizado cyberpunk.
     */
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
            
            // Track de fondo
            g2d.setColor(GameConstants.COLOR_PANEL);
            g2d.fillRoundRect(trackBounds.x, trackBounds.y + cy, cw, 4, 2, 2);
            
            // Porción llenada
            int filledWidth = calculateFilledWidth(cw);
            g2d.setColor(GameConstants.COLOR_CYAN_NEON);
            g2d.fillRoundRect(trackBounds.x, trackBounds.y + cy, filledWidth, 4, 2, 2);
        }
        
        @Override
        public void paintThumb(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Rectangle knobBounds = thumbRect;
            paintThumbGlow(g2d, knobBounds);
            paintThumbBody(g2d, knobBounds);
            paintThumbHighlight(g2d, knobBounds);
        }
        
        private int calculateFilledWidth(int totalWidth) {
            double ratio = (double) (slider.getValue() - slider.getMinimum()) / 
                          (slider.getMaximum() - slider.getMinimum());
            return (int) (ratio * totalWidth);
        }
        
        private void paintThumbGlow(Graphics2D g2d, Rectangle bounds) {
            g2d.setColor(new Color(5, 217, 232, 100));
            g2d.fillOval(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4);
        }
        
        private void paintThumbBody(Graphics2D g2d, Rectangle bounds) {
            g2d.setColor(GameConstants.COLOR_CYAN_NEON);
            g2d.fillOval(bounds.x, bounds.y, bounds.width, bounds.height);
        }
        
        private void paintThumbHighlight(Graphics2D g2d, Rectangle bounds) {
            g2d.setColor(GameConstants.COLOR_TEXT_PRIMARY);
            g2d.fillOval(bounds.x + 3, bounds.y + 3, bounds.width - 6, bounds.height - 6);
        }
    }
}
