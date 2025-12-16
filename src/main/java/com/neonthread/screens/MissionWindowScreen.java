package com.neonthread.screens;

import com.neonthread.*;
import com.neonthread.localization.Localization;
import com.neonthread.ui.CyberpunkButton;
import com.neonthread.ui.MissionCard;
import com.neonthread.ui.MissionBadge;
import com.neonthread.ui.MissionSection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Ventana de misión refactorizada con patrones de diseño modernos.
 * 
 * Mejoras aplicadas:
 * - Separación de responsabilidades (MVP pattern con MissionWindowPresenter)
 * - Componentes UI reutilizables (MissionCard, MissionBadge, MissionSection)
 * - Builder pattern para construcción clara
 * - Internacionalización completa
 * - Single Responsibility Principle: esta clase solo ensambla la UI
 * - DRY: elimina código duplicado usando componentes
 */
public class MissionWindowScreen extends JPanel {
    private final Consumer<GameState> onStateChange;
    private final MissionWindowPresenter presenter;
    
    public MissionWindowScreen(Consumer<GameState> onStateChange) {
        this.onStateChange = onStateChange;
        this.presenter = new MissionWindowPresenter();
        
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 180));
    }
    
    /**
     * Muestra la ventana con la misión actual.
     * Refactorizado para usar Builder pattern y componentes reutilizables.
     */
    public void show() {
        removeAll();
        
        Mission mission = presenter.getCurrentMission();
        if (mission == null) {
            onStateChange.accept(GameState.STATE_DISTRICT_MAP);
            return;
        }
        
        // Crear card principal usando Builder
        MissionCard card = new MissionCard.Builder()
            .maxSize(750, 600)
            .build();
        
        // Ensamblar contenido usando método fluido
        card.addContent(createMissionHeader(mission))
            .addVerticalSpace(20)
            .addSeparator()
            .addVerticalSpace(15)
            .addContent(createDescriptionSection(mission))
            .addVerticalSpace(15)
            .addContent(createInfoBadges(mission))
            .addVerticalSpace(15)
            .addContent(createRiskSection(mission))  // FASE 2 Feature 8
            .addVerticalSpace(15)
            .addContent(createRewardsSection(mission))
            .addVerticalSpace(15)
            .addContent(createRequirementsSection(mission))
            .addVerticalSpace(15)
            .addContent(createOutcomesSection(mission))  // FASE 2 Feature 8
            .addVerticalSpace(20)
            .addSeparator()
            .addVerticalSpace(15)
            .addContent(createActionButtons(mission));
        
        add(card, BorderLayout.CENTER);
        
        // Animación
        card.animateFadeIn();
        
        revalidate();
        repaint();
    }
    
    /**
     * Crea el header con título e icono.
     * Refactorizado para ser más limpio y reutilizable.
     */
    private JComponent createMissionHeader(Mission mission) {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Icono de misión (usa el icono dinámico del presenter)
        JLabel iconLabel = new JLabel(presenter.getMissionIcon(mission));
        iconLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 32));
        iconLabel.setForeground(new Color(255, 215, 0));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        
        // Título
        JLabel titleLabel = new JLabel(presenter.getMissionTitle(mission));
        titleLabel.setFont(GameConstants.FONT_TITLE.deriveFont(24f));
        titleLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        
        // Subtítulo
        JLabel typeLabel = new JLabel(presenter.getMissionSubtitle(mission));
        typeLabel.setFont(GameConstants.FONT_TEXT.deriveFont(12f));
        typeLabel.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(typeLabel);
        
        return headerPanel;
    }
    
    /**
     * Crea la sección de descripción usando MissionSection.
     */
    private JComponent createDescriptionSection(Mission mission) {
        MissionSection section = new MissionSection.Builder()
            .title(t("mission.description"))
            .contentBackground(new Color(0x0A0A0F))
            .build();
        
        JTextArea descArea = new JTextArea(presenter.getMissionDescription(mission));
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setFont(GameConstants.FONT_TEXT.deriveFont(14f));
        descArea.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        descArea.setBackground(new Color(0x0A0A0F));
        descArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        descArea.setOpaque(false);
        descArea.setRows(3);
        
        section.addComponent(descArea);
        return section;
    }
    
    /**
     * Crea los badges de información usando MissionBadge componente.
     */
    private JComponent createInfoBadges(Mission mission) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Badge de dificultad (color dinámico según dificultad)
        MissionBadge difficultyBadge = new MissionBadge.Builder(
            t("mission.difficulty"),
            presenter.getDifficultyText(mission)
        )
            .valueColor(presenter.getDifficultyColor(mission))
            .borderColor(presenter.getDifficultyColor(mission))
            .build();
        
        // Badge de nivel recomendado
        MissionBadge levelBadge = new MissionBadge.Builder(
            t("mission.level.recommended"),
            presenter.getRecommendedLevelText(mission)
        )
            .valueColor(GameConstants.COLOR_CYAN_NEON)
            .borderColor(GameConstants.COLOR_CYAN_NEON)
            .build();
        
        // Badge de riesgo (color dinámico según riesgo)
        MissionBadge riskBadge = new MissionBadge.Builder(
            t("mission.risk"),
            presenter.getRiskText(mission)
        )
            .valueColor(presenter.getRiskColor(mission))
            .borderColor(presenter.getRiskColor(mission))
            .build();
        
        panel.add(difficultyBadge);
        panel.add(levelBadge);
        panel.add(riskBadge);
        
        return panel;
    }
    
    /**
     * Crea la sección de recompensas usando MissionSection.
     * El presenter maneja la lógica de formateo.
     */
    private JComponent createRewardsSection(Mission mission) {
        MissionSection section = new MissionSection.Builder()
            .title(t("mission.rewards"))
            .build();
        
        List<String> rewards = new ArrayList<>();
        presenter.populateRewards(mission, rewards);
        
        for (String reward : rewards) {
            section.addItem(reward, GameConstants.COLOR_MAGENTA_NEON);
        }
        
        return section;
    }
    
    /**
     * Crea la sección de requisitos usando MissionSection.
     */
    private JComponent createRequirementsSection(Mission mission) {
        MissionSection section = new MissionSection.Builder()
            .title(t("mission.requirements"))
            .build();
        
        List<String> requirements = new ArrayList<>();
        presenter.populateRequirements(mission, requirements);
        
        // FASE 2 Feature 8: Iconografía mejorada
        GameSession session = GameSession.getInstance();
        for (String req : requirements) {
            boolean met = presenter.isRequirementMet(req, session);
            Color color = met ? new Color(100, 255, 100) : new Color(255, 100, 100);
            String icon = met ? "✓" : "✗";
            section.addItem(icon + " " + req, color);
        }
        
        // Si hay misiones previas requeridas
        if (!mission.getRequirements().isEmpty()) {
            boolean locked = !presenter.canAcceptMission(mission);
            if (locked) {
                section.addItem("⛓ LOCKED BY PREVIOUS RUN", new Color(255, 150, 50));
            }
        }
        
        return section;
    }
    
    /**
     * FASE 2 Feature 8: Sección de nivel de riesgo.
     */
    private JComponent createRiskSection(Mission mission) {
        MissionSection section = new MissionSection.Builder()
            .title("⚠ RISK ASSESSMENT")
            .build();
        
        // Calcular nivel de riesgo basado en difficulty y urgency
        String riskLevel = presenter.calculateRiskLevel(mission);
        Color riskColor = presenter.getRiskLevelColor(riskLevel);
        
        section.addItem("Risk Level: " + riskLevel, riskColor);
        
        // Factores de riesgo específicos
        List<String> riskFactors = presenter.getRiskFactors(mission);
        for (String factor : riskFactors) {
            section.addItem("• " + factor, GameConstants.COLOR_TEXT_SECONDARY);
        }
        
        return section;
    }
    
    /**
     * FASE 2 Feature 8: Sección de posibles outcomes (parcialmente ocultos).
     */
    private JComponent createOutcomesSection(Mission mission) {
        MissionSection section = new MissionSection.Builder()
            .title("◆ POSSIBLE OUTCOMES")
            .build();
        
        List<String> outcomes = presenter.getPossibleOutcomes(mission);
        for (String outcome : outcomes) {
            section.addItem(outcome, new Color(150, 150, 200));
        }
        
        // Hint de que hay más outcomes ocultos
        section.addItem("[???] Hidden consequences...", new Color(80, 80, 100));
        
        return section;
    }
    
    /**
     * Crea los botones de acción.
     * Los listeners delegan lógica al presenter.
     */
    private JComponent createActionButtons(Mission mission) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        CyberpunkButton acceptButton = new CyberpunkButton(t("mission.accept"));
        acceptButton.setPreferredSize(new Dimension(200, 45));
        acceptButton.addActionListener(e -> handleAcceptMission(mission));
        
        // FASE 2 Feature 8: Tooltip mejorado con requisitos específicos
        if (!presenter.canAcceptMission(mission)) {
            acceptButton.setEnabled(false);
            String detailedReason = presenter.getBlockReason(mission);
            acceptButton.setToolTipText(detailedReason);
        }
        
        CyberpunkButton cancelButton = new CyberpunkButton(t("mission.cancel"));
        cancelButton.setPreferredSize(new Dimension(150, 45));
        cancelButton.addActionListener(e -> handleCancel());
        
        buttonPanel.add(acceptButton);
        buttonPanel.add(cancelButton);
        
        return buttonPanel;
    }
    
    /**
     * Maneja la acción de aceptar misión.
     * Delega lógica de negocio al presenter.
     */
    private void handleAcceptMission(Mission mission) {
        MissionWindowPresenter.AcceptMissionResult result = presenter.acceptMission(mission);
        
        if (result.success) {
            // Transición a escena narrativa
            if (result.nextSceneId != null) {
                onStateChange.accept(GameState.STATE_NARRATIVE_SCENE);
            } else {
                onStateChange.accept(GameState.STATE_NARRATIVE_SCENE);
            }
        } else {
            // Mostrar error
            JOptionPane.showMessageDialog(
                this,
                result.nextSceneId, // Mensaje de error
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    /**
     * Maneja la acción de cancelar.
     */
    private void handleCancel() {
        onStateChange.accept(GameState.STATE_DISTRICT_MAP);
    }
    
    /**
     * Helper para traducción (DRY).
     */
    private String t(String key) {
        return Localization.get(key, key);
    }
}
