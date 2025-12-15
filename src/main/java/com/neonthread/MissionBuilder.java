package com.neonthread;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder para crear misiones de forma fluida y clara (KISS + DRY).
 * Facilita la creación de misiones tanto programáticamente como desde JSON en el futuro.
 * 
 * Uso:
 * <pre>
 * Mission mission = new MissionBuilder("mission_01")
 *     .setTitle("Primera Conexión")
 *     .setDescription("...")
 *     .setType(MissionType.MAIN)
 *     .setPriority(MissionPriority.HIGH)
 *     .setReward(500, "Datos de la megacorp")
 *     .addRequirement("intro_completed")
 *     .addConsequence(c -> c.addFlagToSet("first_contact_made"))
 *     .build();
 * </pre>
 */
public class MissionBuilder {
    private final String id;
    private String title;
    private String description;
    private int rewardCredits = 0;
    private String rewardInfo = null;
    private Mission.MissionType type = Mission.MissionType.SIDE;
    private Mission.MissionPriority priority = Mission.MissionPriority.NORMAL;
    private Mission.MissionUrgency urgency = Mission.MissionUrgency.NORMAL;
    private int difficulty = 1;
    
    private List<String> requirements = new ArrayList<>();
    private List<String> unlocks = new ArrayList<>();
    private String defaultNextScene = null;
    
    // Spawn conditions
    private Integer minReputation = null;
    private Integer maxReputation = null;
    private Integer minNotoriety = null;
    private Integer maxNotoriety = null;
    private Integer minKarma = null;
    private Integer maxKarma = null;
    private List<String> requiredFlags = new ArrayList<>();
    private List<String> forbiddenFlags = new ArrayList<>();
    private String requiredDistrictState = null;
    
    // Consequences
    private List<String> flagsToSet = new ArrayList<>();
    
    // ... setters ...
    public MissionBuilder setMinReputation(int min) { this.minReputation = min; return this; }
    public MissionBuilder setMaxReputation(int max) { this.maxReputation = max; return this; }
    public MissionBuilder setMinNotoriety(int min) { this.minNotoriety = min; return this; }
    public MissionBuilder setMaxNotoriety(int max) { this.maxNotoriety = max; return this; }
    public MissionBuilder setMinKarma(int min) { this.minKarma = min; return this; }
    public MissionBuilder setMaxKarma(int max) { this.maxKarma = max; return this; }
    public MissionBuilder addRequiredFlag(String flag) { this.requiredFlags.add(flag); return this; }
    private List<String> flagsToClear = new ArrayList<>();
    private int reputationDelta = 0;
    private String districtChangeId = null;
    private List<String> narrativeItems = new ArrayList<>();
    
    public MissionBuilder(String id) {
        this.id = id;
    }
    
    // ==================== BASIC INFO ====================
    
    public MissionBuilder setTitle(String title) {
        this.title = title;
        return this;
    }
    
    public MissionBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public MissionBuilder setType(Mission.MissionType type) {
        this.type = type;
        return this;
    }
    
    public MissionBuilder setPriority(Mission.MissionPriority priority) {
        this.priority = priority;
        return this;
    }
    
    public MissionBuilder setUrgency(Mission.MissionUrgency urgency) {
        this.urgency = urgency;
        return this;
    }
    
    public MissionBuilder setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        return this;
    }
    
    // ==================== REWARDS ====================
    
    public MissionBuilder setReward(int credits, String info) {
        this.rewardCredits = credits;
        this.rewardInfo = info;
        return this;
    }
    
    public MissionBuilder setRewardCredits(int credits) {
        this.rewardCredits = credits;
        return this;
    }
    
    public MissionBuilder setRewardInfo(String info) {
        this.rewardInfo = info;
        return this;
    }
    
    // ==================== REQUIREMENTS & UNLOCKS ====================
    
    public MissionBuilder addRequirement(String requirement) {
        this.requirements.add(requirement);
        return this;
    }
    
    public MissionBuilder addUnlock(String unlock) {
        this.unlocks.add(unlock);
        return this;
    }
    
    public MissionBuilder setNextScene(String sceneId) {
        this.defaultNextScene = sceneId;
        return this;
    }
    
    // ==================== SPAWN CONDITIONS ====================
    
    public MissionBuilder addForbiddenFlag(String flag) {
        this.forbiddenFlags.add(flag);
        return this;
    }
    
    public MissionBuilder setRequiredDistrictState(String state) {
        this.requiredDistrictState = state;
        return this;
    }
    
    // ==================== CONSEQUENCES ====================
    
    public MissionBuilder addFlagToSet(String flag) {
        this.flagsToSet.add(flag);
        return this;
    }
    
    public MissionBuilder addFlagToClear(String flag) {
        this.flagsToClear.add(flag);
        return this;
    }
    
    public MissionBuilder setReputationDelta(int delta) {
        this.reputationDelta = delta;
        return this;
    }
    
    public MissionBuilder setDistrictChange(String changeId) {
        this.districtChangeId = changeId;
        return this;
    }
    
    public MissionBuilder addNarrativeItem(String item) {
        this.narrativeItems.add(item);
        return this;
    }
    
    // ==================== BUILD ====================
    
    /**
     * Construye la misión con todos los parámetros configurados.
     */
    public Mission build() {
        // Validación básica
        if (title == null || title.isEmpty()) {
            throw new IllegalStateException("Mission must have a title");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalStateException("Mission must have a description");
        }
        
        // Crear misión base
        Mission mission = new Mission(id, title, description, rewardCredits, type);
        
        // Configurar propiedades básicas
        if (rewardInfo != null) {
            mission.setRewardInfo(rewardInfo);
        }
        mission.setPriority(priority);
        mission.setUrgency(urgency);
        mission.setDifficulty(difficulty);
        
        if (defaultNextScene != null) {
            mission.setNextSceneId(defaultNextScene);
        }
        
        // Agregar requirements y unlocks
        for (String req : requirements) {
            mission.addRequirement(req);
        }
        for (String unlock : unlocks) {
            mission.addUnlock(unlock);
        }
        
        // Configurar spawn conditions
        Mission.SpawnConditions spawnConditions = mission.getSpawnConditions();
        if (minReputation != null) spawnConditions.setMinReputation(minReputation);
        if (maxReputation != null) spawnConditions.setMaxReputation(maxReputation);
        if (minNotoriety != null) spawnConditions.setMinNotoriety(minNotoriety);
        if (maxNotoriety != null) spawnConditions.setMaxNotoriety(maxNotoriety);
        if (minKarma != null) spawnConditions.setMinKarma(minKarma);
        if (maxKarma != null) spawnConditions.setMaxKarma(maxKarma);
        
        for (String flag : requiredFlags) {
            spawnConditions.addRequiredFlag(flag);
        }
        for (String flag : forbiddenFlags) {
            spawnConditions.addForbiddenFlag(flag);
        }
        if (requiredDistrictState != null) {
            spawnConditions.setRequiredDistrictState(requiredDistrictState);
        }
        
        // Configurar consequences
        Mission.MissionConsequences consequences = mission.getConsequences();
        for (String flag : flagsToSet) {
            consequences.addFlagToSet(flag);
        }
        for (String flag : flagsToClear) {
            consequences.addFlagToClear(flag);
        }
        consequences.setReputationDelta(reputationDelta);
        if (districtChangeId != null) {
            consequences.setDistrictChange(districtChangeId);
        }
        for (String item : narrativeItems) {
            consequences.addNarrativeItem(item);
        }
        
        return mission;
    }
    
    // ==================== FACTORY METHODS ====================
    
    /**
     * Crea una misión principal rápidamente.
     */
    public static MissionBuilder createMain(String id, String title, String description) {
        return new MissionBuilder(id)
            .setTitle(title)
            .setDescription(description)
            .setType(Mission.MissionType.MAIN)
            .setPriority(Mission.MissionPriority.HIGH);
    }
    
    /**
     * Crea una misión secundaria rápidamente.
     */
    public static MissionBuilder createSide(String id, String title, String description) {
        return new MissionBuilder(id)
            .setTitle(title)
            .setDescription(description)
            .setType(Mission.MissionType.SIDE);
    }
    
    /**
     * Crea una misión de intel rápidamente.
     */
    public static MissionBuilder createIntel(String id, String title, String description) {
        return new MissionBuilder(id)
            .setTitle(title)
            .setDescription(description)
            .setType(Mission.MissionType.INTEL);
    }
    
    /**
     * Crea una misión de combate rápidamente.
     */
    public static MissionBuilder createCombat(String id, String title, String description) {
        return new MissionBuilder(id)
            .setTitle(title)
            .setDescription(description)
            .setType(Mission.MissionType.COMBAT)
            .setPriority(Mission.MissionPriority.HIGH);
    }
}
