package com.neonthread.ui;

import com.neonthread.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

/**
 * Debug overlay para depuración en desarrollo.
 * FASE 3 Feature 12: Toggle con F12, muestra estado completo del juego.
 * 
 * Patrón: Observer + Overlay para debugging no invasivo.
 */
public class DebugOverlay extends JPanel {
    private static final boolean DEV_MODE = true; // Toggle para producción
    private boolean visible = false;
    private final GameSession session;
    
    private JTextArea debugText;
    private Timer updateTimer;
    
    public DebugOverlay(GameSession session) {
        this.session = session;
        
        if (!DEV_MODE) {
            setVisible(false);
            return;
        }
        
        setLayout(new BorderLayout());
        setBackground(new Color(0, 0, 0, 200));
        setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
        setPreferredSize(new Dimension(400, 600));
        
        buildUI();
        setupKeyListener();
        startUpdateTimer();
        
        // Inicialmente oculto
        setVisible(false);
    }
    
    /**
     * Construye la UI del overlay.
     */
    private void buildUI() {
        JLabel title = new JLabel("[ DEBUG MODE - F12 TO TOGGLE ]");
        title.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 14));
        title.setForeground(Color.GREEN);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);
        
        debugText = new JTextArea();
        debugText.setEditable(false);
        debugText.setFont(new Font("Consolas", Font.PLAIN, 11));
        debugText.setForeground(Color.GREEN);
        debugText.setBackground(new Color(10, 10, 10));
        debugText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(debugText);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
        
        JLabel hint = new JLabel("Press F12 to hide");
        hint.setFont(new Font(GameConstants.FONT_FAMILY, Font.PLAIN, 10));
        hint.setForeground(Color.GRAY);
        hint.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(hint, BorderLayout.SOUTH);
    }
    
    /**
     * Configura el listener de F12.
     */
    private void setupKeyListener() {
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F12) {
                    toggleVisibility();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        };
        
        // Registrar en el componente raíz
        setFocusable(true);
        addKeyListener(keyListener);
    }
    
    /**
     * Inicia el timer de actualización.
     */
    private void startUpdateTimer() {
        updateTimer = new Timer(500, e -> updateDebugInfo());
        updateTimer.start();
    }
    
    /**
     * Toggle visibilidad del overlay.
     */
    public void toggleVisibility() {
        if (!DEV_MODE) return;
        
        visible = !visible;
        setVisible(visible);
        
        if (visible) {
            updateDebugInfo();
            requestFocusInWindow();
        }
    }
    
    /**
     * Actualiza la información de debug.
     */
    private void updateDebugInfo() {
        if (!visible || !DEV_MODE) return;
        
        StringBuilder sb = new StringBuilder();
        
        // Header
        sb.append("=".repeat(50)).append("\n");
        sb.append("  NEONTHREAD DEBUG OVERLAY\n");
        sb.append("=".repeat(50)).append("\n\n");
        
        // Character State
        sb.append("[ CHARACTER ]\n");
        com.neonthread.Character character = session.getCharacter();
        if (character != null) {
            sb.append("  Name: ").append(character.getName()).append("\n");
            sb.append("  Role: ").append(character.getRole().getDisplayName()).append("\n");
            sb.append("  Difficulty: ").append(character.getDifficulty().getDisplayName()).append("\n");
            sb.append("  Credits: ").append(character.getCredits()).append("\n");
            sb.append("  Physical: ").append(character.getPhysical()).append("\n");
            sb.append("  Intelligence: ").append(character.getIntelligence()).append("\n");
            sb.append("  Charisma: ").append(character.getCharisma()).append("\n");
            sb.append("  Reputation: ").append(character.getReputation()).append("\n");
        } else {
            sb.append("  [No character loaded]\n");
        }
        sb.append("\n");
        
        // World State
        sb.append("[ WORLD STATE ]\n");
        WorldState worldState = session.getWorldState();
        if (worldState != null) {
            sb.append("  Global Reputation: ").append(worldState.getReputation()).append("\n");
            sb.append("  Active Flags: ").append(worldState.getActiveFlags().size()).append("\n");
            for (String flag : worldState.getActiveFlags()) {
                sb.append("    ✓ ").append(flag).append("\n");
            }
        }
        sb.append("\n");
        
        // District State
        sb.append("[ DISTRICT ]\n");
        District district = session.getDistrict();
        if (district != null) {
            sb.append("  ID: ").append(district.getId()).append("\n");
            sb.append("  Name: ").append(district.getName()).append("\n");
            sb.append("  Locations: ").append(district.getLocations().size()).append("\n");
            sb.append("  Missions: ").append(district.getMissions().size()).append("\n");
        }
        sb.append("\n");
        
        // Current Mission
        sb.append("[ CURRENT MISSION ]\n");
        Mission currentMission = session.getCurrentMission();
        if (currentMission != null) {
            sb.append("  ID: ").append(currentMission.getId()).append("\n");
            sb.append("  Title: ").append(currentMission.getTitle()).append("\n");
            sb.append("  Status: ").append(currentMission.getStatus()).append("\n");
            sb.append("  Difficulty: ").append(currentMission.getDifficulty()).append("\n");
        } else {
            sb.append("  [No active mission]\n");
        }
        sb.append("\n");
        
        // System Info
        sb.append("[ SYSTEM ]\n");
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        long usedMemory = totalMemory - freeMemory;
        sb.append("  Memory: ").append(usedMemory).append("MB / ").append(totalMemory).append("MB\n");
        sb.append("  Game Log Entries: ").append(session.getGameLog().getEntries().size()).append("\n");
        
        debugText.setText(sb.toString());
        debugText.setCaretPosition(0);
    }
    
    /**
     * Detiene el timer al cerrar.
     */
    public void dispose() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
    }
}
