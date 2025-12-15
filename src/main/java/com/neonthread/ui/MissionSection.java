package com.neonthread.ui;

import com.neonthread.GameConstants;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Componente reutilizable para secciones de contenido en UI de misión.
 * Encapsula título, contenido y estilos consistentes.
 */
public class MissionSection extends JPanel {
    private final JLabel titleLabel;
    private final JPanel contentPanel;
    private final List<JComponent> items;
    
    private MissionSection(Builder builder) {
        this.items = new ArrayList<>();
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Título de la sección
        if (builder.title != null && !builder.title.isEmpty()) {
            titleLabel = new JLabel(builder.title);
            titleLabel.setFont(builder.titleFont);
            titleLabel.setForeground(builder.titleColor);
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(titleLabel);
            add(Box.createVerticalStrut(builder.titleSpacing));
        } else {
            titleLabel = null;
        }
        
        // Panel de contenido
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        if (builder.contentBackground != null) {
            contentPanel.setOpaque(true);
            contentPanel.setBackground(builder.contentBackground);
            contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(builder.contentBorderColor, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
        }
        
        add(contentPanel);
        
        if (builder.bottomSpacing > 0) {
            add(Box.createVerticalStrut(builder.bottomSpacing));
        }
    }
    
    /**
     * Agrega un item a la sección.
     */
    public MissionSection addItem(String text) {
        return addItem(text, GameConstants.COLOR_TEXT_PRIMARY);
    }
    
    /**
     * Agrega un item con color personalizado.
     */
    public MissionSection addItem(String text, Color color) {
        JLabel item = new JLabel("  • " + text);
        item.setFont(GameConstants.FONT_TEXT.deriveFont(13f));
        item.setForeground(color);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(item);
        items.add(item);
        return this;
    }
    
    /**
     * Agrega un componente personalizado.
     */
    public MissionSection addComponent(JComponent component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(component);
        items.add(component);
        return this;
    }
    
    /**
     * Limpia todos los items de la sección.
     */
    public void clearItems() {
        contentPanel.removeAll();
        items.clear();
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Actualiza el título de la sección.
     */
    public void setTitle(String title) {
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
    }
    
    /**
     * Builder pattern para construcción flexible.
     */
    public static class Builder {
        private String title;
        private Color titleColor = GameConstants.COLOR_TEXT_SECONDARY;
        private Font titleFont = new Font(GameConstants.FONT_FAMILY, Font.BOLD, 14);
        private int titleSpacing = 8;
        private Color contentBackground = null;
        private Color contentBorderColor = GameConstants.COLOR_CYAN_NEON.darker();
        private int bottomSpacing = 15;
        
        public Builder() {
        }
        
        public Builder title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder titleColor(Color color) {
            this.titleColor = color;
            return this;
        }
        
        public Builder titleFont(Font font) {
            this.titleFont = font;
            return this;
        }
        
        public Builder titleSpacing(int spacing) {
            this.titleSpacing = spacing;
            return this;
        }
        
        public Builder contentBackground(Color color) {
            this.contentBackground = color;
            return this;
        }
        
        public Builder contentBorderColor(Color color) {
            this.contentBorderColor = color;
            return this;
        }
        
        public Builder bottomSpacing(int spacing) {
            this.bottomSpacing = spacing;
            return this;
        }
        
        public MissionSection build() {
            return new MissionSection(this);
        }
    }
}
