package com.neonthread;

import com.neonthread.screens.*;
import com.neonthread.ui.TransitionOverlay;
import com.neonthread.ui.DebugOverlay;

import com.neonthread.stats.StatType;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * Clase principal del juego NEONTHREAD (KISS + DRY).
 * Maneja el flujo completo desde bootstrap hasta gameplay.
 */
public class NeonThreadGame extends JFrame {
    private GameState currentState;
    private GameState previousState; // Para pausa
    private DebugOverlay debugOverlay; // FASE 3 Feature 12
    
    // Pantallas de inicio
    private BootstrapScreen bootstrapScreen;
    private BootScreen bootScreen;
    private LogoScreen logoScreen;
    private MenuScreen menuScreen;
    private SettingsScreen settingsScreen;
    
    // Pantallas de juego
    private LoadingRunScreen loadingRunScreen;
    private CharacterCreationScreen characterCreationScreen;
    private IntroNarrativeScreen introNarrativeScreen;
    private DistrictMapScreen districtMapScreen;
    private MissionWindowScreen missionWindowScreen;
    private NarrativeSceneScreen narrativeSceneScreen;
    private ResultScreen resultScreen;
    private PauseScreen pauseScreen;
    private InventoryScreen inventoryScreen;
    
    public NeonThreadGame() {
        initializeWindow();
        initializeScreens();
        setupKeyBindings();
        changeState(GameState.STATE_BOOTSTRAP);
    }
    
    private void initializeWindow() {
        setTitle("NEONTHREAD: PROTOCOL 07");
        setSize(GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(GameConstants.COLOR_BACKGROUND);
        
        // Apply initial settings
        new SettingsApplier(this).applyAll();
    }
    
    private void initializeScreens() {
        // Pantallas de inicio
        bootstrapScreen = new BootstrapScreen(this::changeState);
        bootScreen = new BootScreen(this::changeState);
        logoScreen = new LogoScreen(this::changeState);
        menuScreen = new MenuScreen();
        settingsScreen = new SettingsScreen(v -> changeState(GameState.STATE_MENU), this);
        
        // Conectar callbacks del menú
        menuScreen.setOnSettingsRequested(() -> changeState(GameState.STATE_SETTINGS));
        menuScreen.setOnStartRunRequested(this::startNewRun);
        
        // Pantallas de juego (lazy initialization)
        loadingRunScreen = new LoadingRunScreen(this::changeState);
        characterCreationScreen = new CharacterCreationScreen(this::changeState);
        introNarrativeScreen = new IntroNarrativeScreen(this::changeState);
        districtMapScreen = new DistrictMapScreen(this::changeState);
        missionWindowScreen = new MissionWindowScreen(this::changeState);
        narrativeSceneScreen = new NarrativeSceneScreen(this::changeState);
        resultScreen = new ResultScreen(this::changeState);
        inventoryScreen = new InventoryScreen(this::changeState);
    }
    
    /**
     * Configura atajos de teclado (DRY).
     */
    private void setupKeyBindings() {
        JRootPane rootPane = getRootPane();
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();
        
        // ESC para pausa
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "pause");
        actionMap.put("pause", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                handlePause();
            }
        });
        
        // F12 para debug overlay (FASE 3 Feature 12)
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "debug");
        actionMap.put("debug", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (debugOverlay != null) {
                    debugOverlay.toggleVisibility();
                }
            }
        });
        
        // Inicializar debug overlay
        GameSession session = GameSession.getInstance();
        debugOverlay = new DebugOverlay(session);
        getLayeredPane().add(debugOverlay, JLayeredPane.POPUP_LAYER);
        debugOverlay.setBounds(10, 10, 400, 600);
    }
    
    private void handlePause() {
        // District map: ESC exits the district (flow improvement)
        // Handled in DistrictMapScreen now, but keep here as fallback or global override if needed.
        // Actually, if DistrictMapScreen consumes ESC, this might not trigger if focus is there.
        // But JRootPane bindings usually work globally.
        // Let's keep it consistent. If in DistrictMap, maybe just pause?
        // User requested "ESC exits the district" in previous turn, but now we have a Pause Menu.
        // Standard game behavior: ESC -> Pause Menu.
        // Let's change DistrictMap behavior to Pause Menu as well.
        
        if (currentState == null) return;

        if (isGameplayState(currentState) && currentState != GameState.STATE_PAUSE) {
            previousState = currentState;
            changeState(GameState.STATE_PAUSE);
        } else if (currentState == GameState.STATE_PAUSE) {
            if (previousState != null) {
                changeState(previousState);
            } else {
                // Fallback if previous state is lost
                changeState(GameState.STATE_MENU);
            }
        }
    }
    
    private boolean isGameplayState(GameState state) {
        return state == GameState.STATE_DISTRICT_MAP ||
               state == GameState.STATE_MISSION_WINDOW ||
               state == GameState.STATE_NARRATIVE_SCENE ||
               state == GameState.STATE_RESULT_SCREEN;
    }
    
    private void changeState(GameState newState) {
        changeState(newState, false);
    }
    
    private void changeState(GameState newState, boolean skipTransition) {
        // Guardar el estado anterior antes de cambiar
        if (currentState != null && currentState != newState) {
            previousState = currentState;
        }
        
        // Determinar si debe mostrar transición
        boolean shouldShowTransition = !skipTransition && shouldUseTransition(currentState, newState);
        
        if (shouldShowTransition) {
            showTransitionAndChangeState(newState);
        } else {
            executeStateChange(newState);
        }
    }
    
    /**
     * Determina si debe mostrarse una transición entre estados.
     */
    private boolean shouldUseTransition(GameState from, GameState to) {
        // Transiciones en el flujo START RUN
        if (from == GameState.STATE_MENU && to == GameState.STATE_LOADING_RUN) return true;
        if (from == GameState.STATE_INTRO_NARRATIVE && to == GameState.STATE_DISTRICT_MAP) return true;
        if (from == GameState.STATE_CHARACTER_CREATION && to == GameState.STATE_INTRO_NARRATIVE) return true;
        
        // Transiciones de gameplay
        if (to == GameState.STATE_MISSION_WINDOW) return true;
        if (to == GameState.STATE_NARRATIVE_SCENE) return true;
        
        return false;
    }
    
    /**
     * Obtiene el mensaje de transición apropiado.
     */
    private String getTransitionMessage(GameState to) {
        switch (to) {
            case STATE_LOADING_RUN: return "INITIALIZING NEURAL LINK...";
            case STATE_INTRO_NARRATIVE: return "SYNCHRONIZING IDENTITY...";
            case STATE_DISTRICT_MAP: return "CONNECTING TO THE NETWORK...";
            case STATE_MISSION_WINDOW: return "ACCESSING MISSION DATA...";
            case STATE_NARRATIVE_SCENE: return "LOADING SCENARIO...";
            default: return "PROCESSING...";
        }
    }
    
    /**
     * Muestra el overlay de transición y luego cambia el estado.
     */
    private void showTransitionAndChangeState(GameState newState) {
        String message = getTransitionMessage(newState);
        TransitionOverlay overlay = new TransitionOverlay(message);
        overlay.show(getLayeredPane(), () -> executeStateChange(newState));
    }
    
    /**
     * Ejecuta el cambio de estado real (separado para reutilización).
     */
    private void executeStateChange(GameState newState) {
        // Limpiar estado anterior
        if (currentState != null) {
            cleanupCurrentState();
        }
        
        currentState = newState;
        getContentPane().removeAll();
        
        // Activar nuevo estado
        switch (currentState) {
            case STATE_BOOTSTRAP:
                getContentPane().add(bootstrapScreen);
                bootstrapScreen.startSequence();
                break;
                
            case STATE_BOOT:
                getContentPane().add(bootScreen);
                bootScreen.startBootSequence();
                break;
                
            case STATE_LOGO_GLITCH:
                getContentPane().add(logoScreen);
                logoScreen.show();
                break;
                
            case STATE_MENU:
                getContentPane().add(menuScreen);
                menuScreen.show();
                break;
                
            case STATE_SETTINGS:
                getContentPane().add(settingsScreen);
                settingsScreen.setVisible(true);
                break;
                
            case STATE_LOADING_RUN:
                getContentPane().add(loadingRunScreen);
                loadingRunScreen.startLoading();
                break;
                
            case STATE_CHARACTER_CREATION:
                getContentPane().add(characterCreationScreen);
                characterCreationScreen.reset();
                break;
                
            case STATE_INTRO_NARRATIVE:
                getContentPane().add(introNarrativeScreen);
                introNarrativeScreen.startNarrative();
                break;
                
            case STATE_DISTRICT_MAP:
                getContentPane().add(districtMapScreen);
                districtMapScreen.refresh();
                break;
                
            case STATE_MISSION_WINDOW:
                getContentPane().add(missionWindowScreen);
                missionWindowScreen.show();
                break;
                
            case STATE_NARRATIVE_SCENE:
                getContentPane().add(narrativeSceneScreen);
                // Cargar escena inicial desde JSON
                NarrativeScene initialScene = com.neonthread.loaders.SceneLoader.getScene("scene_01");
                if (initialScene == null) {
                    initialScene = createInitialScene();
                }
                narrativeSceneScreen.startScene(initialScene);
                break;
                
            case STATE_RESULT_SCREEN:
                getContentPane().add(resultScreen);
                resultScreen.showResults(narrativeSceneScreen.getHistory());
                break;
                
            case STATE_INVENTORY:
                getContentPane().add(inventoryScreen);
                inventoryScreen.setBackAction(() -> changeState(previousState != null ? previousState : GameState.STATE_MENU));
                inventoryScreen.refresh();
                break;
                
            case STATE_PAUSE:
                pauseScreen = new PauseScreen(
                    this::changeState,
                    () -> {
                        // Mostrar settings desde pausa
                        changeState(GameState.STATE_SETTINGS);
                        // Configure settings screen to go back to pause
                        settingsScreen.setBackAction(v -> changeState(GameState.STATE_PAUSE));
                    },
                    previousState
                );
                getContentPane().add(pauseScreen);
                break;
        }
        
        revalidate();
        repaint();
    }
    
    /**
     * Inicia una nueva partida (KISS).
     */
    private void startNewRun() {
        changeState(GameState.STATE_LOADING_RUN);
    }
    
    /**
     * Crea la escena inicial de ejemplo (temporal).
     */
    private NarrativeScene createInitialScene() {
        NarrativeScene scene = new NarrativeScene(
            "scene_01",
            "Primer Contacto",
            "Te encuentras en un callejón oscuro del distrito industrial. " +
            "Un holograma parpadea frente a ti con un mensaje cifrado. " +
            "Tu contacto te ha dado las coordenadas, pero algo no se siente bien.\n\n" +
            "¿Qué decides hacer?"
        );
        scene.setUbicacion("Distrito Industrial - Sector 7");
        
        // Opción 1: Check de inteligencia
        NarrativeScene.SceneOption option1 = new NarrativeScene.SceneOption(
            "Descifrar el mensaje usando tus implantes", "scene_success"
        );
        option1.addCheck(new NarrativeScene.AttributeCheck(
            StatType.INTELLIGENCE, 4, "Inteligencia ≥ 4"
        ));
        option1.addConsecuencia(new NarrativeScene.Consequence(
            NarrativeScene.Consequence.ConsequenceType.ADD_LOG, 
            "Mensaje descifrado con éxito", 0
        ));
        option1.addConsecuencia(new NarrativeScene.Consequence(
            NarrativeScene.Consequence.ConsequenceType.CHANGE_REPUTATION, "", 1
        ));
        
        // Opción 2: Check de percepción
        NarrativeScene.SceneOption option2 = new NarrativeScene.SceneOption(
            "Buscar trampas en el área antes de avanzar", "scene_success"
        );
        option2.addCheck(new NarrativeScene.AttributeCheck(
            StatType.PERCEPTION, 3, "Percepción ≥ 3"
        ));
        option2.addConsecuencia(new NarrativeScene.Consequence(
            NarrativeScene.Consequence.ConsequenceType.CHANGE_BATTERY, "", -10
        ));
        
        // Opción 3: Sin checks
        NarrativeScene.SceneOption option3 = new NarrativeScene.SceneOption(
            "Retirarse y reportar al contacto", "scene_success"
        );
        option3.addConsecuencia(new NarrativeScene.Consequence(
            NarrativeScene.Consequence.ConsequenceType.CHANGE_CREDITS, "", 50
        ));
        
        scene.addOpcion(option1);
        scene.addOpcion(option2);
        scene.addOpcion(option3);
        
        return scene;
    }
    
    private void cleanupCurrentState() {
        switch (currentState) {
            case STATE_BOOTSTRAP:
                bootstrapScreen.cleanup();
                break;
            case STATE_BOOT:
                bootScreen.cleanup();
                break;
            case STATE_LOGO_GLITCH:
                logoScreen.cleanup();
                break;
            case STATE_MENU:
                menuScreen.cleanup();
                break;
            case STATE_SETTINGS:
            case STATE_LOADING_RUN:
            case STATE_CHARACTER_CREATION:
            case STATE_INTRO_NARRATIVE:
            case STATE_DISTRICT_MAP:
            case STATE_INVENTORY:
            case STATE_MISSION_WINDOW:
            case STATE_NARRATIVE_SCENE:
            case STATE_RESULT_SCREEN:
            case STATE_PAUSE:
                // No cleanup needed for these states
                break;
        }
    }
    
    public static void main(String[] args) {
        // Configurar Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Lanzar en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            NeonThreadGame game = new NeonThreadGame();
            game.setVisible(true);
        });
    }
}
