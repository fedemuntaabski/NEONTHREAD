package com.neonthread.ui;

import com.neonthread.GameConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Componente reutilizable que representa una card/modal de misión.
 * Encapsula el contenedor principal con estilos cyberpunk consistentes.
 */
public class MissionCard extends JPanel {
    private final JPanel contentPanel;
    
    private MissionCard(Builder builder) {
        setLayout(new BorderLayout());
        setBackground(builder.overlayColor);
        
        // Panel principal tipo modal
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(builder.cardBackground);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(builder.borderColor, builder.borderWidth),
            BorderFactory.createEmptyBorder(builder.padding, builder.padding + 10, 
                                           builder.padding, builder.padding + 10)
        ));
        contentPanel.setMaximumSize(builder.maxSize);
        
        // Wrapper para centrar
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(contentPanel);
        
        add(wrapper, BorderLayout.CENTER);
    }
    
    /**
     * Obtiene el panel de contenido donde se agregan componentes.
     */
    public JPanel getContentPanel() {
        return contentPanel;
    }
    
    /**
     * Agrega un componente al contenido de la card.
     */
    public MissionCard addContent(JComponent component) {
        contentPanel.add(component);
        return this;
    }
    
    /**
     * Agrega un espaciador vertical.
     */
    public MissionCard addVerticalSpace(int height) {
        contentPanel.add(Box.createVerticalStrut(height));
        return this;
    }
    
    /**
     * Agrega un separador horizontal.
     */
    public MissionCard addSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(GameConstants.COLOR_CYAN_NEON.darker());
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(separator);
        return this;
    }
    
    /**
     * Aplica animación de fade-in.
     */
    public void animateFadeIn() {
        Timer fadeTimer = new Timer(20, null);
        final float[] opacity = {0.0f};
        
        fadeTimer.addActionListener(e -> {
            opacity[0] += 0.1f;
            if (opacity[0] >= 1.0f) {
                fadeTimer.stop();
            }
            repaint();
        });
        
        fadeTimer.start();
    }
    
    /**
     * Builder pattern para construcción flexible de la card.
     */
    public static class Builder {
        private Color overlayColor = new Color(0, 0, 0, 180);
        private Color cardBackground = new Color(18, 18, 26, 180);
        private Color borderColor = GameConstants.COLOR_CYAN_NEON;
        private int borderWidth = 2;
        private int padding = 30;
        private Dimension maxSize = new Dimension(700, 550);
        
        public Builder() {
        }
        
        public Builder overlayColor(Color color) {
            this.overlayColor = color;
            return this;
        }
        
        public Builder cardBackground(Color color) {
            this.cardBackground = color;
            return this;
        }
        
        public Builder borderColor(Color color) {
            this.borderColor = color;
            return this;
        }
        
        public Builder borderWidth(int width) {
            this.borderWidth = width;
            return this;
        }
        
        public Builder padding(int padding) {
            this.padding = padding;
            return this;
        }
        
        public Builder maxSize(Dimension size) {
            this.maxSize = size;
            return this;
        }
        
        public Builder maxSize(int width, int height) {
            this.maxSize = new Dimension(width, height);
            return this;
        }
        
        public MissionCard build() {
            return new MissionCard(this);
        }
    }
}
