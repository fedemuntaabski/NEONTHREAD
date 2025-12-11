package com.neonthread.ui;

import com.neonthread.GameConstants;

import javax.swing.*;
import java.awt.*;

/**
 * ComboBox básico con estética cyberpunk (KISS).
 */
public class CyberpunkComboBox<T> extends JComboBox<T> {
    
    public CyberpunkComboBox(T[] items) {
        super(items);
        
        // Configuración básica
        setBackground(GameConstants.COLOR_BACKGROUND);
        setForeground(GameConstants.COLOR_TEXT_PRIMARY);
        setFont(GameConstants.FONT_TEXT);
        setBorder(BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON, 1));
        setFocusable(false);
        
        // Hacer el botón más pequeño
        setMaximumSize(new Dimension(250, 35));
        setPreferredSize(new Dimension(200, 30));
        
        // Personalizar el renderer para los items del dropdown y el elemento seleccionado
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                // Configurar colores y fuente
                setFont(GameConstants.FONT_TEXT);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
                if (list == null) {
                    // Este es el renderer para el combo box seleccionado - texto negro
                    setBackground(GameConstants.COLOR_BACKGROUND);
                    setForeground(Color.BLACK);
                } else {
                    // Este es el renderer para los items del dropdown
                    setBackground(isSelected ? GameConstants.COLOR_CYAN_NEON.darker() : GameConstants.COLOR_BACKGROUND);
                    setForeground(isSelected ? GameConstants.COLOR_BACKGROUND : GameConstants.COLOR_TEXT_PRIMARY);
                }
                
                return this;
            }
        });
        
        // Personalizar el popup para fondo oscuro
        if (getUI() instanceof javax.swing.plaf.basic.BasicComboBoxUI) {
            javax.swing.plaf.basic.BasicComboBoxUI ui = (javax.swing.plaf.basic.BasicComboBoxUI) getUI();
            // El popup se puede acceder, pero es complicado. Mejor usar un enfoque diferente.
        }
        
        // Forzar el fondo del popup usando un popup listener
        addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                SwingUtilities.invokeLater(() -> {
                    JComboBox<?> combo = (JComboBox<?>) e.getSource();
                    Object popup = combo.getUI().getAccessibleChild(combo, 0);
                    if (popup instanceof JPopupMenu) {
                        JPopupMenu menu = (JPopupMenu) popup;
                        menu.setBackground(GameConstants.COLOR_BACKGROUND);
                        menu.setBorder(BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON, 1));
                        
                        // Aplicar a la lista dentro del popup
                        for (Component comp : menu.getComponents()) {
                            if (comp instanceof JScrollPane) {
                                JScrollPane scroll = (JScrollPane) comp;
                                scroll.getViewport().getView().setBackground(GameConstants.COLOR_BACKGROUND);
                                scroll.setBackground(GameConstants.COLOR_BACKGROUND);
                                scroll.setBorder(BorderFactory.createLineBorder(GameConstants.COLOR_CYAN_NEON, 1));
                            }
                        }
                    }
                });
            }
            
            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });
    }
}
