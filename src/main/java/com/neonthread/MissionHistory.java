package com.neonthread;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Historial de estadísticas de una misión (DRY).
 * Registra decisiones, checks, tiempo, rutas secretas y outcome final.
 */
public class MissionHistory {
    private String missionId;
    private Instant startTime;
    private Instant endTime;
    private int decisionCount;
    private int checksSucceeded;
    private int checksFailed;
    private List<String> secretRoutesDiscovered;
    private List<String> itemsCollected;
    private Map<String, Boolean> flagsActivated;
    private Mission.MissionOutcome outcome;
    
    public MissionHistory(String missionId) {
        this.missionId = missionId;
        this.startTime = Instant.now();
        this.decisionCount = 0;
        this.checksSucceeded = 0;
        this.checksFailed = 0;
        this.secretRoutesDiscovered = new ArrayList<>();
        this.itemsCollected = new ArrayList<>();
        this.flagsActivated = new HashMap<>();
        this.outcome = null; // Se establece al finalizar
    }
    
    // Getters
    public String getMissionId() { return missionId; }
    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
    public int getDecisionCount() { return decisionCount; }
    public int getChecksSucceeded() { return checksSucceeded; }
    public int getChecksFailed() { return checksFailed; }
    public List<String> getSecretRoutesDiscovered() { return secretRoutesDiscovered; }
    public List<String> getItemsCollected() { return itemsCollected; }
    public Map<String, Boolean> getFlagsActivated() { return flagsActivated; }
    public Mission.MissionOutcome getOutcome() { return outcome; }
    
    /**
     * Finaliza el historial marcando el tiempo final y el outcome.
     */
    public void finish(Mission.MissionOutcome outcome) {
        this.endTime = Instant.now();
        this.outcome = outcome;
    }
    
    /**
     * Finaliza el historial (legacy - por defecto SUCCESS).
     */
    public void finish() {
        finish(Mission.MissionOutcome.SUCCESS);
    }
    
    /**
     * Calcula el tiempo total en segundos.
     */
    public long getTotalTimeSeconds() {
        if (endTime == null) return 0;
        return Duration.between(startTime, endTime).getSeconds();
    }
    
    /**
     * Formatea el tiempo como "Xm Ys".
     */
    public String getFormattedTime() {
        long seconds = getTotalTimeSeconds();
        long minutes = seconds / 60;
        long secs = seconds % 60;
        return String.format("%dm %ds", minutes, secs);
    }
    
    /**
     * Incrementa el contador de decisiones.
     */
    public void incrementDecisions() {
        decisionCount++;
    }
    
    /**
     * Registra un check exitoso.
     */
    public void recordCheckSuccess() {
        checksSucceeded++;
    }
    
    /**
     * Registra un check fallido.
     */
    public void recordCheckFailed() {
        checksFailed++;
    }
    
    /**
     * Añade una ruta secreta descubierta.
     */
    public void addSecretRoute(String routeName) {
        if (!secretRoutesDiscovered.contains(routeName)) {
            secretRoutesDiscovered.add(routeName);
        }
    }
    
    /**
     * Añade un item recolectado.
     */
    public void addItemCollected(String itemId) {
        itemsCollected.add(itemId);
    }
    
    /**
     * Registra un flag activado.
     */
    public void setFlagActivated(String flagKey, boolean value) {
        flagsActivated.put(flagKey, value);
    }
    
    /**
     * Obtiene un resumen del resultado de la misión.
     */
    public String getOutcomeSummary() {
        if (outcome == null) return "En progreso";
        
        switch (outcome) {
            case SUCCESS:
                return "Éxito total";
            case PARTIAL:
                return "Éxito parcial";
            case FAILURE:
                return "Fallida";
            case ABORTED:
                return "Abortada";
            default:
                return "Desconocido";
        }
    }
}
