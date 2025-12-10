package com.neonthread.screens;

import com.neonthread.BlinkingCursor;
import com.neonthread.GameConstants;
import com.neonthread.GlitchEffect;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Menú principal con navegación por teclado.
 */
public class MenuScreen extends JPanel {
    private final String[] menuOptions = {
        "START RUN",
        "LOAD MEMORY",
        "SETTINGS",
        "EXIT"
    };
    
    private int selectedIndex = 0;
    private final JLabel titleLabel;
    private final JLabel subtitleLabel;
    private final JLabel[] optionLabels;
    private final JLabel cursorLabel;
    private final BlinkingCursor cursor;
    private final JLabel scanlineLabel;
    private final JPanel cityInfoPanel;
    private final JLabel[] cityInfoLabels;
    private Timer scanlineTimer;
    private Timer dataStreamTimer;
    
    public MenuScreen() {
        setLayout(new GridBagLayout());
        setBackground(GameConstants.COLOR_BACKGROUND);
        setFocusable(true);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 0, 5, 0);
        
        // Título principal
        titleLabel = new JLabel("NEONTHREAD // PROTOCOL 07");
        titleLabel.setFont(new Font(GameConstants.FONT_FAMILY, Font.BOLD, 28));
        titleLabel.setForeground(GameConstants.COLOR_CYAN_NEON);
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 5, 0);
        add(titleLabel, gbc);
        
        // Subtítulo
        subtitleLabel = new JLabel("\"Every runner leaves a trace. Yours starts now.\"");
        subtitleLabel.setFont(GameConstants.FONT_TEXT);
        subtitleLabel.setForeground(new Color(0xAA00AA));
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 30, 0);
        add(subtitleLabel, gbc);
        
        // Panel con borde holográfico para el menú
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridBagLayout());
        menuPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        menuPanel.setBorder(new LineBorder(GameConstants.COLOR_CYAN_NEON, 2));
        
        GridBagConstraints menuGbc = new GridBagConstraints();
        menuGbc.gridx = 0;
        menuGbc.insets = new Insets(10, 20, 10, 20);
        
        // Cursor parpadeante
        cursorLabel = new JLabel(">");
        cursorLabel.setFont(GameConstants.FONT_MENU);
        cursorLabel.setForeground(GameConstants.COLOR_MAGENTA_NEON);
        menuGbc.gridy = 0;
        menuPanel.add(cursorLabel, menuGbc);
        
        cursor = new BlinkingCursor(this::repaint);
        
        // Opciones del menú
        optionLabels = new JLabel[menuOptions.length];
        for (int i = 0; i < menuOptions.length; i++) {
            optionLabels[i] = new JLabel(menuOptions[i]);
            optionLabels[i].setFont(GameConstants.FONT_MENU);
            menuGbc.gridy = i + 1;
            menuPanel.add(optionLabels[i], menuGbc);
        }
        
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 20, 0);
        add(menuPanel, gbc);
        
        // Label para scanline (mensaje temporal)
        scanlineLabel = new JLabel(" ");
        scanlineLabel.setFont(GameConstants.FONT_TEXT);
        scanlineLabel.setForeground(GameConstants.COLOR_YELLOW_NEON);
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 0, 10, 0);
        add(scanlineLabel, gbc);
        
        // Panel de información de la ciudad
        cityInfoPanel = new JPanel();
        cityInfoPanel.setLayout(new GridLayout(3, 1, 0, 2));
        cityInfoPanel.setBackground(GameConstants.COLOR_BACKGROUND);
        
        String[] cityInfo = {
            "NightCity Weather: Acid Rain / 48°C",
            "Traffic Node 12: Congested",
            "CityNet Status: Encrypted"
        };
        
        cityInfoLabels = new JLabel[3];
        for (int i = 0; i < cityInfo.length; i++) {
            cityInfoLabels[i] = new JLabel(cityInfo[i]);
            cityInfoLabels[i].setFont(new Font(GameConstants.FONT_FAMILY, Font.PLAIN, 12));
            cityInfoLabels[i].setForeground(GameConstants.COLOR_GRAY_SYSTEM);
            cityInfoPanel.add(cityInfoLabels[i]);
        }
        
        gbc.gridy = 4;
        gbc.insets = new Insets(30, 0, 10, 0);
        add(cityInfoPanel, gbc);
        
        updateColors();
        
        // Navegación con teclado
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        selectedIndex = (selectedIndex - 1 + menuOptions.length) % menuOptions.length;
                        updateColors();
                        break;
                    case KeyEvent.VK_DOWN:
                        selectedIndex = (selectedIndex + 1) % menuOptions.length;
                        updateColors();
                        break;
                    case KeyEvent.VK_ENTER:
                        executeOption();
                        break;
                    case KeyEvent.VK_1:
                    case KeyEvent.VK_NUMPAD1:
                        selectedIndex = 0;
                        executeOption();
                        break;
                    case KeyEvent.VK_2:
                    case KeyEvent.VK_NUMPAD2:
                        selectedIndex = 1;
                        executeOption();
                        break;
                    case KeyEvent.VK_3:
                    case KeyEvent.VK_NUMPAD3:
                        selectedIndex = 2;
                        executeOption();
                        break;
                    case KeyEvent.VK_4:
                    case KeyEvent.VK_NUMPAD4:
                        selectedIndex = 3;
                        executeOption();
                        break;
                }
            }
        });
    }
    
    public void show() {
        cursor.start();
        startScanline();
        startDataStream();
        requestFocusInWindow();
    }
    
    public void cleanup() {
        cursor.stop();
        if (scanlineTimer != null) {
            scanlineTimer.stop();
        }
        if (dataStreamTimer != null) {
            dataStreamTimer.stop();
        }
    }
    
    private void updateColors() {
        for (int i = 0; i < optionLabels.length; i++) {
            if (i == selectedIndex) {
                optionLabels[i].setForeground(GameConstants.COLOR_MAGENTA_NEON);
            } else {
                optionLabels[i].setForeground(GameConstants.COLOR_CYAN_NEON);
            }
        }
        cursorLabel.setText(cursor.getCursor());
    }
    
    private void startScanline() {
        scanlineTimer = new Timer(GameConstants.SCANLINE_INTERVAL_MS, e -> {
            scanlineLabel.setText("[SCANNING SYSTEM INTEGRITY... OK]");
            Timer hide = new Timer(1000, ev -> {
                scanlineLabel.setText(" ");
                ((Timer)ev.getSource()).stop();
            });
            hide.setRepeats(false);
            hide.start();
        });
        scanlineTimer.start();
    }
    
    private void startDataStream() {
        // Mensajes de interferencia aleatorios
        dataStreamTimer = new Timer(4000, e -> {
            scanlineLabel.setText(GlitchEffect.generateInterference());
            scanlineLabel.setForeground(GameConstants.COLOR_MAGENTA_NEON);
            Timer hide = new Timer(800, ev -> {
                scanlineLabel.setText(" ");
                scanlineLabel.setForeground(GameConstants.COLOR_YELLOW_NEON);
                ((Timer)ev.getSource()).stop();
            });
            hide.setRepeats(false);
            hide.start();
        });
        dataStreamTimer.start();
    }
    
    private void executeOption() {
        switch (selectedIndex) {
            case 0:
                JOptionPane.showMessageDialog(this, 
                    "Starting new run...\n(Game logic not implemented yet)", 
                    "Start Run", 
                    JOptionPane.INFORMATION_MESSAGE);
                break;
            case 1:
                JOptionPane.showMessageDialog(this, 
                    "Loading saved memory...\n(Save system not implemented yet)", 
                    "Load Memory", 
                    JOptionPane.INFORMATION_MESSAGE);
                break;
            case 2:
                JOptionPane.showMessageDialog(this, 
                    "Settings panel...\n(Settings not implemented yet)", 
                    "Settings", 
                    JOptionPane.INFORMATION_MESSAGE);
                break;
            case 3:
                int result = JOptionPane.showConfirmDialog(this, 
                    "Exit NEONTHREAD?", 
                    "Confirm Exit", 
                    JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
                break;
        }
    }
}
