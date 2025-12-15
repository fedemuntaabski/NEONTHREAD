package com.neonthread.ui;

import javax.swing.UIManager;
import java.awt.Color;

/**
 * Motor de temas para la aplicación.
 * Usa Strategy pattern para aplicar diferentes esquemas de colores.
 */
public class ThemeEngine {
    
    public enum Theme {
        CYAN(new Color(10, 20, 30), new Color(0, 255, 255)),
        PURPLE(new Color(20, 10, 30), new Color(200, 100, 255));
        
        private final Color background;
        private final Color primary;
        
        Theme(Color background, Color primary) {
            this.background = background;
            this.primary = primary;
        }
        
        public Color getBackground() { return background; }
        public Color getPrimary() { return primary; }
    }

    private static Theme currentTheme = Theme.CYAN;

    public static void applyTheme(String themeName) {
        try {
            currentTheme = Theme.valueOf(themeName.toUpperCase());
            updateUIDefaults();
        } catch (IllegalArgumentException e) {
            currentTheme = Theme.CYAN;
            updateUIDefaults();
        }
    }
    
    private static void updateUIDefaults() {
        UIManager.put("Panel.background", currentTheme.getBackground());
        UIManager.put("Label.foreground", currentTheme.getPrimary());
    }

    public static Color getPrimaryColor() {
        return currentTheme.getPrimary();
    }

    public static Color getBackgroundColor() {
        return currentTheme.getBackground();
    }

    /**
     * Retorna color basado en porcentaje de estado (crítico/advertencia/normal).
     */
    public static Color getStatusColor(int percent) {
        if (percent <= 20) return new Color(255, 50, 50);
        if (percent <= 50) return new Color(255, 200, 0);
        return getPrimaryColor();
    }
}
