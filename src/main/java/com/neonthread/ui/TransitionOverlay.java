package com.neonthread.ui;

import com.neonthread.GameConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Overlay de transición reutilizable para feedback visual entre pantallas.
 * Implementa fade-in/fade-out con mensaje personalizable.
 * 
 * Uso: TransitionOverlay overlay = new TransitionOverlay("INITIALIZING...");
 *      overlay.show(parentPanel, () -> { // código post-transición });
 */
public class TransitionOverlay extends JPanel {
    private final JLabel messageLabel;
    private float alpha = 0.0f;
    private Timer fadeTimer;
    private Runnable onComplete;
    
    // Configuración
    private static final int FADE_DURATION_MS = 150;
    private static final int FADE_STEPS = 15;
    private static final int DISPLAY_TIME_MS = 300;
    
    public TransitionOverlay(String message) {
        setLayout(new GridBagLayout());
        setOpaque(false);
        
        // Mensaje central
        messageLabel = new JLabel(message);
        messageLabel.setFont(GameConstants.FONT_MENU);
        messageLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        
        add(messageLabel);
    }
    
    /**
     * Muestra el overlay con animación fade-in/fade-out.
     * @param parent Panel padre sobre el que se dibuja
     * @param onComplete Callback ejecutado tras completar la transición
     */
    public void show(JLayeredPane parent, Runnable onComplete) {
        this.onComplete = onComplete;
        setBounds(0, 0, parent.getWidth(), parent.getHeight());
        parent.add(this, JLayeredPane.MODAL_LAYER);
        
        fadeIn();
    }
    
    private void fadeIn() {
        alpha = 0.0f;
        int delay = FADE_DURATION_MS / FADE_STEPS;
        float increment = 1.0f / FADE_STEPS;
        
        fadeTimer = new Timer(delay, e -> {
            alpha += increment;
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                fadeTimer.stop();
                // Mantener visible brevemente antes de fade-out
                Timer holdTimer = new Timer(DISPLAY_TIME_MS, ev -> fadeOut());
                holdTimer.setRepeats(false);
                holdTimer.start();
            }
            repaint();
        });
        fadeTimer.start();
    }
    
    private void fadeOut() {
        alpha = 1.0f;
        int delay = FADE_DURATION_MS / FADE_STEPS;
        float decrement = 1.0f / FADE_STEPS;
        
        fadeTimer = new Timer(delay, e -> {
            alpha -= decrement;
            if (alpha <= 0.0f) {
                alpha = 0.0f;
                fadeTimer.stop();
                cleanup();
            }
            repaint();
        });
        fadeTimer.start();
    }
    
    private void cleanup() {
        Container parent = getParent();
        if (parent != null) {
            parent.remove(this);
            parent.repaint();
        }
        if (onComplete != null) {
            onComplete.run();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fondo negro semi-transparente
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.85f));
        g2d.setColor(GameConstants.COLOR_BACKGROUND);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.dispose();
    }
}
