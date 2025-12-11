package com.neonthread;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una misión del juego (KISS).
 */
public class Mission {
    private String id;
    private String title;
    private String description;
    private int rewardCredits;
    private String rewardInfo;
    private MissionType type;
    private MissionStatus status;
    private int difficulty;
    private List<String> requirements;
    private List<String> unlocks; // Locaciones/NPCs que desbloquea
    private String nextSceneId; // Primera escena narrativa
    private boolean completed;
    
    public Mission(String id, String title, String description, int rewardCredits, MissionType type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.rewardCredits = rewardCredits;
        this.type = type;
        this.status = MissionStatus.AVAILABLE;
        this.difficulty = 1;
        this.requirements = new ArrayList<>();
        this.unlocks = new ArrayList<>();
        this.nextSceneId = null;
        this.completed = false;
    }
    
    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getRewardCredits() { return rewardCredits; }
    public String getRewardInfo() { return rewardInfo; }
    public MissionType getType() { return type; }
    public MissionStatus getStatus() { return status; }
    public int getDifficulty() { return difficulty; }
    public List<String> getRequirements() { return requirements; }
    public List<String> getUnlocks() { return unlocks; }
    public String getNextSceneId() { return nextSceneId; }
    
    // Setters
    public void setRewardInfo(String rewardInfo) { this.rewardInfo = rewardInfo; }
    public void setStatus(MissionStatus status) { this.status = status; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public void setNextSceneId(String nextSceneId) { this.nextSceneId = nextSceneId; }
    public void addRequirement(String requirement) { this.requirements.add(requirement); }
    public void addUnlock(String unlock) { this.unlocks.add(unlock); }
    public void accept() { this.status = MissionStatus.ACCEPTED; }
    public void complete() { 
        this.completed = true;
        this.status = MissionStatus.COMPLETED;
    }
    
    /**
     * Tipos de misiones.
     */
    public enum MissionType {
        MAIN("Principal"),
        SIDE("Secundaria"),
        INTEL("Inteligencia"),
        COMBAT("Combate");
        
        private final String displayName;
        
        MissionType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
    
    /**
     * Estados de misión (DRY).
     */
    public enum MissionStatus {
        LOCKED("Bloqueada"),
        AVAILABLE("Disponible"),
        ACCEPTED("En Progreso"),
        COMPLETED("Completada");
        
        private final String displayName;
        
        MissionStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
}
