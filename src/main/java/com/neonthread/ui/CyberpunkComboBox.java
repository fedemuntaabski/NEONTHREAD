package com.neonthread.ui;

import com.neonthread.GameConstants;

import javax.swing.*;
import java.awt.*;

/**
 * ComboBox con estética cyberpunk.
 * Refactorizado para eliminar código duplicado y mejorar legibilidad.
 */
public class CyberpunkComboBox<T> extends JComboBox<T> {
    
    public CyberpunkComboBox(T[] items) {
        super(items);
        initializeStyles();
        setupRenderer();
        setupPopupStyling();
    }
    
    private void initializeStyles() {
        setBackground(GameConstants.COLOR_BACKGROUND);
        setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        setFont(GameConstants.FONT_TEXT);
        setBorder(BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON, 1));
        setFocusable(false);
        setMaximumSize(new Dimension(250, 35));
        setPreferredSize(new Dimension(200, 30));
    }
    
    private void setupRenderer() {
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                setFont(GameConstants.FONT_TEXT);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
                if (list == null) {
                    setBackground(GameConstants.COLOR_BACKGROUND);
                    setForeground(Color.BLACK);
                } else {
                    setBackground(isSelected ? GameConstants.COLOR_CYAN_NEON.darker() : GameConstants.COLOR_BACKGROUND);
                    setForeground(isSelected ? GameConstants.COLOR_BACKGROUND : GameConstants.COLOR_TEXT_PRIMARY);
                }
                
                return this;
            }
        });
    }
    
    private void setupPopupStyling() {
        addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                SwingUtilities.invokeLater(() -> applyPopupStyles());
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            
            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });
    }
    
    private void applyPopupStyles() {
        Object popup = getUI().getAccessibleChild(this, 0);
        if (popup instanceof JPopupMenu) {
            JPopupMenu menu = (JPopupMenu) popup;
            menu.setBackground(GameConstants.COLOR_BACKGROUND);
            menu.setBorder(BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON, 1));
            
            for (Component comp : menu.getComponents()) {
                if (comp instanceof JScrollPane) {
                    JScrollPane scroll = (JScrollPane) comp;
                    scroll.getViewport().getView().setBackground(GameConstants.COLOR_BACKGROUND);
                    scroll.setBackground(GameConstants.COLOR_BACKGROUND);
                    scroll.setBorder(BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON, 1));
                }
            }
        }
    }
}
