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
    
    // GravesX-based death penalty tracking
    private double currentDeathPenalty;
    private final java.util.Map<String, GraveData> activeGraves; // UUID -> GraveData
    
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
        this.currentDeathPenalty = 0.0;
        this.activeGraves = new java.util.HashMap<>();
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
    
    // === GravesX Death Penalty Methods ===
    
    public double getCurrentDeathPenalty() {
        return currentDeathPenalty;
    }
    
    public void setCurrentDeathPenalty(double penalty) {
        this.currentDeathPenalty = Math.max(0.0, penalty);
    }
    
    public void addGrave(String graveUUID, GraveData graveData) {
        activeGraves.put(graveUUID, graveData);
    }
    
    public void removeGrave(String graveUUID) {
        activeGraves.remove(graveUUID);
    }
    
    public GraveData getGrave(String graveUUID) {
        return activeGraves.get(graveUUID);
    }
    
    public java.util.Map<String, GraveData> getActiveGraves() {
        return new java.util.HashMap<>(activeGraves);
    }
    
    public void clearOldGraves() {
        activeGraves.entrySet().removeIf(entry -> 
            System.currentTimeMillis() - entry.getValue().getCreationTime() > 3600000L // 1 hour
        );
    }
    
    /**
     * Inner class to track grave data
     */
    public static class GraveData {
        private final String graveUUID;
        private final long creationTime;
        private final double totalItemValue;
        private double recoveredValue;
        
        public GraveData(String graveUUID, double totalItemValue) {
            this.graveUUID = graveUUID;
            this.creationTime = System.currentTimeMillis();
            this.totalItemValue = totalItemValue;
            this.recoveredValue = 0.0;
        }
        
        public String getGraveUUID() { return graveUUID; }
        public long getCreationTime() { return creationTime; }
        public double getTotalItemValue() { return totalItemValue; }
        public double getRecoveredValue() { return recoveredValue; }
        
        public void addRecoveredValue(double value) {
            this.recoveredValue += value;
        }
        
        public double getRecoveryPercentage() {
            return totalItemValue > 0 ? (recoveredValue / totalItemValue) * 100.0 : 100.0;
        }
        
        public double getPenalty() {
            // Penalty reduces as more items are recovered
            double lostPercentage = 100.0 - getRecoveryPercentage();
            return (totalItemValue / 1000.0) * (lostPercentage / 100.0); // Scale penalty
        }
    }
}
