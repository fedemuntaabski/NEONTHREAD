package com.neonthread.inventory;

public class Upgrade {
    private String id;
    private String name;
    private String description;
    private int cost;
    private boolean purchased;

    public Upgrade(String id, String name, String description, int cost) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.purchased = false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCost() { return cost; }
    public boolean isPurchased() { return purchased; }
    public void setPurchased(boolean purchased) { this.purchased = purchased; }
}
