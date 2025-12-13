package com.neonthread.screens;

import com.neonthread.*;
import com.neonthread.NarrativeScene.*;
import com.neonthread.stats.StatType;
import com.neonthread.ui.CyberpunkButton;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Escena narrativa interactiva completa (KISS + DRY).
 * Sistema de nodos con texto, opciones, checks y consecuencias.
 */
public class NarrativeSceneScreen extends JPanel {
    private final Consumer<GameState> onStateChange;
    private final GameSession session;
    
    private JPanel topPanel;
    private JLabel ubicacionLabel;
    private JLabel horaLabel;
    private JTextArea narrativeArea;
    private JPanel optionsPanel;
    private JPanel bottomBar;
    
    private NarrativeScene currentScene;
    private Map<String, Boolean> worldFlags;
    private MissionHistory history;
    private boolean typewriterActive;
    
    public NarrativeSceneScreen(Consumer<GameState> onStateChange) {
        this.onStateChange = onStateChange;
        this.session = GameSession.getInstance();
        this.worldFlags = new HashMap<>();
        
        setLayout(new BorderLayout());
        setBackground(new Color(0x0A0A0F));
        
        buildUI();
    }
    
    /**
     * Construye la interfaz completa (DRY).
     */
    private void buildUI() {
        // Zona superior: contexto
        buildTopPanel();
        
        // Zona media: texto narrativo
        buildNarrativePanel();
        
        // Zona inferior: opciones
        buildOptionsPanel();
        
        // Barra inferior: botones de utilidad
        buildBottomBar();
    }
    
    /**
     * Construye el panel superior con ubicación y hora (KISS).
     */
    private void buildTopPanel() {
        topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(GameConstants.COLOR_PANEL);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, GameConstants.COLOR_CYAN_NEON),
            BorderFactory.createEmptyBorder(15, 30, 15, 30)
        ));
        
        ubicacionLabel = new JLabel("Unknown Sector");
        ubicacionLabel.setFont(GameConstants.FONT_MENU.deriveFont(14f));
        ubicacionLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        
        horaLabel = new JLabel(getCurrentTime());
        horaLabel.setFont(GameConstants.FONT_TEXT.deriveFont(12f));
        horaLabel.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        
        topPanel.add(ubicacionLabel, BorderLayout.WEST);
        topPanel.add(horaLabel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
    }
    
    /**
     * Construye el área de texto narrativo (KISS).
     */
    private void buildNarrativePanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(0x0A0A0F));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 20, 60));
        
        narrativeArea = new JTextArea();
        narrativeArea.setEditable(false);
        narrativeArea.setLineWrap(true);
        narrativeArea.setWrapStyleWord(true);
        narrativeArea.setFont(GameConstants.FONT_TEXT.deriveFont(16f));
        narrativeArea.setForeground(new Color(0xE2E2E2));
        narrativeArea.setBackground(new Color(0x0A0A0F));
        narrativeArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Permitir scroll
        JScrollPane scrollPane = new JScrollPane(narrativeArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON.darker(), 1));
        scrollPane.setBackground(new Color(0x0A0A0F));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Click para saltar efecto de tipeo
        narrativeArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (typewriterActive) {
                    typewriterActive = false;
                }
            }
        });
        
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    /**
     * Construye el panel de opciones (DRY).
     */
    private void buildOptionsPanel() {
        optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(new Color(0x0A0A0F));
        optionsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, GameConstants.COLOR_CYAN_NEON),
            BorderFactory.createEmptyBorder(20, 60, 20, 60)
        ));
        
        add(optionsPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Construye la barra inferior con botones de utilidad (KISS).
     */
    private void buildBottomBar() {
        bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomBar.setBackground(GameConstants.COLOR_PANEL);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GameConstants.COLOR_CYAN_NEON));
        
        CyberpunkButton registroBtn = new CyberpunkButton("REGISTRO");
        registroBtn.setPreferredSize(new Dimension(140, 35));
        registroBtn.addActionListener(e -> showRegistro());
        
        CyberpunkButton inventarioBtn = new CyberpunkButton("INVENTARIO");
        inventarioBtn.setPreferredSize(new Dimension(140, 35));
        inventarioBtn.addActionListener(e -> showInventario());
        
        CyberpunkButton salirBtn = new CyberpunkButton("SALIR A MAPA");
        salirBtn.setPreferredSize(new Dimension(140, 35));
        salirBtn.addActionListener(e -> salirAMapa());
        
        bottomBar.add(registroBtn);
        bottomBar.add(inventarioBtn);
        bottomBar.add(salirBtn);
        
        optionsPanel.add(bottomBar);
    }
    
    /**
     * Inicia una nueva escena narrativa (KISS).
     */
    public void startScene(NarrativeScene scene) {
        this.currentScene = scene;
        
        // Inicializar historial si es primera escena
        if (history == null) {
            Mission mission = session.getCurrentMission();
            if (mission != null) {
                history = new MissionHistory(mission.getId());
            }
        }
        
        // Actualizar UI
        if (scene.getUbicacion() != null) {
            ubicacionLabel.setText(scene.getUbicacion());
        }
        horaLabel.setText(getCurrentTime());
        
        // Mostrar texto con efecto de tipeo
        displayNarrativeText(scene.getTexto());
        
        // Aplicar flags de la escena
        worldFlags.putAll(scene.getFlagsActivos());
        
        // Renderizar opciones
        renderOptions(scene);
        
        // Si es escena de cierre, preparar transición
        if (scene.esCierre()) {
            Timer timer = new Timer(3000, e -> goToResults());
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    /**
     * Muestra el texto narrativo con efecto de tipeo (DRY).
     */
    private void displayNarrativeText(String text) {
        narrativeArea.setText("");
        typewriterActive = true;
        
        new Thread(() -> {
            try {
                for (int i = 0; i < text.length() && typewriterActive; i++) {
                    final int index = i;
                    SwingUtilities.invokeLater(() -> {
                        narrativeArea.append(String.valueOf(text.charAt(index)));
                        narrativeArea.setCaretPosition(narrativeArea.getDocument().getLength());
                    });
                    Thread.sleep(15);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            typewriterActive = false;
        }).start();
    }
    
    /**
     * Renderiza las opciones disponibles (KISS).
     */
    private void renderOptions(NarrativeScene scene) {
        // Limpiar opciones anteriores (excepto bottomBar)
        optionsPanel.removeAll();
        
        int optionNumber = 1;
        com.neonthread.Character character = session.getCharacter();
        
        for (SceneOption option : scene.getOpciones()) {
            // Verificar si es visible según flags
            if (!option.esVisible(worldFlags)) {
                continue;
            }
            
            // Crear botón de opción
            JPanel optionContainer = createOptionButton(optionNumber, option, character);
            optionsPanel.add(optionContainer);
            optionsPanel.add(Box.createVerticalStrut(10));
            
            optionNumber++;
        }
        
        // Re-agregar barra inferior
        optionsPanel.add(bottomBar);
        
        optionsPanel.revalidate();
        optionsPanel.repaint();
    }
    
    /**
     * Crea un botón de opción con checks visibles (DRY).
     */
    private JPanel createOptionButton(int number, SceneOption option, com.neonthread.Character character) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(0x0A0A0F));
        container.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Texto de la opción
        CyberpunkButton optionBtn = new CyberpunkButton(number + ") " + option.getTexto());
        optionBtn.setMaximumSize(new Dimension(900, 45));
        optionBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionBtn.addActionListener(e -> selectOption(option));
        
        container.add(optionBtn);
        
        // Mostrar checks requeridos
        if (!option.getChecks().isEmpty()) {
            JPanel checksPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            checksPanel.setBackground(new Color(0x0A0A0F));
            checksPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            for (AttributeCheck check : option.getChecks()) {
                boolean passes = check.evaluar(character);
                JLabel checkLabel = new JLabel("   • " + check.getDescripcion());
                checkLabel.setFont(GameConstants.FONT_TEXT.deriveFont(11f));
                checkLabel.setForeground(passes ? GameConstants.COLOR_CYAN_NEON : GameConstants.COLOR_RED_NEON);
                checksPanel.add(checkLabel);
            }
            
            container.add(checksPanel);
        }
        
        return container;
    }
    
    /**
     * Selecciona una opción y ejecuta su lógica (KISS).
     */
    private void selectOption(SceneOption option) {
        com.neonthread.Character character = session.getCharacter();
        
        // Registrar decisión
        if (history != null) {
            history.incrementDecisions();
        }
        
        // Evaluar checks
        boolean passesChecks = option.pasaChecks(character);
        
        if (passesChecks) {
            // Registrar éxito
            if (history != null && !option.getChecks().isEmpty()) {
                history.recordCheckSuccess();
            }
            
            // Aplicar consecuencias
            option.aplicarConsecuencias(character, worldFlags, session.getGameLog());
            
            // Ir a siguiente escena
            String nextSceneId = option.getSiguienteEscena();
            if (nextSceneId != null) {
                loadScene(nextSceneId);
            }
        } else {
            // Registrar fallo
            if (history != null) {
                history.recordCheckFailed();
            }
            
            // Ir a escena de fallo
            String failSceneId = option.getEscenaFallo();
            if (failSceneId != null) {
                loadScene(failSceneId);
            } else {
                // Sin escena de fallo, mostrar mensaje
                session.getGameLog().add("Check fallido: no puedes realizar esa acción");
            }
        }
    }
    
    /**
     * Carga una nueva escena por ID (DRY).
     */
    private void loadScene(String sceneId) {
        NarrativeScene nextScene = com.neonthread.loaders.SceneLoader.getScene(sceneId);
        if (nextScene == null) {
            // Fallback a ejemplo si no existe en JSON
            nextScene = createExampleScene(sceneId);
        }
        startScene(nextScene);
    }
    
    /**
     * Crea escenas de ejemplo (temporal, será reemplazado por sistema de carga).
     */
    private NarrativeScene createExampleScene(String sceneId) {
        NarrativeScene scene = new NarrativeScene(
            sceneId,
            "Contacto en el Distrito",
            "Te encuentras en un callejón oscuro del distrito industrial. " +
            "Un holograma parpadea frente a ti con un mensaje cifrado. " +
            "Tu contacto te ha dado las coordenadas, pero algo no se siente bien.\n\n" +
            "¿Qué decides hacer?"
        );
        scene.setUbicacion("Industrial District - Sector 7");
        
        // Opción 1: Check de inteligencia
        SceneOption option1 = new SceneOption("Descifrar el mensaje", "scene_success");
        option1.addCheck(new AttributeCheck(StatType.INTELLIGENCE, 4, "Inteligencia ≥ 4"));
        option1.addConsecuencia(new Consequence(Consequence.ConsequenceType.ADD_LOG, "Mensaje descifrado con éxito", 0));
        option1.addConsecuencia(new Consequence(Consequence.ConsequenceType.CHANGE_REPUTATION, "", 1));
        
        // Opción 2: Check de percepción
        SceneOption option2 = new SceneOption("Buscar trampas en el área", "scene_trap_found");
        option2.addCheck(new AttributeCheck(StatType.PERCEPTION, 3, "Percepción ≥ 3"));
        option2.setEscenaFallo("scene_trap_triggered");
        
        // Opción 3: Sin checks
        SceneOption option3 = new SceneOption("Retirarse y reportar", "scene_retreat");
        option3.addConsecuencia(new Consequence(Consequence.ConsequenceType.CHANGE_CREDITS, "", 50));
        
        scene.addOpcion(option1);
        scene.addOpcion(option2);
        scene.addOpcion(option3);
        
        // Si es la última escena, marcar como cierre
        if (sceneId.equals("scene_success") || sceneId.equals("scene_retreat")) {
            scene.setEsCierre(true);
        }
        
        return scene;
    }
    
    /**
     * Muestra el registro de eventos (KISS).
     */
    private void showRegistro() {
        GameLog log = session.getGameLog();
        if (log == null) return;
        
        StringBuilder logText = new StringBuilder("REGISTRO DE EVENTOS:\n\n");
        for (GameLog.LogEntry entry : log.getRecentEntries(20)) {
            logText.append("• ").append(entry.toString()).append("\n");
        }
        
        JTextArea logArea = new JTextArea(logText.toString());
        logArea.setEditable(false);
        logArea.setFont(GameConstants.FONT_TEXT.deriveFont(12f));
        logArea.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        logArea.setBackground(GameConstants.COLOR_PANEL);
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Registro", JOptionPane.PLAIN_MESSAGE);
    }
    
    /**
     * Muestra el inventario en un diálogo modal.
     */
    private void showInventario() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Inventory", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);
        
        InventoryScreen inventoryScreen = new InventoryScreen(state -> {});
        inventoryScreen.refresh();
        inventoryScreen.setBackAction(() -> dialog.dispose());
        
        inventoryScreen.setBorder(BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON, 2));
        
        dialog.add(inventoryScreen);
        dialog.setVisible(true);
    }
    
    /**
     * Vuelve al mapa (solo si es seguro).
     */
    private void salirAMapa() {
        if (currentScene != null && !currentScene.esCierre()) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Abandonar la misión? Perderás el progreso.",
                "Salir",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                onStateChange.accept(GameState.STATE_DISTRICT_MAP);
            }
        }
    }
    
    /**
     * Transición a pantalla de resultados (KISS).
     */
    private void goToResults() {
        if (history != null) {
            history.finish();
        }
        
        Mission mission = session.getCurrentMission();
        if (mission != null) {
            mission.complete(session.getCharacter());
        }
        
        onStateChange.accept(GameState.STATE_RESULT_SCREEN);
    }
    
    /**
     * Obtiene la hora actual formateada.
     */
    private String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    
    /**
     * Expone el historial para ResultScreen.
     */
    public MissionHistory getHistory() {
        return history;
    }
}
