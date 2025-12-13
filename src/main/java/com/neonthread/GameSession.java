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
    private final Set<String> completedMissions = new HashSet<>();
    
    private GameSession() {
        this.gameLog = new GameLog();
        this.worldState = WorldState.getInstance();
        this.upgradeManager = new UpgradeManager();
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
     */
    public void startNewGame(Character character) {
        this.character = character;
        this.district = new District("Distrito Theta-5");
        this.currentMission = null;
        this.gameLog.clear();
        this.worldState.reset();
        this.completedMissions.clear();
        this.upgradeManager = new UpgradeManager();
        this.gameLog.add("Sesión iniciada: " + character.getName());
        initializeStartingMissions();
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
            .build();
            district.addMission(firstMission);
        } else {
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
    
    // Getters y setters
    public Character getCharacter() { return character; }
    public District getDistrict() { return district; }
    public Mission getCurrentMission() { return currentMission; }
    public GameLog getGameLog() { return gameLog; }
    public WorldState getWorldState() { return worldState; }
    public void setCurrentMission(Mission mission) { this.currentMission = mission; }
    
    public boolean hasActiveSession() {
        return character != null && district != null;
    }

    public void registerMissionCompleted(String missionId) {
        completedMissions.add(missionId);
    }

    public boolean hasCompleted(String missionId) {
        return completedMissions.contains(missionId);
    }
}
