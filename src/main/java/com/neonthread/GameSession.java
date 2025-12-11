package com.neonthread;

/**
 * Singleton que mantiene el estado de la sesión de juego actual (DRY).
 * Centraliza el acceso al personaje, distrito y misión activa.
 */
public class GameSession {
    private static GameSession instance;
    
    private Character character;
    private District district;
    private Mission currentMission;
    private GameLog gameLog;
    
    private GameSession() {
        this.gameLog = new GameLog();
    }
    
    public static GameSession getInstance() {
        if (instance == null) {
            instance = new GameSession();
        }
        return instance;
    }
    
    /**
     * Inicia una nueva sesión de juego.
     */
    public void startNewGame(Character character) {
        this.character = character;
        this.district = new District("Distrito Theta-5");
        this.currentMission = null;
        this.gameLog.clear();
        this.gameLog.add("Sesión iniciada: " + character.getName());
        initializeStartingMissions();
    }
    
    /**
     * Inicializa las misiones de inicio.
     */
    private void initializeStartingMissions() {
        Mission firstMission = new Mission(
            "mission_01",
            "Primera Conexión",
            "Una figura encapuchada te contacta en la esquina. Dice tener información sobre la megacorp que controla el sector.",
            500,
            Mission.MissionType.MAIN
        );
        district.addMission(firstMission);
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
    public void setCurrentMission(Mission mission) { this.currentMission = mission; }
    
    public boolean hasActiveSession() {
        return character != null && district != null;
    }
}
