package com.neonthread.screens;

import com.neonthread.*;
import com.neonthread.ui.CyberpunkButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Pantalla del mapa del distrito - HUB principal del juego (KISS + DRY).
 * Mapa minimalista geométrico con sistema de locaciones interactivas.
 */
public class DistrictMapScreen extends JPanel {
    private final Consumer<GameState> onStateChange;
    private final GameSession session;
    
    private JPanel statsPanel;
    private DistrictMapPanel mapPanel;
    private JPanel bottomBar;
    
    public DistrictMapScreen(Consumer<GameState> onStateChange) {
        this.onStateChange = onStateChange;
        this.session = GameSession.getInstance();
        
        setLayout(new BorderLayout());
        setBackground(new Color(0x0A0A0F)); // Fondo casi negro
        
        buildUI();
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
        
        // Botón de pausa (top-right)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        topPanel.setOpaque(false);
        CyberpunkButton pauseButton = new CyberpunkButton("PAUSE");
        pauseButton.addActionListener(e -> onStateChange.accept(GameState.STATE_PAUSE));
        topPanel.add(pauseButton);
        add(topPanel, BorderLayout.NORTH);
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
        
        return panel;
    }
    
    /**
     * Actualiza toda la interfaz con los datos actuales (DRY).
     */
    public void refresh() {
        refreshStatsPanel();
        refreshBottomBar();
        mapPanel.repaint();
    }
    
    /**
     * Actualiza el panel de stats del jugador.
     */
    private void refreshStatsPanel() {
        statsPanel.removeAll();
        
        com.neonthread.Character character = session.getCharacter();
        if (character == null) return;
        
        // Título
        addStatLabel(statsPanel, "OPERADOR", GameConstants.FONT_TITLE.deriveFont(20f), GameConstants.COLOR_CYAN_NEON);
        statsPanel.add(Box.createVerticalStrut(10));
        
        // Nombre y rol
        addStatLabel(statsPanel, character.getName(), GameConstants.FONT_MENU.deriveFont(18f), GameConstants.COLOR_TEXT_PRIMARY);
        addStatLabel(statsPanel, character.getRole().getDisplayName(), GameConstants.FONT_TEXT.deriveFont(12f), GameConstants.COLOR_TEXT_SECONDARY);
        statsPanel.add(Box.createVerticalStrut(15));
        
        // Stats principales
        addStatLabel(statsPanel, "Nivel: " + character.getLevel(), GameConstants.FONT_TEXT, GameConstants.COLOR_TEXT_PRIMARY);
        addStatLabel(statsPanel, "Créditos: " + character.getCredits(), GameConstants.FONT_TEXT, GameConstants.COLOR_TEXT_PRIMARY);
        statsPanel.add(Box.createVerticalStrut(10));
        
        // Estado
        JLabel statusLabel = new JLabel("ESTADO");
        statusLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 12));
        statusLabel.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.add(statusLabel);
        statsPanel.add(Box.createVerticalStrut(5));
        
        addStatBar(statsPanel, "Salud", character.getHealth(), Color.RED);
        addStatBar(statsPanel, "Energía", character.getEnergy(), Color.CYAN);
        statsPanel.add(Box.createVerticalStrut(10));
        
        addStatLabel(statsPanel, "Karma: " + character.getKarma(), GameConstants.FONT_TEXT, GameConstants.COLOR_TEXT_PRIMARY);
        addStatLabel(statsPanel, "Notoriedad: " + character.getNotoriety(), GameConstants.FONT_TEXT, GameConstants.COLOR_TEXT_PRIMARY);
        
        // Botones de acción
        statsPanel.add(Box.createVerticalStrut(20));
        CyberpunkButton logsButton = new CyberpunkButton("Ver Logs");
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
        bottomBar.removeAll();
        
        District district = session.getDistrict();
        if (district == null) return;
        
        // Hora y clima simulado
        JLabel timeLabel = new JLabel("23:48 - Lluvia ligera");
        timeLabel.setFont(GameConstants.FONT_TEXT);
        timeLabel.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        bottomBar.add(timeLabel, BorderLayout.WEST);
        
        // Notificación central
        String notification = getLatestNotification();
        if (notification != null) {
            JLabel notifLabel = new JLabel(notification);
            notifLabel.setFont(GameConstants.FONT_TEXT);
            notifLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
            bottomBar.add(notifLabel, BorderLayout.CENTER);
        }
        
        // Distrito actual
        JLabel districtLabel = new JLabel(district.getName().toUpperCase());
        districtLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 12));
        districtLabel.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        bottomBar.add(districtLabel, BorderLayout.EAST);
        
        bottomBar.revalidate();
        bottomBar.repaint();
    }
    
    /**
     * Agrega una etiqueta de stat (DRY).
     */
    private void addStatLabel(JPanel panel, String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
    }
    
    /**
     * Agrega una barra de progreso para stats (KISS).
     */
    private void addStatBar(JPanel panel, String name, int value, Color barColor) {
        JLabel nameLabel = new JLabel(name + ": " + value + "%");
        nameLabel.setFont(GameConstants.FONT_TEXT.deriveFont(12f));
        nameLabel.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);
        
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(value);
        bar.setForeground(barColor);
        bar.setBackground(GameConstants.COLOR_DARK_GRAY);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        bar.setMaximumSize(new Dimension(210, 8));
        panel.add(bar);
        panel.add(Box.createVerticalStrut(5));
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
        
        JOptionPane.showMessageDialog(this, scrollPane, "Logs del Sistema", JOptionPane.PLAIN_MESSAGE);
    }
    
    /**
     * Panel de mapa geométrico con locaciones interactivas (KISS + DRY).
     */
    private class DistrictMapPanel extends JPanel {
        private District.Location hoveredLocation = null;
        
        public DistrictMapPanel() {
            setBackground(new Color(0x0A0A0F));
            setLayout(null); // Posicionamiento absoluto para iconos
            
            // Mouse listener para interacción
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    handleLocationClick(e.getX(), e.getY());
                }
            });
            
            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    handleLocationHover(e.getX(), e.getY());
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            drawDistrictGrid(g2d);
            drawLocations(g2d);
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
                
                // Tooltip si está en hover
                if (location.equals(hoveredLocation)) {
                    drawTooltip(g2d, location.getName(), x, y - 30);
                }
                
                index++;
            }
            
            // Dibujar misiones disponibles
            drawMissionIcons(g2d, centerX, centerY - 100);
        }
        
        /**
         * Dibuja un icono de locación (KISS).
         */
        private void drawLocationIcon(Graphics2D g2d, District.Location location, int x, int y) {
            String icon = getLocationIcon(location.getType());
            
            // Glow si está en hover
            if (location.equals(hoveredLocation)) {
                g2d.setColor(new Color(31, 199, 208, 100));
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
        }
        
        /**
         * Dibuja iconos de misiones con parpadeo (KISS).
         */
        private void drawMissionIcons(Graphics2D g2d, int baseX, int baseY) {
            District district = session.getDistrict();
            if (district == null) return;
            
            int index = 0;
            for (Mission mission : district.getAvailableMissions()) {
                if (mission.getStatus() == Mission.MissionStatus.COMPLETED) continue;
                
                int x = baseX + (index - 1) * 100;
                int y = baseY;
                
                // Parpadeo para misiones disponibles
                long time = System.currentTimeMillis();
                int alpha = mission.getStatus() == Mission.MissionStatus.AVAILABLE ?
                        (int) (150 + 105 * Math.sin(time / 600.0)) : 255;
                
                // Estrella de misión
                g2d.setColor(new Color(255, 215, 0, alpha)); // Dorado
                g2d.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 32));
                g2d.drawString("★", x - 16, y + 10);
                
                // Tooltip
                if (isPointNear(x, y, hoveredLocation != null ? hoveredLocation.getX() : -1000,
                        hoveredLocation != null ? hoveredLocation.getY() : -1000, 25)) {
                    drawTooltip(g2d, mission.getTitle(), x, y - 30);
                }
                
                index++;
            }
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
        
        /**
         * Maneja click en locación (KISS).
         */
        private void handleLocationClick(int mouseX, int mouseY) {
            District district = session.getDistrict();
            if (district == null) return;
            
            // Verificar locaciones
            for (District.Location location : district.getLocations()) {
                if (isPointNear(mouseX, mouseY, location.getX(), location.getY(), 20)) {
                    onLocationClick(location);
                    return;
                }
            }
            
            // Verificar misiones
            int baseX = getWidth() / 2;
            int baseY = getHeight() / 2 - 100;
            int index = 0;
            
            for (Mission mission : district.getAvailableMissions()) {
                if (mission.getStatus() == Mission.MissionStatus.COMPLETED) continue;
                
                int x = baseX + (index - 1) * 100;
                int y = baseY;
                
                if (isPointNear(mouseX, mouseY, x, y, 25)) {
                    onMissionClick(mission);
                    return;
                }
                index++;
            }
        }
        
        /**
         * Maneja hover sobre locación.
         */
        private void handleLocationHover(int mouseX, int mouseY) {
            District district = session.getDistrict();
            if (district == null) return;
            
            District.Location newHovered = null;
            
            for (District.Location location : district.getLocations()) {
                if (isPointNear(mouseX, mouseY, location.getX(), location.getY(), 20)) {
                    newHovered = location;
                    break;
                }
            }
            
            if (newHovered != hoveredLocation) {
                hoveredLocation = newHovered;
                repaint();
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
        
        /**
         * Acción al clickear una locación.
         */
        private void onLocationClick(District.Location location) {
            session.getGameLog().add("Visitando: " + location.getName());
            
            switch (location.getType()) {
                case MISSION:
                    // Ya manejado por onMissionClick
                    break;
                case SHOP:
                    JOptionPane.showMessageDialog(DistrictMapScreen.this,
                            "Mercado Negro (funcionalidad en desarrollo)",
                            location.getName(),
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
                case HACK:
                    JOptionPane.showMessageDialog(DistrictMapScreen.this,
                            "Nodo de Red (funcionalidad en desarrollo)",
                            location.getName(),
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(DistrictMapScreen.this,
                            "Locación: " + location.getName(),
                            "Información",
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
        }
        
        /**
         * Acción al clickear una misión.
         */
        private void onMissionClick(Mission mission) {
            session.setCurrentMission(mission);
            session.getGameLog().add("Consultando misión: " + mission.getTitle());
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
    }
}
