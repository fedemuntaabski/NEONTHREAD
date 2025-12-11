package com.neonthread;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Estado global del mundo del juego (Singleton).
 * Maneja flags narrativos, reputación, items narrativos y estado del distrito.
 * 
 * Implementación KISS: todo centralizado, fácil de consultar y modificar.
 */
public class WorldState {
    private static WorldState instance;
    
    // Sistema de flags narrativos
    private Map<String, Boolean> narrativeFlags;
    
    // Sistema de reputación (puede expandirse a múltiples facciones)
    private int globalReputation;
    
    // Items narrativos obtenidos (llaves, chips, datos, etc.)
    private Set<String> narrativeItems;
    
    // Estado actual del distrito
    private String districtState;
    
    private WorldState() {
        this.narrativeFlags = new HashMap<>();
        this.globalReputation = 0;
        this.narrativeItems = new HashSet<>();
        this.districtState = "normal";
    }
    
    public static WorldState getInstance() {
        if (instance == null) {
            instance = new WorldState();
        }
        return instance;
    }
    
    /**
     * Resetea el estado del mundo (para nueva partida).
     */
    public void reset() {
        narrativeFlags.clear();
        globalReputation = 0;
        narrativeItems.clear();
        districtState = "normal";
    }
    
    // ==================== FLAGS NARRATIVOS ====================
    
    /**
     * Establece un flag narrativo.
     */
    public void setFlag(String flagName, boolean value) {
        narrativeFlags.put(flagName, value);
    }
    
    /**
     * Verifica si un flag está activo.
     */
    public boolean hasFlag(String flagName) {
        return narrativeFlags.getOrDefault(flagName, false);
    }
    
    /**
     * Obtiene todos los flags activos.
     */
    public Set<String> getActiveFlags() {
        Set<String> active = new HashSet<>();
        for (Map.Entry<String, Boolean> entry : narrativeFlags.entrySet()) {
            if (entry.getValue()) {
                active.add(entry.getKey());
            }
        }
        return active;
    }
    
    /**
     * Limpia un flag específico.
     */
    public void clearFlag(String flagName) {
        narrativeFlags.put(flagName, false);
    }
    
    // ==================== REPUTACIÓN ====================
    
    /**
     * Obtiene la reputación global.
     */
    public int getReputation() {
        return globalReputation;
    }
    
    /**
     * Establece la reputación global.
     */
    public void setReputation(int reputation) {
        this.globalReputation = Math.max(-100, Math.min(100, reputation));
    }
    
    /**
     * Modifica la reputación (suma o resta).
     */
    public void modifyReputation(int delta) {
        setReputation(globalReputation + delta);
    }
    
    /**
     * Obtiene el nivel de reputación como String.
     */
    public String getReputationLevel() {
        if (globalReputation >= 80) return "Legendario";
        if (globalReputation >= 50) return "Respetado";
        if (globalReputation >= 20) return "Conocido";
        if (globalReputation >= -20) return "Neutral";
        if (globalReputation >= -50) return "Sospechoso";
        return "Enemigo";
    }
    
    // ==================== ITEMS NARRATIVOS ====================
    
    /**
     * Añade un item narrativo (llave, chip, dato, etc.).
     */
    public void addNarrativeItem(String item) {
        narrativeItems.add(item);
    }
    
    /**
     * Verifica si tiene un item narrativo.
     */
    public boolean hasNarrativeItem(String item) {
        return narrativeItems.contains(item);
    }
    
    /**
     * Remueve un item narrativo (si se consume).
     */
    public void removeNarrativeItem(String item) {
        narrativeItems.remove(item);
    }
    
    /**
     * Obtiene todos los items narrativos.
     */
    public Set<String> getNarrativeItems() {
        return new HashSet<>(narrativeItems);
    }
    
    // ==================== ESTADO DEL DISTRITO ====================
    
    /**
     * Obtiene el estado actual del distrito.
     */
    public String getDistrictState() {
        return districtState;
    }
    
    /**
     * Cambia el estado del distrito.
     * Ejemplos: "normal", "lockdown", "mercado_negro_abierto", "guerra_bandas"
     */
    public void setDistrictState(String state) {
        this.districtState = state;
    }
    
    // ==================== DEBUG Y UTILIDADES ====================
    
    /**
     * Imprime el estado actual del mundo (útil para debugging).
     */
    public String getDebugInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== WORLD STATE DEBUG ===\n");
        sb.append("Reputación: ").append(globalReputation).append(" (").append(getReputationLevel()).append(")\n");
        sb.append("Estado Distrito: ").append(districtState).append("\n");
        sb.append("Flags Activos: ").append(getActiveFlags().size()).append("\n");
        for (String flag : getActiveFlags()) {
            sb.append("  - ").append(flag).append("\n");
        }
        sb.append("Items Narrativos: ").append(narrativeItems.size()).append("\n");
        for (String item : narrativeItems) {
            sb.append("  - ").append(item).append("\n");
        }
        return sb.toString();
    }
}
