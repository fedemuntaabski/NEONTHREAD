package com.neonthread;

import com.neonthread.inventory.UpgradeManager;
import java.util.HashSet;
import java.util.Set;

/**
 * Singleton que mantiene el estado de la sesión de juego actual (DRY).
 * Centraliza el acceso al personaje, distrito, misión activa y estado del mundo.
 */
public class GameSession {
    private static GameSession instance;
    
    private Character character;
    private District district;
    private Mission currentMission;
    private GameLog gameLog;
    private WorldState worldState;
    private UpgradeManager upgradeManager;
    private RunMemory runMemory;
    private DistrictModifier districtModifier;
    private FactionReputation factionReputation; // FASE 3 Feature 11
    private final Set<String> completedMissions = new HashSet<>();
    
    private GameSession() {
        this.gameLog = new GameLog();
        this.worldState = WorldState.getInstance();
        this.upgradeManager = new UpgradeManager();
        this.runMemory = new RunMemory();
        this.factionReputation = new FactionReputation();
    }
    
    public static GameSession getInstance() {
        if (instance == null) {
            instance = new GameSession();
        }
        return instance;
    }

    public UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }
    
    /**
     * Inicia una nueva sesión de juego.
     * FASE 3 Feature 9: Usa DistrictLoader para cargar distrito desde JSON.
     */
    public void startNewGame(Character character) {
        this.character = character;
        
        // FASE 3 Feature 9: Cargar distrito desde JSON
        try {
            this.district = com.neonthread.loaders.DistrictLoader.loadDistrict("theta_5");
        } catch (Exception e) {
            System.err.println("Error loading district: " + e.getMessage());
            this.district = new District("Distrito Theta-5 (Fallback)");
        }
        
        this.currentMission = null;
        this.gameLog.clear();
        this.worldState.reset();
        this.completedMissions.clear();
        this.upgradeManager = new UpgradeManager();
        this.runMemory = new RunMemory();
        this.districtModifier = new DistrictModifier(district, worldState);
        this.factionReputation.reset(); // FASE 3 Feature 11: Reset faction reputation
        
        this.gameLog.add("Sesión iniciada: " + character.getName());
        this.runMemory.recordSystemEvent("Run Started", 
            "Operator " + character.getName() + " deployed as " + character.getRole().getDisplayName());
        
        initializeStartingMissions();
        applyDistrictModifications();
    }
    
    /**
     * Inicializa las misiones de inicio.
     */
    private void initializeStartingMissions() {
        java.util.List<Mission> missions = com.neonthread.loaders.MissionLoader.loadMissions();
        
        if (missions.isEmpty()) {
            // Fallback if no missions loaded
            Mission firstMission = MissionBuilder.createMain(
                "mission_01",
                "Primera Conexión",
                "Una figura encapuchada te contacta en la esquina. Dice tener información sobre la megacorp que controla el sector."
            )
            .setReward(500, "Datos encriptados")
            .setUrgency(Mission.MissionUrgency.NORMAL)
            .setTutorial(true) // Marcar como tutorial
            .build();
            district.addMission(firstMission);
        } else {
            // Marcar la primera misión como tutorial si no está marcada
            if (!missions.isEmpty()) {
                Mission firstMission = missions.get(0);
                if (!firstMission.isTutorial()) {
                    firstMission.setTutorial(true);
                }
            }
            for (Mission mission : missions) {
                district.addMission(mission);
            }
        }
    }
    
    /**
     * Resetea la sesión (para volver al menú).
     */
    public void reset() {
        character = null;
        district = null;
        currentMission = null;
    }
    
    // Accessors
    public Character getCharacter() { return character; }
    public District getDistrict() { return district; }
    public Mission getCurrentMission() { return currentMission; }
    public GameLog getGameLog() { return gameLog; }
    public WorldState getWorldState() { return worldState; }
    public RunMemory getRunMemory() { return runMemory; }
    public DistrictModifier getDistrictModifier() { return districtModifier; }
    public FactionReputation getFactionReputation() { return factionReputation; } // FASE 3 Feature 11
    
    public void setCurrentMission(Mission mission) { this.currentMission = mission; }
    
    public boolean hasActiveSession() {
        return character != null && district != null;
    }
    
    /**
     * Aplica modificaciones del distrito según el estado del mundo.
     */
    public void applyDistrictModifications() {
        if (districtModifier != null) {
            districtModifier.applyModifications();
        }
    }

    public void registerMissionCompleted(String missionId) {
        completedMissions.add(missionId);
    }

    public boolean hasCompleted(String missionId) {
        return completedMissions.contains(missionId);
    }
}
