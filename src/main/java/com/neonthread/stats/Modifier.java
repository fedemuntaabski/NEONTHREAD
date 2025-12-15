package com.neonthread.stats;

public class Modifier {
    private final String id;
    private final StatType targetStat;
    private final int value;
    private int duration; // Number of missions/turns. -1 for permanent/indefinite.
    private final String description;

    public Modifier(String id, StatType targetStat, int value, int duration, String description) {
        this.id = id;
        this.targetStat = targetStat;
        this.value = value;
        this.duration = duration;
        this.description = description;
    }

    public String getId() { return id; }
    public StatType getTargetStat() { return targetStat; }
    public int getValue() { return value; }
    public int getDuration() { return duration; }
    public String getDescription() { return description; }

    public void decreaseDuration() {
        if (duration > 0) {
            duration--;
        }
    }

    public boolean isExpired() {
        return duration == 0;
    }
}
