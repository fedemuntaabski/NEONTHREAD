package com.neonthread.screens;

import com.neonthread.District;
import com.neonthread.GameConstants;
import com.neonthread.GameSession;
import com.neonthread.Mission;
import com.neonthread.WorldState;
import com.neonthread.localization.Localization;

import java.awt.Color;
import java.util.List;

/**
 * Presenter para MissionWindowScreen (patrón MVP).
 * Separa la lógica de presentación de la vista, facilitando testing y mantenimiento.
 * 
 * Responsabilidades:
 * - Formatear datos de Mission para la vista
 * - Manejar lógica de negocio (aceptar misión, validaciones)
 * - Intermediar entre GameSession y la vista
 */
public class MissionWindowPresenter {
    private final GameSession session;
    
    public MissionWindowPresenter() {
        this.session = GameSession.getInstance();
    }
    
    /**
     * Obtiene la misión actual o null.
     */
    public Mission getCurrentMission() {
        return session.getCurrentMission();
    }
    
    /**
     * Obtiene el título formateado de la misión.
     */
    public String getMissionTitle(Mission mission) {
        return mission != null ? mission.getTitle().toUpperCase() : "";
    }
    
    /**
     * Obtiene el subtítulo con tipo y estado.
     */
    public String getMissionSubtitle(Mission mission) {
        if (mission == null) return "";
        return String.format("[%s - %s]", 
            mission.getType().getDisplayName(),
            mission.getStatus().getDisplayName());
    }
    
    /**
     * Obtiene la descripción de la misión.
     */
    public String getMissionDescription(Mission mission) {
        return mission != null ? mission.getDescription() : "";
    }
    
    /**
     * Obtiene el texto de dificultad.
     */
    public String getDifficultyText(Mission mission) {
        return mission != null ? String.valueOf(mission.getDifficulty()) : "?";
    }
    
    /**
     * Obtiene el color según dificultad.
     */
    public Color getDifficultyColor(Mission mission) {
        if (mission == null) return GameConstants.COLOR_TEXT_SECONDARY;
        
        int difficulty = mission.getDifficulty();
        if (difficulty <= 2) return GameConstants.COLOR_CYAN_NEON;
        if (difficulty <= 4) return GameConstants.COLOR_YELLOW_NEON;
        if (difficulty <= 6) return GameConstants.COLOR_MAGENTA_NEON;
        return new Color(255, 50, 50); // Rojo para muy difícil
    }
    
    /**
     * Obtiene el texto de nivel recomendado.
     */
    public String getRecommendedLevelText(Mission mission) {
        // TODO: Implementar cálculo real basado en dificultad
        return "1";
    }
    
    /**
     * Obtiene el texto de riesgo.
     */
    public String getRiskText(Mission mission) {
        if (mission == null) return t("mission.risk.unknown");
        
        int difficulty = mission.getDifficulty();
        if (difficulty <= 2) return t("mission.risk.low");
        if (difficulty <= 4) return t("mission.risk.medium");
        if (difficulty <= 6) return t("mission.risk.high");
        return t("mission.risk.critical");
    }
    
    /**
     * Obtiene el color del riesgo.
     */
    public Color getRiskColor(Mission mission) {
        if (mission == null) return GameConstants.COLOR_TEXT_SECONDARY;
        
        int difficulty = mission.getDifficulty();
        if (difficulty <= 2) return GameConstants.COLOR_BLUE_ELECTRIC;
        if (difficulty <= 4) return GameConstants.COLOR_YELLOW_NEON;
        return GameConstants.COLOR_MAGENTA_NEON;
    }
    
    /**
     * Formatea los créditos de recompensa.
     */
    public String getRewardCreditsText(Mission mission) {
        if (mission == null) return "0";
        return String.format("%d %s", mission.getRewardCredits(), t("mission.credits"));
    }
    
    /**
     * Obtiene la lista de recompensas adicionales.
     */
    public void populateRewards(Mission mission, List<String> rewards) {
        rewards.clear();
        if (mission == null) return;
        
        rewards.add(getRewardCreditsText(mission));
        
        if (mission.getRewardInfo() != null && !mission.getRewardInfo().isEmpty()) {
            rewards.add(mission.getRewardInfo());
        }
        
        if (!mission.getUnlocks().isEmpty()) {
            for (String unlock : mission.getUnlocks()) {
                rewards.add(t("mission.unlocks") + ": " + unlock);
            }
        } else {
            rewards.add(t("mission.reward.intel"));
        }
    }
    
    /**
     * Obtiene la lista de requisitos.
     */
    public void populateRequirements(Mission mission, List<String> requirements) {
        requirements.clear();
        if (mission == null) return;
        
        if (mission.getRequirements().isEmpty()) {
            requirements.add(t("mission.requirements.none"));
        } else {
            requirements.addAll(mission.getRequirements());
        }
    }
    
    /**
     * Verifica si el jugador cumple los requisitos.
     */
    public boolean canAcceptMission(Mission mission) {
        if (mission == null) return false;
        
        // Verificar requisitos de misiones previas
        for (String requiredMissionId : mission.getRequirements()) {
            if (!session.hasCompleted(requiredMissionId)) {
                return false;
            }
        }
        
        // Verificar condiciones de spawn
        WorldState worldState = session.getWorldState();
        com.neonthread.Character character = session.getCharacter();
        return mission.canSpawn(worldState, character);
    }
    
    /**
     * Acepta la misión actual (lógica de negocio).
     */
    public AcceptMissionResult acceptMission(Mission mission) {
        if (mission == null) {
            return new AcceptMissionResult(false, t("mission.error.notfound"));
        }
        
        if (!canAcceptMission(mission)) {
            return new AcceptMissionResult(false, t("mission.error.requirements"));
        }
        
        // Actualizar estado
        mission.accept();
        
        // Registrar en log
        session.getGameLog().add(t("mission.accepted") + ": " + mission.getTitle());
        
        // Aplicar desbloqueos
        District district = session.getDistrict();
        for (String unlockId : mission.getUnlocks()) {
            district.unlockLocationById(unlockId);
            session.getGameLog().add(t("mission.unlocked") + ": " + unlockId);
        }
        
        return new AcceptMissionResult(true, mission.getNextSceneId());
    }
    
    /**
     * Obtiene el icono visual de la misión.
     */
    public String getMissionIcon(Mission mission) {
        return mission != null ? mission.getVisualIcon() : "?";
    }
    
    /**
     * Helper para traducción.
     */
    private String t(String key) {
        return Localization.get(key, key);
    }
    
    /**
     * Resultado de aceptar una misión.
     */
    public static class AcceptMissionResult {
        public final boolean success;
        public final String nextSceneId; // o mensaje de error
        
        public AcceptMissionResult(boolean success, String data) {
            this.success = success;
            this.nextSceneId = data;
        }
    }
}
