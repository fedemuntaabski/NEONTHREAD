package com.neonthread.settings.appliers;

import com.neonthread.settings.LocalizationSettings;
import com.neonthread.localization.Localization;

public class LocalizationSettingsApplier {
    public void apply(LocalizationSettings settings) {
        Localization.getInstance().loadLanguage(settings.getLanguage());
    }
}
