package com.neonthread;

import javax.swing.*;
import java.awt.*;

/**
 * Aplica configuraciones simples del juego (KISS).
 * Solo maneja resolución, pantalla completa, volumen y tamaño de texto.
 */
public class SettingsApplier {
    private final JFrame window;
    private final GameSettings settings;
    
    public SettingsApplier(JFrame window) {
        this.window = window;
        this.settings = GameSettings.getInstance();
    }
    
    /**
     * Aplica todas las configuraciones.
     */
    public void applyAll() {
        applyResolution();
        applyFullscreen();
        applyVolume();
        applyTextSize();
    }
    
    /**
     * Aplica la resolución (solo en modo ventana).
     */
    private void applyResolution() {
        if (settings.isFullscreen()) {
            return; // En fullscreen se usa resolución nativa
        }
        
        String resolution = settings.getResolution();
        String[] parts = resolution.split("x");
        
        if (parts.length == 2) {
            try {
                int width = Integer.parseInt(parts[0]);
                int height = Integer.parseInt(parts[1]);
                window.setSize(width, height);
                window.setLocationRelativeTo(null);
            } catch (NumberFormatException e) {
                System.err.println("Resolución inválida: " + resolution);
            }
        }
    }
    
    /**
     * Aplica modo pantalla completa o ventana.
     */
    private void applyFullscreen() {
        GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice();
        
        if (settings.isFullscreen()) {
            // Activar pantalla completa
            window.dispose();
            window.setUndecorated(true);
            window.setVisible(true);
            
            if (device.isFullScreenSupported()) {
                device.setFullScreenWindow(window);
            }
        } else {
            // Modo ventana
            if (device.getFullScreenWindow() == window) {
                device.setFullScreenWindow(null);
            }
            
            window.dispose();
            window.setUndecorated(false);
            window.setVisible(true);
            applyResolution();
        }
    }
    
    /**
     * Aplica el volumen maestro.
     */
    private void applyVolume() {
        int volume = settings.getMasterVolume();
        // En un juego real aquí se aplicaría al sistema de audio
        System.out.println("Volumen maestro: " + volume + "%");
    }
    
    /**
     * Aplica el tamaño de texto.
     */
    private void applyTextSize() {
        int baseSize = settings.isLargeText() ? 18 : 14;
        
        Font textFont = new Font(GameConstants.FONT_FAMILY, Font.PLAIN, baseSize);
        Font buttonFont = new Font(GameConstants.FONT_FAMILY, Font.BOLD, baseSize + 4);
        
        UIManager.put("Label.font", textFont);
        UIManager.put("Button.font", buttonFont);
        UIManager.put("RadioButton.font", textFont);
        UIManager.put("CheckBox.font", textFont);
        
        SwingUtilities.updateComponentTreeUI(window);
        window.revalidate();
        window.repaint();
    }
}
