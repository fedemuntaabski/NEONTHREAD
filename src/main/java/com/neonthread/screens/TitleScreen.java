package com.neonthread.screens;

import com.neonthread.GameConstants;
import com.neonthread.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Pantalla de título con ASCII art minimalista.
 */
public class TitleScreen extends JPanel {
    private final Consumer<GameState> onComplete;
    private final JTextArea textArea;
    
    private final String titleArt = 
        "\n\n" +
        "           ███╗   ██╗███████╗ ██████╗ ███╗   ██╗\n" +
        "           ████╗  ██║██╔════╝██╔═══██╗████╗  ██║\n" +
        "           ██╔██╗ ██║█████╗  ██║   ██║██╔██╗ ██║\n" +
        "           ██║╚██╗██║██╔══╝  ██║   ██║██║╚██╗██║\n" +
        "           ██║ ╚████║███████╗╚██████╔╝██║ ╚████║\n" +
        "           ╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═══╝\n" +
        "\n" +
        "             NEONTHREAD // PROTOCOL 07\n" +
        "\n" +
        "    _\"Every runner leaves a trace. Yours starts now.\"_\n";
    
    public TitleScreen(Consumer<GameState> onComplete) {
        this.onComplete = onComplete;
        
        setLayout(new BorderLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        
        textArea = new JTextArea();
        textArea.setBackground(GameConstants.COLOR_BACKGROUND);
        textArea.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        textArea.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 16));
        textArea.setEditable(false);
        textArea.setText(titleArt);
        textArea.setAlignmentX(CENTER_ALIGNMENT);
        
        add(textArea, BorderLayout.CENTER);
    }
    
    public void show() {
        // Mostrar título por 2.5 segundos y pasar al menú
        Timer delay = new Timer(2500, e -> {
            ((Timer)e.getSource()).stop();
            onComplete.accept(GameState.STATE_MENU);
        });
        delay.setRepeats(false);
        delay.start();
    }
}
