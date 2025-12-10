package com.neonthread;

import com.neonthread.screens.BootScreen;
import com.neonthread.screens.BootstrapScreen;
import com.neonthread.screens.LogoScreen;
import com.neonthread.screens.MenuScreen;
import com.neonthread.screens.SettingsScreen;
import com.neonthread.screens.TitleScreen;

import javax.swing.*;
import java.awt.*;

/**
 * Clase principal del juego NEONTHREAD.
 * Maneja el flujo de estados y la ventana principal.
 */
public class NeonThreadGame extends JFrame {
    private GameState currentState;
    private BootstrapScreen bootstrapScreen;
    private BootScreen bootScreen;
    private LogoScreen logoScreen;
    private TitleScreen titleScreen;
    private MenuScreen menuScreen;
    private SettingsScreen settingsScreen;
    
    public NeonThreadGame() {
        initializeWindow();
        initializeScreens();
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
        bootstrapScreen = new BootstrapScreen(this::changeState);
        bootScreen = new BootScreen(this::changeState);
        logoScreen = new LogoScreen(this::changeState);
        titleScreen = new TitleScreen(this::changeState);
        menuScreen = new MenuScreen();
        settingsScreen = new SettingsScreen(v -> showMenu(), this);
        
        // Connect menu to settings
        menuScreen.setOnSettingsRequested(this::showSettings);
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
        }
        
        revalidate();
        repaint();
    }
    
    private void showSettings() {
        getContentPane().removeAll();
        getContentPane().add(settingsScreen);
        settingsScreen.show();
        revalidate();
        repaint();
    }
    
    private void showMenu() {
        getContentPane().removeAll();
        getContentPane().add(menuScreen);
        menuScreen.show();
        revalidate();
        repaint();
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
        // Configurar Look and Feel para mejor apariencia
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
