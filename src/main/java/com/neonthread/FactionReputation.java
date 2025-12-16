package com.neonthread;

import java.util.HashMap;
import java.util.Map;

/**
 * Sistema de reputación local por facción.
 * FASE 3 Feature 11: Reputación específica que afecta precios, misiones y diálogos.
 * 
 * Patrón: Strategy + Observer para reacciones basadas en reputación.
 */
public class FactionReputation {
    private final Map<String, Integer> reputationByFaction;
    
    // Umbrales de reputación
    private static final int REP_HOSTILE = -50;
    private static final int REP_UNFRIENDLY = -20;
    private static final int REP_NEUTRAL = 0;
    private static final int REP_FRIENDLY = 20;
    private static final int REP_ALLIED = 50;
    
    public FactionReputation() {
        this.reputationByFaction = new HashMap<>();
        initializeDefaultFactions();
    }
    
    /**
     * Inicializa facciones por defecto.
     */
    private void initializeDefaultFactions() {
        reputationByFaction.put("corpo", 0);           // Corporaciones
        reputationByFaction.put("street_gangs", 0);    // Pandillas callejeras
        reputationByFaction.put("netrunners", 0);      // Colectivo de hackers
        reputationByFaction.put("fixers", 0);          // Intermediarios
        reputationByFaction.put("nomads", 0);          // Nómadas del desierto
        reputationByFaction.put("police", 0);          // Seguridad corporativa
    }
    
    /**
     * Obtiene la reputación con una facción.
     */
    public int getReputation(String factionId) {
        return reputationByFaction.getOrDefault(factionId, 0);
    }
    
    /**
     * Modifica la reputación con una facción.
     */
    public void modifyReputation(String factionId, int delta) {
        int current = getReputation(factionId);
        int newRep = Math.max(-100, Math.min(100, current + delta));
        reputationByFaction.put(factionId, newRep);
        
        // Log del cambio
        String direction = delta > 0 ? "increased" : "decreased";
        System.out.println(String.format("Reputation with %s %s by %d (now: %d)", 
            factionId, direction, Math.abs(delta), newRep));
    }
    
    /**
     * Obtiene el standing con una facción.
     */
    public Standing getStanding(String factionId) {
        int rep = getReputation(factionId);
        
        if (rep >= REP_ALLIED) return Standing.ALLIED;
        if (rep >= REP_FRIENDLY) return Standing.FRIENDLY;
        if (rep >= REP_NEUTRAL) return Standing.NEUTRAL;
        if (rep >= REP_UNFRIENDLY) return Standing.UNFRIENDLY;
        return Standing.HOSTILE;
    }
    
    /**
     * Calcula el modificador de precio basado en reputación.
     */
    public double getPriceModifier(String factionId) {
        Standing standing = getStanding(factionId);
        
        switch (standing) {
            case ALLIED:     return 0.70;  // 30% descuento
            case FRIENDLY:   return 0.85;  // 15% descuento
            case NEUTRAL:    return 1.00;  // Sin cambio
            case UNFRIENDLY: return 1.25;  // 25% recargo
            case HOSTILE:    return 1.50;  // 50% recargo
            default:         return 1.00;
        }
    }
    
    /**
     * Verifica si una misión está disponible según reputación.
     */
    public boolean canAccessMission(String factionId, int requiredRep) {
        return getReputation(factionId) >= requiredRep;
    }
    
    /**
     * Obtiene el texto de diálogo según reputación.
     */
    public String getDialogueVariant(String factionId) {
        Standing standing = getStanding(factionId);
        
        switch (standing) {
            case ALLIED:     return "dialogue_allied";
            case FRIENDLY:   return "dialogue_friendly";
            case NEUTRAL:    return "dialogue_neutral";
            case UNFRIENDLY: return "dialogue_unfriendly";
            case HOSTILE:    return "dialogue_hostile";
            default:         return "dialogue_neutral";
        }
    }
    
    /**
     * Obtiene todas las facciones y sus reputaciones.
     */
    public Map<String, Integer> getAllReputations() {
        return new HashMap<>(reputationByFaction);
    }
    
    /**
     * Resetea todas las reputaciones.
     */
    public void reset() {
        reputationByFaction.clear();
        initializeDefaultFactions();
    }
    
    /**
     * Obtiene descripción del standing.
     */
    public static String getStandingDescription(Standing standing) {
        switch (standing) {
            case ALLIED:     return "Allied - Máximo descuento y acceso";
            case FRIENDLY:   return "Friendly - Descuento moderado";
            case NEUTRAL:    return "Neutral - Sin modificadores";
            case UNFRIENDLY: return "Unfriendly - Precios elevados";
            case HOSTILE:    return "Hostile - Acceso restringido";
            default:         return "Unknown";
        }
    }
    
    /**
     * Niveles de standing con facciones.
     */
    public enum Standing {
        HOSTILE,
        UNFRIENDLY,
        NEUTRAL,
        FRIENDLY,
        ALLIED
    }
}
