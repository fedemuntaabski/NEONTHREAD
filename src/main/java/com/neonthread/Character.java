package com.neonthread;

import com.neonthread.stats.BaseAttributes;
import com.neonthread.stats.Modifier;
import com.neonthread.stats.RuntimeStats;
import com.neonthread.stats.StatType;
import com.neonthread.inventory.Inventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Representa el personaje del jugador (DRY).
 * Almacena toda la información relevante de la run.
 * Refactored to use layered stats architecture.
 */
public class Character {
    private String name;
    private Role role;
    private Difficulty difficulty;
    
    // Layer 1: Base Attributes (What you ARE)
    private BaseAttributes baseAttributes;
    
    // Layer 2: Derived Capabilities (What you CAN DO) - Calculated on fly
    // private DerivedCapabilities derivedCapabilities; // Not stored, calculated.
    
    // Layer 3: Runtime Stats (Volatile)
    private RuntimeStats runtimeStats;
    
    // Layer 4: Modifiers
    private List<Modifier> modifiers;
    
    // Layer 5: Inventory
    private Inventory inventory;
    
    // Persistent Stats (Meta/Long-term)
    private int level;
    private int credits;
    private int karma;
    private int notoriety;
    private int reputation;
    
    public Character(String name, Role role, Difficulty difficulty) {
        this.name = name;
        this.role = role;
        this.difficulty = difficulty;
        
        // Initialize lists
        this.modifiers = new ArrayList<>();
        this.inventory = new Inventory(this);
        
        // Initialize Attributes based on Role
        initializeAttributesByRole(role);
        
        // Initialize Runtime Stats
        this.runtimeStats = new RuntimeStats(100, 100, 100); // Default max values
        
        // Initialize Persistent Stats
        this.level = 1;
        this.credits = 1000;
        this.karma = 0;
        this.notoriety = 0;
        this.reputation = 0;
    }
    
    private void initializeAttributesByRole(Role role) {
        int intel = 0, phys = 0, per = 0, cha = 0;
        switch (role) {
            case HACKER:
                intel = 5; phys = 1; per = 3; cha = 2;
                break;
            case MERC:
                intel = 2; phys = 5; per = 3; cha = 1;
                break;
            case INFO_BROKER:
                intel = 3; phys = 1; per = 4; cha = 4;
                break;
        }
        this.baseAttributes = new BaseAttributes(intel, phys, per, cha);
    }
    
    // --- Core Stat Logic ---

    public int getEffectiveAttribute(StatType type) {
        int base = 0;
        switch (type) {
            case INTELLIGENCE: base = baseAttributes.getIntelligence(); break;
            case PHYSICAL: base = baseAttributes.getPhysical(); break;
            case PERCEPTION: base = baseAttributes.getPerception(); break;
            case CHARISMA: base = baseAttributes.getCharisma(); break;
            default: return 0; // Should not happen for attributes
        }
        return base + getModifierTotal(type);
    }

    public int getEffectiveCapability(StatType type) {
        int intel = getEffectiveAttribute(StatType.INTELLIGENCE);
        int phys = getEffectiveAttribute(StatType.PHYSICAL);
        int per = getEffectiveAttribute(StatType.PERCEPTION);
        int cha = getEffectiveAttribute(StatType.CHARISMA);
        
        int base = 0;
        switch (type) {
            case HACK: base = intel + (per / 2); break;
            case COMBAT: base = phys + (per / 2); break;
            case STEALTH: base = per + (phys / 2); break;
            case NEGOTIATION: base = cha + (intel / 2); break;
            case ANALYSIS: base = intel + (per / 2); break;
            default: return 0;
        }
        return base + getModifierTotal(type);
    }

    private int getModifierTotal(StatType type) {
        return modifiers.stream()
                .filter(m -> m.getTargetStat() == type)
                .mapToInt(Modifier::getValue)
                .sum();
    }

    public void addModifier(Modifier modifier) {
        modifiers.add(modifier);
    }

    public void removeModifier(Modifier modifier) {
        modifiers.remove(modifier);
    }

    public void tickModifiers() {
        Iterator<Modifier> it = modifiers.iterator();
        while (it.hasNext()) {
            Modifier m = it.next();
            m.decreaseDuration();
            if (m.isExpired()) {
                it.remove();
            }
        }
    }

    // --- Getters for Legacy/Direct Access ---
    
    public String getName() { return name; }
    public Role getRole() { return role; }
    public Difficulty getDifficulty() { return difficulty; }
    
    // Attributes (Raw)
    public BaseAttributes getBaseAttributes() { return baseAttributes; }
    
    public Inventory getInventory() { return inventory; }
    
    // Runtime Stats
    public RuntimeStats getRuntimeStats() { return runtimeStats; }
    public int getHealth() { return runtimeStats.getHealth(); }
    public void setHealth(int val) { runtimeStats.setHealth(val); }
    public int getEnergy() { return runtimeStats.getEnergy(); }
    public void setEnergy(int val) { runtimeStats.setEnergy(val); }
    public int getBattery() { return runtimeStats.getBattery(); }
    public void setBattery(int val) { runtimeStats.setBattery(val); }
    
    // Persistent Stats
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public void removeCredits(int amount) { this.credits -= amount; }
    public int getKarma() { return karma; }
    public void setKarma(int karma) { this.karma = karma; }
    public int getNotoriety() { return notoriety; }
    public void setNotoriety(int notoriety) { this.notoriety = notoriety; }
    public int getReputation() { return reputation; }
    public void setReputation(int reputation) { this.reputation = reputation; }

    // Helper for legacy attribute access (returns effective value now)
    public int getIntelligence() { return getEffectiveAttribute(StatType.INTELLIGENCE); }
    public int getPhysical() { return getEffectiveAttribute(StatType.PHYSICAL); }
    public int getPerception() { return getEffectiveAttribute(StatType.PERCEPTION); }
    public int getCharisma() { return getEffectiveAttribute(StatType.CHARISMA); }

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
