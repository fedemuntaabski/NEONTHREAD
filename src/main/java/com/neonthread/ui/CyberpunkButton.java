package com.neonthread.ui;

import com.neonthread.GameConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Botón personalizado con estética cyberpunk.
 * Simplificado para eliminar código no usado y mejorar mantenibilidad.
 */
public class CyberpunkButton extends JButton {
    
    public CyberpunkButton(String text) {
        super(text);
        setFont(GameConstants.FONT_MENU);
        setFocusPainted(false);
        setContentAreaFilled(true);
        setOpaque(true);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
