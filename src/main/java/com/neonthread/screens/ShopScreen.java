package com.neonthread.screens;

import com.neonthread.GameConstants;
import com.neonthread.GameSession;
import com.neonthread.inventory.Upgrade;
import com.neonthread.inventory.UpgradeManager;
import com.neonthread.localization.Localization;
import com.neonthread.ui.CyberpunkButton;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Pantalla de tienda para compra de upgrades.
 * Usa Observer pattern para actualizar UI tras compras.
 */
public class ShopScreen extends JPanel {
    private final Consumer<Void> onBack;
    private final GameSession session;
    private final UpgradeManager upgradeManager;

    public ShopScreen(Consumer<Void> onBack) {
        this.onBack = onBack;
        this.session = GameSession.getInstance();
        this.upgradeManager = session.getUpgradeManager();

        setLayout(new BorderLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);

        buildUI();
    }

    private void buildUI() {
        // Title
        JLabel titleLabel = new JLabel("SHOP - " + Localization.get("shop.title", "UPGRADES"));
        titleLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 40));
        titleLabel.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 40, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        // Credits display
        JPanel creditsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        creditsPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        JLabel creditsLabel = new JLabel("Credits: " + session.getCharacter().getCredits());
        creditsLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 20));
        creditsLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        creditsPanel.add(creditsLabel);
        centerPanel.add(creditsPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Upgrades list
        List<Upgrade> upgrades = upgradeManager.getAvailableUpgrades();
        for (Upgrade upgrade : upgrades) {
            centerPanel.add(createUpgradePanel(upgrade));
            centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        add(centerPanel, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        bottomPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));

        CyberpunkButton backButton = new CyberpunkButton(Localization.get("settings.back", "BACK"));
        backButton.addActionListener(e -> onBack.accept(null));

        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createUpgradePanel(Upgrade upgrade) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0x12, 0x12, 0x1A));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON.darker(), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setMaximumSize(new Dimension(600, 80));

        // Left: Name and description
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(0x12, 0x12, 0x1A));

        JLabel nameLabel = new JLabel(upgrade.getName());
        nameLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 16));
        nameLabel.setForeground(GameConstants.COLOR_TEXT_PRIMARY);

        JLabel descLabel = new JLabel(upgrade.getDescription());
        descLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.PLAIN, 12));
        descLabel.setForeground(GameConstants.COLOR_TEXT_SECONDARY);

        leftPanel.add(nameLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(descLabel);

        // Right: Cost and button
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(0x12, 0x12, 0x1A));

        JLabel costLabel = new JLabel(upgrade.getCost() + " credits");
        costLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 14));
        costLabel.setForeground(GameConstants.COLOR_MAGENTA_NEON);

        CyberpunkButton buyButton = new CyberpunkButton(upgrade.isPurchased() ? "OWNED" : "BUY");
        buyButton.setEnabled(!upgrade.isPurchased());
        buyButton.addActionListener(e -> {
            if (upgradeManager.purchaseUpgrade(upgrade)) {
                buyButton.setText("OWNED");
                buyButton.setEnabled(false);
                // Refresh credits display
                rebuildUI();
            } else {
                JOptionPane.showMessageDialog(this, "Not enough credits!");
            }
        });

        rightPanel.add(costLabel, BorderLayout.NORTH);
        rightPanel.add(buyButton, BorderLayout.SOUTH);

        panel.add(leftPanel, BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private void rebuildUI() {
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }
}
