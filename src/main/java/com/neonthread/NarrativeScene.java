package com.neonthread;

import com.neonthread.inventory.InventoryItem;
import com.neonthread.inventory.ItemRegistry;
import com.neonthread.stats.StatEffectApplier;
import com.neonthread.stats.StatType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Nodo narrativo interactivo (KISS + DRY).
 * Cada escena tiene texto, opciones con checks y consecuencias.
 */
public class NarrativeScene {
    private String id;
    private String titulo;
    private String texto;
    private String ubicacion;
    private List<SceneOption> opciones;
    private Map<String, Boolean> flagsActivos;
    private Map<String, Boolean> flagsRequeridos;
    private String musicaOpcional;
    private boolean esCierre; // Si es true, ir a ResultScreen
    
    public NarrativeScene(String id, String titulo, String texto) {
        this.id = id;
        this.titulo = titulo;
        this.texto = texto;
        this.opciones = new ArrayList<>();
        this.flagsActivos = new HashMap<>();
        this.flagsRequeridos = new HashMap<>();
        this.esCierre = false;
    }
    
    // Getters
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getTexto() { return texto; }
    public String getUbicacion() { return ubicacion; }
    public List<SceneOption> getOpciones() { return opciones; }
    public Map<String, Boolean> getFlagsActivos() { return flagsActivos; }
    public Map<String, Boolean> getFlagsRequeridos() { return flagsRequeridos; }
    public String getMusicaOpcional() { return musicaOpcional; }
    public boolean esCierre() { return esCierre; }
    
    // Setters
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public void setMusicaOpcional(String musica) { this.musicaOpcional = musica; }
    public void setEsCierre(boolean esCierre) { this.esCierre = esCierre; }
    public void addOpcion(SceneOption opcion) { this.opciones.add(opcion); }
    public void setFlag(String key, boolean value) { this.flagsActivos.put(key, value); }
    public void addFlagRequerido(String key, boolean value) { this.flagsRequeridos.put(key, value); }
    
    /**
     * Verifica si los flags requeridos coinciden con el estado actual.
     */
    public boolean cumpleFlagsRequeridos(Map<String, Boolean> worldFlags) {
        for (Map.Entry<String, Boolean> entry : flagsRequeridos.entrySet()) {
            Boolean worldValue = worldFlags.get(entry.getKey());
            if (worldValue == null || !worldValue.equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Opción de diálogo/acción en una escena (KISS).
     */
    public static class SceneOption {
        private String texto;
        private List<AttributeCheck> checks;
        private List<Consequence> consecuencias;
        private String siguienteEscena;
        private String escenaFallo; // Si falla un check
        private Map<String, Boolean> flagsRequeridos; // Para mostrar/ocultar opción
        
        public SceneOption(String texto, String siguienteEscena) {
            this.texto = texto;
            this.siguienteEscena = siguienteEscena;
            this.checks = new ArrayList<>();
            this.consecuencias = new ArrayList<>();
            this.flagsRequeridos = new HashMap<>();
        }
        
        // Getters
        public String getTexto() { return texto; }
        public List<AttributeCheck> getChecks() { return checks; }
        public List<Consequence> getConsecuencias() { return consecuencias; }
        public String getSiguienteEscena() { return siguienteEscena; }
        public String getEscenaFallo() { return escenaFallo; }
        public Map<String, Boolean> getFlagsRequeridos() { return flagsRequeridos; }
        
        // Setters
        public void setEscenaFallo(String escenaFallo) { this.escenaFallo = escenaFallo; }
        public void addCheck(AttributeCheck check) { this.checks.add(check); }
        public void addConsecuencia(Consequence consecuencia) { this.consecuencias.add(consecuencia); }
        public void addFlagRequerido(String key, boolean value) { this.flagsRequeridos.put(key, value); }
        
        /**
         * Verifica si la opción debe ser visible según flags.
         */
        public boolean esVisible(Map<String, Boolean> worldFlags) {
            for (Map.Entry<String, Boolean> entry : flagsRequeridos.entrySet()) {
                Boolean worldValue = worldFlags.get(entry.getKey());
                if (worldValue == null || !worldValue.equals(entry.getValue())) {
                    return false;
                }
            }
            return true;
        }
        
        /**
         * Ejecuta todos los checks de la opción.
         */
        public boolean pasaChecks(Character character) {
            for (AttributeCheck check : checks) {
                if (!check.evaluar(character)) {
                    return false;
                }
            }
            return true;
        }
        
        /**
         * Aplica todas las consecuencias.
         */
        public void aplicarConsecuencias(Character character, Map<String, Boolean> worldFlags, GameLog log) {
            for (Consequence c : consecuencias) {
                c.aplicar(character, worldFlags, log);
            }
        }
    }
    
    /**
     * Check de atributo o recurso (DRY).
     */
    public static class AttributeCheck {
        private StatType tipo;
        private int valorMinimo;
        private String descripcion;
        
        public AttributeCheck(StatType tipo, int valorMinimo, String descripcion) {
            this.tipo = tipo;
            this.valorMinimo = valorMinimo;
            this.descripcion = descripcion;
        }
        
        public StatType getTipo() { return tipo; }
        public int getValorMinimo() { return valorMinimo; }
        public String getDescripcion() { return descripcion; }
        
        /**
         * Evalúa el check contra el personaje.
         */
        public boolean evaluar(Character character) {
            switch (tipo) {
                case INTELLIGENCE:
                case PHYSICAL:
                case PERCEPTION:
                case CHARISMA:
                    return character.getEffectiveAttribute(tipo) >= valorMinimo;
                case HACK:
                case COMBAT:
                case STEALTH:
                case NEGOTIATION:
                case ANALYSIS:
                    return character.getEffectiveCapability(tipo) >= valorMinimo;
                case BATTERY: return character.getBattery() >= valorMinimo;
                case CREDITS: return character.getCredits() >= valorMinimo;
                case REPUTATION: return character.getReputation() >= valorMinimo;
                case KARMA: return character.getKarma() >= valorMinimo;
                case NOTORIETY: return character.getNotoriety() >= valorMinimo;
                case ENERGY: return character.getEnergy() >= valorMinimo;
                case HEALTH: return character.getHealth() >= valorMinimo;
                default: return false;
            }
        }
    }
    
    /**
     * Consecuencia de una opción (KISS).
     */
    public static class Consequence {
        public enum ConsequenceType {
            CHANGE_BATTERY, CHANGE_CREDITS, CHANGE_REPUTATION, CHANGE_NOTORIETY,
            CHANGE_KARMA, CHANGE_ENERGY, CHANGE_HEALTH,
            ADD_ITEM, REMOVE_ITEM, SET_FLAG, ADD_LOG
        }
        
        private ConsequenceType tipo;
        private String key; // Para flags, items, logs
        private int valor; // Para cambios numéricos
        
        public Consequence(ConsequenceType tipo, String key, int valor) {
            this.tipo = tipo;
            this.key = key;
            this.valor = valor;
        }
        
        public ConsequenceType getTipo() { return tipo; }
        public String getKey() { return key; }
        public int getValor() { return valor; }
        
        /**
         * Aplica la consecuencia.
         */
        public void aplicar(Character character, Map<String, Boolean> worldFlags, GameLog log) {
            switch (tipo) {
                case CHANGE_BATTERY:
                    if (valor < 0) StatEffectApplier.consumeBattery(character, -valor);
                    else character.setBattery(character.getBattery() + valor);
                    if (log != null) log.add("Batería: " + (valor > 0 ? "+" : "") + valor);
                    break;
                case CHANGE_CREDITS:
                    character.setCredits(character.getCredits() + valor);
                    if (log != null) log.add("Créditos: " + (valor > 0 ? "+" : "") + valor);
                    break;
                case CHANGE_REPUTATION:
                    StatEffectApplier.addReputation(character, valor);
                    if (log != null) log.add("Reputación: " + (valor > 0 ? "+" : "") + valor);
                    break;
                case CHANGE_NOTORIETY:
                    StatEffectApplier.addNotoriety(character, valor);
                    if (log != null) log.add("Notoriedad: " + (valor > 0 ? "+" : "") + valor);
                    break;
                case CHANGE_KARMA:
                    StatEffectApplier.addKarma(character, valor);
                    if (log != null) log.add("Karma: " + (valor > 0 ? "+" : "") + valor);
                    break;
                case CHANGE_ENERGY:
                    if (valor < 0) StatEffectApplier.consumeEnergy(character, -valor);
                    else character.setEnergy(character.getEnergy() + valor);
                    if (log != null) log.add("Energía: " + (valor > 0 ? "+" : "") + valor);
                    break;
                case CHANGE_HEALTH:
                    if (valor < 0) StatEffectApplier.applyDamage(character, -valor);
                    else StatEffectApplier.applyHealing(character, valor);
                    if (log != null) log.add("Salud: " + (valor > 0 ? "+" : "") + valor);
                    break;
                case SET_FLAG:
                    worldFlags.put(key, valor > 0);
                    break;
                case ADD_LOG:
                    if (log != null) log.add(key);
                    break;
                case ADD_ITEM:
                    InventoryItem itemToAdd = ItemRegistry.getItem(key);
                    if (itemToAdd != null) {
                        character.getInventory().addItem(itemToAdd);
                        if (log != null) log.add("Item adquirido: " + itemToAdd.getName());
                    } else {
                        if (log != null) log.add("Error: Item desconocido " + key);
                    }
                    break;
                case REMOVE_ITEM:
                    // Need to find item in inventory by ID
                    // For now, simplified removal if we had a way to look up in inventory by ID
                    // character.getInventory().removeItemById(key);
                    if (log != null) log.add("Item usado: " + key);
                    break;
            }
        }
    }
}
