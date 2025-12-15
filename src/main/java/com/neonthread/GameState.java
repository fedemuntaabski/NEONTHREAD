package com.neonthread;

/**
 * Estados del juego siguiendo patrón State Machine simple.
 * Flujo completo desde bootstrap hasta gameplay.
 */
public enum GameState {
    // Estados de inicio
    STATE_BOOTSTRAP,
    STATE_BOOT,
    STATE_LOGO_GLITCH,
    STATE_MENU,
    STATE_SETTINGS,
    
    // Estados de juego
    STATE_LOADING_RUN,
    STATE_CHARACTER_CREATION,
    STATE_INTRO_NARRATIVE,
    STATE_DISTRICT_MAP,
    STATE_MISSION_WINDOW,
    STATE_NARRATIVE_SCENE,
    STATE_RESULT_SCREEN,
    STATE_PAUSE,
    STATE_INVENTORY
}
