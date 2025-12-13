package com.neonthread.screens;

import com.neonthread.*;
import com.neonthread.ui.CyberpunkButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * Pantalla del mapa del distrito - HUB principal del juego (KISS + DRY).
 * Mapa minimalista geométrico con sistema de locaciones interactivas.
 */
public class DistrictMapScreen extends JPanel {
    private final Consumer<GameState> onStateChange;
    private final GameSession session;
    private final GameSettings settings;
    
    private JPanel statsPanel;
    private DistrictMapPanel mapPanel;
    private JPanel bottomBar;
    private JPanel rightPanel;

    private JTextArea historyArea;
    private JLabel contextTitle;
    private JTextArea contextBody;
    private JPanel contextActions;

    private boolean showAdvancedStats = false;
    private boolean showTooltips = true;
    private boolean enteredDistrictOnce = false;

    // Bottom bar live components
    private JLabel timeLabel;
    private JLabel notifLabel;
    private JLabel districtLabel;
    private JLabel weatherIcon;
    private JLabel alertIcon;
    private JLabel stateIcon;
    
    public DistrictMapScreen(Consumer<GameState> onStateChange) {
        this.onStateChange = onStateChange;
        this.session = GameSession.getInstance();
        this.settings = GameSettings.getInstance();
        
        setLayout(new BorderLayout());
        setBackground(new Color(0x0A0A0F)); // Fondo casi negro
        
        buildUI();

        setupDistrictShortcuts();
    }

    private void setupDistrictShortcuts() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "pauseGame");
        // Removed to let global handler take over

        inputMap.put(KeyStroke.getKeyStroke('L'), "openLogs");
        actionMap.put("openLogs", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showLogs();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke('T'), "toggleTooltips");
        actionMap.put("toggleTooltips", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showTooltips = !showTooltips;
                refreshMapOnly();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke('I'), "openInventory");
        actionMap.put("openInventory", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                onStateChange.accept(GameState.STATE_INVENTORY);
            }
        });

        inputMap.put(KeyStroke.getKeyStroke('M'), "selectNearestMission");
        actionMap.put("selectNearestMission", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                mapPanel.selectNearestMissionToCursor();
            }
        });
    }
    
    private void buildUI() {
        // Panel lateral izquierdo: Stats del jugador
        statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.WEST);
        
        // Mapa central: Vista geométrica del distrito
        mapPanel = new DistrictMapPanel();
        add(mapPanel, BorderLayout.CENTER);
        
        // Barra inferior: Estado del mundo
        bottomBar = createBottomBar();
        add(bottomBar, BorderLayout.SOUTH);

        // Panel derecho: mini-historial + panel contextual
        rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.EAST);
        
        // Botón de pausa (top-right)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        topPanel.setOpaque(false);
        CyberpunkButton pauseButton = new CyberpunkButton("PAUSE");
        pauseButton.addActionListener(e -> onStateChange.accept(GameState.STATE_PAUSE));
        topPanel.add(pauseButton);
        add(topPanel, BorderLayout.NORTH);
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(0x11131A, true));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 2, 0, 0, getAccentColor().darker()),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setPreferredSize(new Dimension(300, 0));

        panel.add(createMiniHistoryPanel(), BorderLayout.CENTER);
        panel.add(createContextPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JComponent createMiniHistoryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel(t("district.history"));
        title.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 12));
        title.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        panel.add(title, BorderLayout.NORTH);

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setFont(GameConstants.FONT_TEXT.deriveFont(12f));
        historyArea.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        historyArea.setBackground(new Color(0x0A0F1B));
        historyArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(historyArea);
        scroll.setBorder(BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON.darker(), 1));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JComponent createContextPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        contextTitle = new JLabel(t("district.context"));
        contextTitle.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 12));
        contextTitle.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        header.add(contextTitle, BorderLayout.NORTH);
        panel.add(header, BorderLayout.NORTH);

        contextBody = new JTextArea();
        contextBody.setEditable(false);
        contextBody.setLineWrap(true);
        contextBody.setWrapStyleWord(true);
        contextBody.setFont(GameConstants.FONT_TEXT.deriveFont(12f));
        contextBody.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        contextBody.setBackground(new Color(0x0A0F1B));
        contextBody.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON.darker(), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        contextBody.setText(t("district.context.empty"));
        contextBody.setRows(6);
        panel.add(contextBody, BorderLayout.CENTER);

        contextActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        contextActions.setOpaque(false);
        panel.add(contextActions, BorderLayout.SOUTH);

        return panel;
    }
    
    /**
     * Panel lateral izquierdo con información del jugador (DRY).
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(0x11131A, true)); // 60% opacidad
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 2, GameConstants.COLOR_CYAN_NEON),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(250, 0));
        
        return panel;
    }
    
    /**
     * Barra inferior con estado del mundo (KISS).
     */
    private JPanel createBottomBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x0F1015));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GameConstants.COLOR_CYAN_NEON.darker()),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        panel.setPreferredSize(new Dimension(0, 50));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        weatherIcon = createBottomIcon("W");
        alertIcon = createBottomIcon("!");
        stateIcon = createBottomIcon("i");
        left.add(weatherIcon);
        left.add(alertIcon);
        left.add(stateIcon);

        timeLabel = new JLabel();
        timeLabel.setFont(GameConstants.FONT_TEXT);
        timeLabel.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        left.add(Box.createHorizontalStrut(8));
        left.add(timeLabel);

        left.add(Box.createHorizontalStrut(15));
        
        JLabel invLabel = createBottomIcon("INV");
        invLabel.setToolTipText("Inventory (I)");
        invLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onStateChange.accept(GameState.STATE_INVENTORY);
            }
        });
        left.add(invLabel);

        notifLabel = new JLabel();
        notifLabel.setFont(GameConstants.FONT_TEXT);
        notifLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        notifLabel.setHorizontalAlignment(SwingConstants.CENTER);

        districtLabel = new JLabel();
        districtLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 12));
        districtLabel.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        districtLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(left, BorderLayout.WEST);
        panel.add(notifLabel, BorderLayout.CENTER);
        panel.add(districtLabel, BorderLayout.EAST);

        wireBottomBarPopups(panel);
        return panel;
    }

    private JLabel createBottomIcon(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 14));
        label.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return label;
    }

    private void wireBottomBarPopups(JComponent parent) {
        weatherIcon.setToolTipText("Weather");
        alertIcon.setToolTipText("Alerts");
        stateIcon.setToolTipText("District State");

        weatherIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPopupMenu menu = new JPopupMenu();
                JMenuItem item = new JMenuItem("Light rain / 23:48");
                menu.add(item);
                Point p = SwingUtilities.convertPoint(weatherIcon.getParent(), weatherIcon.getX(), weatherIcon.getY(), parent);
                menu.show(parent, p.x, Math.max(0, p.y - 30));
            }
        });

        alertIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GameLog log = session.getGameLog();
                String message = (log != null && !log.getEntries().isEmpty())
                    ? log.getRecentEntries(1).get(0).getMessage()
                    : "No alerts";

                JPopupMenu menu = new JPopupMenu();
                JMenuItem item = new JMenuItem(message);
                menu.add(item);
                Point p = SwingUtilities.convertPoint(alertIcon.getParent(), alertIcon.getX(), alertIcon.getY(), parent);
                menu.show(parent, p.x, Math.max(0, p.y - 30));
            }
        });

        stateIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                WorldState worldState = session.getWorldState();
                String state = worldState != null ? worldState.getDistrictState() : null;
                if (state == null || state.trim().isEmpty()) state = "stable";

                JPopupMenu menu = new JPopupMenu();
                JMenuItem item = new JMenuItem("State: " + state);
                menu.add(item);
                Point p = SwingUtilities.convertPoint(stateIcon.getParent(), stateIcon.getX(), stateIcon.getY(), parent);
                menu.show(parent, p.x, Math.max(0, p.y - 30));
            }
        });
    }
    
    /**
     * Actualiza toda la interfaz con los datos actuales (DRY).
     */
    public void refresh() {
        if (!enteredDistrictOnce) {
            enteredDistrictOnce = true;
            applyDistrictMapConfig();
            maybeTriggerDistrictEvent();
        }

        refreshStatsPanel();
        refreshBottomBar();
        refreshHistoryPanel();
        refreshContextPanel();
        mapPanel.refresh();
    }

    public void refreshStatsOnly() {
        refreshStatsPanel();
    }

    public void refreshBottomBarOnly() {
        refreshBottomBar();
    }

    public void refreshHistoryOnly() {
        refreshHistoryPanel();
    }

    public void refreshContextOnly() {
        refreshContextPanel();
    }

    public void refreshMapOnly() {
        mapPanel.refresh();
    }

    private void refreshHistoryPanel() {
        if (historyArea == null) return;

        GameLog log = session.getGameLog();
        if (log == null) {
            historyArea.setText("");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (GameLog.LogEntry entry : log.getRecentEntries(12)) {
            sb.append("• ").append(entry.getMessage()).append("\n");
        }
        historyArea.setText(sb.toString());
        historyArea.setCaretPosition(Math.max(0, historyArea.getDocument().getLength()));
    }

    private void refreshContextPanel() {
        // No-op unless selection changes (kept for symmetry)
    }
    
    /**
     * Actualiza el panel de stats del jugador.
     */
    private void refreshStatsPanel() {
        statsPanel.removeAll();
        
        com.neonthread.Character character = session.getCharacter();
        if (character == null) return;

        // Header
        addStatLabel(statsPanel, t("hud.operator"), GameConstants.FONT_TITLE.deriveFont(20f), getAccentColor(), t("hud.operator.tip"));
        statsPanel.add(Box.createVerticalStrut(10));

        // Sub-header
        addStatLabel(statsPanel, character.getName(), GameConstants.FONT_MENU.deriveFont(18f), GameConstants.COLOR_TEXT_PRIMARY, null);
        addStatLabel(statsPanel, character.getRole().getDisplayName(), GameConstants.FONT_TEXT.deriveFont(12f), GameConstants.COLOR_TEXT_SECONDARY, null);
        statsPanel.add(Box.createVerticalStrut(15));

        // Category 1: Physical
        addCategoryHeader(statsPanel, t("hud.physical"));
        addStatBar(statsPanel, t("hud.health"), character.getHealth(), GameConstants.COLOR_RED_NEON, t("hud.health.tip"));
        addStatBar(statsPanel, t("hud.energy"), character.getEnergy(), getAccentColor(), t("hud.energy.tip"));
        statsPanel.add(Box.createVerticalStrut(10));

        // Category 2: Social
        addCategoryHeader(statsPanel, t("hud.social"));
        addStatValue(statsPanel, t("hud.karma"), String.valueOf(character.getKarma()), t("hud.karma.tip"));
        addStatValue(statsPanel, t("hud.notoriety"), String.valueOf(character.getNotoriety()), t("hud.notoriety.tip"));
        statsPanel.add(Box.createVerticalStrut(10));

        // Category 3: Resources
        addCategoryHeader(statsPanel, t("hud.resources"));
        addStatValue(statsPanel, t("hud.credits"), String.valueOf(character.getCredits()), t("hud.credits.tip"));
        addStatValue(statsPanel, t("hud.level"), String.valueOf(character.getLevel()), t("hud.level.tip"));

        statsPanel.add(Box.createVerticalStrut(15));
        CyberpunkButton advancedBtn = new CyberpunkButton(showAdvancedStats ? t("hud.advanced.hide") : t("hud.advanced.show"));
        advancedBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        advancedBtn.addActionListener(e -> {
            showAdvancedStats = !showAdvancedStats;
            refreshStatsOnly();
        });
        statsPanel.add(advancedBtn);

        if (showAdvancedStats) {
            statsPanel.add(Box.createVerticalStrut(10));
            addCategoryHeader(statsPanel, t("hud.advanced"));
            statsPanel.add(Box.createVerticalStrut(5));

            // Derived names to match spec (kept KISS: derived from existing attributes)
            addStatValue(statsPanel, t("hud.force"), String.valueOf(character.getPhysical()), t("hud.force.tip"));
            addStatValue(statsPanel, t("hud.technique"), String.valueOf(character.getIntelligence()), t("hud.technique.tip"));
            addStatValue(statsPanel, t("hud.hack"), String.valueOf(character.getIntelligence()), t("hud.hack.tip"));
            addStatValue(statsPanel, t("hud.stealth"), String.valueOf(character.getPerception()), t("hud.stealth.tip"));
        }
        
        // Botones de acción
        statsPanel.add(Box.createVerticalStrut(20));
        CyberpunkButton logsButton = new CyberpunkButton(t("hud.logs"));
        logsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logsButton.addActionListener(e -> showLogs());
        statsPanel.add(logsButton);
        
        statsPanel.revalidate();
        statsPanel.repaint();
    }
    
    /**
     * Actualiza la barra inferior con estado del mundo.
     */
    private void refreshBottomBar() {
        District district = session.getDistrict();
        if (district == null) return;

        // Time + simulated weather
        timeLabel.setText("23:48");
        weatherIcon.setToolTipText(t("bottom.weather") + ": " + t("bottom.weather.value"));

        // Center notification
        String notification = getLatestNotification();
        if (notification != null && !notification.isBlank()) {
            notifLabel.setText(t("bottom.lastEvent") + ": \"" + notification + "\"");
        } else {
            notifLabel.setText("");
        }

        // District name
        districtLabel.setText(district.getName().toUpperCase());

        bottomBar.revalidate();
        bottomBar.repaint();
    }
    
    /**
     * Agrega una etiqueta de stat (DRY).
     */
    private void addStatLabel(JPanel panel, String text, Font font, Color color) {
        addStatLabel(panel, text, font, color, null);
    }

    private void addStatLabel(JPanel panel, String text, Font font, Color color, String tooltip) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (tooltip != null && !tooltip.isBlank()) {
            label.setToolTipText(tooltip);
        }
        panel.add(label);
    }

    private void addCategoryHeader(JPanel panel, String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 12));
        label.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
    }

    private void addStatValue(JPanel panel, String label, String value, String tooltip) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(220, 18));

        JLabel left = new JLabel(label);
        left.setFont(GameConstants.FONT_TEXT.deriveFont(12f));
        left.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        if (tooltip != null && !tooltip.isBlank()) {
            left.setToolTipText(tooltip);
        }

        JLabel right = new JLabel(value);
        right.setFont(GameConstants.FONT_TEXT.deriveFont(12f));
        right.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        if (tooltip != null && !tooltip.isBlank()) {
            right.setToolTipText(tooltip);
        }

        row.add(left, BorderLayout.WEST);
        row.add(right, BorderLayout.EAST);

        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(row);
        panel.add(Box.createVerticalStrut(4));
    }
    
    /**
     * Agrega una barra de progreso para stats (KISS).
     */
    private void addStatBar(JPanel panel, String name, int value, Color barColor, String tooltip) {
        JLabel nameLabel = new JLabel(name + ": " + value + "%");
        nameLabel.setFont(GameConstants.FONT_TEXT.deriveFont(12f));
        nameLabel.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (tooltip != null && !tooltip.isBlank()) {
            nameLabel.setToolTipText(tooltip);
        }
        panel.add(nameLabel);
        
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(value);
        bar.setForeground(applySaturation(barColor, value));
        bar.setBackground(GameConstants.COLOR_DARK_GRAY);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        bar.setMaximumSize(new Dimension(210, 8));
        if (tooltip != null && !tooltip.isBlank()) {
            bar.setToolTipText(tooltip);
        }
        panel.add(bar);
        panel.add(Box.createVerticalStrut(5));
    }

    private Color applySaturation(Color base, int value) {
        int v = Math.max(0, Math.min(100, value));
        double factor = 0.25 + 0.75 * (v / 100.0);
        int r = (int) Math.round(base.getRed() * factor);
        int g = (int) Math.round(base.getGreen() * factor);
        int b = (int) Math.round(base.getBlue() * factor);
        return new Color(Math.min(255, r), Math.min(255, g), Math.min(255, b));
    }
    
    /**
     * Obtiene la última notificación del log.
     */
    private String getLatestNotification() {
        GameLog log = session.getGameLog();
        if (log != null && !log.getEntries().isEmpty()) {
            return log.getRecentEntries(1).get(0).getMessage();
        }
        return null;
    }
    
    /**
     * Muestra el diálogo de logs.
     */
    private void showLogs() {
        GameLog log = session.getGameLog();
        if (log == null) return;
        
        StringBuilder sb = new StringBuilder();
        for (GameLog.LogEntry entry : log.getRecentEntries(20)) {
            sb.append(entry.getMessage()).append("\n");
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(GameConstants.FONT_TEXT);
        textArea.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        textArea.setBackground(GameConstants.COLOR_PANEL);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "System Logs", JOptionPane.PLAIN_MESSAGE);
    }

    private String t(String key) {
        return com.neonthread.localization.Localization.get(key, key);
    }



    private Color getAccentColor() {
        return "PURPLE".equalsIgnoreCase(settings.getTheme())
            ? GameConstants.COLOR_PURPLE_NEON
            : GameConstants.COLOR_CYAN_NEON;
    }

    private void maybeTriggerDistrictEvent() {
        // Small random events to give life to the HUB
        Random rnd = new Random();
        if (rnd.nextDouble() > 0.55) return;

        String msg;
        int roll = rnd.nextInt(3);
        if (roll == 0) msg = t("toast.rumor");
        else if (roll == 1) msg = t("toast.drone");
        else msg = t("toast.signal");

        GameLog log = session.getGameLog();
        if (log != null) {
            log.add(msg);
        }

        showBottomToast(msg);
        refreshHistoryOnly();
        refreshBottomBarOnly();
    }

    private void showBottomToast(String message) {
        if (notifLabel == null) return;

        notifLabel.setOpaque(true);
        notifLabel.setBackground(new Color(0x0A0F1B));
        notifLabel.setBorder(BorderFactory.createLineBorder(getAccentColor().darker(), 1));
        notifLabel.setText(message);

        Timer timer = new Timer(2500, e -> {
            ((Timer) e.getSource()).stop();
            notifLabel.setOpaque(false);
            notifLabel.setBorder(null);
            refreshBottomBarOnly();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void applyDistrictMapConfig() {
        District district = session.getDistrict();
        if (district == null) return;

        com.neonthread.map.MapConfig config = com.neonthread.map.MapConfigLoader.load();
        
        // Apply theme if needed (not implemented in District yet, but ready)
        // String theme = config.getTheme();
        
        for (java.util.Map.Entry<String, com.neonthread.map.MapConfig.Point> entry : config.getLocations().entrySet()) {
            String id = entry.getKey();
            com.neonthread.map.MapConfig.Point p = entry.getValue();
            
            District.Location loc = district.findLocationById(id);
            if (loc != null) {
                loc.setPosition(p.x, p.y);
            }
        }
    }

    private void updateContextForSelection(District.Location location, Mission mission) {
        if (contextBody == null || contextActions == null || contextTitle == null) return;

        contextActions.removeAll();

        if (mission != null) {
            contextTitle.setText(t("context.mission"));
            contextBody.setText(mission.getTitle() + "\n\n" + mission.getDescription() + "\n\n" + t("hint.doubleClick"));

            CyberpunkButton open = new CyberpunkButton(t("context.open"));
            open.addActionListener(e -> {
                session.setCurrentMission(mission);
                onStateChange.accept(GameState.STATE_MISSION_WINDOW);
            });
            contextActions.add(open);
        } else if (location != null) {
            contextTitle.setText(t("context.location"));
            contextBody.setText(location.getName() + "\n" + location.getType().getDisplayName());

            CyberpunkButton interact = new CyberpunkButton(t("context.interact"));
            interact.addActionListener(e -> mapPanel.interactWith(location));
            contextActions.add(interact);
        } else {
            contextTitle.setText(t("district.context"));
            contextBody.setText(t("district.context.empty"));
        }

        CyberpunkButton clear = new CyberpunkButton(t("context.clear"));
        clear.addActionListener(e -> mapPanel.clearSelection());
        contextActions.add(clear);

        contextActions.revalidate();
        contextActions.repaint();
    }
    
    /**
     * Panel de mapa geométrico con locaciones interactivas (KISS + DRY).
     */
    private class DistrictMapPanel extends JPanel {
        private District.Location hoveredLocation;
        private Mission hoveredMission;
        private District.Location selectedLocation;
        private Mission selectedMission;

        private int lastMouseX;
        private int lastMouseY;
        private long hoverStartAtMs;

        private double zoom = 1.0;
        private static final double ZOOM_STEP = 0.10;
        private static final double ZOOM_MIN = 0.80;
        private static final double ZOOM_MAX = 1.20;

        private final Map<String, String> missionSignatureById = new HashMap<>();
        private final Map<String, MissionBadge> missionBadgeById = new HashMap<>();
        private final Set<String> scoutedMissionIds = new HashSet<>();

        private static final int HOVER_DELAY_MS = 300;

        public DistrictMapPanel() {
            setBackground(new Color(0x0A0A0F));
            setLayout(null);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                    handleClick(e);
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    lastMouseX = e.getX();
                    lastMouseY = e.getY();
                    handleHover(e.getX(), e.getY());
                }
            });

            addMouseWheelListener(this::handleZoom);
        }

        public void refresh() {
            syncMissionVisualState();
            repaint();
        }

        public void clearSelection() {
            selectedLocation = null;
            selectedMission = null;
            updateContextForSelection(null, null);
            repaint();
        }

        public void interactWith(District.Location location) {
            if (location == null) return;
            onLocationAction(location);
        }

        public void selectNearestMissionToCursor() {
            District district = session.getDistrict();
            if (district == null) return;

            Point2D worldCursor = toWorld(lastMouseX, lastMouseY);
            Mission nearest = null;
            double best = Double.MAX_VALUE;

            for (Mission m : getRenderableMissions(district)) {
                Point2D pos = getMissionWorldPosition(district, m);
                double dx = worldCursor.getX() - pos.getX();
                double dy = worldCursor.getY() - pos.getY();
                double d = dx * dx + dy * dy;
                if (d < best) {
                    best = d;
                    nearest = m;
                }
            }

            if (nearest != null) {
                selectedMission = nearest;
                selectedLocation = null;
                updateContextForSelection(null, nearest);
                repaint();
            }
        }

        private void handleZoom(MouseWheelEvent e) {
            int rotation = e.getWheelRotation();
            if (rotation == 0) return;

            double nextZoom = zoom + (rotation < 0 ? ZOOM_STEP : -ZOOM_STEP);
            nextZoom = Math.max(ZOOM_MIN, Math.min(ZOOM_MAX, nextZoom));
            if (Math.abs(nextZoom - zoom) < 0.0001) return;

            zoom = nextZoom;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            AffineTransform oldTx = g2d.getTransform();
            g2d.transform(getMapTransform());

            drawDistrictGrid(g2d);
            drawLocations(g2d);

            g2d.setTransform(oldTx);
            drawOverlay(g2d);
        }

        private AffineTransform getMapTransform() {
            int width = getWidth();
            int height = getHeight();
            AffineTransform tx = new AffineTransform();
            tx.translate(width / 2.0, height / 2.0);
            tx.scale(zoom, zoom);
            tx.translate(-width / 2.0, -height / 2.0);
            return tx;
        }

        private Point2D toWorld(int screenX, int screenY) {
            try {
                AffineTransform inv = getMapTransform().createInverse();
                return inv.transform(new Point2D.Double(screenX, screenY), null);
            } catch (NoninvertibleTransformException ex) {
                return new Point2D.Double(screenX, screenY);
            }
        }

        private Point2D toScreen(double worldX, double worldY) {
            Point2D out = new Point2D.Double();
            getMapTransform().transform(new Point2D.Double(worldX, worldY), out);
            return out;
        }
        
        /**
         * Dibuja el grid geométrico del distrito (KISS).
         */
        private void drawDistrictGrid(Graphics2D g2d) {
            int width = getWidth();
            int height = getHeight();
            
            // Líneas de calles (cian suave)
            g2d.setColor(new Color(31, 199, 208, 100));
            g2d.setStroke(new BasicStroke(1));
            
            // Calles verticales
            for (int i = 100; i < width; i += 150) {
                g2d.drawLine(i, 50, i, height - 50);
            }
            
            // Calles horizontales
            for (int i = 100; i < height; i += 120) {
                g2d.drawLine(100, i, width - 100, i);
            }
            
            // Bloques de edificios (púrpura oscuro)
            g2d.setColor(new Color(0x403058));
            for (int x = 120; x < width - 120; x += 150) {
                for (int y = 120; y < height - 120; y += 120) {
                    g2d.fillRect(x, y, 120, 90);
                    g2d.setColor(new Color(31, 199, 208, 80));
                    g2d.drawRect(x, y, 120, 90);
                    g2d.setColor(new Color(0x403058));
                }
            }
        }
        
        /**
         * Dibuja las locaciones interactivas (DRY).
         */
        private void drawLocations(Graphics2D g2d) {
            District district = session.getDistrict();
            if (district == null) return;
            
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            // Determine mission hub anchor (all missions live on the hub)
            Point missionHub = getMissionHubAnchor(district, centerX, centerY);
            
            int index = 0;
            for (District.Location location : district.getLocations()) {
                if (!location.isUnlocked()) continue;
                
                // Calcular posición si no está definida
                int x = location.getX() == 0 ? centerX + (index % 3 - 1) * 200 : location.getX();
                int y = location.getY() == 0 ? centerY + (index / 3 - 1) * 150 : location.getY();
                
                // Actualizar posición en la locación
                if (location.getX() == 0) {
                    location.setPosition(x, y);
                }
                
                drawLocationIcon(g2d, location, x, y);
                
                index++;
            }
            
            // Draw missions anchored to mission hub
            drawMissionIcons(g2d, missionHub.x, missionHub.y - 80);
        }

        private Point getMissionHubAnchor(District district, int fallbackX, int fallbackY) {
            for (District.Location location : district.getLocations()) {
                if (!location.isUnlocked()) continue;
                if (location.getType() == District.LocationType.MISSION) {
                    if (location.getX() != 0 || location.getY() != 0) {
                        return new Point(location.getX(), location.getY());
                    }
                }
            }
            return new Point(fallbackX, fallbackY);
        }
        
        /**
         * Dibuja un icono de locación (KISS).
         */
        private void drawLocationIcon(Graphics2D g2d, District.Location location, int x, int y) {
            String icon = getLocationIcon(location.getType());

            boolean isHovered = location.equals(hoveredLocation);
            boolean isSelected = location.equals(selectedLocation);
            double scale = isHovered ? 1.10 : (isSelected ? 1.06 : 1.0);

            AffineTransform old = g2d.getTransform();
            g2d.translate(x, y);
            g2d.scale(scale, scale);
            g2d.translate(-x, -y);
            
            // Glow si está en hover
            if (isHovered) {
                g2d.setColor(new Color(getAccentColor().getRed(), getAccentColor().getGreen(), getAccentColor().getBlue(), 80));
                g2d.fillOval(x - 24, y - 24, 48, 48);
            }
            if (isSelected) {
                g2d.setColor(new Color(getAccentColor().getRed(), getAccentColor().getGreen(), getAccentColor().getBlue(), 60));
                g2d.fillOval(x - 22, y - 22, 44, 44);
            }
            
            // Círculo de fondo
            g2d.setColor(GameConstants.COLOR_PANEL);
            g2d.fillOval(x - 18, y - 18, 36, 36);
            
            // Borde
            g2d.setColor(GameConstants.COLOR_CYAN_NEON);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x - 18, y - 18, 36, 36);
            
            // Icono
            g2d.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 24));
            FontMetrics fm = g2d.getFontMetrics();
            int iconWidth = fm.stringWidth(icon);
            g2d.drawString(icon, x - iconWidth / 2, y + 8);

            // Minimal composition: show top mission icon on mission-type locations
            if (location.getType() == District.LocationType.MISSION) {
                Mission top = getTopAvailableMission();
                if (top != null) {
                    g2d.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 14));
                    g2d.setColor(applyBrightness(getMissionColor(top), getPriorityBrightness(top)));
                    g2d.drawString(top.getVisualIcon(), x + 12, y - 8);
                }
            }

            g2d.setTransform(old);
        }

        private Mission getTopAvailableMission() {
            District district = session.getDistrict();
            if (district == null) return null;

            for (Mission m : district.getAvailableMissions()) {
                Mission.MissionStatus status = m.getStatus();
                if (status == Mission.MissionStatus.COMPLETED ||
                    status == Mission.MissionStatus.HIDDEN ||
                    status == Mission.MissionStatus.EXPIRED) {
                    continue;
                }
                return m;
            }
            return null;
        }
        
        /**
         * Dibuja iconos de misiones con parpadeo según tipo y prioridad (KISS + mejoras).
         */
        private void drawMissionIcons(Graphics2D g2d, int baseX, int baseY) {
            District district = session.getDistrict();
            if (district == null) return;

            int index = 0;
            for (Mission mission : getRenderableMissions(district)) {
                Point2D pos = getMissionWorldPosition(district, mission);
                int x = (int) Math.round(pos.getX());
                int y = (int) Math.round(pos.getY());
                
                // Calcular alpha según estado y urgencia
                long time = System.currentTimeMillis();
                int alpha;
                Mission.MissionStatus status = mission.getStatus();
                if (status == Mission.MissionStatus.AVAILABLE) {
                    // Parpadeo más rápido para misiones urgentes
                    double speed = mission.getUrgency() == Mission.MissionUrgency.CRITICAL ? 400.0 : 600.0;
                    alpha = (int) (150 + 105 * Math.sin(time / speed));
                } else {
                    alpha = 255;
                }
                
                // Color según tipo de misión
                Color iconColor = getMissionColor(mission);

                // Brillo según prioridad
                double brightness = getPriorityBrightness(mission);
                Color brightColor = applyBrightness(iconColor, brightness);

                boolean isHovered = mission.equals(hoveredMission);
                boolean isSelected = mission.equals(selectedMission);
                double scale = isHovered ? 1.12 : (isSelected ? 1.06 : 1.0);
                
                // Obtener ícono visual según tipo y prioridad
                String icon = mission.getVisualIcon();
                
                // Dibujar ícono de misión
                if (isHovered || isSelected) {
                    g2d.setColor(new Color(brightColor.getRed(), brightColor.getGreen(), brightColor.getBlue(), 80));
                    g2d.fillOval(x - 24, y - 24, 48, 48);
                }

                AffineTransform old = g2d.getTransform();
                g2d.translate(x, y);
                g2d.scale(scale, scale);
                g2d.translate(-x, -y);

                g2d.setColor(new Color(brightColor.getRed(), brightColor.getGreen(), brightColor.getBlue(), alpha));
                g2d.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 32));
                g2d.drawString(icon, x - 16, y + 10);

                drawMissionBadge(g2d, mission, x, y);
                
                // Indicador de urgencia para misiones críticas
                if (mission.getUrgency() == Mission.MissionUrgency.CRITICAL) {
                    g2d.setColor(new Color(255, 50, 50, alpha));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawOval(x - 20, y - 20, 40, 40);
                }

                g2d.setTransform(old);
                
                index++;
            }
        }

        private void drawMissionBadge(Graphics2D g2d, Mission mission, int x, int y) {
            MissionBadge badge = missionBadgeById.get(mission.getId());
            if (badge == null || badge == MissionBadge.NONE) return;

            String text;
            Color color;
            long time = System.currentTimeMillis();
            int pulseAlpha = 220;
            int borderW = 1;
            switch (badge) {
                case NEW:
                    text = "N";
                    color = GameConstants.COLOR_CYAN_NEON;
                    pulseAlpha = (int) (160 + 70 * Math.sin(time / 240.0));
                    break;
                case UPDATED:
                    text = "U";
                    color = GameConstants.COLOR_MAGENTA_NEON;
                    borderW = (int) (1 + Math.round(1.0 + Math.sin(time / 420.0)) / 2.0);
                    break;
                case SCOUTED:
                    text = "S";
                    color = GameConstants.COLOR_YELLOW_NEON;
                    break;
                default:
                    return;
            }

            g2d.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 12));
            g2d.setColor(new Color(0, 0, 0, pulseAlpha));
            g2d.fillRoundRect(x + 10, y - 26, 16, 16, 6, 6);
            g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), pulseAlpha));
            g2d.setStroke(new BasicStroke(borderW));
            g2d.drawRoundRect(x + 10, y - 26, 16, 16, 6, 6);
            g2d.drawString(text, x + 14, y - 14);
        }

        private double getPriorityBrightness(Mission mission) {
            if (mission == null) return 0.5;
            return switch (mission.getPriority()) {
                case HIGH -> 0.80;
                case CRITICAL -> 1.00;
                case LOW -> 0.25;
                case NORMAL -> 0.50;
            };
        }

        private Color applyBrightness(Color base, double factor) {
            double f = Math.max(0.0, Math.min(1.0, factor));
            int r = (int) Math.round(base.getRed() * f);
            int g = (int) Math.round(base.getGreen() * f);
            int b = (int) Math.round(base.getBlue() * f);
            return new Color(Math.min(255, r), Math.min(255, g), Math.min(255, b));
        }

        private java.util.List<Mission> getRenderableMissions(District district) {
            java.util.List<Mission> list = new java.util.ArrayList<>();
            WorldState ws = session.getWorldState();
            
            for (Mission m : district.getAvailableMissions()) {
                // Check spawn conditions dynamically
                if (!m.canSpawn(ws, session.getCharacter())) {
                    continue;
                }
                
                Mission.MissionStatus status = m.getStatus();
                if (status == Mission.MissionStatus.COMPLETED ||
                    status == Mission.MissionStatus.HIDDEN ||
                    status == Mission.MissionStatus.EXPIRED) {
                    continue;
                }
                list.add(m);
            }
            return list;
        }

        private Point2D getMissionWorldPosition(District district, Mission mission) {
            Point missionHub = getMissionHubAnchor(district, getWidth() / 2, getHeight() / 2);
            int index = 0;
            for (Mission m : getRenderableMissions(district)) {
                if (m == mission) {
                    int x = missionHub.x + (index - 1) * 100;
                    int y = missionHub.y - 80;
                    return new Point2D.Double(x, y);
                }
                index++;
            }
            return new Point2D.Double(missionHub.x, missionHub.y - 80);
        }
        
        /**
         * Obtiene el color según el tipo de misión.
         */
        private Color getMissionColor(Mission mission) {
            switch (mission.getType()) {
                case MAIN:
                    return new Color(255, 215, 0); // Dorado
                case INTEL:
                    return new Color(31, 199, 208); // Cian
                case COMBAT:
                    return new Color(255, 50, 50); // Rojo
                case SIDE:
                default:
                    return new Color(200, 200, 200); // Gris claro
            }
        }
        
        /**
         * Dibuja tooltip avanzado de misión con información adicional.
         */
        private void drawMissionTooltip(Graphics2D g2d, Mission mission, int x, int y) {
            g2d.setFont(GameConstants.FONT_TEXT.deriveFont(12f));
            FontMetrics fm = g2d.getFontMetrics();
            
            String title = mission.getTitle();
            String type = mission.getType().getDisplayName();
            String priority = mission.getPriority() == Mission.MissionPriority.HIGH || 
                             mission.getPriority() == Mission.MissionPriority.CRITICAL ? " [!]" : "";
            
            int titleWidth = fm.stringWidth(title);
            int typeWidth = fm.stringWidth(type + priority);
            int width = Math.max(titleWidth, typeWidth) + 20;
            int height = 44;
            
            // Fondo
            g2d.setColor(new Color(0, 0, 0, 220));
            g2d.fillRoundRect(x - width / 2, y - height, width, height, 8, 8);
            
            // Borde según prioridad
            Color borderColor = mission.getPriority() == Mission.MissionPriority.HIGH || 
                               mission.getPriority() == Mission.MissionPriority.CRITICAL ?
                               new Color(255, 215, 0) : GameConstants.COLOR_CYAN_NEON;
            g2d.setColor(borderColor);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x - width / 2, y - height, width, height, 8, 8);
            
            // Título
            g2d.setFont(GameConstants.FONT_TEXT.deriveFont(Font.BOLD, 12f));
            g2d.setColor(GameConstants.COLOR_TEXT_PRIMARY);
            g2d.drawString(title, x - titleWidth / 2, y - 24);
            
            // Tipo + Prioridad
            g2d.setFont(GameConstants.FONT_TEXT.deriveFont(10f));
            g2d.setColor(GameConstants.COLOR_TEXT_SECONDARY);
            g2d.drawString(type + priority, x - typeWidth / 2, y - 8);
        }
        
        /**
         * Dibuja un tooltip (DRY).
         */
        private void drawTooltip(Graphics2D g2d, String text, int x, int y) {
            g2d.setFont(GameConstants.FONT_TEXT.deriveFont(12f));
            FontMetrics fm = g2d.getFontMetrics();
            int width = fm.stringWidth(text) + 16;
            int height = 24;
            
            // Fondo
            g2d.setColor(new Color(0, 0, 0, 200));
            g2d.fillRoundRect(x - width / 2, y - height, width, height, 8, 8);
            
            // Borde
            g2d.setColor(GameConstants.COLOR_CYAN_NEON);
            g2d.drawRoundRect(x - width / 2, y - height, width, height, 8, 8);
            
            // Texto
            g2d.drawString(text, x - fm.stringWidth(text) / 2, y - 6);
        }

        private void drawOverlay(Graphics2D g2d) {
            g2d.setFont(GameConstants.FONT_TEXT.deriveFont(12f));
            g2d.setColor(GameConstants.COLOR_TEXT_SECONDARY);
            g2d.drawString(getBreadcrumbFor(lastMouseX, lastMouseY), 12, 18);
            g2d.drawString(String.format("Zoom: %d%%", (int) Math.round(zoom * 100)), 12, 34);

            long now = System.currentTimeMillis();

            if (showTooltips && (now - hoverStartAtMs) >= HOVER_DELAY_MS) {
                if (hoveredMission != null) {
                    drawMissionTooltip(g2d, hoveredMission, lastMouseX, lastMouseY - 10);
                } else if (hoveredLocation != null) {
                    drawTooltip(g2d, hoveredLocation.getName(), lastMouseX, lastMouseY - 10);
                }
            }

            if (showTooltips) {
                drawSelectionCard(g2d);
            }
        }

        private void drawSelectionCard(Graphics2D g2d) {
            District district = session.getDistrict();
            if (district == null) return;

            String title;
            String body;
            Point2D anchor;

            if (selectedMission != null) {
                title = selectedMission.getTitle();
                body = t("hint.doubleClick");
                Point2D pos = getMissionWorldPosition(district, selectedMission);
                anchor = toScreen(pos.getX(), pos.getY());
            } else if (selectedLocation != null) {
                title = selectedLocation.getName();
                body = selectedLocation.getType().getDisplayName();
                anchor = toScreen(selectedLocation.getX(), selectedLocation.getY());
            } else {
                return;
            }

            g2d.setFont(GameConstants.FONT_TEXT.deriveFont(12f));
            FontMetrics fm = g2d.getFontMetrics();
            int w = Math.max(fm.stringWidth(title), fm.stringWidth(body)) + 18;
            int h = 42;
            int x = (int) Math.round(anchor.getX()) + 18;
            int y = (int) Math.round(anchor.getY()) + 18;

            g2d.setColor(new Color(0, 0, 0, 210));
            g2d.fillRoundRect(x, y, w, h, 8, 8);
            g2d.setColor(getAccentColor());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x, y, w, h, 8, 8);

            g2d.setColor(GameConstants.COLOR_TEXT_PRIMARY);
            g2d.drawString(title, x + 9, y + 16);
            g2d.setColor(GameConstants.COLOR_TEXT_SECONDARY);
            g2d.setFont(GameConstants.FONT_TEXT.deriveFont(10f));
            g2d.drawString(body, x + 9, y + 32);
        }

        private String getBreadcrumbFor(int screenX, int screenY) {
            int w = Math.max(1, getWidth());
            int h = Math.max(1, getHeight());
            String horiz = screenX < w / 3 ? "WEST" : (screenX > (2 * w) / 3 ? "EAST" : "CENTRAL");
            String vert = screenY < h / 3 ? "NORTH" : (screenY > (2 * h) / 3 ? "SOUTH" : "MID");
            return "Sector: " + vert + " / " + horiz;
        }
        
        /**
         * Maneja click en locación (KISS).
         */
        private void handleClick(MouseEvent e) {
            Point2D world = toWorld(e.getX(), e.getY());
            int mouseX = (int) Math.round(world.getX());
            int mouseY = (int) Math.round(world.getY());

            District district = session.getDistrict();
            if (district == null) return;

            District.Location hitLoc = findLocationAt(district, mouseX, mouseY);
            if (hitLoc != null) {
                selectedLocation = hitLoc;
                selectedMission = null;
                updateContextForSelection(hitLoc, null);

                if (e.getClickCount() >= 2) {
                    onLocationAction(hitLoc);
                }

                repaint();
                return;
            }

            Mission hitMission = findMissionAt(district, mouseX, mouseY);
            if (hitMission != null) {
                selectedMission = hitMission;
                selectedLocation = null;
                updateContextForSelection(null, hitMission);

                if (e.getClickCount() >= 2) {
                    onMissionAction(hitMission);
                }

                repaint();
            }
        }
        
        /**
         * Maneja hover sobre locación.
         */
        private void handleHover(int screenX, int screenY) {
            Point2D world = toWorld(screenX, screenY);
            int mouseX = (int) Math.round(world.getX());
            int mouseY = (int) Math.round(world.getY());

            District district = session.getDistrict();
            if (district == null) return;

            District.Location newLoc = findLocationAt(district, mouseX, mouseY);
            Mission newMission = findMissionAt(district, mouseX, mouseY);
            if (newMission != null) {
                scoutedMissionIds.add(newMission.getId());
            }

            if (newLoc != hoveredLocation || newMission != hoveredMission) {
                hoveredLocation = newLoc;
                hoveredMission = newMission;
                hoverStartAtMs = System.currentTimeMillis();
                syncMissionVisualState();
                repaint();
            }
        }

        private District.Location findLocationAt(District district, int x, int y) {
            for (District.Location location : district.getLocations()) {
                if (!location.isUnlocked()) continue;
                if (isPointInDiamond(x, y, location.getX(), location.getY(), 28) ||
                    isPointNear(x, y, location.getX(), location.getY(), 22)) {
                    return location;
                }
            }
            return null;
        }

        private Mission findMissionAt(District district, int x, int y) {
            Point missionHub = getMissionHubAnchor(district, getWidth() / 2, getHeight() / 2);
            int baseX = missionHub.x;
            int baseY = missionHub.y - 80;
            int index = 0;

            for (Mission mission : getRenderableMissions(district)) {

                int mx = baseX + (index - 1) * 100;
                int my = baseY;
                if (isPointInDiamond(x, y, mx, my, 36) || isPointNear(x, y, mx, my, 28)) {
                    return mission;
                }
                index++;
            }
            return null;
        }

        private void syncMissionVisualState() {
            District district = session.getDistrict();
            if (district == null) return;

            for (Mission mission : district.getAvailableMissions()) {
                String id = mission.getId();
                String sig = mission.getStatus() + "|" + mission.getPriority() + "|" + mission.getUrgency();
                String prev = missionSignatureById.get(id);

                MissionBadge badge;
                if (prev == null) {
                    badge = MissionBadge.NEW;
                } else if (!prev.equals(sig)) {
                    badge = MissionBadge.UPDATED;
                } else if (scoutedMissionIds.contains(id)) {
                    badge = MissionBadge.SCOUTED;
                } else {
                    badge = MissionBadge.NONE;
                }

                missionSignatureById.put(id, sig);
                missionBadgeById.put(id, badge);
            }
        }
        
        /**
         * Verifica si un punto está cerca de otro (DRY).
         */
        private boolean isPointNear(int x1, int y1, int x2, int y2, int threshold) {
            int dx = x1 - x2;
            int dy = y1 - y2;
            return Math.sqrt(dx * dx + dy * dy) < threshold;
        }

        private boolean isPointInDiamond(int x1, int y1, int x2, int y2, int threshold) {
            int dx = Math.abs(x1 - x2);
            int dy = Math.abs(y1 - y2);
            return (dx + dy) < threshold;
        }
        
        /**
         * Acción al clickear una locación.
         */
        private void onLocationAction(District.Location location) {
            session.getGameLog().add("Visiting: " + location.getName());
            
            switch (location.getType()) {
                case MISSION:
                    // Las misiones se abren con doble clic en el ícono de misión (hub)
                    break;
                case SHOP:
                    JOptionPane.showMessageDialog(DistrictMapScreen.this,
                        "Black Market (work in progress)",
                            location.getName(),
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
                case HACK:
                    JOptionPane.showMessageDialog(DistrictMapScreen.this,
                        "Network Node (work in progress)",
                            location.getName(),
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(DistrictMapScreen.this,
                        "Location: " + location.getName(),
                        "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
        }
        
        /**
         * Acción al clickear una misión.
         */
        private void onMissionAction(Mission mission) {
            session.setCurrentMission(mission);
            session.getGameLog().add("Viewing mission: " + mission.getTitle());
            onStateChange.accept(GameState.STATE_MISSION_WINDOW);
        }
        
        /**
         * Obtiene el icono de una locación (DRY).
         */
        private String getLocationIcon(District.LocationType type) {
            switch (type) {
                case BASE: return "●";
                case SHOP: return "▲";
                case HACK: return "◆";
                case MISSION: return "⬢";
                case INFO: return "◉";
                default: return "●";
            }
        }

        private enum MissionBadge {
            NONE,
            NEW,
            UPDATED,
            SCOUTED
        }
    }
}
