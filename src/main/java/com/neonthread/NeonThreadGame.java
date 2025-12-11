package com.neonthread;

import com.neonthread.screens.*;

import javax.swing.*;
import java.awt.*;

/**
 * Clase principal del juego NEONTHREAD (KISS + DRY).
 * Maneja el flujo completo desde bootstrap hasta gameplay.
 */
public class NeonThreadGame extends JFrame {
    private GameState currentState;
    private GameState previousState; // Para pausa
    
    // Pantallas de inicio
    private BootstrapScreen bootstrapScreen;
    private BootScreen bootScreen;
    private LogoScreen logoScreen;
    private TitleScreen titleScreen;
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
    }
    
    private void initializeScreens() {
        // Pantallas de inicio
        bootstrapScreen = new BootstrapScreen(this::changeState);
        bootScreen = new BootScreen(this::changeState);
        logoScreen = new LogoScreen(this::changeState);
        titleScreen = new TitleScreen(this::changeState);
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
    }
    
    private void handlePause() {
        // Solo pausar durante gameplay
        if (isGameplayState(currentState) && currentState != GameState.STATE_PAUSE) {
            previousState = currentState;
            changeState(GameState.STATE_PAUSE);
        }
    }
    
    private boolean isGameplayState(GameState state) {
        return state == GameState.STATE_DISTRICT_MAP ||
               state == GameState.STATE_MISSION_WINDOW ||
               state == GameState.STATE_NARRATIVE_SCENE ||
               state == GameState.STATE_RESULT_SCREEN;
    }
    
    private void changeState(GameState newState) {
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
                
            case STATE_TITLE:
                getContentPane().add(titleScreen);
                titleScreen.show();
                break;
                
            case STATE_MENU:
                getContentPane().add(menuScreen);
                menuScreen.show();
                break;
                
            case STATE_SETTINGS:
                getContentPane().add(settingsScreen);
                settingsScreen.show();
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
                // Crear escena inicial de ejemplo
                NarrativeScene initialScene = createInitialScene();
                narrativeSceneScreen.startScene(initialScene);
                break;
                
            case STATE_RESULT_SCREEN:
                getContentPane().add(resultScreen);
                resultScreen.showResults(narrativeSceneScreen.getHistory());
                break;
                
            case STATE_PAUSE:
                pauseScreen = new PauseScreen(
                    this::changeState,
                    () -> {
                        // TODO: Mostrar settings desde pausa
                        changeState(previousState);
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
            NarrativeScene.AttributeCheck.CheckType.INTELLIGENCE, 4, "Inteligencia ≥ 4"
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
            NarrativeScene.AttributeCheck.CheckType.PERCEPTION, 3, "Percepción ≥ 3"
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
