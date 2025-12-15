package com.neonthread.screens;

import com.neonthread.GameConstants;
import com.neonthread.GameSession;
import com.neonthread.GameState;
import com.neonthread.ui.CyberpunkButton;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Pantalla de pausa con opciones de navegación.
 * Usa Builder pattern implícito en construcción de UI.
 */
public class PauseScreen extends JPanel {
    private final Consumer<GameState> onStateChange;
    private final Runnable onSettings;
    private final GameState previousState;
    
    public PauseScreen(Consumer<GameState> onStateChange, Runnable onSettings, GameState previousState) {
        this.onStateChange = onStateChange;
        this.onSettings = onSettings;
        this.previousState = previousState;
        
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 200)); // Semi-transparente
        
        buildUI();
    }
    
    private void buildUI() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(GameConstants.COLOR_PANEL);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON, 2),
            BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));
        
        // Título
        JLabel titleLabel = new JLabel("PAUSE");
        titleLabel.setFont(GameConstants.FONT_TITLE);
        titleLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(40));
        
        // Botones
        addButton(centerPanel, "CONTINUE", () -> onStateChange.accept(previousState));
        centerPanel.add(Box.createVerticalStrut(15));
        
        addButton(centerPanel, "INVENTORY", () -> onStateChange.accept(GameState.STATE_INVENTORY));
        centerPanel.add(Box.createVerticalStrut(15));
        
        addButton(centerPanel, "SETTINGS", onSettings);
        centerPanel.add(Box.createVerticalStrut(15));
        
        addButton(centerPanel, "EXIT TO MENU", this::exitToMenu);
        
        // Centrar
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(centerPanel);
        
        add(wrapper, BorderLayout.CENTER);
    }
    
    private void addButton(JPanel panel, String text, Runnable action) {
        CyberpunkButton button = new CyberpunkButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(280, 50));
        button.addActionListener(e -> action.run());
        panel.add(button);
    }
    
    private void exitToMenu() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit? Unsaved progress will be lost.",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            GameSession.getInstance().reset();
            onStateChange.accept(GameState.STATE_MENU);
        }
    }
}
