package com.neonthread;

import java.util.HashMap;
import java.util.Map;

/**
 * Aplica modificaciones visuales y funcionales al distrito según el WorldState.
 * Hace que las decisiones del jugador tengan impacto visible en el mundo.
 * 
 * Patrón: Strategy para diferentes tipos de modificaciones.
 */
public class DistrictModifier {
    private final District district;
    private final WorldState worldState;
    private final Map<String, ZoneModification> zoneModifications;
    
    public DistrictModifier(District district, WorldState worldState) {
        this.district = district;
        this.worldState = worldState;
        this.zoneModifications = new HashMap<>();
    }
    
    /**
     * Aplica todas las modificaciones basadas en el estado del mundo.
     */
    public void applyModifications() {
        applyFlagBasedChanges();
        applyReputationChanges();
        applyZoneModifications();
    }
    
    /**
     * Aplica cambios basados en flags del mundo.
     */
    private void applyFlagBasedChanges() {
        // Bloquear zonas según flags
        if (worldState.hasFlag("district_lockdown")) {
            blockZone("corporate_sector", "Corporate lockdown in effect");
        }
        
        if (worldState.hasFlag("gang_war_active")) {
            blockZone("undercity", "Gang warfare - area too dangerous");
        }
        
        // Desbloquear zonas
        if (worldState.hasFlag("corpo_access_granted")) {
            unlockZone("corporate_sector", "Access credentials acquired");
        }
        
        // Modificar disponibilidad de misiones
        if (worldState.hasFlag("first_mission_completed")) {
            // Nuevas misiones disponibles tras completar la primera
            district.refreshMissions();
        }
    }
    
    /**
     * Aplica cambios según la reputación del jugador.
     */
    private void applyReputationChanges() {
        int reputation = worldState.getReputation();
        
        if (reputation >= 50) {
            // Alta reputación: más misiones disponibles
            unlockZone("fixer_den", "Your reputation precedes you");
        } else if (reputation <= -30) {
            // Mala reputación: algunas áreas hostiles
            blockZone("market_district", "Locals won't deal with you");
        }
    }
    
    /**
     * Aplica modificaciones específicas de zona.
     */
    private void applyZoneModifications() {
        for (ZoneModification mod : zoneModifications.values()) {
            mod.apply();
        }
    }
    
    /**
     * Bloquea una zona del distrito.
     */
    public void blockZone(String zoneId, String reason) {
        ZoneModification mod = new ZoneModification(zoneId, false, reason);
        zoneModifications.put(zoneId, mod);
    }
    
    /**
     * Desbloquea una zona del distrito.
     */
    public void unlockZone(String zoneId, String reason) {
        ZoneModification mod = new ZoneModification(zoneId, true, reason);
        zoneModifications.put(zoneId, mod);
    }
    
    /**
     * Verifica si una zona está bloqueada.
     */
    public boolean isZoneBlocked(String zoneId) {
        ZoneModification mod = zoneModifications.get(zoneId);
        return mod != null && !mod.isAccessible();
    }
    
    /**
     * Obtiene el motivo del bloqueo de una zona.
     */
    public String getBlockReason(String zoneId) {
        ZoneModification mod = zoneModifications.get(zoneId);
        return mod != null ? mod.getReason() : "";
    }
    
    /**
     * Obtiene información de estado del distrito.
     */
    public String getDistrictStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("District Status:\n");
        
        int blockedZones = (int) zoneModifications.values().stream()
            .filter(m -> !m.isAccessible())
            .count();
        
        if (blockedZones > 0) {
            sb.append(String.format("  %d zone(s) restricted\n", blockedZones));
        }
        
        if (worldState.hasFlag("district_alert")) {
            sb.append("  ⚠ High Alert Status\n");
        }
        
        if (worldState.hasFlag("safe_haven")) {
            sb.append("  ✓ Safe Haven Established\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Obtiene lista de IDs de zonas bloqueadas (para visualización).
     */
    public java.util.List<String> getBlockedZones() {
        return zoneModifications.entrySet().stream()
            .filter(e -> !e.getValue().isAccessible())
            .map(Map.Entry::getKey)
            .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Clase interna para modificaciones de zona.
     */
    private static class ZoneModification {
        private final String zoneId;
        private final boolean accessible;
        private final String reason;
        
        public ZoneModification(String zoneId, boolean accessible, String reason) {
            this.zoneId = zoneId;
            this.accessible = accessible;
            this.reason = reason;
        }
        
        public void apply() {
            // Lógica de aplicación (placeholder para futura implementación)
        }
        
        public boolean isAccessible() { return accessible; }
        public String getReason() { return reason; }
    }
}
