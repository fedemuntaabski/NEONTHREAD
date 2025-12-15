package com.neonthread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa una misión del juego (KISS + DRY).
 * Incluye: ciclo de vida avanzado, consecuencias narrativas, spawn conditions,
 * prioridad, urgencia y sistema de outcomes múltiples.
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
    
    // Sistema de requisitos y spawning
    private List<String> requirements; // Misiones previas
    private SpawnConditions spawnConditions;
    
    // Sistema de resultados y consecuencias
    private Map<MissionOutcome, String> nextSceneByOutcome;
    private Map<MissionOutcome, MissionReward> rewardsByOutcome;
    private MissionConsequences consequences;
    
    // Sistema de visualización
    private MissionPriority priority;
    private MissionUrgency urgency;
    
    // Legado (mantener compatibilidad)
    @Deprecated
    private List<String> unlocks; // Locaciones/NPCs que desbloquea
    @Deprecated
    private String nextSceneId; // Primera escena narrativa (default)
    
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
        
        // Nuevos sistemas (valores por defecto)
        this.spawnConditions = new SpawnConditions();
        this.nextSceneByOutcome = new HashMap<>();
        this.rewardsByOutcome = new HashMap<>();
        this.consequences = new MissionConsequences();
        this.priority = MissionPriority.NORMAL;
        this.urgency = MissionUrgency.NORMAL;
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
    public SpawnConditions getSpawnConditions() { return spawnConditions; }
    public Map<MissionOutcome, String> getNextSceneByOutcome() { return nextSceneByOutcome; }
    public Map<MissionOutcome, MissionReward> getRewardsByOutcome() { return rewardsByOutcome; }
    public MissionConsequences getConsequences() { return consequences; }
    public MissionPriority getPriority() { return priority; }
    public MissionUrgency getUrgency() { return urgency; }
    
    // Setters
    public void setRewardInfo(String rewardInfo) { this.rewardInfo = rewardInfo; }
    public void setStatus(MissionStatus status) { this.status = status; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public void setNextSceneId(String nextSceneId) { this.nextSceneId = nextSceneId; }
    public void setPriority(MissionPriority priority) { this.priority = priority; }
    public void setUrgency(MissionUrgency urgency) { this.urgency = urgency; }
    public void addRequirement(String requirement) { this.requirements.add(requirement); }
    public void addUnlock(String unlock) { this.unlocks.add(unlock); }
    public void addNextSceneForOutcome(MissionOutcome outcome, String sceneId) { 
        this.nextSceneByOutcome.put(outcome, sceneId); 
    }
    public void addRewardForOutcome(MissionOutcome outcome, MissionReward reward) {
        this.rewardsByOutcome.put(outcome, reward);
    }
    
    /**
     * Acepta la misión.
     */
    public void accept() { 
        this.status = MissionStatus.ACCEPTED; 
    }
    
    /**
     * Completa la misión con un outcome específico.
     */
    public void complete(MissionOutcome outcome, Character character) { 
        
        switch (outcome) {
            case SUCCESS:
                this.status = MissionStatus.COMPLETED;
                break;
            case FAILURE:
                this.status = MissionStatus.FAILED;
                break;
            case PARTIAL:
                this.status = MissionStatus.COMPLETED;
                break;
            case ABORTED:
                this.status = MissionStatus.ABORTED;
                break;
        }
        
        // Aplicar consecuencias
        consequences.apply(character);
    }
    
    /**
     * Completa la misión (legacy - por defecto SUCCESS).
     */
    public void complete(Character character) { 
        complete(MissionOutcome.SUCCESS, character);
    }
    
    /**
     * Falla la misión.
     */
    public void fail(Character character) {
        complete(MissionOutcome.FAILURE, character);
    }
    
    /**
     * Aborta la misión.
     */
    public void abort(Character character) {
        complete(MissionOutcome.ABORTED, character);
    }
    
    /**
     * Marca como expirada.
     */
    public void expire() {
        this.status = MissionStatus.EXPIRED;
    }
    
    /**
     * Oculta la misión.
     */
    public void hide() {
        this.status = MissionStatus.HIDDEN;
    }
    
    /**
     * Pone la misión en cooldown (para misiones repetibles).
     */
    public void setCooldown() {
        this.status = MissionStatus.COOLDOWN;
    }
    
    /**
     * Obtiene la escena siguiente según el outcome.
     * Si no hay específica, retorna nextSceneId por defecto.
     */
    public String getNextSceneForOutcome(MissionOutcome outcome) {
        return nextSceneByOutcome.getOrDefault(outcome, nextSceneId);
    }
    
    /**
     * Obtiene la recompensa según el outcome.
     * Si no hay específica, retorna recompensa por defecto.
     */
    public MissionReward getRewardForOutcome(MissionOutcome outcome) {
        return rewardsByOutcome.getOrDefault(outcome, 
            new MissionReward(rewardCredits, rewardInfo));
    }
    
    /**
     * Verifica si la misión puede aparecer según las condiciones.
     */
    public boolean canSpawn(WorldState worldState, Character character) {
        // Verificar requisitos de misiones previas
        GameSession session = GameSession.getInstance();
        for (String requiredMissionId : requirements) {
            if (!session.hasCompleted(requiredMissionId)) {
                return false;
            }
        }
        
        return spawnConditions.isMet(worldState, character);
    }
    
    /**
     * Obtiene el símbolo visual según tipo y prioridad.
     */
    public String getVisualIcon() {
        if (priority == MissionPriority.HIGH) {
            switch (type) {
                case MAIN: return "★";
                case INTEL: return "◆";
                case COMBAT: return "▲";
                default: return "●";
            }
        } else {
            switch (type) {
                case MAIN: return "☆";
                case INTEL: return "◇";
                case COMBAT: return "△";
                default: return "○";
            }
        }
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
     * Estados de misión (ciclo de vida completo).
     */
    public enum MissionStatus {
        HIDDEN("Oculta"),
        LOCKED("Bloqueada"),
        AVAILABLE("Disponible"),
        ACCEPTED("En Progreso"),
        COMPLETED("Completada"),
        FAILED("Fallida"),
        ABORTED("Abortada"),
        EXPIRED("Expirada"),
        COOLDOWN("En Espera");
        
        private final String displayName;
        
        MissionStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
    
    /**
     * Resultados posibles de una misión.
     */
    public enum MissionOutcome {
        SUCCESS,   // Completada con éxito total
        FAILURE,   // Fallada
        PARTIAL,   // Completada parcialmente
        ABORTED    // Abandonada/abortada
    }
    
    /**
     * Prioridad visual de la misión.
     */
    public enum MissionPriority {
        LOW,
        NORMAL,
        HIGH,
        CRITICAL
    }
    
    /**
     * Urgencia de la misión (afecta expiración).
     */
    public enum MissionUrgency {
        LOW,       // Sin límite de tiempo
        NORMAL,    // Tiempo estándar
        HIGH,      // Debe hacerse pronto
        CRITICAL   // Puede expirar rápido
    }
    
    /**
     * Condiciones para que aparezca una misión (KISS).
     */
    public static class SpawnConditions {
        private int minReputation = Integer.MIN_VALUE;
        private int maxReputation = Integer.MAX_VALUE;
        private int minNotoriety = Integer.MIN_VALUE;
        private int maxNotoriety = Integer.MAX_VALUE;
        private int minKarma = Integer.MIN_VALUE;
        private int maxKarma = Integer.MAX_VALUE;
        private List<String> requiredFlags = new ArrayList<>();
        private List<String> forbiddenFlags = new ArrayList<>();
        private String requiredDistrictState = null;
        
        public void setMinReputation(int min) { this.minReputation = min; }
        public void setMaxReputation(int max) { this.maxReputation = max; }
        public void setMinNotoriety(int min) { this.minNotoriety = min; }
        public void setMaxNotoriety(int max) { this.maxNotoriety = max; }
        public void setMinKarma(int min) { this.minKarma = min; }
        public void setMaxKarma(int max) { this.maxKarma = max; }
        public void addRequiredFlag(String flag) { this.requiredFlags.add(flag); }
        public void addForbiddenFlag(String flag) { this.forbiddenFlags.add(flag); }
        public void setRequiredDistrictState(String state) { this.requiredDistrictState = state; }
        
        public boolean isMet(WorldState worldState, Character character) {
            if (worldState == null) return true;
            
            // Verificar reputación, notoriedad y karma
            if (character != null) {
                if (character.getReputation() < minReputation || character.getReputation() > maxReputation) return false;
                if (character.getNotoriety() < minNotoriety || character.getNotoriety() > maxNotoriety) return false;
                if (character.getKarma() < minKarma || character.getKarma() > maxKarma) return false;
            }
            
            // Verificar flags requeridos
            for (String flag : requiredFlags) {
                if (!worldState.hasFlag(flag)) return false;
            }
            
            // Verificar flags prohibidos
            for (String flag : forbiddenFlags) {
                if (worldState.hasFlag(flag)) return false;
            }
            
            // Verificar estado del distrito
            if (requiredDistrictState != null && 
                !requiredDistrictState.equals(worldState.getDistrictState())) {
                return false;
            }
            
            return true;
        }
    }
    
    /**
     * Consecuencias narrativas de completar una misión (DRY).
     */
    public static class MissionConsequences {
        private List<String> flagsToSet = new ArrayList<>();
        private List<String> flagsToClear = new ArrayList<>();
        private int reputationDelta = 0;
        private String districtChangeId = null;
        private List<String> narrativeItems = new ArrayList<>();
        
        public void addFlagToSet(String flag) { this.flagsToSet.add(flag); }
        public void addFlagToClear(String flag) { this.flagsToClear.add(flag); }
        public void setReputationDelta(int delta) { this.reputationDelta = delta; }
        public void setDistrictChange(String changeId) { this.districtChangeId = changeId; }
        public void addNarrativeItem(String item) { this.narrativeItems.add(item); }
        
        public List<String> getFlagsToSet() { return flagsToSet; }
        public List<String> getFlagsToClear() { return flagsToClear; }
        public int getReputationDelta() { return reputationDelta; }
        public String getDistrictChangeId() { return districtChangeId; }
        public List<String> getNarrativeItems() { return narrativeItems; }
        
        /**
         * Aplica las consecuencias al WorldState y Character.
         */
        public void apply(Character character) {
            WorldState worldState = WorldState.getInstance();
            
            // Aplicar flags
            for (String flag : flagsToSet) {
                worldState.setFlag(flag, true);
            }
            for (String flag : flagsToClear) {
                worldState.setFlag(flag, false);
            }
            
            // Aplicar reputación
            if (reputationDelta != 0) {
                // Update both for now to maintain compatibility
                worldState.modifyReputation(reputationDelta);
                if (character != null) {
                    character.setReputation(character.getReputation() + reputationDelta);
                }
            }
            
            // Aplicar items narrativos
            for (String item : narrativeItems) {
                worldState.addNarrativeItem(item);
            }
            
            // Estado del distrito (implementación futura)
            if (districtChangeId != null) {
                try {
                    worldState.applyChange(DistrictChange.valueOf(districtChangeId));
                } catch (IllegalArgumentException e) {
                    // Fallback for legacy string states if any
                    worldState.setDistrictState(districtChangeId);
                }
            }
        }
    }
    
    /**
     * Recompensa de una misión (puede variar según outcome).
     */
    public static class MissionReward {
        private int credits;
        private String info;
        private List<String> narrativeItems = new ArrayList<>();
        private int reputationBonus = 0;
        
        public MissionReward(int credits, String info) {
            this.credits = credits;
            this.info = info;
        }
        
        public int getCredits() { return credits; }
        public String getInfo() { return info; }
        public List<String> getNarrativeItems() { return narrativeItems; }
        public int getReputationBonus() { return reputationBonus; }
        
        public void setCredits(int credits) { this.credits = credits; }
        public void setInfo(String info) { this.info = info; }
        public void addNarrativeItem(String item) { this.narrativeItems.add(item); }
        public void setReputationBonus(int bonus) { this.reputationBonus = bonus; }
    }
}
