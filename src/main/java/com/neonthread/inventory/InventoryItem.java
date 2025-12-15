package com.neonthread.inventory;

import com.neonthread.stats.Modifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un item en el inventario.
 * Encapsula propiedades y modificadores de stats.
 */
public class InventoryItem {
    private String id;
    private String name;
    private String description;
    private List<Modifier> modifiers;

    public InventoryItem(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.modifiers = new ArrayList<>();
    }

    public void addModifier(Modifier modifier) {
        this.modifiers.add(modifier);
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getId() { return id; }
}
