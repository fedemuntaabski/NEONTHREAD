package com.neonthread.ui;

import com.neonthread.GameConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Componente de hint visual para tutorial implícito.
 * Se auto-destruye tras interacción o timeout.
 * 
 * Implementa patrón Observer para auto-gestión de ciclo de vida.
 */
public class TutorialHint extends JPanel {
    private final JLabel messageLabel;
    private Timer pulseTimer;
    private Timer autoHideTimer;
    private float pulseAlpha = 1.0f;
    private boolean pulsing = true;
    
    private static final int PULSE_INTERVAL_MS = 50;
    private static final int AUTO_HIDE_DELAY_MS = 10000; // 10 segundos
    
    public TutorialHint(String message) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setOpaque(false);
        
        // Icono
        JLabel iconLabel = new JLabel("★");
        iconLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 20));
        iconLabel.setForeground(GameConstants.COLOR_YELLOW_NEON);
        
        // Mensaje
        messageLabel = new JLabel(message);
        messageLabel.setFont(GameConstants.FONT_TEXT);
        messageLabel.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        
        add(iconLabel);
        add(messageLabel);
        
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GameConstants.COLOR_YELLOW_NEON, 2),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        startPulse();
        startAutoHide();
    }
    
    /**
     * Inicia la animación de pulse (parpadeo suave).
     */
    private void startPulse() {
        pulseTimer = new Timer(PULSE_INTERVAL_MS, new ActionListener() {
            private boolean increasing = false;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (increasing) {
                    pulseAlpha += 0.05f;
                    if (pulseAlpha >= 1.0f) {
                        pulseAlpha = 1.0f;
                        increasing = false;
                    }
                } else {
                    pulseAlpha -= 0.05f;
                    if (pulseAlpha <= 0.4f) {
                        pulseAlpha = 0.4f;
                        increasing = true;
                    }
                }
                repaint();
            }
        });
        pulseTimer.start();
    }
    
    /**
     * Inicia el timer de auto-ocultación.
     */
    private void startAutoHide() {
        autoHideTimer = new Timer(AUTO_HIDE_DELAY_MS, e -> hide());
        autoHideTimer.setRepeats(false);
        autoHideTimer.start();
    }
    
    /**
     * Oculta y remueve el hint.
     */
    public void hide() {
        if (pulseTimer != null) {
            pulseTimer.stop();
        }
        if (autoHideTimer != null) {
            autoHideTimer.stop();
        }
        
        // Fade out rápido
        Timer fadeTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pulseAlpha -= 0.1f;
                if (pulseAlpha <= 0.0f) {
                    ((Timer) e.getSource()).stop();
                    removeFromParent();
                }
                repaint();
            }
        });
        fadeTimer.start();
    }
    
    private void removeFromParent() {
        Container parent = getParent();
        if (parent != null) {
            parent.remove(this);
            parent.revalidate();
            parent.repaint();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Fondo semi-transparente con alpha pulsante
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulseAlpha * 0.9f));
        g2d.setColor(GameConstants.COLOR_PANEL);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        
        g2d.dispose();
        super.paintComponent(g);
    }
}
