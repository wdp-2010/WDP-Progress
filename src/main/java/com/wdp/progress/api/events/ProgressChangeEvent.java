package com.wdp.progress.api.events;

import com.wdp.progress.progress.ProgressCalculator;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event fired when a player's progress changes significantly.
 * This event is called asynchronously.
 */
public class ProgressChangeEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private final Player player;
    private final double oldProgress;
    private final double newProgress;
    private final ProgressCalculator.ProgressResult result;
    
    public ProgressChangeEvent(Player player, double oldProgress, double newProgress, 
                               ProgressCalculator.ProgressResult result) {
        super(true); // Async event
        this.player = player;
        this.oldProgress = oldProgress;
        this.newProgress = newProgress;
        this.result = result;
    }
    
    /**
     * Get the player whose progress changed
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Get the previous progress value
     */
    public double getOldProgress() {
        return oldProgress;
    }
    
    /**
     * Get the new progress value
     */
    public double getNewProgress() {
        return newProgress;
    }
    
    /**
     * Get the change in progress (positive or negative)
     */
    public double getProgressDelta() {
        return newProgress - oldProgress;
    }
    
    /**
     * Check if progress increased
     */
    public boolean isIncrease() {
        return newProgress > oldProgress;
    }
    
    /**
     * Check if progress decreased
     */
    public boolean isDecrease() {
        return newProgress < oldProgress;
    }
    
    /**
     * Get the detailed progress calculation result
     */
    public ProgressCalculator.ProgressResult getResult() {
        return result;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
