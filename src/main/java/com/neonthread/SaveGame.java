package com.neonthread;

import com.neonthread.inventory.UpgradeManager;
import com.neonthread.utils.SimpleJsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Sistema de guardado de run con serialización JSON.
 * FASE 3 Feature 10: Persistencia completa del estado del juego.
 * 
 * Patrón: Memento + Serializer para captura y restauración de estado.
 */
public class SaveGame {
    private static final String SAVE_DIR = "saves";
    private static final String SAVE_FILE = "save_slot_1.json";
    private static final int SAVE_VERSION = 1;
    
    private String timestamp;
    private int version;
    
    // Estado del personaje
    private CharacterData characterData;
    
    // Estado del mundo
    private WorldStateData worldStateData;
    
    // Estado de misiones
    private MissionsData missionsData;
    
    // Estado del inventario
    private InventoryData inventoryData;
    
    // Metadata
    private String districtId;
    private int playTime; // en segundos
    
    public SaveGame() {
        this.version = SAVE_VERSION;
        this.timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.playTime = 0;
    }
    
    /**
     * Guarda el estado actual de la sesión.
     */
    public static boolean save(GameSession session) {
        try {
            SaveGame saveGame = new SaveGame();
            saveGame.captureState(session);
            saveGame.writeToFile();
            
            System.out.println("Game saved successfully: " + saveGame.timestamp);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error saving game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Carga el estado guardado.
     */
    public static SaveGame load() throws IOException {
        Path savePath = Paths.get(SAVE_DIR, SAVE_FILE);
        
        if (!Files.exists(savePath)) {
            throw new IOException("Save file not found");
        }
        
        String content = new String(Files.readAllBytes(savePath), "UTF-8");
        return deserialize(content);
    }
    
    /**
     * Verifica si existe un guardado.
     */
    public static boolean hasSavedGame() {
        return Files.exists(Paths.get(SAVE_DIR, SAVE_FILE));
    }
    
    /**
     * Captura el estado actual de la sesión.
     */
    private void captureState(GameSession session) {
        // Character
        Character character = session.getCharacter();
        if (character != null) {
            characterData = new CharacterData(character);
        }
        
        // WorldState
        WorldState worldState = session.getWorldState();
        if (worldState != null) {
            worldStateData = new WorldStateData(worldState);
        }
        
        // Missions
        District district = session.getDistrict();
        if (district != null) {
            this.districtId = district.getId();
            missionsData = new MissionsData(district, session);
        }
        
        // Inventory
        UpgradeManager upgradeManager = session.getUpgradeManager();
        if (upgradeManager != null) {
            inventoryData = new InventoryData(upgradeManager);
        }
    }
    
    /**
     * Restaura el estado en la sesión.
     */
    public void restoreState(GameSession session) {
        // TODO: Implementar restauración completa
        System.out.println("Restoring save from: " + timestamp);
    }
    
    /**
     * Escribe el guardado a archivo JSON.
     */
    private void writeToFile() throws IOException {
        Path saveDir = Paths.get(SAVE_DIR);
        if (!Files.exists(saveDir)) {
            Files.createDirectories(saveDir);
        }
        
        String json = serialize();
        Path savePath = Paths.get(SAVE_DIR, SAVE_FILE);
        Files.write(savePath, json.getBytes("UTF-8"));
    }
    
    /**
     * Serializa a JSON (formato manual para evitar dependencias).
     */
    private String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"version\": ").append(version).append(",\n");
        sb.append("  \"timestamp\": \"").append(timestamp).append("\",\n");
        sb.append("  \"playTime\": ").append(playTime).append(",\n");
        sb.append("  \"districtId\": \"").append(districtId).append("\",\n");
        
        if (characterData != null) {
            sb.append("  \"character\": ").append(characterData.toJson()).append(",\n");
        }
        
        if (worldStateData != null) {
            sb.append("  \"worldState\": ").append(worldStateData.toJson()).append(",\n");
        }
        
        if (missionsData != null) {
            sb.append("  \"missions\": ").append(missionsData.toJson()).append(",\n");
        }
        
        if (inventoryData != null) {
            sb.append("  \"inventory\": ").append(inventoryData.toJson()).append("\n");
        }
        
        sb.append("}\n");
        return sb.toString();
    }
    
    /**
     * Deserializa desde JSON.
     */
    @SuppressWarnings("unchecked")
    private static SaveGame deserialize(String json) {
        Map<String, Object> data = (Map<String, Object>) SimpleJsonParser.parse(json);
        
        SaveGame saveGame = new SaveGame();
        saveGame.version = ((Number) data.get("version")).intValue();
        saveGame.timestamp = (String) data.get("timestamp");
        saveGame.playTime = ((Number) data.getOrDefault("playTime", 0)).intValue();
        saveGame.districtId = (String) data.get("districtId");
        
        // TODO: Deserializar character, worldState, missions, inventory
        
        return saveGame;
    }
    
    /**
     * Metadata del guardado.
     */
    public String getTimestamp() { return timestamp; }
    public int getPlayTime() { return playTime; }
    public String getDistrictId() { return districtId; }
    
    // ========== Data Classes ==========
    
    /**
     * Captura estado del personaje.
     */
    private static class CharacterData {
        String name;
        String role;
        String difficulty;
        int credits;
        Map<String, Integer> baseAttributes;
        
        CharacterData(Character character) {
            this.name = character.getName();
            this.role = character.getRole().name();
            this.difficulty = character.getDifficulty().name();
            this.credits = character.getCredits();
            this.baseAttributes = new HashMap<>();
            this.baseAttributes.put("physical", character.getPhysical());
            this.baseAttributes.put("intelligence", character.getIntelligence());
            this.baseAttributes.put("charisma", character.getCharisma());
        }
        
        String toJson() {
            return "{\n" +
                   "    \"name\": \"" + name + "\",\n" +
                   "    \"role\": \"" + role + "\",\n" +
                   "    \"difficulty\": \"" + difficulty + "\",\n" +
                   "    \"credits\": " + credits + ",\n" +
                   "    \"baseAttributes\": " + baseAttributesToJson() + "\n" +
                   "  }";
        }
        
        private String baseAttributesToJson() {
            StringBuilder sb = new StringBuilder("{\n");
            for (Map.Entry<String, Integer> entry : baseAttributes.entrySet()) {
                sb.append("      \"").append(entry.getKey()).append("\": ").append(entry.getValue()).append(",\n");
            }
            // Remove trailing comma
            if (!baseAttributes.isEmpty()) {
                sb.setLength(sb.length() - 2);
                sb.append("\n");
            }
            sb.append("    }");
            return sb.toString();
        }
    }
    
    /**
     * Captura estado del mundo.
     */
    private static class WorldStateData {
        int reputation;
        List<String> flags;
        
        WorldStateData(WorldState worldState) {
            this.reputation = worldState.getReputation();
            this.flags = new ArrayList<>(worldState.getActiveFlags());
        }
        
        String toJson() {
            StringBuilder sb = new StringBuilder("{\n");
            sb.append("    \"reputation\": ").append(reputation).append(",\n");
            sb.append("    \"flags\": [\n");
            for (int i = 0; i < flags.size(); i++) {
                sb.append("      \"").append(flags.get(i)).append("\"");
                if (i < flags.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("    ]\n");
            sb.append("  }");
            return sb.toString();
        }
    }
    
    /**
     * Captura estado de misiones.
     */
    private static class MissionsData {
        List<String> completedMissions;
        String currentMissionId;
        
        MissionsData(District district, GameSession session) {
            this.completedMissions = new ArrayList<>();
            
            // Obtener misiones completadas
            for (Mission mission : district.getMissions()) {
                if (mission.getStatus() == Mission.MissionStatus.COMPLETED) {
                    completedMissions.add(mission.getId());
                }
            }
            
            // Misión actual
            Mission current = session.getCurrentMission();
            this.currentMissionId = (current != null) ? current.getId() : null;
        }
        
        String toJson() {
            StringBuilder sb = new StringBuilder("{\n");
            sb.append("    \"completed\": [\n");
            for (int i = 0; i < completedMissions.size(); i++) {
                sb.append("      \"").append(completedMissions.get(i)).append("\"");
                if (i < completedMissions.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("    ],\n");
            sb.append("    \"currentMissionId\": ");
            if (currentMissionId != null) {
                sb.append("\"").append(currentMissionId).append("\"");
            } else {
                sb.append("null");
            }
            sb.append("\n  }");
            return sb.toString();
        }
    }
    
    /**
     * Captura estado del inventario.
     */
    private static class InventoryData {
        List<String> upgrades;
        
        InventoryData(UpgradeManager upgradeManager) {
            this.upgrades = new ArrayList<>();
            // TODO: Obtener upgrades del manager
        }
        
        String toJson() {
            return "{\n    \"upgrades\": []\n  }";
        }
    }
}
