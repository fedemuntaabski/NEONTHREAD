package com.neonthread.screens;

import com.neonthread.Character;
import com.neonthread.GameConstants;
import com.neonthread.ui.CyberpunkButton;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Panel de confirmación que muestra resumen del personaje antes de iniciar el run.
 * Implementa patrón Builder para construcción de UI compleja.
 */
public class CharacterSummaryPanel extends JPanel {
    private final Character character;
    private final Runnable onConfirm;
    private final Runnable onEdit;
    
    public CharacterSummaryPanel(Character character, Runnable onConfirm, Runnable onEdit) {
        this.character = character;
        this.onConfirm = onConfirm;
        this.onEdit = onEdit;
        
        setLayout(new BorderLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        
        buildUI();
    }
    
    private void buildUI() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(60, 100, 60, 100));
        
        // Título
        JLabel titleLabel = new JLabel("OPERATOR PROFILE CONFIRMATION");
        titleLabel.setFont(GameConstants.FONT_TITLE.deriveFont(26f));
        titleLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        
        // Subtítulo
        JLabel subtitleLabel = new JLabel("Review your operator before deployment");
        subtitleLabel.setFont(GameConstants.FONT_TEXT);
        subtitleLabel.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(subtitleLabel);
        centerPanel.add(Box.createVerticalStrut(40));
        
        // Panel de información con borde
        JPanel infoPanel = createInfoPanel();
        centerPanel.add(infoPanel);
        centerPanel.add(Box.createVerticalStrut(40));
        
        // Warning/Disclaimer
        JLabel warningLabel = new JLabel("⚠ Once confirmed, this operator will enter District Theta-5");
        warningLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.ITALIC, 12));
        warningLabel.setForeground(GameConstants.COLOR_YELLOW_NEON);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(warningLabel);
        centerPanel.add(Box.createVerticalStrut(25));
        
        // Botones
        JPanel buttonPanel = createButtonPanel();
        centerPanel.add(buttonPanel);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(GameConstants.COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(GameConstants.COLOR_CYAN_NEON, 2),
            BorderFactory.createEmptyBorder(25, 40, 25, 40)
        ));
        panel.setMaximumSize(new Dimension(600, 400));
        
        // Nombre del operador
        addInfoRow(panel, "OPERATOR NAME", character.getName().toUpperCase());
        panel.add(Box.createVerticalStrut(20));
        
        // Rol
        addInfoRow(panel, "INITIAL ROLE", character.getRole().getDisplayName());
        addDescriptionRow(panel, character.getRole().getDescription());
        panel.add(Box.createVerticalStrut(20));
        
        // Atributos base
        JLabel attrTitle = new JLabel("BASE ATTRIBUTES");
        attrTitle.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 14));
        attrTitle.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        attrTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(attrTitle);
        panel.add(Box.createVerticalStrut(8));
        
        addAttributeRow(panel, "Intelligence", character.getBaseAttributes().getIntelligence());
        addAttributeRow(panel, "Physical", character.getBaseAttributes().getPhysical());
        addAttributeRow(panel, "Perception", character.getBaseAttributes().getPerception());
        addAttributeRow(panel, "Charisma", character.getBaseAttributes().getCharisma());
        panel.add(Box.createVerticalStrut(20));
        
        // Dificultad
        addInfoRow(panel, "DIFFICULTY MODE", character.getDifficulty().getDisplayName());
        addDescriptionRow(panel, character.getDifficulty().getDescription());
        
        return panel;
    }
    
    private void addInfoRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setBackground(GameConstants.COLOR_PANEL);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel labelComp = new JLabel(label + ":");
        labelComp.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 14));
        labelComp.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 16));
        valueComp.setForeground(GameConstants.COLOR_CYAN_NEON);
        
        row.add(labelComp);
        row.add(valueComp);
        panel.add(row);
    }
    
    private void addDescriptionRow(JPanel panel, String description) {
        JLabel desc = new JLabel("  ↳ " + description);
        desc.setFont(new Font(GameConstants.FONT_FAMILY, Font.ITALIC, 12));
        desc.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(desc);
    }
    
    private void addAttributeRow(JPanel panel, String name, int value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setBackground(GameConstants.COLOR_PANEL);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel nameLabel = new JLabel("  • " + name + ":");
        nameLabel.setFont(GameConstants.FONT_TEXT);
        nameLabel.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        nameLabel.setPreferredSize(new Dimension(150, 20));
        
        // Barra visual de atributo
        JLabel valueLabel = new JLabel(String.valueOf(value));
        valueLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 14));
        valueLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        
        String bars = "▮".repeat(value) + "▯".repeat(Math.max(0, 5 - value));
        JLabel barLabel = new JLabel(bars);
        barLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.PLAIN, 14));
        barLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        
        row.add(nameLabel);
        row.add(valueLabel);
        row.add(barLabel);
        panel.add(row);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        panel.setBackground(GameConstants.COLOR_BACKGROUND);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        CyberpunkButton editButton = new CyberpunkButton("← EDIT");
        editButton.setPreferredSize(new Dimension(150, 40));
        editButton.addActionListener(e -> onEdit.run());
        
        CyberpunkButton confirmButton = new CyberpunkButton("CONFIRM RUN →");
        confirmButton.setPreferredSize(new Dimension(200, 40));
        confirmButton.setFont(GameConstants.FONT_MENU.deriveFont(Font.BOLD));
        confirmButton.addActionListener(e -> onConfirm.run());
        
        panel.add(editButton);
        panel.add(confirmButton);
        
        return panel;
    }
}
