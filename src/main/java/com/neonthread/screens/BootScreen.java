package com.neonthread.screens;

import com.neonthread.GameConstants;
import com.neonthread.GameState;
import com.neonthread.TypewriterEffect;
import com.neonthread.GlitchEffect;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Pantalla de arranque con efecto terminal.
 * Usa Template Method pattern para secuencia de boot.
 */
public class BootScreen extends JPanel {
    private final Consumer<GameState> onComplete;
    private final JTextArea textArea;
    private TypewriterEffect currentEffect;
    private int lineIndex = 0;
    
    private final String[] bootLines = {
        "> NEONTHREAD OS v3.77",
        "> SYSTEM BOOT INITIATED",
        "> ACCESSING MEMORY DRIVERS... [OK]",
        "> CONNECTING TO CITYNET NODE 14-A... [FAILED]",
        "> RETRYING...",
        "> CONNECTING TO CITYNET NODE 07-D... [OK]",
        "> CHECKING INTEGRITY: ████████████░ 92%",
        "> LOADING MODULES:",
        "    - Neural Interface [OK]",
        "    - Ghostrunner Engine [OK]",
        "    - Blackwall Shields [WARNING]",
        "",
        GlitchEffect.generateInterference(),
        "",
        "> BOOT SEQUENCE COMPLETE"
    };
    
    public BootScreen(Consumer<GameState> onComplete) {
        this.onComplete = onComplete;
        
        setLayout(new BorderLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        
        textArea = new JTextArea();
        textArea.setBackground(GameConstants.COLOR_BACKGROUND);
        textArea.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        textArea.setFont(GameConstants.FONT_TEXT);
        textArea.setEditable(false);
        textArea.setMargin(new Insets(20, 20, 20, 20));
        textArea.setCaretColor(GameConstants.COLOR_CYAN_NEON);
        
        add(textArea, BorderLayout.CENTER);
    }
    
    public void startBootSequence() {
        lineIndex = 0;
        textArea.setText("");
        showNextLine();
    }
    
    private void showNextLine() {
        if (lineIndex >= bootLines.length) {
            // Esperar 1s y pasar al logo
            Timer delay = new Timer(1000, e -> {
                ((Timer)e.getSource()).stop();
                onComplete.accept(GameState.STATE_LOGO_GLITCH);
            });
            delay.setRepeats(false);
            delay.start();
            return;
        }
        
        String line = bootLines[lineIndex];
        String currentText = textArea.getText();
        
        // Color especial para warnings y errores
        if (line.contains("[WARNING]") || line.contains("[FAILED]")) {
            textArea.setForeground(GameConstants.COLOR_YELLOW_NEON);
        } else if (line.contains("INTERFERENCE") || line.contains("CORRUPTED")) {
            textArea.setForeground(GameConstants.COLOR_MAGENTA_NEON);
        } else {
            textArea.setForeground(GameConstants.COLOR_CYAN_NEON);
        }
        
        currentEffect = new TypewriterEffect(
            line + "\n",
            text -> textArea.setText(currentText + text),
            () -> {
                lineIndex++;
                // Pausa variable según el tipo de línea
                int pauseDuration = line.isEmpty() ? 100 : 
                                   (line.contains("RETRYING") ? 500 : 200);
                Timer pause = new Timer(pauseDuration, e -> {
                    ((Timer)e.getSource()).stop();
                    showNextLine();
                });
                pause.setRepeats(false);
                pause.start();
            }
        );
        currentEffect.start();
    }
    
    public void cleanup() {
        if (currentEffect != null) {
            currentEffect.stop();
        }
    }
}
