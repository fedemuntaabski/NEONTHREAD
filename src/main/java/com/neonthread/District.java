package com.neonthread;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa el distrito (mapa hub) del juego (KISS).
 */
public class District {
    private String name;
    private List<Location> locations;
    private List<Mission> availableMissions;
    
    public District(String name) {
        this.name = name;
        this.locations = new ArrayList<>();
        this.availableMissions = new ArrayList<>();
        initializeDefaultLocations();
    }
    
    private void initializeDefaultLocations() {
        locations.add(new Location("apartment", "Apartamento", LocationType.BASE));
        locations.add(new Location("black_market", "Mercado Negro", LocationType.SHOP));
        locations.add(new Location("network_node", "Nodo de Red", LocationType.HACK));
        locations.add(new Location("street_corner", "Esquina Urbana", LocationType.MISSION));
    }
    
    public String getName() { return name; }
    public List<Location> getLocations() { return locations; }
    public List<Mission> getAvailableMissions() { return availableMissions; }
    public List<Mission> getMissions() { return availableMissions; }
    
    public void addMission(Mission mission) {
        availableMissions.add(mission);
    }
    
    public void unlockLocation(Location location) {
        if (!locations.contains(location)) {
            locations.add(location);
        }
    }
    
    /**
     * Desbloquea una ubicación por ID (DRY).
     */
    public void unlockLocationById(String locationId) {
        for (Location loc : locations) {
            if (loc.getId().equals(locationId)) {
                loc.unlock();
                return;
            }
        }
    }
    
    /**
     * Busca una ubicación por ID.
     */
    public Location findLocationById(String id) {
        for (Location loc : locations) {
            if (loc.getId().equals(id)) {
                return loc;
            }
        }
        return null;
    }
    
    /**
     * Representa una localización en el distrito con coordenadas (DRY).
     */
    public static class Location {
        private String id;
        private String name;
        private LocationType type;
        private boolean unlocked;
        private int x; // Posición X en el mapa
        private int y; // Posición Y en el mapa
        
        public Location(String id, String name, LocationType type) {
            this(id, name, type, 0, 0);
        }
        
        public Location(String id, String name, LocationType type, int x, int y) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.x = x;
            this.y = y;
            this.unlocked = true; // Por defecto desbloqueado
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public LocationType getType() { return type; }
        public boolean isUnlocked() { return unlocked; }
        public int getX() { return x; }
        public int getY() { return y; }
        public void setPosition(int x, int y) { this.x = x; this.y = y; }
        public void unlock() { this.unlocked = true; }
        public void lock() { this.unlocked = false; }
    }
    
    /**
     * Refresca las misiones disponibles según el estado del mundo.
     * Útil para aplicar modificaciones del distrito.
     */
    public void refreshMissions() {
        // Placeholder para lógica de refrescado dinámico
        // Puede cargar nuevas misiones o cambiar disponibilidad según flags
    }
    
    /**
     * Tipos de locaciones.
     */
    public enum LocationType {
        BASE("Base"),
        SHOP("Tienda"),
        HACK("Punto de Hackeo"),
        MISSION("Misión"),
        INFO("Información");
        
        private final String displayName;
        
        LocationType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
}
