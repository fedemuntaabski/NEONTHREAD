package com.neonthread.stats;

public class BaseAttributes {
    private int intelligence;
    private int physical;
    private int perception;
    private int charisma;

    public BaseAttributes(int intelligence, int physical, int perception, int charisma) {
        this.intelligence = intelligence;
        this.physical = physical;
        this.perception = perception;
        this.charisma = charisma;
    }

    public int getIntelligence() { return intelligence; }
    public int getPhysical() { return physical; }
    public int getPerception() { return perception; }
    public int getCharisma() { return charisma; }

    public void setIntelligence(int intelligence) { this.intelligence = intelligence; }
    public void setPhysical(int physical) { this.physical = physical; }
    public void setPerception(int perception) { this.perception = perception; }
    public void setCharisma(int charisma) { this.charisma = charisma; }
}
