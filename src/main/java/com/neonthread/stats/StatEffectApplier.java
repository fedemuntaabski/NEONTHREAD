package com.neonthread.stats;

import com.neonthread.Character;

public class StatEffectApplier {

    public static void applyDamage(Character character, int amount) {
        int current = character.getHealth();
        character.setHealth(Math.max(0, current - amount));
        // Feedback hook could go here
    }

    public static void applyHealing(Character character, int amount) {
        int current = character.getHealth();
        character.setHealth(Math.min(100, current + amount)); // Assuming 100 max for now
    }

    public static void consumeEnergy(Character character, int amount) {
        int current = character.getEnergy();
        if (current >= amount) {
            character.setEnergy(current - amount);
        } else {
            // Not enough energy - apply penalty?
            // For now, just set to 0 and maybe apply fatigue/stress?
            character.setEnergy(0);
            // Example: Exhaustion penalty
            character.addModifier(new Modifier("exhaustion", StatType.PHYSICAL, -1, 1, "Exhausted"));
        }
    }

    public static void consumeBattery(Character character, int amount) {
        int current = character.getBattery();
        if (current >= amount) {
            character.setBattery(current - amount);
        } else {
            character.setBattery(0);
            // Example: Notoriety increase due to clumsy hacking
            character.setNotoriety(character.getNotoriety() + 5);
            // Log: "Battery depleted! Clumsy hack increased Notoriety."
        }
    }

    public static void addReputation(Character character, int amount) {
        character.setReputation(character.getReputation() + amount);
    }

    public static void addKarma(Character character, int amount) {
        character.setKarma(character.getKarma() + amount);
    }

    public static void addNotoriety(Character character, int amount) {
        character.setNotoriety(character.getNotoriety() + amount);
    }
    
    public static void applyModifier(Character character, Modifier modifier) {
        character.addModifier(modifier);
    }
}
