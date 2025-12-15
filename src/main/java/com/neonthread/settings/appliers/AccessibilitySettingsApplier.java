package com.neonthread.settings.appliers;

import com.neonthread.settings.AccessibilitySettings;
import javax.swing.UIManager;
import java.awt.Font;
import java.util.Enumeration;
import javax.swing.plaf.FontUIResource;

public class AccessibilitySettingsApplier {
    public void apply(AccessibilitySettings settings) {
        int baseSize = settings.isLargeText() ? 18 : 14;
        updateUIFonts(baseSize);
    }

    private void updateUIFonts(int size) {
        FontUIResource font = new FontUIResource("Consolas", Font.PLAIN, size);
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, font);
            }
        }
    }
}
