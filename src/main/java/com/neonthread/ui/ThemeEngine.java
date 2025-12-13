package com.neonthread.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class ThemeEngine {
    
    public enum Theme {
        CYAN, PURPLE
    }

    private static Theme currentTheme = Theme.CYAN;

    public static void applyTheme(String themeName) {
        try {
            currentTheme = Theme.valueOf(themeName.toUpperCase());
        } catch (IllegalArgumentException e) {
            currentTheme = Theme.CYAN;
        }
        
        // Apply global UI defaults based on theme
        switch (currentTheme) {
            case PURPLE:
                UIManager.put("Panel.background", new Color(20, 10, 30));
                UIManager.put("Label.foreground", new Color(200, 100, 255));
                break;
            case CYAN:
            default:
                UIManager.put("Panel.background", new Color(10, 20, 30));
                UIManager.put("Label.foreground", new Color(0, 255, 255));
                break;
        }
    }

    public static Color getPrimaryColor() {
        return currentTheme == Theme.PURPLE ? new Color(180, 0, 255) : new Color(0, 255, 255);
    }

    public static Color getBackgroundColor() {
        return currentTheme == Theme.PURPLE ? new Color(20, 10, 30) : new Color(10, 20, 30);
    }

    public static Color getStatusColor(int percent) {
        if (percent <= 20) return new Color(255, 50, 50); // Critical
        if (percent <= 50) return new Color(255, 200, 0); // Warning
        return getPrimaryColor(); // Normal
    }
}
