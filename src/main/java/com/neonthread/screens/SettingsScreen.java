package com.neonthread.screens;

import com.neonthread.GameConstants;
import com.neonthread.GameSettings;
import com.neonthread.SettingsApplier;
import com.neonthread.ui.CyberpunkButton;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * Pantalla de configuración con tabs para diferentes categorías.
 */
public class SettingsScreen extends JPanel {
    private final Consumer<Void> onBack;
    private final GameSettings settings;
    private final SettingsApplier applier;
    private final JPanel contentPanel;
    private final JLabel[] tabLabels;
    private int selectedTab = 0;
    private final JLabel savingLabel;
    private Timer savingTimer;
    
    private final String[] tabs = {"Video", "Audio", "Gameplay", "Controls", "Accessibility"};
    
    public SettingsScreen(Consumer<Void> onBack, JFrame window) {
        this.onBack = onBack;
        this.settings = GameSettings.getInstance();
        this.applier = new SettingsApplier(window);
        
        setLayout(new BorderLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        setFocusable(true);
        
        // Header
        JLabel titleLabel = new JLabel("SETTINGS", SwingConstants.CENTER);
        titleLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 32));
        titleLabel.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Center container with tabs and content
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(GameConstants.COLOR_BACKGROUND);
        
        // Tabs panel
        JPanel tabsPanel = new JPanel(new GridLayout(tabs.length, 1, 0, 5));
        tabsPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        tabsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        
        tabLabels = new JLabel[tabs.length];
        for (int i = 0; i < tabs.length; i++) {
            final int index = i;
            tabLabels[i] = new JLabel("• " + tabs[i]);
            tabLabels[i].setFont(GameConstants.FONT_MENU);
            tabLabels[i].setForeground(GameConstants.COLOR_TEXT_SECONDARY);
            tabLabels[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            tabLabels[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    selectTab(index);
                }
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (index != selectedTab) {
                        tabLabels[index].setForeground(GameConstants.COLOR_BUTTON_HOVER);
                    }
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (index != selectedTab) {
                        tabLabels[index].setForeground(GameConstants.COLOR_TEXT_SECONDARY);
                    }
                }
            });
            tabsPanel.add(tabLabels[i]);
        }
        
        centerContainer.add(tabsPanel, BorderLayout.WEST);
        
        // Content panel (scrollable)
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        contentPanel.setBorder(new LineBorder(GameConstants.COLOR_CYAN_NEON, 2));
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(GameConstants.COLOR_BACKGROUND);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        centerContainer.add(scrollPane, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);
        
        // Bottom panel with buttons and saving indicator
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Saving indicator (top right)
        savingLabel = new JLabel(" ");
        savingLabel.setFont(GameConstants.FONT_TEXT);
        savingLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        savingLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bottomPanel.add(savingLabel, BorderLayout.NORTH);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        
        CyberpunkButton defaultsButton = new CyberpunkButton("DEFAULTS");
        defaultsButton.addActionListener(e -> resetToDefaults());
        
        CyberpunkButton applyButton = new CyberpunkButton("APPLY");
        applyButton.addActionListener(e -> applySettings());
        
        CyberpunkButton saveButton = new CyberpunkButton("SAVE");
        saveButton.addActionListener(e -> save());
        
        CyberpunkButton cancelButton = new CyberpunkButton("CANCEL");
        cancelButton.addActionListener(e -> cancel());
        
        buttonsPanel.add(defaultsButton);
        buttonsPanel.add(applyButton);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);
        
        bottomPanel.add(buttonsPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Keyboard navigation
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        selectTab((selectedTab - 1 + tabs.length) % tabs.length);
                        break;
                    case KeyEvent.VK_DOWN:
                        selectTab((selectedTab + 1) % tabs.length);
                        break;
                    case KeyEvent.VK_ESCAPE:
                        cancel();
                        break;
                }
            }
        });
        
        // Load first tab
        selectTab(0);
    }
    
    private void selectTab(int index) {
        selectedTab = index;
        
        // Update tab colors
        for (int i = 0; i < tabLabels.length; i++) {
            if (i == selectedTab) {
                tabLabels[i].setForeground(GameConstants.COLOR_CYAN_NEON);
                tabLabels[i].setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 20));
            } else {
                tabLabels[i].setForeground(GameConstants.COLOR_TEXT_SECONDARY);
                tabLabels[i].setFont(GameConstants.FONT_MENU);
            }
        }
        
        // Load appropriate content
        contentPanel.removeAll();
        JPanel settingsPanel = createSettingsPanel(tabs[selectedTab]);
        contentPanel.add(settingsPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private JPanel createSettingsPanel(String tab) {
        switch (tab) {
            case "Video": return createVideoSettings();
            case "Audio": return createAudioSettings();
            case "Gameplay": return createGameplaySettings();
            case "Controls": return createControlsSettings();
            case "Accessibility": return createAccessibilitySettings();
            default: return new JPanel();
        }
    }
    
    private JPanel createVideoSettings() {
        return new com.neonthread.screens.settings.VideoSettingsPanel(settings);
    }
    
    private JPanel createAudioSettings() {
        return new com.neonthread.screens.settings.AudioSettingsPanel(settings);
    }
    
    private JPanel createGameplaySettings() {
        return new com.neonthread.screens.settings.GameplaySettingsPanel(settings);
    }
    
    private JPanel createControlsSettings() {
        return new com.neonthread.screens.settings.ControlsSettingsPanel(settings);
    }
    
    private JPanel createAccessibilitySettings() {
        return new com.neonthread.screens.settings.AccessibilitySettingsPanel(settings);
    }
    
    private void resetToDefaults() {
        int result = JOptionPane.showConfirmDialog(this,
            "Reset all settings to defaults?",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            settings.resetToDefaults();
            selectTab(selectedTab); // Reload current tab
            showSaving();
        }
    }
    
    private void applySettings() {
        showSaving();
        applier.applyAll();
        
        // Mostrar mensaje de confirmación
        Timer delay = new Timer(500, e -> {
            ((Timer)e.getSource()).stop();
            JOptionPane.showMessageDialog(this,
                "Settings applied successfully!\nSome changes may require a restart.",
                "Applied",
                JOptionPane.INFORMATION_MESSAGE);
        });
        delay.setRepeats(false);
        delay.start();
    }
    
    private void save() {
        showSaving();
        // Settings are already saved in real-time through the panels
        Timer delay = new Timer(800, e -> {
            ((Timer)e.getSource()).stop();
            cancel();
        });
        delay.setRepeats(false);
        delay.start();
    }
    
    private void cancel() {
        onBack.accept(null);
    }
    
    private void showSaving() {
        savingLabel.setText("[SAVING…]");
        
        if (savingTimer != null) {
            savingTimer.stop();
        }
        
        savingTimer = new Timer(1500, e -> {
            savingLabel.setText(" ");
            ((Timer)e.getSource()).stop();
        });
        savingTimer.setRepeats(false);
        savingTimer.start();
    }
    
    public void show() {
        requestFocusInWindow();
    }
}
