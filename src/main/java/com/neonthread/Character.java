package com.neonthread;

/**
 * Representa el personaje del jugador (DRY).
 * Almacena toda la información relevante de la run.
 */
public class Character {
    private String name;
    private Role role;
    private Difficulty difficulty;
    
    // Atributos base
    private int intelligence;
    private int physical;
    private int perception;
    private int charisma;
    
    // Stats del personaje
    private int level;
    private int credits;
    private int health;
    private int energy;
    private int karma;
    private int notoriety;
    private int battery; // Energía para acciones especiales
    private int reputation; // Reputación en la ciudad
    
    public Character(String name, Role role, Difficulty difficulty) {
        this.name = name;
        this.role = role;
        this.difficulty = difficulty;
        
        // Atributos base según rol
        initializeAttributesByRole(role);
        
        // Valores iniciales
        this.level = 1;
        this.credits = 1000;
        this.health = 100;
        this.energy = 100;
        this.karma = 0;
        this.notoriety = 0;
        this.battery = 100;
        this.reputation = 0;
    }
    
    /**
     * Inicializa atributos según el rol (DRY).
     */
    private void initializeAttributesByRole(Role role) {
        switch (role) {
            case HACKER:
                this.intelligence = 5;
                this.physical = 1;
                this.perception = 3;
                this.charisma = 2;
                break;
            case MERC:
                this.intelligence = 2;
                this.physical = 5;
                this.perception = 3;
                this.charisma = 1;
                break;
            case INFO_BROKER:
                this.intelligence = 3;
                this.physical = 1;
                this.perception = 4;
                this.charisma = 4;
                break;
        }
    }
    
    // Getters y setters
    public String getName() { return name; }
    public Role getRole() { return role; }
    public Difficulty getDifficulty() { return difficulty; }
    public int getIntelligence() { return intelligence; }
    public int getPhysical() { return physical; }
    public int getPerception() { return perception; }
    public int getCharisma() { return charisma; }
    public int getLevel() { return level; }
    public int getCredits() { return credits; }
    public int getHealth() { return health; }
    public int getEnergy() { return energy; }
    public int getKarma() { return karma; }
    public int getNotoriety() { return notoriety; }
    public int getBattery() { return battery; }
    public int getReputation() { return reputation; }
    
    public void setLevel(int level) { this.level = level; }
    public void setCredits(int credits) { this.credits = credits; }
    public void setHealth(int health) { this.health = Math.max(0, Math.min(100, health)); }
    public void setEnergy(int energy) { this.energy = Math.max(0, Math.min(100, energy)); }
    public void setKarma(int karma) { this.karma = karma; }
    public void setNotoriety(int notoriety) { this.notoriety = Math.max(0, notoriety); }
    public void setBattery(int battery) { this.battery = Math.max(0, Math.min(100, battery)); }
    public void setReputation(int reputation) { this.reputation = reputation; }
    
    /**
     * Obtiene el multiplicador de daño enemigo según dificultad (DRY).
     */
    public double getEnemyDamageMultiplier() {
        return difficulty.getEnemyDamageMultiplier();
    }
    
    /**
     * Obtiene el modificador de probabilidad de fallo narrativo (DRY).
     */
    public int getNarrativeFailModifier() {
        return difficulty.getNarrativeFailModifier();
    }
    
    /**
     * Obtiene el multiplicador de costos en mercado (DRY).
     */
    public double getMarketCostMultiplier() {
        return difficulty.getMarketCostMultiplier();
    }
    
    /**
     * Roles disponibles del personaje.
     */
    public enum Role {
        HACKER("Hacker", "Especialista en sistemas y redes"),
        MERC("Merc", "Mercenario de combate urbano"),
        INFO_BROKER("Info Broker", "Intermediario de información");
        
        private final String displayName;
        private final String description;
        
        Role(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    /**
     * Dificultades disponibles.
     */
    public enum Difficulty {
        EASY("Fácil", "Narrativa relajada", 0.7, -10, 0.85),
        NORMAL("Normal", "Equilibrio entre narrativa y desafío", 1.0, 0, 1.0),
        HARD("Difícil", "Cada decisión cuenta", 1.25, 12, 1.15);
        
        private final String displayName;
        private final String description;
        private final double enemyDamageMultiplier;
        private final int narrativeFailModifier; // +/- porcentaje
        private final double marketCostMultiplier;
        
        Difficulty(String displayName, String description, double enemyDamageMultiplier, 
                   int narrativeFailModifier, double marketCostMultiplier) {
            this.displayName = displayName;
            this.description = description;
            this.enemyDamageMultiplier = enemyDamageMultiplier;
            this.narrativeFailModifier = narrativeFailModifier;
            this.marketCostMultiplier = marketCostMultiplier;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public double getEnemyDamageMultiplier() { return enemyDamageMultiplier; }
        public int getNarrativeFailModifier() { return narrativeFailModifier; }
        public double getMarketCostMultiplier() { return marketCostMultiplier; }
    }
}
