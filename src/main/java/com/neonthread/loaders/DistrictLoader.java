package com.neonthread.loaders;

import com.neonthread.District;
import com.neonthread.utils.SimpleJsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Carga configuración de distritos desde JSON.
 * FASE 3 Feature 9: Sistema Data-Driven completo.
 * 
 * Patrón: Factory + Data-Driven Architecture.
 */
public class DistrictLoader {
    private static final String DISTRICTS_FILE = "config/districts.json";
    
    /**
     * Carga todos los distritos disponibles.
     */
    @SuppressWarnings("unchecked")
    public static List<District> loadDistricts() {
        List<District> districts = new ArrayList<>();
        
        try {
            Path path = Paths.get(DISTRICTS_FILE);
            if (!Files.exists(path)) {
                System.err.println("Districts file not found: " + DISTRICTS_FILE);
                return createFallbackDistrict();
            }
            
            String content = new String(Files.readAllBytes(path), "UTF-8");
            List<Object> districtList = (List<Object>) SimpleJsonParser.parse(content);
            
            for (Object obj : districtList) {
                Map<String, Object> districtData = (Map<String, Object>) obj;
                District district = parseDistrict(districtData);
                districts.add(district);
            }
            
        } catch (IOException e) {
            System.err.println("Error loading districts: " + e.getMessage());
            return createFallbackDistrict();
        }
        
        return districts;
    }
    
    /**
     * Carga un distrito específico por ID.
     */
    public static District loadDistrict(String districtId) {
        List<District> districts = loadDistricts();
        for (District d : districts) {
            if (d.getId().equals(districtId)) {
                return d;
            }
        }
        
        // Fallback
        System.err.println("District not found: " + districtId);
        return createFallbackDistrict().get(0);
    }
    
    /**
     * Parsea un distrito desde Map.
     */
    @SuppressWarnings("unchecked")
    private static District parseDistrict(Map<String, Object> data) {
        String id = (String) data.get("id");
        String name = (String) data.get("name");
        String description = (String) data.getOrDefault("description", "");
        
        District district = new District(name);
        district.setId(id);
        district.setDescription(description);
        
        // Parsear locations
        if (data.containsKey("locations")) {
            List<Object> locations = (List<Object>) data.get("locations");
            for (Object locObj : locations) {
                Map<String, Object> locData = (Map<String, Object>) locObj;
                District.Location location = parseLocation(locData);
                district.addLocation(location);
            }
        }
        
        return district;
    }
    
    /**
     * Parsea una location desde Map.
     */
    private static District.Location parseLocation(Map<String, Object> data) {
        String id = (String) data.get("id");
        String name = (String) data.get("name");
        String typeStr = (String) data.get("type");
        
        Number xNum = (Number) data.getOrDefault("x", 0);
        Number yNum = (Number) data.getOrDefault("y", 0);
        int x = xNum.intValue();
        int y = yNum.intValue();
        
        boolean unlocked = (boolean) data.getOrDefault("unlocked", false);
        String description = (String) data.getOrDefault("description", "");
        
        District.LocationType type = parseLocationType(typeStr);
        
        District.Location location = new District.Location(id, name, type, x, y);
        location.setDescription(description);
        if (unlocked) {
            location.unlock();
        }
        
        return location;
    }
    
    /**
     * Convierte string a LocationType.
     */
    private static District.LocationType parseLocationType(String typeStr) {
        try {
            return District.LocationType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid location type: " + typeStr);
            return District.LocationType.INFO;
        }
    }
    
    /**
     * Crea distrito de fallback si falla la carga.
     */
    private static List<District> createFallbackDistrict() {
        List<District> fallback = new ArrayList<>();
        District district = new District("Distrito Theta-5");
        district.setId("theta_5_fallback");
        district.setDescription("Distrito de fallback - Error cargando configuración");
        
        // Agregar location básica
        District.Location safehouse = new District.Location(
            "loc_safehouse", 
            "Safe House", 
            District.LocationType.BASE, 
            200, 
            150
        );
        safehouse.unlock();
        district.addLocation(safehouse);
        
        fallback.add(district);
        return fallback;
    }
}
