package com.neonthread.inventory;

import com.neonthread.GameSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona upgrades disponibles y compras.
 * Usa Strategy pattern para lógica de purchase.
 */
public class UpgradeManager {
    private List<Upgrade> upgrades;

    public UpgradeManager() {
        this.upgrades = new ArrayList<>();
        // Add some dummy upgrades
        upgrades.add(new Upgrade("upg_01", "Cybernetic Eye", "Improves accuracy and scanning.", 500));
        upgrades.add(new Upgrade("upg_02", "Dermal Plating", "Increases damage resistance.", 800));
        upgrades.add(new Upgrade("upg_03", "Neural Link", "Faster hacking speed.", 1200));
    }

    public List<Upgrade> getAvailableUpgrades() {
        return upgrades;
    }

    public boolean purchaseUpgrade(Upgrade upgrade) {
        if (upgrade.isPurchased()) return false;
        
        com.neonthread.Character character = GameSession.getInstance().getCharacter();
        if (character.getCredits() >= upgrade.getCost()) {
            character.removeCredits(upgrade.getCost());
            upgrade.setPurchased(true);
            return true;
        }
        return false;
    }
}
