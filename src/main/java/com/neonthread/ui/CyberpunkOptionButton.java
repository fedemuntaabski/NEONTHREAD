package com.neonthread.ui;

import com.neonthread.GameConstants;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Botón de opción cyberpunk agresivo para elecciones narrativas.
 * Inspirado en Cyberpunk 2077 con bordes afilados y efectos de click.
 */
public class CyberpunkOptionButton extends JButton {
    private boolean isPressed = false;
    private Color glowColor = GameConstants.COLOR_CYAN_NEON;
    private Color baseColor = new Color(10, 15, 20);
    private Color textColor = GameConstants.COLOR_TEXT_PRIMARY;

    public CyberpunkOptionButton(String text) {
        super(text);
        customizeUI();
        setupMouseEffects();
    }

    private void customizeUI() {
        setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 16));
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(new CyberpunkBorder());
        setPreferredSize(new Dimension(400, 45));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
    }

    private void setupMouseEffects() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Sin efecto hover
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Sin efecto hover
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                glowColor = GameConstants.COLOR_RED_NEON;
                textColor = GameConstants.COLOR_RED_NEON;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                glowColor = GameConstants.COLOR_CYAN_NEON;
                textColor = GameConstants.COLOR_TEXT_PRIMARY;
                repaint();
            }
        });
    }    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Fondo base
        g2d.setColor(baseColor);
        g2d.fillRect(0, 0, width, height);

        // Efecto de glow exterior
        if (isPressed) {
            g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 100));
            g2d.fillRect(0, 0, width, height);
        }

        // Líneas de escaneo cyberpunk (solo al presionar)
        if (isPressed) {
            g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 150));
            g2d.fillRect(0, height/2 - 1, width, 2);
        }

        // Texto
        g2d.setColor(textColor);
        g2d.setFont(getFont());
        FontMetrics fm = g2d.getFontMetrics();
        String text = getText();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        int x = (width - textWidth) / 2;
        int y = (height + textHeight) / 2 - fm.getDescent();

        // Sombra de texto para efecto 3D
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.drawString(text, x + 1, y + 1);

        // Texto principal
        g2d.setColor(textColor);
        g2d.drawString(text, x, y);

        g2d.dispose();
    }

    /**
     * Borde cyberpunk con esquinas afiladas y líneas diagonales.
     */
    private class CyberpunkBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(glowColor);

            // Líneas principales del borde
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(x + 2, y + 2, width - 4, height - 4);

            // Esquinas afiladas (triángulos pequeños)
            int cornerSize = 8;
            g2d.fillPolygon(
                new int[]{x, x + cornerSize, x},
                new int[]{y, y, y + cornerSize}, 3
            );
            g2d.fillPolygon(
                new int[]{x + width, x + width - cornerSize, x + width},
                new int[]{y, y, y + cornerSize}, 3
            );
            g2d.fillPolygon(
                new int[]{x, x + cornerSize, x},
                new int[]{y + height, y + height, y + height - cornerSize}, 3
            );
            g2d.fillPolygon(
                new int[]{x + width, x + width - cornerSize, x + width},
                new int[]{y + height, y + height, y + height - cornerSize}, 3
            );

            // Líneas diagonales para efecto cyberpunk
            g2d.setStroke(new BasicStroke(1));
            g2d.drawLine(x + 10, y, x, y + 10);
            g2d.drawLine(x + width - 10, y, x + width, y + 10);
            g2d.drawLine(x + 10, y + height, x, y + height - 10);
            g2d.drawLine(x + width - 10, y + height, x + width, y + height - 10);

            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 8, 8, 8);
        }
    }
}