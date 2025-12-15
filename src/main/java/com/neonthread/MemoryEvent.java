package com.neonthread;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa un evento individual en la memoria del run.
 * Almacena información sobre decisiones, consecuencias y momentos clave.
 */
public class MemoryEvent {
    private final String id;
    private final EventType type;
    private final String description;
    private final String details;
    private final LocalDateTime timestamp;
    private String missionId;
    
    public MemoryEvent(String id, EventType type, String description, String details) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters
    public String getId() { return id; }
    public EventType getType() { return type; }
    public String getDescription() { return description; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getMissionId() { return missionId; }
    
    // Setter
    public void setMissionId(String missionId) { this.missionId = missionId; }
    
    /**
     * Obtiene el icono visual según el tipo de evento.
     */
    public String getIcon() {
        switch (type) {
            case DECISION: return "◆";
            case CONSEQUENCE: return "►";
            case ACHIEVEMENT: return "★";
            case SYSTEM: return "●";
            default: return "·";
        }
    }
    
    /**
     * Genera string formateado para display en UI.
     */
    public String toDisplayString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return String.format("[%s] %s %s - %s",
            timestamp.format(formatter),
            getIcon(),
            description,
            details
        );
    }
    
    /**
     * Tipos de eventos de memoria.
     */
    public enum EventType {
        DECISION("Decision"),           // Decisión tomada por el jugador
        CONSEQUENCE("Consequence"),     // Consecuencia de una acción
        ACHIEVEMENT("Achievement"),     // Logro desbloqueado
        SYSTEM("System Event");         // Evento del sistema/mundo
        
        private final String displayName;
        
        EventType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
}
