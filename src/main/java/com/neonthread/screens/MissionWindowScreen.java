package com.neonthread.screens;

import com.neonthread.*;
import com.neonthread.ui.CyberpunkButton;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Ventana de misión mejorada (KISS + DRY).
 * Panel holográfico con descripción, recompensas, requisitos y acciones.
 */
public class MissionWindowScreen extends JPanel {
    private final Consumer<GameState> onStateChange;
    private final GameSession session;
    
    public MissionWindowScreen(Consumer<GameState> onStateChange) {
        this.onStateChange = onStateChange;
        this.session = GameSession.getInstance();
        
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 180)); // Semi-transparente
    }
    
    /**
     * Muestra la ventana con la misión actual (DRY).
     */
    public void show() {
        removeAll();
        
        Mission mission = session.getCurrentMission();
        if (mission == null) {
            onStateChange.accept(GameState.STATE_DISTRICT_MAP);
            return;
        }
        
        // Panel central tipo modal holográfico
        JPanel modalPanel = new JPanel();
        modalPanel.setLayout(new BoxLayout(modalPanel, BoxLayout.Y_AXIS));
        modalPanel.setBackground(new Color(18, 18, 26, 180)); // Translucidez 70%
        modalPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON, 2),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        modalPanel.setMaximumSize(new Dimension(700, 550));
        
        // Header: Título y tipo
        addMissionHeader(modalPanel, mission);
        
        // Descripción
        addMissionDescription(modalPanel, mission);
        
        // Información lateral (dificultad, nivel, riesgo)
        addMissionInfo(modalPanel, mission);
        
        // Recompensas
        addRewardsSection(modalPanel, mission);
        
        // Requisitos
        addRequirementsSection(modalPanel, mission);
        
        // Botones de acción
        addActionButtons(modalPanel, mission);
        
        // Centrar el modal
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(modalPanel);
        
        add(wrapper, BorderLayout.CENTER);
        
        // Animación de fade-in
        animateFadeIn();
        
        revalidate();
        repaint();
    }
    
    /**
     * Agrega el header con título e icono (KISS).
     */
    private void addMissionHeader(JPanel panel, Mission mission) {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Icono de misión
        JLabel iconLabel = new JLabel("★");
        iconLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 32));
        iconLabel.setForeground(new Color(255, 215, 0)); // Dorado
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        
        // Título
        JLabel titleLabel = new JLabel(mission.getTitle().toUpperCase());
        titleLabel.setFont(GameConstants.FONT_TITLE.deriveFont(24f));
        titleLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        
        // Tipo de misión
        JLabel typeLabel = new JLabel("[" + mission.getType().getDisplayName() + " - " + 
                                      mission.getStatus().getDisplayName() + "]");
        typeLabel.setFont(GameConstants.FONT_TEXT.deriveFont(12f));
        typeLabel.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(typeLabel);
        
        panel.add(headerPanel);
        panel.add(Box.createVerticalStrut(20));
        
        // Separador
        panel.add(createSeparator());
        panel.add(Box.createVerticalStrut(15));
    }
    
    /**
     * Agrega la descripción de la misión (DRY).
     */
    private void addMissionDescription(JPanel panel, Mission mission) {
        JLabel descTitle = new JLabel("Descripción:");
        descTitle.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 14));
        descTitle.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        descTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(descTitle);
        panel.add(Box.createVerticalStrut(8));
        
        JTextArea descArea = new JTextArea(mission.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(GameConstants.FONT_TEXT.deriveFont(14f));
        descArea.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        descArea.setBackground(new Color(0x0A0A0F));
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON.darker(), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        descArea.setMaximumSize(new Dimension(620, 100));
        
        panel.add(descArea);
        panel.add(Box.createVerticalStrut(15));
    }
    
    /**
     * Agrega información de la misión (dificultad, nivel, riesgo).
     */
    private void addMissionInfo(JPanel panel, Mission mission) {
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        infoPanel.setOpaque(false);
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        addInfoBadge(infoPanel, "Dificultad", String.valueOf(mission.getDifficulty()), GameConstants.COLOR_YELLOW_NEON);
        addInfoBadge(infoPanel, "Nivel Rec.", "1", GameConstants.COLOR_CYAN_NEON);
        addInfoBadge(infoPanel, "Riesgo", "Bajo", GameConstants.COLOR_BLUE_ELECTRIC);
        
        panel.add(infoPanel);
        panel.add(Box.createVerticalStrut(15));
    }
    
    /**
     * Agrega un badge de información (DRY).
     */
    private void addInfoBadge(JPanel panel, String label, String value, Color color) {
        JPanel badge = new JPanel();
        badge.setLayout(new BoxLayout(badge, BoxLayout.Y_AXIS));
        badge.setBackground(new Color(0x0A0A0F));
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(GameConstants.FONT_TEXT.deriveFont(10f));
        labelComp.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        labelComp.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueComp = new JLabel(value);
        valueComp.setFont(GameConstants.FONT_MENU.deriveFont(14f));
        valueComp.setForeground(color);
        valueComp.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        badge.add(labelComp);
        badge.add(valueComp);
        
        panel.add(badge);
    }
    
    /**
     * Agrega la sección de recompensas (KISS).
     */
    private void addRewardsSection(JPanel panel, Mission mission) {
        JLabel rewardsTitle = new JLabel("Recompensas:");
        rewardsTitle.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 14));
        rewardsTitle.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        rewardsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(rewardsTitle);
        panel.add(Box.createVerticalStrut(8));
        
        JPanel rewardsPanel = new JPanel();
        rewardsPanel.setLayout(new BoxLayout(rewardsPanel, BoxLayout.Y_AXIS));
        rewardsPanel.setOpaque(false);
        rewardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        addRewardItem(rewardsPanel, mission.getRewardCredits() + " créditos");
        
        if (mission.getRewardInfo() != null) {
            addRewardItem(rewardsPanel, mission.getRewardInfo());
        }
        
        // Desbloqueos
        if (!mission.getUnlocks().isEmpty()) {
            for (String unlock : mission.getUnlocks()) {
                addRewardItem(rewardsPanel, "Desbloquea: " + unlock);
            }
        } else {
            addRewardItem(rewardsPanel, "+1 información clasificada");
        }
        
        panel.add(rewardsPanel);
        panel.add(Box.createVerticalStrut(15));
    }
    
    /**
     * Agrega un item de recompensa (DRY).
     */
    private void addRewardItem(JPanel panel, String text) {
        JLabel item = new JLabel("  • " + text);
        item.setFont(GameConstants.FONT_TEXT.deriveFont(13f));
        item.setForeground(GameConstants.COLOR_MAGENTA_NEON);
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(item);
    }
    
    /**
     * Agrega la sección de requisitos (KISS).
     */
    private void addRequirementsSection(JPanel panel, Mission mission) {
        JLabel reqTitle = new JLabel("Requisitos:");
        reqTitle.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 14));
        reqTitle.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        reqTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(reqTitle);
        panel.add(Box.createVerticalStrut(8));
        
        if (mission.getRequirements().isEmpty()) {
            JLabel noneLabel = new JLabel("  • Ninguno");
            noneLabel.setFont(GameConstants.FONT_TEXT.deriveFont(13f));
            noneLabel.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
            noneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(noneLabel);
        } else {
            for (String req : mission.getRequirements()) {
                JLabel reqLabel = new JLabel("  • " + req);
                reqLabel.setFont(GameConstants.FONT_TEXT.deriveFont(13f));
                reqLabel.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
                reqLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(reqLabel);
            }
        }
        
        panel.add(Box.createVerticalStrut(20));
    }
    
    /**
     * Agrega los botones de acción (DRY).
     */
    private void addActionButtons(JPanel panel, Mission mission) {
        panel.add(createSeparator());
        panel.add(Box.createVerticalStrut(15));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        CyberpunkButton acceptButton = new CyberpunkButton("ACEPTAR MISIÓN");
        acceptButton.setPreferredSize(new Dimension(200, 45));
        acceptButton.addActionListener(e -> acceptMission(mission));
        
        CyberpunkButton cancelButton = new CyberpunkButton("CANCELAR");
        cancelButton.setPreferredSize(new Dimension(150, 45));
        cancelButton.addActionListener(e -> cancelMission());
        
        buttonPanel.add(acceptButton);
        buttonPanel.add(cancelButton);
        
        panel.add(buttonPanel);
    }
    
    /**
     * Acepta la misión y ejecuta todas las acciones (KISS).
     */
    private void acceptMission(Mission mission) {
        // Paso 1: Actualizar estado interno
        mission.accept();
        
        // Paso 2: Registrar en el log
        com.neonthread.Character character = session.getCharacter();
        session.getGameLog().add("Has aceptado misión: " + mission.getTitle());
        
        // Paso 3: Desbloqueos inmediatos
        District district = session.getDistrict();
        for (String unlockId : mission.getUnlocks()) {
            district.unlockLocationById(unlockId);
            session.getGameLog().add("Desbloqueado: " + unlockId);
        }
        
        // Paso 4: Transición a la primera escena narrativa
        if (mission.getNextSceneId() != null) {
            // Cargar escena específica (funcionalidad futura)
            onStateChange.accept(GameState.STATE_NARRATIVE_SCENE);
        } else {
            // Por defecto, ir a escena narrativa genérica
            onStateChange.accept(GameState.STATE_NARRATIVE_SCENE);
        }
    }
    
    /**
     * Cancela y vuelve al mapa (KISS).
     */
    private void cancelMission() {
        // No cambiar estado de misión
        // No registrar en log
        onStateChange.accept(GameState.STATE_DISTRICT_MAP);
    }
    
    /**
     * Crea un separador horizontal (DRY).
     */
    private JSeparator createSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(GameConstants.COLOR_CYAN_NEON.darker());
        separator.setMaximumSize(new Dimension(620, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        return separator;
    }
    
    /**
     * Animación de fade-in (KISS).
     */
    private void animateFadeIn() {
        Timer fadeTimer = new Timer(20, null);
        final float[] opacity = {0.0f};
        
        fadeTimer.addActionListener(e -> {
            opacity[0] += 0.1f;
            if (opacity[0] >= 1.0f) {
                fadeTimer.stop();
            }
            repaint();
        });
        
        fadeTimer.start();
    }
}
