package com.neonthread.inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * Registro de items del juego.
 * Usa Singleton pattern (implícito con static) y Factory pattern para creación.
 */
public class ItemRegistry {
    private static final Map<String, InventoryItem> items = new HashMap<>();

    static {
        // Initialize some default items
        register(new InventoryItem("DATA_SHARD", "Data Shard", "A standard data storage unit."));
        register(new InventoryItem("MEDKIT", "Medkit", "Restores health."));
        register(new InventoryItem("ENERGY_CELL", "Energy Cell", "Restores energy."));
    }

    public static void register(InventoryItem item) {
        items.put(item.getId(), item);
    }

    public static InventoryItem getItem(String id) {
        return items.get(id);
    }
}
