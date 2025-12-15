package com.neonthread.screens;

import com.neonthread.District;
import com.neonthread.GameConstants;
import com.neonthread.GameLog;
import com.neonthread.GameSession;
import com.neonthread.GameState;
import com.neonthread.Mission;
import com.neonthread.MissionHistory;
import com.neonthread.ui.CyberpunkButton;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Pantalla de resultados post-misión.
 * Usa Template Method pattern para construcción secuencial de secciones.
 */
public class ResultScreen extends JPanel {
    private final Consumer<GameState> onStateChange;
    private final GameSession session;
    
    private MissionHistory history;
    private boolean rewardsApplied;
    
    public ResultScreen(Consumer<GameState> onStateChange) {
        this.onStateChange = onStateChange;
        this.session = GameSession.getInstance();
        this.rewardsApplied = false;
        
        setLayout(new BorderLayout());
        setBackground(new Color(0x0A0A0F));
    }
    
    /**
     * Shows mission results with history (KISS).
     */
    public void showResults(MissionHistory missionHistory) {
        this.history = missionHistory;
        removeAll();
        
        Mission mission = session.getCurrentMission();
        if (mission == null) {
            onStateChange.accept(GameState.STATE_DISTRICT_MAP);
            return;
        }
        
        // Panel central con scroll
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(0x0A0A0F));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(60, 100, 60, 100));
        
        // Main title
        addTitle(contentPanel);
        
        // Narrative result
        addNarrativeResult(contentPanel, mission);
        
        // Rewards
        addRewardsSection(contentPanel, mission);
        
        // Statistics
        if (history != null) {
            addStatisticsSection(contentPanel, history);
        }
        
        // Action buttons
        addActionButtons(contentPanel, mission);
        
        // Aplicar recompensas al personaje
        if (!rewardsApplied) {
            applyRewards(mission);
            rewardsApplied = true;
        }
        
        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(new Color(0x0A0A0F));
        
        add(scrollPane, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
    
    /**
     * Adds the main title (KISS).
     */
    private void addTitle(JPanel panel) {
        JLabel titleLabel = new JLabel("MISSION COMPLETED");
        titleLabel.setFont(GameConstants.FONT_TITLE.deriveFont(32f));
        titleLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        
        // Icono de éxito
        JLabel iconLabel = new JLabel("✓");
        iconLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 48));
        iconLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(30));
        panel.add(createSeparator());
        panel.add(Box.createVerticalStrut(30));
    }
    
    /**
     * Adds the narrative result (DRY).
     */
    private void addNarrativeResult(JPanel panel, Mission mission) {
        JLabel sectionTitle = new JLabel("NARRATIVE RESULT");
        sectionTitle.setFont(GameConstants.FONT_MENU.deriveFont(16f));
        sectionTitle.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(sectionTitle);
        panel.add(Box.createVerticalStrut(12));
        
        // Texto narrativo dinámico según misión
        String narrativeText = generateNarrativeResult(mission);
        
        JTextArea narrativeArea = new JTextArea(narrativeText);
        narrativeArea.setEditable(false);
        narrativeArea.setLineWrap(true);
        narrativeArea.setWrapStyleWord(true);
        narrativeArea.setFont(GameConstants.FONT_TEXT.deriveFont(14f));
        narrativeArea.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        narrativeArea.setBackground(new Color(0x12, 0x12, 0x1A));
        narrativeArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON.darker(), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        narrativeArea.setMaximumSize(new Dimension(800, 120));
        narrativeArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(narrativeArea);
        panel.add(Box.createVerticalStrut(30));
    }
    
    /**
     * Generates narrative result text (DRY).
     */
    private String generateNarrativeResult(Mission mission) {
        com.neonthread.Character character = session.getCharacter();
        
        return String.format(
            "You have completed the mission '%s'. Your intervention in the district has left its mark. " +
            "The data you collected will be of great value for future operations. " +
            "%s observes your actions with interest.",
            mission.getTitle(),
            character.getRole().getDisplayName()
        );
    }
    
    /**
     * Adds the rewards section with animation (KISS).
     */
    private void addRewardsSection(JPanel panel, Mission mission) {
        panel.add(createSeparator());
        panel.add(Box.createVerticalStrut(25));
        
        JLabel sectionTitle = new JLabel("REWARDS");
        sectionTitle.setFont(GameConstants.FONT_MENU.deriveFont(16f));
        sectionTitle.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(sectionTitle);
        panel.add(Box.createVerticalStrut(15));
        
        JPanel rewardsPanel = new JPanel();
        rewardsPanel.setLayout(new BoxLayout(rewardsPanel, BoxLayout.Y_AXIS));
        rewardsPanel.setBackground(new Color(0x0A0A0F));
        rewardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Créditos
        addRewardItem(rewardsPanel, "✦ Credits earned: +" + mission.getRewardCredits(), 
                     GameConstants.COLOR_MAGENTA_NEON);
        
        // Información
        if (mission.getRewardInfo() != null) {
            addRewardItem(rewardsPanel, "✦ " + mission.getRewardInfo(), 
                         GameConstants.COLOR_CYAN_NEON);
        } else {
            addRewardItem(rewardsPanel, "✦ Classified information: +1", 
                         GameConstants.COLOR_CYAN_NEON);
        }
        
        // Desbloqueos
        if (!mission.getUnlocks().isEmpty()) {
            for (String unlock : mission.getUnlocks()) {
                addRewardItem(rewardsPanel, "✦ Unlocked: " + unlock, 
                             GameConstants.COLOR_YELLOW_NEON);
            }
        }
        
        // Reputación
        addRewardItem(rewardsPanel, "✦ Reputation: +1", GameConstants.COLOR_BLUE_ELECTRIC);
        
        panel.add(rewardsPanel);
        panel.add(Box.createVerticalStrut(30));
    }
    
    /**
     * Adds a reward item (DRY).
     */
    private void addRewardItem(JPanel panel, String text, Color color) {
        JLabel itemLabel = new JLabel(text);
        itemLabel.setFont(GameConstants.FONT_TEXT.deriveFont(14f));
        itemLabel.setForeground(color);
        itemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(itemLabel);
        panel.add(Box.createVerticalStrut(8));
    }
    
    /**
     * Adds the statistics section (KISS).
     */
    private void addStatisticsSection(JPanel panel, MissionHistory history) {
        panel.add(createSeparator());
        panel.add(Box.createVerticalStrut(25));
        
        JLabel sectionTitle = new JLabel("RUN STATISTICS");
        sectionTitle.setFont(GameConstants.FONT_MENU.deriveFont(16f));
        sectionTitle.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(sectionTitle);
        panel.add(Box.createVerticalStrut(15));
        
        // Grid de estadísticas
        JPanel statsGrid = new JPanel(new GridLayout(0, 2, 30, 12));
        statsGrid.setBackground(new Color(0x0A0A0F));
        statsGrid.setMaximumSize(new Dimension(700, 200));
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        addStatItem(statsGrid, "Choices made", String.valueOf(history.getDecisionCount()));
        addStatItem(statsGrid, "Checks passed", String.valueOf(history.getChecksSucceeded()));
        addStatItem(statsGrid, "Checks failed", String.valueOf(history.getChecksFailed()));
        addStatItem(statsGrid, "Secret routes", String.valueOf(history.getSecretRoutesDiscovered().size()));
        addStatItem(statsGrid, "Items collected", String.valueOf(history.getItemsCollected().size()));
        addStatItem(statsGrid, "Total time", history.getFormattedTime());
        
        panel.add(statsGrid);
        panel.add(Box.createVerticalStrut(30));
    }
    
    /**
     * Adds a statistics item (DRY).
     */
    private void addStatItem(JPanel panel, String label, String value) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBackground(new Color(0x12, 0x12, 0x1A));
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON.darker(), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(GameConstants.FONT_TEXT.deriveFont(12f));
        labelComp.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(GameConstants.FONT_MENU.deriveFont(16f));
        valueComp.setForeground(GameConstants.COLOR_CYAN_NEON);
        
        itemPanel.add(labelComp, BorderLayout.WEST);
        itemPanel.add(valueComp, BorderLayout.EAST);
        
        panel.add(itemPanel);
    }
    
    /**
     * Adds the action buttons (KISS).
     */
    private void addActionButtons(JPanel panel, Mission mission) {
        panel.add(createSeparator());
        panel.add(Box.createVerticalStrut(30));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        CyberpunkButton volverBtn = new CyberpunkButton("RETURN TO DISTRICT");
        volverBtn.setPreferredSize(new Dimension(220, 50));
        volverBtn.addActionListener(e -> volverAlDistrito());
        
        CyberpunkButton repetirBtn = new CyberpunkButton("REPEAT MISSION");
        repetirBtn.setPreferredSize(new Dimension(180, 50));
        repetirBtn.addActionListener(e -> repetirMision(mission));
        
        buttonPanel.add(volverBtn);
        buttonPanel.add(repetirBtn);
        
        panel.add(buttonPanel);
    }
    
    /**
     * Applies rewards to the character (KISS).
     */
    private void applyRewards(Mission mission) {
        com.neonthread.Character character = session.getCharacter();
        District district = session.getDistrict();
        GameLog log = session.getGameLog();
        
        // Créditos
        character.setCredits(character.getCredits() + mission.getRewardCredits());
        
        // Reputación
        character.setReputation(character.getReputation() + 1);
        
        // Desbloqueos
        for (String unlockId : mission.getUnlocks()) {
            if (district != null) {
                district.unlockLocationById(unlockId);
            }
        }
        
        // Marcar misión completada
        mission.complete(character);
        GameSession.getInstance().registerMissionCompleted(mission.getId());
        
        // Registrar en log
        if (log != null) {
            log.add("Misión completada: " + mission.getTitle());
            log.add("Créditos ganados: +" + mission.getRewardCredits());
            log.add("Reputación aumentada: +1");
        }
        
        // Desbloquear siguiente misión (si existe)
        unlockNextMissions(mission, district);
    }
    
    /**
     * Unlocks next missions in chain (DRY).
     */
    private void unlockNextMissions(Mission completedMission, District district) {
        if (district == null) return;
        
        // Lógica de desbloqueo de misiones siguientes
        // En producción, esto vendría de un sistema de progresión
        for (Mission mission : district.getMissions()) {
            if (mission.getStatus() == Mission.MissionStatus.LOCKED) {
                // Ejemplo: desbloquear si la misión actual era prerequisito
                mission.setStatus(Mission.MissionStatus.AVAILABLE);
                break; // Solo desbloquear una por vez
            }
        }
    }
    
    /**
     * Clears temporary states (KISS).
     */
    private void clearTemporaryStates() {
        // Resetear flags temporales
        // Resetear buffs/debuffs
        // En producción, más gestión de estado aquí
    }
    
    /**
     * Returns to district with update (KISS).
     */
    private void volverAlDistrito() {
        clearTemporaryStates();
        rewardsApplied = false;
        onStateChange.accept(GameState.STATE_DISTRICT_MAP);
    }
    
    /**
     * Restarts the mission (DRY).
     */
    private void repetirMision(Mission mission) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Repeat the mission? You won't receive duplicate rewards.",
            "Repeat Mission",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Resetear estado de misión
            mission.setStatus(Mission.MissionStatus.ACCEPTED);
            
            // Resetear historial
            history = null;
            rewardsApplied = false;
            
            // Volver a escena narrativa
            onStateChange.accept(GameState.STATE_NARRATIVE_SCENE);
        }
    }
    
    /**
     * Creates a horizontal separator (DRY).
     */
    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(GameConstants.COLOR_CYAN_NEON.darker());
        separator.setMaximumSize(new Dimension(800, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        return separator;
    }
}
