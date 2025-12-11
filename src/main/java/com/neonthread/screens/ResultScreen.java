package com.neonthread.screens;

import com.neonthread.*;
import com.neonthread.ui.CyberpunkButton;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Pantalla de resultados completa (KISS + DRY).
 * Muestra resultado narrativo, recompensas, estadísticas y acciones.
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
     * Muestra los resultados de la misión con historial (KISS).
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
        
        // Título principal
        addTitle(contentPanel);
        
        // Resultado narrativo
        addNarrativeResult(contentPanel, mission);
        
        // Recompensas
        addRewardsSection(contentPanel, mission);
        
        // Estadísticas
        if (history != null) {
            addStatisticsSection(contentPanel, history);
        }
        
        // Botones de acción
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
     * Añade el título principal (KISS).
     */
    private void addTitle(JPanel panel) {
        JLabel titleLabel = new JLabel("MISIÓN COMPLETADA");
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
     * Añade el resultado narrativo (DRY).
     */
    private void addNarrativeResult(JPanel panel, Mission mission) {
        JLabel sectionTitle = new JLabel("RESULTADO NARRATIVO");
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
     * Genera texto narrativo del resultado (DRY).
     */
    private String generateNarrativeResult(Mission mission) {
        com.neonthread.Character character = session.getCharacter();
        
        return String.format(
            "Has completado la misión '%s'. Tu intervención en el distrito ha dejado huella. " +
            "Los datos que recopilaste serán de gran valor para futuras operaciones. " +
            "%s observa tus acciones con interés.",
            mission.getTitle(),
            character.getRole().getDisplayName()
        );
    }
    
    /**
     * Añade la sección de recompensas con animación (KISS).
     */
    private void addRewardsSection(JPanel panel, Mission mission) {
        panel.add(createSeparator());
        panel.add(Box.createVerticalStrut(25));
        
        JLabel sectionTitle = new JLabel("RECOMPENSAS");
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
        addRewardItem(rewardsPanel, "✦ Créditos ganados: +" + mission.getRewardCredits(), 
                     GameConstants.COLOR_MAGENTA_NEON);
        
        // Información
        if (mission.getRewardInfo() != null) {
            addRewardItem(rewardsPanel, "✦ " + mission.getRewardInfo(), 
                         GameConstants.COLOR_CYAN_NEON);
        } else {
            addRewardItem(rewardsPanel, "✦ Información clasificada: +1", 
                         GameConstants.COLOR_CYAN_NEON);
        }
        
        // Desbloqueos
        if (!mission.getUnlocks().isEmpty()) {
            for (String unlock : mission.getUnlocks()) {
                addRewardItem(rewardsPanel, "✦ Desbloqueado: " + unlock, 
                             GameConstants.COLOR_YELLOW_NEON);
            }
        }
        
        // Reputación
        addRewardItem(rewardsPanel, "✦ Reputación: +1", GameConstants.COLOR_BLUE_ELECTRIC);
        
        panel.add(rewardsPanel);
        panel.add(Box.createVerticalStrut(30));
    }
    
    /**
     * Añade un item de recompensa (DRY).
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
     * Añade la sección de estadísticas (KISS).
     */
    private void addStatisticsSection(JPanel panel, MissionHistory history) {
        panel.add(createSeparator());
        panel.add(Box.createVerticalStrut(25));
        
        JLabel sectionTitle = new JLabel("ESTADÍSTICAS DE LA RUN");
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
        
        addStatItem(statsGrid, "Opciones tomadas", String.valueOf(history.getDecisionCount()));
        addStatItem(statsGrid, "Checks superados", String.valueOf(history.getChecksSucceeded()));
        addStatItem(statsGrid, "Checks fallados", String.valueOf(history.getChecksFailed()));
        addStatItem(statsGrid, "Rutas secretas", String.valueOf(history.getSecretRoutesDiscovered().size()));
        addStatItem(statsGrid, "Items recolectados", String.valueOf(history.getItemsCollected().size()));
        addStatItem(statsGrid, "Tiempo total", history.getFormattedTime());
        
        panel.add(statsGrid);
        panel.add(Box.createVerticalStrut(30));
    }
    
    /**
     * Añade un item de estadística (DRY).
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
     * Añade los botones de acción (KISS).
     */
    private void addActionButtons(JPanel panel, Mission mission) {
        panel.add(createSeparator());
        panel.add(Box.createVerticalStrut(30));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        CyberpunkButton volverBtn = new CyberpunkButton("VOLVER AL DISTRITO");
        volverBtn.setPreferredSize(new Dimension(220, 50));
        volverBtn.addActionListener(e -> volverAlDistrito());
        
        CyberpunkButton repetirBtn = new CyberpunkButton("REPETIR MISIÓN");
        repetirBtn.setPreferredSize(new Dimension(180, 50));
        repetirBtn.addActionListener(e -> repetirMision(mission));
        
        buttonPanel.add(volverBtn);
        buttonPanel.add(repetirBtn);
        
        panel.add(buttonPanel);
    }
    
    /**
     * Aplica las recompensas al personaje (KISS).
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
        mission.complete();
        
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
     * Desbloquea misiones siguientes en cadena (DRY).
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
     * Limpia estados temporales (KISS).
     */
    private void clearTemporaryStates() {
        // Resetear flags temporales
        // Resetear buffs/debuffs
        // En producción, más gestión de estado aquí
    }
    
    /**
     * Vuelve al distrito con actualización (KISS).
     */
    private void volverAlDistrito() {
        clearTemporaryStates();
        rewardsApplied = false;
        onStateChange.accept(GameState.STATE_DISTRICT_MAP);
    }
    
    /**
     * Reinicia la misión (DRY).
     */
    private void repetirMision(Mission mission) {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Repetir la misión? No recibirás recompensas duplicadas.",
            "Repetir Misión",
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
     * Crea un separador horizontal (DRY).
     */
    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(GameConstants.COLOR_CYAN_NEON.darker());
        separator.setMaximumSize(new Dimension(800, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        return separator;
    }
}
