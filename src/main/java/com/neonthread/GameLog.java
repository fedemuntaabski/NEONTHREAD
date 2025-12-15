package com.neonthread;

import java.util.ArrayList;
import java.util.List;

/**
 * Sistema de logs del juego (KISS).
 * Registra eventos y acciones del jugador.
 */
public class GameLog {
    private List<LogEntry> entries;
    private int maxEntries;
    
    public GameLog() {
        this(100); // Máximo 100 entradas por defecto
    }
    
    public GameLog(int maxEntries) {
        this.entries = new ArrayList<>();
        this.maxEntries = maxEntries;
    }
    
    /**
     * Agrega una nueva entrada al log.
     */
    public void add(String message) {
        entries.add(new LogEntry(message, System.currentTimeMillis()));
        
        // Mantener solo las últimas N entradas
        if (entries.size() > maxEntries) {
            entries.remove(0);
        }
    }
    
    /**
     * Obtiene todas las entradas del log.
     */
    public List<LogEntry> getEntries() {
        return new ArrayList<>(entries);
    }
    
    /**
     * Obtiene las últimas N entradas.
     */
    public List<LogEntry> getRecentEntries(int count) {
        int start = Math.max(0, entries.size() - count);
        return new ArrayList<>(entries.subList(start, entries.size()));
    }
    
    /**
     * Limpia el log.
     */
    public void clear() {
        entries.clear();
    }
    
    /**
     * Entrada individual del log (DRY).
     */
    public static class LogEntry {
        private String message;
        private long timestamp;
        
        public LogEntry(String message, long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }
        
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return message;
        }
    }
}
