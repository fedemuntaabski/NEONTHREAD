package com.neonthread.screens;

import com.neonthread.GameConstants;
import com.neonthread.GameSettings;
import com.neonthread.SettingsApplier;
import com.neonthread.ui.CyberpunkButton;
import com.neonthread.ui.CyberpunkComboBox;
import com.neonthread.ui.CyberpunkSlider;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Pantalla de configuración simple y minimalista (KISS).
 * Solo opciones esenciales: resolución, pantalla completa, volumen y tamaño de texto.
 */
public class SettingsScreen extends JPanel {
    private final Consumer<Void> onBack;
    private final GameSettings settings;
    private final SettingsApplier applier;
    
    private CyberpunkComboBox<String> resolutionCombo;
    private JCheckBox fullscreenCheck;
    private CyberpunkSlider volumeSlider;
    private JRadioButton normalTextRadio;
    private JRadioButton largeTextRadio;
    
    public SettingsScreen(Consumer<Void> onBack, JFrame window) {
        this.onBack = onBack;
        this.settings = GameSettings.getInstance();
        this.applier = new SettingsApplier(window);
        
        setLayout(new BorderLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        
        buildUI();
    }
    
    private void buildUI() {
        // Título
        JLabel titleLabel = new JLabel("SETTINGS", SwingConstants.CENTER);
        titleLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 40));
        titleLabel.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 40, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Panel central con todas las opciones
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));
        
        // Resolución
        addSeparator(centerPanel);
        addSettingRow(centerPanel, "Resolución", createResolutionControl());
        addSeparator(centerPanel);
        
        // Pantalla completa
        addSettingRow(centerPanel, "Pantalla completa", createFullscreenControl());
        addSeparator(centerPanel);
        
        // Volumen maestro
        addSettingRow(centerPanel, "Volumen maestro", createVolumeControl());
        addSeparator(centerPanel);
        
        // Tamaño de texto
        addSettingRow(centerPanel, "Tamaño de texto", createTextSizeControl());
        addSeparator(centerPanel);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Botones inferiores
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        bottomPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));
        
        CyberpunkButton saveButton = createStyledButton("GUARDAR", e -> saveAndApply());
        CyberpunkButton backButton = createStyledButton("VOLVER", e -> onBack.accept(null));
        
        bottomPanel.add(saveButton);
        bottomPanel.add(backButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Crea un botón estilizado (DRY).
     */
    private CyberpunkButton createStyledButton(String text, java.awt.event.ActionListener action) {
        CyberpunkButton button = new CyberpunkButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.addActionListener(action);
        return button;
    }
    
    private JComponent createResolutionControl() {
        String[] resolutions = {"1280x720", "1600x900", "1920x1080"};
        resolutionCombo = new CyberpunkComboBox<>(resolutions);
        resolutionCombo.setSelectedItem(settings.getResolution());
        resolutionCombo.setPreferredSize(new Dimension(200, 35));
        
        // Deshabilitar si está en pantalla completa
        resolutionCombo.setEnabled(!settings.isFullscreen());
        
        return resolutionCombo;
    }
    
    private JComponent createFullscreenControl() {
        fullscreenCheck = new JCheckBox();
        fullscreenCheck.setSelected(settings.isFullscreen());
        fullscreenCheck.setBackground(GameConstants.COLOR_BACKGROUND);
        fullscreenCheck.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        fullscreenCheck.setFont(GameConstants.FONT_TEXT);
        fullscreenCheck.setFocusPainted(false);
        
        // Al cambiar, habilitar/deshabilitar resolución
        fullscreenCheck.addActionListener(e -> {
            resolutionCombo.setEnabled(!fullscreenCheck.isSelected());
        });
        
        return fullscreenCheck;
    }
    
    private JComponent createVolumeControl() {
        volumeSlider = new CyberpunkSlider(0, 100, settings.getMasterVolume());
        volumeSlider.setPreferredSize(new Dimension(300, 40));
        
        return volumeSlider;
    }
    
    private JComponent createTextSizeControl() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(GameConstants.COLOR_BACKGROUND);
        
        normalTextRadio = createStyledRadioButton("Normal", !settings.isLargeText());
        largeTextRadio = createStyledRadioButton("Grande", settings.isLargeText());
        
        ButtonGroup group = new ButtonGroup();
        group.add(normalTextRadio);
        group.add(largeTextRadio);
        
        panel.add(normalTextRadio);
        panel.add(Box.createVerticalStrut(5));
        panel.add(largeTextRadio);
        
        return panel;
    }
    
    /**
     * Crea un radio button estilizado (DRY).
     */
    private JRadioButton createStyledRadioButton(String text, boolean selected) {
        JRadioButton radio = new JRadioButton(text);
        radio.setSelected(selected);
        radio.setBackground(GameConstants.COLOR_BACKGROUND);
        radio.setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        radio.setFont(GameConstants.FONT_TEXT);
        radio.setFocusPainted(false);
        return radio;
    }
    
    private void addSettingRow(JPanel parent, String label, JComponent control) {
        JPanel row = new JPanel(new BorderLayout(20, 0));
        row.setBackground(GameConstants.COLOR_BACKGROUND);
        row.setMaximumSize(new Dimension(600, 80));
        
        JLabel labelComp = new JLabel(label + ":");
        labelComp.setFont(GameConstants.FONT_TEXT);
        labelComp.setForeground(GameConstants.COLOR_TEXT_SECONDARY);
        
        row.add(labelComp, BorderLayout.WEST);
        row.add(control, BorderLayout.EAST);
        
        parent.add(row);
        parent.add(Box.createVerticalStrut(15));
    }
    
    private void addSeparator(JPanel parent) {
        JSeparator separator = new JSeparator();
        separator.setForeground(GameConstants.COLOR_CYAN_NEON);
        separator.setMaximumSize(new Dimension(600, 1));
        parent.add(separator);
        parent.add(Box.createVerticalStrut(10));
    }
    
    private void saveAndApply() {
        // Guardar valores
        settings.setResolution((String) resolutionCombo.getSelectedItem());
        settings.setFullscreen(fullscreenCheck.isSelected());
        settings.setMasterVolume(volumeSlider.getValue());
        settings.setLargeText(largeTextRadio.isSelected());
        
        // Persistir
        settings.saveSettings();
        
        // Aplicar cambios
        applier.applyAll();
        
        // Volver al menú
        Timer timer = new Timer(200, e -> {
            ((Timer) e.getSource()).stop();
            onBack.accept(null);
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    public void show() {
        requestFocusInWindow();
    }
}
