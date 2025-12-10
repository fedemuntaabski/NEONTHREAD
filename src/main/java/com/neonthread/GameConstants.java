package com.neonthread;

import java.awt.Color;
import java.awt.Font;

/**
 * Constantes del juego centralizadas siguiendo principio DRY.
 */
public final class GameConstants {
    
    // Prevenir instanciación
    private GameConstants() {}
    
    // Colores Cyberpunk mejorados
    public static final Color COLOR_BACKGROUND = new Color(0x000000);
    public static final Color COLOR_CYAN_NEON = new Color(0x00F7FF);
    public static final Color COLOR_MAGENTA_NEON = new Color(0xFF00E6);
    public static final Color COLOR_YELLOW_NEON = new Color(0xFFE600);
    public static final Color COLOR_BLUE_ELECTRIC = new Color(0x00A4FF);
    public static final Color COLOR_GRAY_SYSTEM = new Color(0x303030);
    public static final Color COLOR_WHITE_FLASH = new Color(0xFFFFFF);
    public static final Color COLOR_DARK_GRAY = new Color(0x404040);
    
    // Dimensiones
    public static final int WINDOW_WIDTH = 1024;
    public static final int WINDOW_HEIGHT = 768;
    
    // Fuentes
    public static final String FONT_FAMILY = "Consolas";
    public static final int FONT_TITLE_SIZE = 32;
    public static final int FONT_MENU_SIZE = 20;
    public static final int FONT_TEXT_SIZE = 14;
    
    // Efectos mejorados
    public static final int TYPEWRITER_DELAY_MS = 25;
    public static final int CURSOR_BLINK_DELAY_MS = 600;
    public static final int SCANLINE_INTERVAL_MS = 7000;
    public static final int BOOTSTRAP_FADE_DURATION_MS = 600;
    public static final int FLASH_DURATION_MS = 50;
    public static final int GLITCH_DURATION_MS = 500;
    public static final int LOGO_DISPLAY_MS = 2000;
    
    // Fuentes precargadas
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, FONT_TITLE_SIZE);
    public static final Font FONT_MENU = new Font(FONT_FAMILY, Font.PLAIN, FONT_MENU_SIZE);
    public static final Font FONT_TEXT = new Font(FONT_FAMILY, Font.PLAIN, FONT_TEXT_SIZE);
}
