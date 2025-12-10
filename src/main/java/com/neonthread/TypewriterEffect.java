package com.neonthread;

import javax.swing.Timer;
import java.util.function.Consumer;

/**
 * Efecto typewriter reutilizable siguiendo DRY.
 */
public class TypewriterEffect {
    private final String fullText;
    private final int delayMs;
    private final Consumer<String> onUpdate;
    private final Runnable onComplete;
    
    private int currentIndex = 0;
    private Timer timer;
    
    public TypewriterEffect(String text, Consumer<String> onUpdate, Runnable onComplete) {
        this(text, GameConstants.TYPEWRITER_DELAY_MS, onUpdate, onComplete);
    }
    
    public TypewriterEffect(String text, int delayMs, Consumer<String> onUpdate, Runnable onComplete) {
        this.fullText = text;
        this.delayMs = delayMs;
        this.onUpdate = onUpdate;
        this.onComplete = onComplete;
    }
    
    public void start() {
        currentIndex = 0;
        timer = new Timer(delayMs, e -> {
            if (currentIndex < fullText.length()) {
                currentIndex++;
                onUpdate.accept(fullText.substring(0, currentIndex));
            } else {
                stop();
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
        timer.start();
    }
    
    public void stop() {
        if (timer != null) {
            timer.stop();
        }
    }
    
    public boolean isComplete() {
        return currentIndex >= fullText.length();
    }
}
