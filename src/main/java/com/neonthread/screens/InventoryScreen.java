package com.neonthread.screens;

import com.neonthread.GameConstants;
import com.neonthread.GameSession;
import com.neonthread.inventory.Inventory;
import com.neonthread.inventory.InventoryItem;
import com.neonthread.stats.Modifier;
import com.neonthread.ui.CyberpunkButton;
import com.neonthread.ui.ThemeEngine;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class InventoryScreen extends JPanel {
    private final Consumer<com.neonthread.GameState> onStateChange;
    private JPanel itemsPanel;
    private JTextArea itemDescriptionArea;
    private CyberpunkButton backButton;

    public InventoryScreen(Consumer<com.neonthread.GameState> onStateChange) {
        this.onStateChange = onStateChange;
        setLayout(new BorderLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);

        initializeUI();
    }

    private void initializeUI() {
        // Header
        JLabel titleLabel = new JLabel("INVENTORY", SwingConstants.CENTER);
        titleLabel.setFont(GameConstants.FONT_TITLE);
        titleLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Main Content
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Items List
        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON));
        contentPanel.add(scrollPane);

        // Item Details
        itemDescriptionArea = new JTextArea();
        itemDescriptionArea.setEditable(false);
        itemDescriptionArea.setLineWrap(true);
        itemDescriptionArea.setWrapStyleWord(true);
        itemDescriptionArea.setFont(GameConstants.FONT_TEXT);
        itemDescriptionArea.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        itemDescriptionArea.setBackground(GameConstants.COLOR_BACKGROUND);
        itemDescriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        contentPanel.add(itemDescriptionArea);

        add(contentPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        
        backButton = new CyberpunkButton("BACK");
        backButton.addActionListener(e -> onStateChange.accept(com.neonthread.GameState.STATE_MENU)); // Default back
        footerPanel.add(backButton);
        
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void refresh() {
        itemsPanel.removeAll();
        itemDescriptionArea.setText("Select an item to view details.");
        
        Inventory inventory = GameSession.getInstance().getCharacter().getInventory();
        
        if (inventory.getItems().isEmpty()) {
            JLabel emptyLabel = new JLabel("No items in inventory.");
            emptyLabel.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
            emptyLabel.setFont(GameConstants.FONT_TEXT);
            itemsPanel.add(emptyLabel);
        } else {
            for (InventoryItem item : inventory.getItems()) {
                CyberpunkButton itemButton = new CyberpunkButton(item.getName());
                itemButton.addActionListener(e -> showItemDetails(item));
                // Make button fill width
                itemButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                itemsPanel.add(itemButton);
                itemsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        revalidate();
        repaint();
    }

    private void showItemDetails(InventoryItem item) {
        StringBuilder sb = new StringBuilder();
        sb.append("NAME: ").append(item.getName()).append("\n\n");
        sb.append("DESCRIPTION:\n").append(item.getDescription()).append("\n\n");
        
        if (!item.getModifiers().isEmpty()) {
            sb.append("MODIFIERS:\n");
            for (Modifier m : item.getModifiers()) {
                sb.append("- ").append(m.getDescription())
                  .append(" (").append(m.getTargetStat()).append(" ")
                  .append(m.getValue() > 0 ? "+" : "").append(m.getValue()).append(")\n");
            }
        }
        
        itemDescriptionArea.setText(sb.toString());
    }
    
    public void setBackAction(Runnable action) {
        // Remove all listeners
        for (java.awt.event.ActionListener al : backButton.getActionListeners()) {
            backButton.removeActionListener(al);
        }
        // Add new action
        backButton.addActionListener(e -> action.run());
    }
}
