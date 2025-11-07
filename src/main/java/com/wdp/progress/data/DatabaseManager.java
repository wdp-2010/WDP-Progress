package com.wdp.progress.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wdp.progress.WDPProgressPlugin;
import com.wdp.progress.config.ConfigManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Manages database connections and operations.
 * Supports both SQLite and MySQL with connection pooling.
 */
public class DatabaseManager {
    
    private final WDPProgressPlugin plugin;
    private final ConfigManager config;
    private final Gson gson;
    
    private HikariDataSource dataSource;
    private String databaseType;
    
    public DatabaseManager(WDPProgressPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.gson = new Gson();
    }
    
    /**
     * Connect to the database and initialize tables
     */
    public boolean connect() {
        try {
            databaseType = config.getDatabaseType().toUpperCase();
            
            HikariConfig hikariConfig = new HikariConfig();
            
            if (databaseType.equals("SQLITE")) {
                setupSQLite(hikariConfig);
            } else if (databaseType.equals("MYSQL")) {
                setupMySQL(hikariConfig);
            } else {
                plugin.getLogger().severe("Unknown database type: " + databaseType);
                return false;
            }
            
            dataSource = new HikariDataSource(hikariConfig);
            
            // Initialize tables
            initializeTables();
            
            plugin.getLogger().info("Connected to " + databaseType + " database successfully");
            return true;
            
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to connect to database", e);
            return false;
        }
    }
    
    /**
     * Setup SQLite connection
     */
    private void setupSQLite(HikariConfig config) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        String dbFile = this.config.getSQLiteFile();
        File databaseFile = new File(dataFolder, dbFile);
        
        config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(1); // SQLite doesn't support multiple connections well
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    }
    
    /**
     * Setup MySQL connection
     */
    private void setupMySQL(HikariConfig config) {
        String host = this.config.getMySQLHost();
        int port = this.config.getMySQLPort();
        String database = this.config.getMySQLDatabase();
        String username = this.config.getMySQLUsername();
        String password = this.config.getMySQLPassword();
        
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setMaximumPoolSize(10);
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
    }
    
    /**
     * Initialize database tables
     */
    private void initializeTables() throws SQLException {
        try (Connection conn = getConnection()) {
            // Player progress table
            String createPlayersTable = databaseType.equals("SQLITE") ?
                "CREATE TABLE IF NOT EXISTS wdp_progress (" +
                "uuid TEXT PRIMARY KEY," +
                "current_progress REAL NOT NULL," +
                "last_progress REAL NOT NULL," +
                "last_update INTEGER NOT NULL," +
                "last_death_time INTEGER NOT NULL," +
                "completed_achievements TEXT," +
                "first_join INTEGER NOT NULL," +
                "last_seen INTEGER NOT NULL," +
                "last_equipment_value REAL NOT NULL" +
                ")" :
                "CREATE TABLE IF NOT EXISTS wdp_progress (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "current_progress DOUBLE NOT NULL," +
                "last_progress DOUBLE NOT NULL," +
                "last_update BIGINT NOT NULL," +
                "last_death_time BIGINT NOT NULL," +
                "completed_achievements TEXT," +
                "first_join BIGINT NOT NULL," +
                "last_seen BIGINT NOT NULL," +
                "last_equipment_value DOUBLE NOT NULL" +
                ")";
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createPlayersTable);
            }
            
            // Progress history table (for tracking changes over time)
            String createHistoryTable = databaseType.equals("SQLITE") ?
                "CREATE TABLE IF NOT EXISTS wdp_progress_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "uuid TEXT NOT NULL," +
                "progress REAL NOT NULL," +
                "timestamp INTEGER NOT NULL" +
                ")" :
                "CREATE TABLE IF NOT EXISTS wdp_progress_history (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "uuid VARCHAR(36) NOT NULL," +
                "progress DOUBLE NOT NULL," +
                "timestamp BIGINT NOT NULL," +
                "INDEX idx_uuid (uuid)," +
                "INDEX idx_timestamp (timestamp)" +
                ")";
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createHistoryTable);
            }
            
            plugin.getLogger().info("Database tables initialized successfully");
        }
    }
    
    /**
     * Get a database connection from the pool
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Database connection pool is not available");
        }
        return dataSource.getConnection();
    }
    
    /**
     * Disconnect from the database
     */
    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("Database connection closed");
        }
    }
    
    /**
     * Load player data from database
     */
    public PlayerData loadPlayerData(UUID uuid) {
        String sql = "SELECT * FROM wdp_progress WHERE uuid = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, uuid.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    PlayerData data = new PlayerData(uuid);
                    data.setCurrentProgress(rs.getDouble("current_progress"));
                    data.setFirstJoin(rs.getLong("first_join"));
                    data.setLastSeen(rs.getLong("last_seen"));
                    data.setLastDeathTime(rs.getLong("last_death_time"));
                    data.setLastEquipmentValue(rs.getDouble("last_equipment_value"));
                    
                    // Load achievements
                    String achievementsJson = rs.getString("completed_achievements");
                    if (achievementsJson != null && !achievementsJson.isEmpty()) {
                        Type setType = new TypeToken<HashSet<String>>(){}.getType();
                        Set<String> achievements = gson.fromJson(achievementsJson, setType);
                        if (achievements != null) {
                            for (String achievement : achievements) {
                                data.addAchievement(achievement);
                            }
                        }
                    }
                    
                    return data;
                }
            }
            
            // Player not found, return new data
            return new PlayerData(uuid);
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load player data for " + uuid, e);
            return new PlayerData(uuid);
        }
    }
    
    /**
     * Save player data to database
     */
    public boolean savePlayerData(PlayerData data) {
        String sql = "INSERT OR REPLACE INTO wdp_progress " +
                    "(uuid, current_progress, last_progress, last_update, last_death_time, " +
                    "completed_achievements, first_join, last_seen, last_equipment_value) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        if (databaseType.equals("MYSQL")) {
            sql = "INSERT INTO wdp_progress " +
                  "(uuid, current_progress, last_progress, last_update, last_death_time, " +
                  "completed_achievements, first_join, last_seen, last_equipment_value) " +
                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                  "ON DUPLICATE KEY UPDATE " +
                  "current_progress = VALUES(current_progress), " +
                  "last_progress = VALUES(last_progress), " +
                  "last_update = VALUES(last_update), " +
                  "last_death_time = VALUES(last_death_time), " +
                  "completed_achievements = VALUES(completed_achievements), " +
                  "last_seen = VALUES(last_seen), " +
                  "last_equipment_value = VALUES(last_equipment_value)";
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, data.getUUID().toString());
            stmt.setDouble(2, data.getCurrentProgress());
            stmt.setDouble(3, data.getLastProgress());
            stmt.setLong(4, data.getLastUpdate());
            stmt.setLong(5, data.getLastDeathTime());
            
            // Serialize achievements
            String achievementsJson = gson.toJson(data.getCompletedAchievements());
            stmt.setString(6, achievementsJson);
            
            stmt.setLong(7, data.getFirstJoin());
            stmt.setLong(8, data.getLastSeen());
            stmt.setDouble(9, data.getLastEquipmentValue());
            
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player data for " + data.getUUID(), e);
            return false;
        }
    }
    
    /**
     * Record progress in history table
     */
    public void recordProgressHistory(UUID uuid, double progress) {
        String sql = "INSERT INTO wdp_progress_history (uuid, progress, timestamp) VALUES (?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, uuid.toString());
            stmt.setDouble(2, progress);
            stmt.setLong(3, System.currentTimeMillis());
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to record progress history", e);
        }
    }
    
    /**
     * Get progress history for a player
     */
    public List<ProgressHistoryEntry> getProgressHistory(UUID uuid, int limit) {
        List<ProgressHistoryEntry> history = new ArrayList<>();
        String sql = "SELECT progress, timestamp FROM wdp_progress_history WHERE uuid = ? ORDER BY timestamp DESC LIMIT ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, uuid.toString());
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    history.add(new ProgressHistoryEntry(
                        rs.getDouble("progress"),
                        rs.getLong("timestamp")
                    ));
                }
            }
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to get progress history", e);
        }
        
        return history;
    }
    
    /**
     * Get top players by progress
     */
    public List<Map.Entry<UUID, Double>> getTopPlayers(int limit) {
        List<Map.Entry<UUID, Double>> topPlayers = new ArrayList<>();
        String sql = "SELECT uuid, current_progress FROM wdp_progress ORDER BY current_progress DESC LIMIT ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    double progress = rs.getDouble("current_progress");
                    topPlayers.add(new AbstractMap.SimpleEntry<>(uuid, progress));
                }
            }
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to get top players", e);
        }
        
        return topPlayers;
    }
    
    /**
     * Delete old progress history entries
     */
    public void cleanupHistory(long olderThanMillis) {
        String sql = "DELETE FROM wdp_progress_history WHERE timestamp < ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            long cutoff = System.currentTimeMillis() - olderThanMillis;
            stmt.setLong(1, cutoff);
            
            int deleted = stmt.executeUpdate();
            if (deleted > 0) {
                plugin.getLogger().info("Cleaned up " + deleted + " old progress history entries");
            }
            
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to cleanup history", e);
        }
    }
    
    /**
     * Progress history entry
     */
    public static class ProgressHistoryEntry {
        private final double progress;
        private final long timestamp;
        
        public ProgressHistoryEntry(double progress, long timestamp) {
            this.progress = progress;
            this.timestamp = timestamp;
        }
        
        public double getProgress() {
            return progress;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
}
