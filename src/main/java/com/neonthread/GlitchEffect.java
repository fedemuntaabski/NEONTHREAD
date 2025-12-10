package com.neonthread;

import java.util.Random;

/**
 * Efecto de glitch de texto reutilizable siguiendo DRY.
 * Corrompe caracteres aleatorios con símbolos cyberpunk.
 */
public class GlitchEffect {
    private static final char[] GLITCH_CHARS = {
        '▓', '▒', '░', '█', '@', '#', '$', '%', '&', '*',
        '╬', '╣', '║', '╗', '╝', '╚', '╔', '╩', '╦', '╠',
        '≈', '±', '÷', '×', '¿', '¡', '~', '^', '|', '\\'
    };
    
    private static final Random random = new Random();
    
    private GlitchEffect() {} // Utility class
    
    /**
     * Aplica glitch a un texto reemplazando caracteres aleatorios.
     * Respeta la configuración de intensidad de glitch.
     * @param text Texto original
     * @param intensity Intensidad (0.0 a 1.0) - porcentaje de caracteres a glitchear
     * @return Texto glitcheado
     */
    public static String applyGlitch(String text, double intensity) {
        // Verificar si los glitches están deshabilitados
        GameSettings settings = GameSettings.getInstance();
        if (settings.isDisableGlitchEffects()) {
            return text;
        }
        
        // Aplicar intensidad configurada
        double configuredIntensity = settings.getGlitchIntensity() / 100.0;
        intensity = intensity * configuredIntensity;
        
        if (text == null || text.isEmpty() || intensity <= 0) {
            return text;
        }
        
        char[] chars = text.toCharArray();
        int charsToGlitch = (int) (chars.length * Math.min(intensity, 1.0));
        
        for (int i = 0; i < charsToGlitch; i++) {
            int pos = random.nextInt(chars.length);
            // No glitchear espacios ni saltos de línea
            if (chars[pos] != ' ' && chars[pos] != '\n' && chars[pos] != '\r') {
                chars[pos] = GLITCH_CHARS[random.nextInt(GLITCH_CHARS.length)];
            }
        }
        
        return new String(chars);
    }
    
    /**
     * Genera una línea de interferencia aleatoria.
     */
    public static String generateInterference() {
        String[] interferences = {
            "> WARNING: SIGNAL INTERFERENCE DETECTED_#@$^_",
            "> DATA STREAM CORRUPTED: ▓▒░█@#$%",
            "> NEURAL LINK UNSTABLE: ╬╣║╗_ERROR_",
            "> PACKET LOSS: ████░░░░ 42%",
            "> SECURITY BREACH ATTEMPT: ▓▓▓ BLOCKED"
        };
        return interferences[random.nextInt(interferences.length)];
    }
}
