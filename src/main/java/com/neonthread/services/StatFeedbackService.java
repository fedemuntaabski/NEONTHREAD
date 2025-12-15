package com.neonthread.services;

import com.neonthread.GameLog;
import com.neonthread.GameSession;
import com.neonthread.stats.StatType;

import java.awt.Color;

/**
 * Servicio centralizado para gestionar feedback visual de checks de atributos.
 * Proporciona mensajes consistentes y logs automáticos.
 * 
 * Patrón Service Layer para desacoplar lógica de feedback de UI.
 */
public class StatFeedbackService {
    
    // Colores de feedback
    public static final Color SUCCESS_COLOR = new Color(0x00FFE7); // Cyan
    public static final Color FAILURE_COLOR = new Color(0xFF00E6); // Magenta
    public static final Color CRITICAL_COLOR = new Color(0xFF1976); // Rosa
    
    /**
     * Registra un check de stat exitoso.
     * @param statType Tipo de stat verificado
     * @param requiredValue Valor mínimo requerido
     * @param actualValue Valor efectivo del personaje
     * @return Mensaje formateado para mostrar en UI
     */
    public static String logSuccess(StatType statType, int requiredValue, int actualValue) {
        String message = String.format("[%s SUCCESS] %d/%d", 
            statType.name(), actualValue, requiredValue);
        
        GameSession session = GameSession.getInstance();
        if (session.hasActiveSession()) {
            session.getGameLog().add("✓ " + message);
        }
        
        return message;
    }
    
    /**
     * Registra un check de stat fallido.
     * @param statType Tipo de stat verificado
     * @param requiredValue Valor mínimo requerido
     * @param actualValue Valor efectivo del personaje
     * @return Mensaje formateado para mostrar en UI
     */
    public static String logFailure(StatType statType, int requiredValue, int actualValue) {
        String message = String.format("[%s FAILED] %d/%d", 
            statType.name(), actualValue, requiredValue);
        
        GameSession session = GameSession.getInstance();
        if (session.hasActiveSession()) {
            session.getGameLog().add("✗ " + message);
        }
        
        return message;
    }
    
    /**
     * Genera un mensaje de feedback formateado para UI.
     * @param success Si el check fue exitoso
     * @param statType Tipo de stat
     * @param description Descripción opcional del check
     * @return HTML formateado para JLabel
     */
    public static String formatUIMessage(boolean success, StatType statType, String description) {
        String status = success ? "SUCCESS" : "FAILED";
        String colorHex = success ? "00FFE7" : "FF00E6";
        
        if (description != null && !description.isEmpty()) {
            return String.format("<html><span style='color:#%s'>[%s %s]</span> %s</html>",
                colorHex, statType.name(), status, description);
        } else {
            return String.format("<html><span style='color:#%s'>[%s %s]</span></html>",
                colorHex, statType.name(), status);
        }
    }
    
    /**
     * Obtiene el color apropiado según el resultado del check.
     * @param success Si el check fue exitoso
     * @return Color para usar en UI
     */
    public static Color getFeedbackColor(boolean success) {
        return success ? SUCCESS_COLOR : FAILURE_COLOR;
    }
    
    /**
     * Registra un check crítico (fallar puede tener consecuencias graves).
     * @param statType Tipo de stat
     * @param success Si se pasó el check
     * @param consequence Consecuencia de fallar
     */
    public static void logCriticalCheck(StatType statType, boolean success, String consequence) {
        GameSession session = GameSession.getInstance();
        if (session.hasActiveSession()) {
            String prefix = success ? "✓ [CRITICAL]" : "✗ [CRITICAL]";
            String message = String.format("%s %s check %s", 
                prefix, statType.name(), success ? "passed" : "failed");
            
            if (!success && consequence != null) {
                message += " → " + consequence;
            }
            
            session.getGameLog().add(message);
        }
    }
}
