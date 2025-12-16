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
     * FASE 2 Feature 8: Calcula nivel de riesgo basado en difficulty y urgency.
     */
    public String calculateRiskLevel(Mission mission) {
        if (mission == null) return "UNKNOWN";
        
        int difficulty = mission.getDifficulty();
        Mission.MissionUrgency urgency = mission.getUrgency();
        
        // Base risk por difficulty
        int riskScore = difficulty * 10;
        
        // Modificador por urgency
        switch (urgency) {
            case CRITICAL: riskScore += 30; break;
            case HIGH: riskScore += 15; break;
            case NORMAL: break;
            case LOW: riskScore -= 10; break;
        }
        
        if (riskScore < 20) return "LOW";
        if (riskScore < 40) return "MEDIUM";
        if (riskScore < 65) return "HIGH";
        return "CRITICAL";
    }
    
    /**
     * FASE 2 Feature 8: Color del risk level.
     */
    public Color getRiskLevelColor(String riskLevel) {
        switch (riskLevel) {
            case "LOW": return new Color(100, 255, 100);
            case "MEDIUM": return GameConstants.COLOR_YELLOW_NEON;
            case "HIGH": return new Color(255, 150, 50);
            case "CRITICAL": return new Color(255, 50, 50);
            default: return GameConstants.COLOR_TEXT_SECONDARY;
        }
    }
    
    /**
     * FASE 2 Feature 8: Lista de factores de riesgo específicos.
     */
    public List<String> getRiskFactors(Mission mission) {
        List<String> factors = new java.util.ArrayList<>();
        if (mission == null) return factors;
        
        // Factores basados en dificultad
        if (mission.getDifficulty() >= 5) {
            factors.add("High complexity scenarios");
        }
        
        // Factores basados en urgency
        if (mission.getUrgency() == Mission.MissionUrgency.CRITICAL) {
            factors.add("Time-sensitive operation");
        }
        
        // Factores basados en requisitos
        if (!mission.getRequirements().isEmpty()) {
            factors.add("Requires previous mission completion");
        }
        
        // Factor de tipo de misión
        switch (mission.getType()) {
            case COMBAT:
                factors.add("Direct confrontation expected");
                break;
            case MAIN:
                factors.add("Critical path objective");
                break;
            case INTEL:
                factors.add("Information security breach");
                break;
        }
        
        if (factors.isEmpty()) {
            factors.add("Standard operational risks");
        }
        
        return factors;
    }
    
    /**
     * FASE 2 Feature 8: Posibles outcomes (parcialmente ocultos).
     */
    public List<String> getPossibleOutcomes(Mission mission) {
        List<String> outcomes = new java.util.ArrayList<>();
        if (mission == null) return outcomes;
        
        // Outcomes generales basados en tipo
        switch (mission.getType()) {
            case COMBAT:
                outcomes.add("› Elimination completed");
                outcomes.add("› Area secured");
                break;
            case INTEL:
                outcomes.add("› Data extracted");
                outcomes.add("› Intel recovered");
                break;
            case MAIN:
                outcomes.add("› Critical objective achieved");
                outcomes.add("› Story progression unlocked");
                break;
            case SIDE:
                outcomes.add("› Optional objective completed");
                outcomes.add("› Additional rewards earned");
                break;
            default:
                outcomes.add("› Mission objectives achieved");
        }
        
        // Hint de consecuencias narrativas
        outcomes.add("› World state affected");
        
        return outcomes;
    }
    
    /**
     * FASE 2 Feature 8: Verifica si un requisito específico se cumple.
     */
    public boolean isRequirementMet(String requirement, GameSession session) {
        // Si el requisito es un mission ID
        return session.hasCompleted(requirement);
    }
    
    /**
     * FASE 2 Feature 8: Razón detallada de bloqueo.
     */
    public String getBlockReason(Mission mission) {
        if (mission == null) return "Mission not found";
        
        com.neonthread.Character character = session.getCharacter();
        
        // Verificar misiones previas requeridas
        for (String requiredId : mission.getRequirements()) {
            if (!session.hasCompleted(requiredId)) {
                return "⛓ Requires completion of: " + requiredId;
            }
        }
        
        // Verificar spawn conditions
        Mission.SpawnConditions conditions = mission.getSpawnConditions();
        if (conditions.getMinReputation() > 0) {
            int currentRep = session.getWorldState().getReputation();
            if (currentRep < conditions.getMinReputation()) {
                return "Requires Reputation ≥ " + conditions.getMinReputation() + 
                       " (current: " + currentRep + ")";
            }
        }
        
        return "Requirements not met";
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
