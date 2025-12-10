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
    public static final Color COLOR_BACKGROUND = new Color(0x02030A); // Negro azulado profundo
    public static final Color COLOR_PANEL = new Color(0x0A0F1B); // Paneles y bordes
    public static final Color COLOR_CYAN_NEON = new Color(0x05D9E8); // Líneas y divisores brillantes
    public static final Color COLOR_TEXT_PRIMARY = new Color(0xE3F8FF); // Texto principal
    public static final Color COLOR_TEXT_SECONDARY = new Color(0x7BAAB5); // Texto secundario
    public static final Color COLOR_BUTTON_HOVER = new Color(0x05D9E8); // Botones hover
    public static final Color COLOR_BUTTON_ACTIVE = new Color(0xFF1976); // Rosa neón warning
    public static final Color COLOR_MAGENTA_NEON = new Color(0xFF00E6); // Legacy magenta
    public static final Color COLOR_YELLOW_NEON = new Color(0xFFE600); // Legacy yellow
    public static final Color COLOR_BLUE_ELECTRIC = new Color(0x00A4FF); // Legacy blue
    public static final Color COLOR_GRAY_SYSTEM = new Color(0x303030); // Legacy gray
    public static final Color COLOR_WHITE_FLASH = new Color(0xFFFFFF);
    public static final Color COLOR_DARK_GRAY = new Color(0x404040);
    
    // Colores de accesibilidad
    public static final Color COLOR_HIGH_CONTRAST_BG = new Color(0x000000);
    public static final Color COLOR_HIGH_CONTRAST_BORDER = new Color(0xFF1976);
    public static final Color COLOR_HIGH_CONTRAST_TEXT = new Color(0xFFFFFF);
    
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
    public static final int HOVER_TRANSITION_MS = 150;
    
    // Settings defaults
    public static final int DEFAULT_MASTER_VOLUME = 80;
    public static final int DEFAULT_MUSIC_VOLUME = 70;
    public static final int DEFAULT_SFX_VOLUME = 80;
    public static final int DEFAULT_VOICE_VOLUME = 90;
    public static final int DEFAULT_BRIGHTNESS = 50;
    public static final int DEFAULT_UI_SCALE = 100;
    public static final int DEFAULT_TEXT_SPEED = 1; // 0=slow, 1=normal, 2=fast
    public static final int DEFAULT_GLITCH_INTENSITY = 50;
    
    // Fuentes precargadas
    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, FONT_TITLE_SIZE);
    public static final Font FONT_MENU = new Font(FONT_FAMILY, Font.PLAIN, FONT_MENU_SIZE);
    public static final Font FONT_TEXT = new Font(FONT_FAMILY, Font.PLAIN, FONT_TEXT_SIZE);
}
