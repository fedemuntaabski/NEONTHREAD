package com.neonthread.settings.appliers;

import com.neonthread.settings.GameplaySettings;
import com.neonthread.ui.ThemeEngine;

public class ThemeSettingsApplier {
    public void apply(GameplaySettings settings) {
        ThemeEngine.applyTheme(settings.getTheme());
    }
}
