package com.neonthread.screens;

import com.neonthread.GameConstants;
import com.neonthread.GameState;
import com.neonthread.GlitchEffect;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Pantalla de logo corporativo con efecto glitch.
 */
public class LogoScreen extends JPanel {
    private final Consumer<GameState> onComplete;
    private final JTextArea logoArea;
    
    private final String cleanLogo = 
        "██╗███╗   ██╗███████╗ ██████╗ ███╗   ██╗████████╗███████╗██████╗ \n" +
        "██║████╗  ██║██╔════╝██╔═══██╗████╗  ██║╚══██╔══╝██╔════╝██╔══██╗\n" +
        "██║██╔██╗ ██║█████╗  ██║   ██║██╔██╗ ██║   ██║   █████╗  ██████╔╝\n" +
        "██║██║╚██╗██║██╔══╝  ██║   ██║██║╚██╗██║   ██║   ██╔══╝  ██╔══██╗\n" +
        "██║██║ ╚████║███████╗╚██████╔╝██║ ╚████║   ██║   ███████╗██║  ██║\n" +
        "╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═══╝   ╚═╝   ╚══════╝╚═╝  ╚═╝";
    
    private Timer glitchTimer;
    private int glitchCount = 0;
    private static final int MAX_GLITCHES = 8;
    
    public LogoScreen(Consumer<GameState> onComplete) {
        this.onComplete = onComplete;
        
        setLayout(new GridBagLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        
        logoArea = new JTextArea();
        logoArea.setBackground(GameConstants.COLOR_BACKGROUND);
        logoArea.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        logoArea.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 14));
        logoArea.setEditable(false);
        logoArea.setText(cleanLogo);
        
        GridBagConstraints gbc = new GridBagConstraints();
        add(logoArea, gbc);
    }
    
    public void show() {
        glitchCount = 0;
        
        // Aplicar glitches rápidos durante 500ms
        glitchTimer = new Timer(60, e -> {
            glitchCount++;
            
            if (glitchCount < MAX_GLITCHES) {
                // Alternar entre glitcheado y limpio
                if (glitchCount % 2 == 0) {
                    logoArea.setText(GlitchEffect.applyGlitch(cleanLogo, 0.15));
                    logoArea.setForeground(GameConstants.COLOR_MAGENTA_NEON);
                } else {
                    logoArea.setText(cleanLogo);
                    logoArea.setForeground(GameConstants.COLOR_CYAN_NEON);
                }
            } else {
                // Terminar con logo limpio
                glitchTimer.stop();
                logoArea.setText(cleanLogo);
                logoArea.setForeground(GameConstants.COLOR_CYAN_NEON);
                
                // Mostrar logo limpio por 1.5s antes de ir al menú
                Timer displayTimer = new Timer(1500, ev -> {
                    ((Timer)ev.getSource()).stop();
                    cleanup();
                    onComplete.accept(GameState.STATE_TITLE);
                });
                displayTimer.setRepeats(false);
                displayTimer.start();
            }
        });
        glitchTimer.start();
    }
    
    public void cleanup() {
        if (glitchTimer != null) {
            glitchTimer.stop();
        }
    }
}
