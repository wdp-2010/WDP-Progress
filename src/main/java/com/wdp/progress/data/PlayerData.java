package com.wdp.progress.data;

import java.util.HashSet;
import java.util.Set;

/**
 * Player data container.
 * Stores all persistent data for a player's progress tracking.
 */
public class PlayerData {
    
    private final java.util.UUID uuid;
    private double currentProgress;
    private double lastProgress;
    private long lastUpdate;
    private long lastDeathTime;
    private final Set<String> completedAchievements;
    private long firstJoin;
    private long lastSeen;
    
    // Cached equipment values (for death penalty calculations)
    private double lastEquipmentValue;
    
    // Death tracking
    private int totalDeaths;
    
    public PlayerData(java.util.UUID uuid) {
        this.uuid = uuid;
        this.currentProgress = 1.0;
        this.lastProgress = 1.0;
        this.lastUpdate = System.currentTimeMillis();
        this.lastDeathTime = 0;
        this.completedAchievements = new HashSet<>();
        this.firstJoin = System.currentTimeMillis();
        this.lastSeen = System.currentTimeMillis();
        this.lastEquipmentValue = 0.0;
        this.totalDeaths = 0;
    }
    
    public java.util.UUID getUUID() {
        return uuid;
    }
    
    public double getCurrentProgress() {
        return currentProgress;
    }
    
    public void setCurrentProgress(double progress) {
        this.lastProgress = this.currentProgress;
        this.currentProgress = progress;
        this.lastUpdate = System.currentTimeMillis();
    }
    
    public double getLastProgress() {
        return lastProgress;
    }
    
    public long getLastUpdate() {
        return lastUpdate;
    }
    
    public long getLastUpdated() {
        return lastUpdate;
    }
    
    public long getLastDeathTime() {
        return lastDeathTime;
    }
    
    public int getTotalDeaths() {
        return totalDeaths;
    }
    
    public void incrementDeaths() {
        this.totalDeaths++;
    }
    
    public void setTotalDeaths(int deaths) {
        this.totalDeaths = deaths;
    }
    
    public void setLastDeathTime(long time) {
        this.lastDeathTime = time;
    }
    
    public Set<String> getCompletedAchievements() {
        return new HashSet<>(completedAchievements);
    }
    
    public void addAchievement(String achievementId) {
        completedAchievements.add(achievementId);
    }
    
    public void removeAchievement(String achievementId) {
        completedAchievements.remove(achievementId);
    }
    
    public boolean hasAchievement(String achievementId) {
        return completedAchievements.contains(achievementId);
    }
    
    public long getFirstJoin() {
        return firstJoin;
    }
    
    public void setFirstJoin(long firstJoin) {
        this.firstJoin = firstJoin;
    }
    
    public long getLastSeen() {
        return lastSeen;
    }
    
    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }
    
    public double getLastEquipmentValue() {
        return lastEquipmentValue;
    }
    
    public void setLastEquipmentValue(double value) {
        this.lastEquipmentValue = value;
    }
    
    /**
     * Get the progress change since last update
     */
    public double getProgressDelta() {
        return currentProgress - lastProgress;
    }
    
    /**
     * Check if progress has changed significantly
     */
    public boolean hasSignificantChange(double threshold) {
        return Math.abs(getProgressDelta()) >= threshold;
    }
}
