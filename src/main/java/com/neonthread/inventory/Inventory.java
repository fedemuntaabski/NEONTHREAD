package com.neonthread.inventory;

import com.neonthread.Character;
import com.neonthread.stats.Modifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona items del jugador.
 * Usa Observer pattern para aplicar modifiers automáticamente.
 */
public class Inventory {
    private List<InventoryItem> items;
    private Character owner;

    public Inventory(Character owner) {
        this.owner = owner;
        this.items = new ArrayList<>();
    }

    public void addItem(InventoryItem item) {
        items.add(item);
        // Apply modifiers
        for (Modifier m : item.getModifiers()) {
            owner.addModifier(m);
        }
    }

    public void removeItem(InventoryItem item) {
        if (items.remove(item)) {
            for (Modifier m : item.getModifiers()) {
                owner.removeModifier(m);
            }
        }
    }
    
    public List<InventoryItem> getItems() { return items; }
}
