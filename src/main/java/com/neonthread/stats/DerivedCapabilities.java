package com.neonthread.stats;

public class DerivedCapabilities {
    private final BaseAttributes attributes;

    public DerivedCapabilities(BaseAttributes attributes) {
        this.attributes = attributes;
    }

    public int getHack() {
        return attributes.getIntelligence() + (attributes.getPerception() / 2);
    }

    public int getCombat() {
        return attributes.getPhysical() + (attributes.getPerception() / 2);
    }

    public int getStealth() {
        return attributes.getPerception() + (attributes.getPhysical() / 2);
    }

    public int getNegotiation() {
        return attributes.getCharisma() + (attributes.getIntelligence() / 2);
    }

    public int getAnalysis() {
        return attributes.getIntelligence() + (attributes.getPerception() / 2);
    }
}
