package com.neonthread;

import javax.swing.*;
import java.awt.*;

/**
 * Aplica configuraciones del juego a la ventana y componentes.
 * Sigue patrón Command y KISS.
 */
public class SettingsApplier {
    private final JFrame window;
    private final GameSettings settings;
    
    public SettingsApplier(JFrame window) {
        this.window = window;
        this.settings = GameSettings.getInstance();
    }
    
    /**
     * Aplica todas las configuraciones actuales.
     */
    public void applyAll() {
        applyVideoSettings();
        applyUISettings();
        // Audio settings se aplicarían aquí si tuviéramos un sistema de audio real
    }
    
    /**
     * Aplica configuraciones de video.
     */
    public void applyVideoSettings() {
        // Aplicar resolución
        String resolution = settings.getResolution();
        String[] parts = resolution.split("x");
        if (parts.length == 2) {
            try {
                int width = Integer.parseInt(parts[0]);
                int height = Integer.parseInt(parts[1]);
                window.setSize(width, height);
                window.setLocationRelativeTo(null); // Centrar
            } catch (NumberFormatException e) {
                System.err.println("Invalid resolution format: " + resolution);
            }
        }
        
        // Aplicar modo de ventana
        String windowMode = settings.getWindowMode();
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        switch (windowMode) {
            case "Fullscreen":
                window.dispose();
                window.setUndecorated(true);
                window.setVisible(true);
                if (device.isFullScreenSupported()) {
                    device.setFullScreenWindow(window);
                }
                break;
                
            case "Borderless":
                if (device.getFullScreenWindow() == window) {
                    device.setFullScreenWindow(null);
                }
                window.dispose();
                window.setUndecorated(true);
                window.setExtendedState(JFrame.MAXIMIZED_BOTH);
                window.setVisible(true);
                break;
                
            case "Windowed":
            default:
                if (device.getFullScreenWindow() == window) {
                    device.setFullScreenWindow(null);
                }
                window.dispose();
                window.setUndecorated(false);
                window.setExtendedState(JFrame.NORMAL);
                window.setVisible(true);
                window.setLocationRelativeTo(null);
                break;
        }
        
        // VSync (solo log, requeriría configuración de rendering más avanzada)
        if (settings.isVsync()) {
            System.out.println("VSync enabled");
        }
    }
    
    /**
     * Aplica configuraciones de UI (escala, fuentes, etc).
     */
    public void applyUISettings() {
        float uiScale = settings.getUiScale() / 100.0f;
        
        // Aplicar escala a las fuentes
        Font baseFont = new Font(GameConstants.FONT_FAMILY, Font.PLAIN, 14);
        Font scaledFont = baseFont.deriveFont(baseFont.getSize() * uiScale);
        
        // Actualizar constantes de fuente (para futuros componentes)
        UIManager.put("Label.font", scaledFont);
        UIManager.put("Button.font", scaledFont);
        UIManager.put("TextField.font", scaledFont);
        
        // Aplicar accesibilidad
        if (settings.isHighContrast()) {
            applyHighContrastMode();
        } else {
            restoreNormalColors();
        }
        
        // Refrescar todos los componentes
        SwingUtilities.updateComponentTreeUI(window);
    }
    
    /**
     * Aplica modo de alto contraste.
     */
    private void applyHighContrastMode() {
        window.getContentPane().setBackground(GameConstants.COLOR_HIGH_CONTRAST_BG);
        // Los componentes individuales deberían actualizar sus colores según esta preferencia
    }
    
    /**
     * Restaura colores normales.
     */
    private void restoreNormalColors() {
        window.getContentPane().setBackground(GameConstants.COLOR_BACKGROUND);
    }
    
    /**
     * Aplica brillo ajustando el alpha de los componentes.
     */
    public void applyBrightness(Component component) {
        float brightness = settings.getBrightness() / 100.0f;
        // Ajustar brillo mediante alpha composite (simplificado)
        if (component instanceof JComponent) {
            JComponent jcomp = (JComponent) component;
            jcomp.setOpaque(brightness > 0.3f);
        }
    }
    
    /**
     * Obtiene el delay del typewriter según la velocidad de texto.
     */
    public int getTypewriterDelay() {
        switch (settings.getTextSpeed()) {
            case 0: return 50;  // Slow
            case 2: return 10;  // Fast
            case 1:
            default: return 25; // Normal
        }
    }
    
    /**
     * Obtiene la intensidad de glitch como decimal.
     */
    public double getGlitchIntensity() {
        if (settings.isDisableGlitchEffects()) {
            return 0.0;
        }
        return settings.getGlitchIntensity() / 100.0;
    }
}
