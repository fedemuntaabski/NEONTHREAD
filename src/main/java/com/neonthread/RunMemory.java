package com.neonthread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sistema de memoria de run que registra eventos importantes.
 * Permite rastrear decisiones, consecuencias y momentos clave del jugador.
 * 
 * Patrón: Memento + Observer para tracking de eventos.
 */
public class RunMemory {
    private final List<MemoryEvent> events;
    private int decisionCount = 0;
    private int consequenceCount = 0;
    
    public RunMemory() {
        this.events = new ArrayList<>();
    }
    
    /**
     * Registra un evento en la memoria del run.
     */
    public void recordEvent(MemoryEvent event) {
        events.add(event);
        
        // Actualizar contadores
        switch (event.getType()) {
            case DECISION:
                decisionCount++;
                break;
            case CONSEQUENCE:
                consequenceCount++;
                break;
        }
    }
    
    /**
     * Registra una decisión tomada por el jugador.
     */
    public void recordDecision(String missionId, String description, String choice) {
        MemoryEvent event = new MemoryEvent(
            generateId(),
            MemoryEvent.EventType.DECISION,
            description,
            choice
        );
        event.setMissionId(missionId);
        recordEvent(event);
    }
    
    /**
     * Registra una consecuencia de una decisión.
     */
    public void recordConsequence(String missionId, String description, String consequence) {
        MemoryEvent event = new MemoryEvent(
            generateId(),
            MemoryEvent.EventType.CONSEQUENCE,
            description,
            consequence
        );
        event.setMissionId(missionId);
        recordEvent(event);
    }
    
    /**
     * Registra un logro o hito importante.
     */
    public void recordAchievement(String description, String details) {
        MemoryEvent event = new MemoryEvent(
            generateId(),
            MemoryEvent.EventType.ACHIEVEMENT,
            description,
            details
        );
        recordEvent(event);
    }
    
    /**
     * Registra un evento del sistema (mundo, distrito, etc).
     */
    public void recordSystemEvent(String description, String details) {
        MemoryEvent event = new MemoryEvent(
            generateId(),
            MemoryEvent.EventType.SYSTEM,
            description,
            details
        );
        recordEvent(event);
    }
    
    /**
     * Obtiene todos los eventos.
     */
    public List<MemoryEvent> getAllEvents() {
        return Collections.unmodifiableList(events);
    }
    
    /**
     * Obtiene los eventos más recientes.
     */
    public List<MemoryEvent> getRecentEvents(int count) {
        int size = events.size();
        int start = Math.max(0, size - count);
        return new ArrayList<>(events.subList(start, size));
    }
    
    /**
     * Obtiene eventos por tipo.
     */
    public List<MemoryEvent> getEventsByType(MemoryEvent.EventType type) {
        return events.stream()
            .filter(e -> e.getType() == type)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene eventos de una misión específica.
     */
    public List<MemoryEvent> getEventsByMission(String missionId) {
        return events.stream()
            .filter(e -> missionId.equals(e.getMissionId()))
            .collect(Collectors.toList());
    }
    
    /**
     * Genera un resumen del run para mostrar en ResultScreen.
     */
    public String generateSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append("       RUN MEMORY SUMMARY\n");
        sb.append("═══════════════════════════════════════════\n\n");
        
        sb.append(String.format("Total Decisions: %d\n", decisionCount));
        sb.append(String.format("Consequences Triggered: %d\n", consequenceCount));
        sb.append(String.format("Achievements: %d\n", getEventsByType(MemoryEvent.EventType.ACHIEVEMENT).size()));
        sb.append("\n");
        
        // Mostrar eventos clave
        List<MemoryEvent> keyEvents = getRecentEvents(10);
        if (!keyEvents.isEmpty()) {
            sb.append("─── Key Moments ───\n");
            for (MemoryEvent event : keyEvents) {
                sb.append(event.toDisplayString()).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Genera ID único para eventos.
     */
    private String generateId() {
        return "event_" + System.currentTimeMillis() + "_" + events.size();
    }
    
    // Getters para estadísticas
    public int getDecisionCount() { return decisionCount; }
    public int getConsequenceCount() { return consequenceCount; }
    public int getTotalEvents() { return events.size(); }
    
    /**
     * Limpia toda la memoria (para nuevo run).
     */
    public void clear() {
        events.clear();
        decisionCount = 0;
        consequenceCount = 0;
    }
}
