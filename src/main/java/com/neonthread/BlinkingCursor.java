package com.neonthread;

import javax.swing.Timer;

/**
 * Cursor parpadeante reutilizable.
 */
public class BlinkingCursor {
    private boolean visible = true;
    private Timer timer;
    private final Runnable onBlink;
    
    public BlinkingCursor(Runnable onBlink) {
        this.onBlink = onBlink;
    }
    
    public void start() {
        timer = new Timer(GameConstants.CURSOR_BLINK_DELAY_MS, e -> {
            visible = !visible;
            if (onBlink != null) {
                onBlink.run();
            }
        });
        timer.start();
    }
    
    public void stop() {
        if (timer != null) {
            timer.stop();
        }
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public String getCursor() {
        return visible ? "█" : " ";
    }
}
