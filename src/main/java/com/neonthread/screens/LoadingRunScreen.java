package com.neonthread.screens;

import com.neonthread.GameConstants;
import com.neonthread.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Pantalla de carga inicial del run (KISS).
 * Muestra animación de "booting" con mensajes tipo consola.
 */
public class LoadingRunScreen extends JPanel {
    private final Consumer<GameState> onComplete;
    private JTextArea consoleArea;
    private Timer loadingTimer;
    private int messageIndex = 0;
    
    private final String[] loadingMessages = {
        "> Initializing urban environment...",
        "> Loading neural modules...",
        "> Connecting to the network...",
        "> Synchronizing identity...",
        "> System ready."
    };
    
    public LoadingRunScreen(Consumer<GameState> onComplete) {
        this.onComplete = onComplete;
        setLayout(new BorderLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        
        initializeUI();
    }
    
    private void initializeUI() {
        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setFont(new Font(GameConstants.FONT_FAMILY, Font.PLAIN, 16));
        consoleArea.setForeground(GameConstants.COLOR_CYAN_NEON);
        consoleArea.setBackground(GameConstants.COLOR_BACKGROUND);
        consoleArea.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
        
        add(consoleArea, BorderLayout.CENTER);
    }
    
    /**
     * Inicia la secuencia de carga.
     */
    public void startLoading() {
        messageIndex = 0;
        consoleArea.setText("");
        
        loadingTimer = new Timer(600, e -> {
            if (messageIndex < loadingMessages.length) {
                consoleArea.append(loadingMessages[messageIndex] + "\n");
                messageIndex++;
            } else {
                loadingTimer.stop();
                // Esperar un poco antes de continuar
                Timer delayTimer = new Timer(1000, ev -> {
                    onComplete.accept(GameState.STATE_CHARACTER_CREATION);
                });
                delayTimer.setRepeats(false);
                delayTimer.start();
            }
        });
        
        loadingTimer.start();
    }
}
