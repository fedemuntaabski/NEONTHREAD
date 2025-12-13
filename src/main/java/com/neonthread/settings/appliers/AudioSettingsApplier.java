package com.neonthread.settings.appliers;

import com.neonthread.settings.AudioSettings;

public class AudioSettingsApplier {
    public void apply(AudioSettings settings) {
        // In a real game, this would update the SoundSystem
        System.out.println("Applying Audio Settings: Master Volume = " + settings.getMasterVolume());
    }
}
