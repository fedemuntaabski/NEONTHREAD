package com.neonthread.settings.appliers;

import com.neonthread.settings.VideoSettings;
import javax.swing.JFrame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class VideoSettingsApplier {
    private final JFrame window;

    public VideoSettingsApplier(JFrame window) {
        this.window = window;
    }

    public void apply(VideoSettings settings) {
        applyFullscreen(settings);
        // Resolution is applied within applyFullscreen logic or separately if needed
    }

    private void applyFullscreen(VideoSettings settings) {
        GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice();
        
        boolean isFullscreen = settings.isFullscreen();
        
        // Only change if state is different to avoid flickering, 
        // but for simplicity we re-apply. 
        // In a real app, check current state.
        
        if (window.isUndecorated() != isFullscreen) {
             window.dispose();
             window.setUndecorated(isFullscreen);
             window.setVisible(true);
        }

        if (isFullscreen) {
            if (device.isFullScreenSupported()) {
                device.setFullScreenWindow(window);
            }
        } else {
            if (device.getFullScreenWindow() == window) {
                device.setFullScreenWindow(null);
            }
            applyResolution(settings);
        }
    }

    private void applyResolution(VideoSettings settings) {
        String resolution = settings.getResolution();
        String[] parts = resolution.split("x");
        
        if (parts.length == 2) {
            try {
                int width = Integer.parseInt(parts[0]);
                int height = Integer.parseInt(parts[1]);
                window.setSize(width, height);
                window.setLocationRelativeTo(null);
            } catch (NumberFormatException e) {
                System.err.println("Invalid resolution: " + resolution);
            }
        }
    }
}
