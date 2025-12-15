package com.neonthread.ui;

import com.neonthread.GameConstants;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Componente reutilizable para mostrar badges de información de misión.
 * Sigue el principio Single Responsibility - solo maneja la visualización de un badge.
 */
public class MissionBadge extends JPanel {
    private final JLabel labelComponent;
    private final JLabel valueComponent;
    
    private MissionBadge(Builder builder) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(builder.backgroundColor);
        
        Border border = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(builder.borderColor, builder.borderWidth),
            BorderFactory.createEmptyBorder(builder.padding, builder.padding * 2, 
                                           builder.padding, builder.padding * 2)
        );
        setBorder(border);
        
        // Label
        labelComponent = new JLabel(builder.label);
        labelComponent.setFont(builder.labelFont);
        labelComponent.setForeground(builder.labelColor);
        labelComponent.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Value
        valueComponent = new JLabel(builder.value);
        valueComponent.setFont(builder.valueFont);
        valueComponent.setForeground(builder.valueColor);
        valueComponent.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        add(labelComponent);
        add(valueComponent);
        
        if (builder.tooltip != null) {
            setToolTipText(builder.tooltip);
        }
    }
    
    /**
     * Actualiza el valor del badge dinámicamente.
     */
    public void setValue(String value) {
        valueComponent.setText(value);
    }
    
    /**
     * Actualiza el color del valor.
     */
    public void setValueColor(Color color) {
        valueComponent.setForeground(color);
    }
    
    /**
     * Builder pattern para construcción flexible y legible.
     */
    public static class Builder {
        // Required
        private final String label;
        private final String value;
        
        // Optional con defaults razonables
        private Color backgroundColor = new Color(0x0A0A0F);
        private Color borderColor = GameConstants.COLOR_CYAN_NEON;
        private Color labelColor = GameConstants.COLOR_TEXT_SECONDARY;
        private Color valueColor = GameConstants.COLOR_CYAN_NEON;
        private Font labelFont = GameConstants.FONT_TEXT.deriveFont(10f);
        private Font valueFont = GameConstants.FONT_MENU.deriveFont(14f);
        private int borderWidth = 1;
        private int padding = 5;
        private String tooltip = null;
        
        public Builder(String label, String value) {
            this.label = label;
            this.value = value;
        }
        
        public Builder backgroundColor(Color color) {
            this.backgroundColor = color;
            return this;
        }
        
        public Builder borderColor(Color color) {
            this.borderColor = color;
            return this;
        }
        
        public Builder labelColor(Color color) {
            this.labelColor = color;
            return this;
        }
        
        public Builder valueColor(Color color) {
            this.valueColor = color;
            return this;
        }
        
        public Builder labelFont(Font font) {
            this.labelFont = font;
            return this;
        }
        
        public Builder valueFont(Font font) {
            this.valueFont = font;
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
        
        public Builder tooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }
        
        public MissionBadge build() {
            return new MissionBadge(this);
        }
    }
}
