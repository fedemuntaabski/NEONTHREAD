package com.neonthread.screens;

import com.neonthread.GameConstants;
import com.neonthread.GameSession;
import com.neonthread.GameState;
import com.neonthread.TypewriterEffect;
import com.neonthread.ui.CyberpunkButton;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Pantalla de intro narrativa mejorada (KISS).
 * Estilo terminal/consola cyberpunk con texto progresivo.
 */
public class IntroNarrativeScreen extends JPanel {
    private final Consumer<GameState> onComplete;
    private JTextArea narrativeArea;
    private TypewriterEffect typewriter;
    private CyberpunkButton continueButton;
    
    public IntroNarrativeScreen(Consumer<GameState> onComplete) {
        this.onComplete = onComplete;
        setLayout(new BorderLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        
        buildUI();
    }
    
    private void buildUI() {
        // Panel superior: Header tipo terminal
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Área de texto central
        narrativeArea = new JTextArea();
        narrativeArea.setEditable(false);
        narrativeArea.setLineWrap(true);
        narrativeArea.setWrapStyleWord(true);
        narrativeArea.setFont(new Font(GameConstants.FONT_FAMILY, Font.PLAIN, 16));
        narrativeArea.setForeground(GameConstants.COLOR_CYAN_NEON);
        narrativeArea.setBackground(GameConstants.COLOR_BACKGROUND);
        narrativeArea.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));
        
        JScrollPane scrollPane = new JScrollPane(narrativeArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(GameConstants.COLOR_BACKGROUND);
        add(scrollPane, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        buttonPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        
        CyberpunkButton skipButton = new CyberpunkButton("SKIP");
        skipButton.addActionListener(e -> skipIntro());
        
        continueButton = new CyberpunkButton("CONTINUE");
        continueButton.addActionListener(e -> continueToDistrict());
        continueButton.setEnabled(false);
        
        buttonPanel.add(skipButton);
        buttonPanel.add(continueButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Crea el header estilo terminal (DRY).
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(GameConstants.COLOR_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, GameConstants.COLOR_CYAN_NEON),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("URBAN INFILTRATION SYSTEM - v1.9");
        titleLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 14));
        titleLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        panel.add(titleLabel);
        
        JLabel separatorLabel = new JLabel("─".repeat(70));
        separatorLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.PLAIN, 10));
        separatorLabel.setForeground(GameConstants.COLOR_CYAN_NEON.darker());
        panel.add(separatorLabel);
        
        return panel;
    }
    
    /**
     * Genera el texto narrativo completo (DRY).
     */
    private String buildNarrativeText() {
        GameSession session = GameSession.getInstance();
        String name = "Operator";
        String roleDesc = "city operator";
        
        if (session.hasActiveSession()) {
            name = session.getCharacter().getName();
            roleDesc = session.getCharacter().getRole().getDescription().toLowerCase();
        }
        
        return "> Initializing access protocols...\n" +
               "> Authenticating operator: " + name.toUpperCase() + "\n" +
               "> Synchronizing neural implants...\n" +
               "> Loading environment: District Theta-5...\n\n" +
               "─".repeat(60) + "\n\n" +
               "The megacity of NEONFALL never sleeps.\n\n" +
               "Its skyscrapers hide encrypted data networks, old corporate wars\n" +
               "and alleys where life is worth less than a byte.\n\n" +
               "The year is 2087. The city no longer has a name.\n" +
               "Only corporate codes and numbered sectors.\n\n" +
               "One megacorporation controls everything: water, electricity, information.\n\n" +
               "You are " + name + ", " + roleDesc + ".\n\n" +
               "You live in the shadows of District Theta-5, where corporate law doesn't reach.\n\n" +
               "This is your first 'run'. Your first chance to change the rules.\n" +
               "Or to disappear trying.\n\n" +
               "─".repeat(60) + "\n\n" +
               "> MISSION 01 UNLOCKED: First Contact\n" +
               "> Available location: District Theta-5\n" +
               "> System status: OPERATIONAL\n\n" +
               "Initiation protocol complete. Welcome to the network.";
    }
    
    /**
     * Inicia la animación del texto con efecto typewriter.
     */
    public void startNarrative() {
        narrativeArea.setText("");
        String fullText = buildNarrativeText();
        
        // Typewriter effect más rápido (estilo terminal)
        typewriter = new TypewriterEffect(fullText, 15, text -> {
            narrativeArea.setText(text);
            // Auto-scroll al final
            narrativeArea.setCaretPosition(text.length());
        }, () -> {
            continueButton.setEnabled(true);
            unlockFirstMission();
        });
        
        typewriter.start();
    }
    
    /**
     * Desbloquea la primera misión al completar el intro (KISS).
     */
    private void unlockFirstMission() {
        GameSession session = GameSession.getInstance();
        if (session.hasActiveSession() && session.getDistrict() != null) {
            // La misión ya se añadió en GameSession.startNewGame()
            // Aquí podríamos añadir efectos adicionales
        }
    }
    
    private void skipIntro() {
        if (typewriter != null) {
            typewriter.stop();
        }
        unlockFirstMission();
        continueToDistrict();
    }
    
    private void continueToDistrict() {
        onComplete.accept(GameState.STATE_DISTRICT_MAP);
    }
}
