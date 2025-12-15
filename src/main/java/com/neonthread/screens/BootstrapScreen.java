package com.neonthread.screens;

import com.neonthread.GameConstants;
import com.neonthread.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Pantalla inicial con efecto [NO SIGNAL] y fade-in.
 * Usa State pattern para manejar transiciones visuales.
 */
public class BootstrapScreen extends JPanel {
    private final Consumer<GameState> onComplete;
    private final JLabel signalLabel;
    private final JLabel connectingLabel;
    private float alpha = 0.0f;
    private Timer fadeTimer;
    
    public BootstrapScreen(Consumer<GameState> onComplete) {
        this.onComplete = onComplete;
        
        setLayout(new GridBagLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        signalLabel = new JLabel("[ NO SIGNAL ]");
        signalLabel.setFont(GameConstants.FONT_TEXT);
        signalLabel.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        add(signalLabel, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 0, 0);
        connectingLabel = new JLabel("> Attempting connection...");
        connectingLabel.setFont(GameConstants.FONT_TEXT);
        connectingLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        connectingLabel.setVisible(false);
        add(connectingLabel, gbc);
    }
    
    public void startSequence() {
        alpha = 0.0f;
        signalLabel.setVisible(true);
        connectingLabel.setVisible(false);
        
        // Fade-in del texto "NO SIGNAL"
        fadeTimer = new Timer(20, e -> {
            alpha += 0.03f;
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                fadeTimer.stop();
                // Después del fade, esperar 600ms y mostrar "Attempting connection"
                Timer waitTimer = new Timer(600, ev -> {
                    ((Timer)ev.getSource()).stop();
                    showFlashAndConnect();
                });
                waitTimer.setRepeats(false);
                waitTimer.start();
            }
            updateAlpha();
        });
        fadeTimer.start();
    }
    
    private void updateAlpha() {
        int grayValue = (int)(0x40 * alpha);
        signalLabel.setForeground(new Color(grayValue, grayValue, grayValue));
        repaint();
    }
    
    private void showFlashAndConnect() {
        // Flash blanco de 50ms
        setBackground(GameConstants.COLOR_WHITE_FLASH);
        
        Timer flashTimer = new Timer(GameConstants.FLASH_DURATION_MS, e -> {
            ((Timer)e.getSource()).stop();
            setBackground(GameConstants.COLOR_BACKGROUND);
            connectingLabel.setVisible(true);
            
            // Después de 800ms, pasar al boot
            Timer transitionTimer = new Timer(800, ev -> {
                ((Timer)ev.getSource()).stop();
                cleanup();
                onComplete.accept(GameState.STATE_BOOT);
            });
            transitionTimer.setRepeats(false);
            transitionTimer.start();
        });
        flashTimer.setRepeats(false);
        flashTimer.start();
    }
    
    public void cleanup() {
        if (fadeTimer != null) {
            fadeTimer.stop();
        }
    }
}
