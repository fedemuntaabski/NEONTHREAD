package com.neonthread.stats;

public class RuntimeStats {
    private int health;
    private int energy;
    private int battery;
    
    // Max values might be modified, so we store current values here.
    // Max values are likely calculated from base + modifiers.

    public RuntimeStats(int health, int energy, int battery) {
        this.health = health;
        this.energy = energy;
        this.battery = battery;
    }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }

    public int getBattery() { return battery; }
    public void setBattery(int battery) { this.battery = battery; }
}
