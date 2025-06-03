package insea.neobrain.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Configuration Manager for external configuration support
 * Provides centralized configuration management with file-based and environment variable support
 */
public class ConfigurationManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
    private static final String DEFAULT_CONFIG_FILE = "application.properties";
    private static final String ENV_CONFIG_FILE = "INVENTORY_CONFIG_FILE";
    
    private static ConfigurationManager instance;
    private final Properties properties;
    private final Map<String, String> runtimeProperties;
    private final String configFilePath;
    
    private ConfigurationManager() {
        this.properties = new Properties();
        this.runtimeProperties = new ConcurrentHashMap<>();
        this.configFilePath = determineConfigFilePath();
        loadConfiguration();
    }
    
    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }
    
    /**
     * Determine the configuration file path from environment or use default
     */
    private String determineConfigFilePath() {
        String envConfigPath = System.getenv(ENV_CONFIG_FILE);
        if (envConfigPath != null && !envConfigPath.trim().isEmpty()) {
            logger.info("Using configuration file from environment: {}", envConfigPath);
            return envConfigPath;
        }
        
        // Try to find config file in classpath
        String classpathConfig = getClass().getClassLoader().getResource(DEFAULT_CONFIG_FILE) != null ?
            DEFAULT_CONFIG_FILE : null;
        
        if (classpathConfig != null) {
            logger.info("Using configuration file from classpath: {}", DEFAULT_CONFIG_FILE);
            return classpathConfig;
        }
        
        // Use external file in current directory
        String externalConfig = System.getProperty("user.dir") + File.separator + DEFAULT_CONFIG_FILE;
        logger.info("Using external configuration file: {}", externalConfig);
        return externalConfig;
    }
    
    /**
     * Load configuration from file and environment variables
     */
    private void loadConfiguration() {
        loadDefaultConfiguration();
        loadFileConfiguration();
        loadEnvironmentOverrides();
        logger.info("Configuration loaded successfully from: {}", configFilePath);
    }
    
    /**
     * Load default configuration values
     */
    private void loadDefaultConfiguration() {
        // Database configuration defaults
        properties.setProperty("db.url", "jdbc:postgresql://localhost:5432/inventory_db");
        properties.setProperty("db.username", "inventory_user");
        properties.setProperty("db.password", "inventory_pass");
        properties.setProperty("db.driver", "org.postgresql.Driver");
        properties.setProperty("db.pool.max.size", "20");
        properties.setProperty("db.pool.min.size", "5");
        
        // Application configuration defaults
        properties.setProperty("app.name", "Inventory Management System");
        properties.setProperty("app.version", "1.0.0");
        properties.setProperty("app.debug.enabled", "false");
        properties.setProperty("app.audit.enabled", "true");
        properties.setProperty("app.export.directory", "exports");
        
        // UI configuration defaults
        properties.setProperty("ui.theme", "default");
        properties.setProperty("ui.language", "fr");
        properties.setProperty("ui.window.width", "1200");
        properties.setProperty("ui.window.height", "800");
        properties.setProperty("ui.table.rows.per.page", "50");
        
        // Security configuration defaults
        properties.setProperty("security.password.min.length", "8");
        properties.setProperty("security.password.require.special", "true");
        properties.setProperty("security.session.timeout", "3600");
        properties.setProperty("security.max.login.attempts", "3");
        
        // Inventory configuration defaults
        properties.setProperty("inventory.auto.schedule.enabled", "true");
        properties.setProperty("inventory.low.stock.threshold", "10");
        properties.setProperty("inventory.alert.email.enabled", "false");
        properties.setProperty("inventory.backup.frequency.days", "7");
        
        logger.debug("Default configuration loaded");
    }
    
    /**
     * Load configuration from file
     */
    private void loadFileConfiguration() {
        try {
            File configFile = new File(configFilePath);
            if (configFile.exists() && configFile.isFile()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    properties.load(fis);
                    logger.info("Configuration loaded from file: {}", configFilePath);
                }
            } else {
                // Try to load from classpath
                try (InputStream is = getClass().getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE)) {
                    if (is != null) {
                        properties.load(is);
                        logger.info("Configuration loaded from classpath: {}", DEFAULT_CONFIG_FILE);
                    } else {
                        logger.warn("Configuration file not found: {}, using defaults", configFilePath);
                        createDefaultConfigFile();
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error loading configuration file: {}", configFilePath, e);
        }
    }
    
    /**
     * Load environment variable overrides
     */
    private void loadEnvironmentOverrides() {
        Map<String, String> env = System.getenv();
        for (Map.Entry<String, String> entry : env.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("INVENTORY_")) {
                // Convert INVENTORY_DB_URL to db.url
                String propertyKey = key.substring(10).toLowerCase().replace('_', '.');
                properties.setProperty(propertyKey, entry.getValue());
                logger.debug("Environment override: {} = {}", propertyKey, entry.getValue());
            }
        }
    }
    
    /**
     * Create a default configuration file
     */
    private void createDefaultConfigFile() {
        try {
            File configFile = new File(configFilePath);
            configFile.getParentFile().mkdirs();
            
            try (FileOutputStream fos = new FileOutputStream(configFile)) {
                properties.store(fos, "Inventory Management System Configuration");
                logger.info("Default configuration file created: {}", configFilePath);
            }
        } catch (IOException e) {
            logger.error("Error creating default configuration file: {}", configFilePath, e);
        }
    }
    
    /**
     * Get a configuration property value
     */
    public String getProperty(String key) {
        // Check runtime properties first (highest priority)
        String runtimeValue = runtimeProperties.get(key);
        if (runtimeValue != null) {
            return runtimeValue;
        }
        
        // Check system properties
        String systemValue = System.getProperty("inventory." + key);
        if (systemValue != null) {
            return systemValue;
        }
        
        // Check loaded properties
        return properties.getProperty(key);
    }
    
    /**
     * Get a configuration property value with default
     */
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Get a configuration property as integer
     */
    public int getIntProperty(String key, int defaultValue) {
        try {
            String value = getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for property {}: {}", key, getProperty(key));
            return defaultValue;
        }
    }
    
    /**
     * Get a configuration property as boolean
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    /**
     * Set a runtime property (highest priority)
     */
    public void setRuntimeProperty(String key, String value) {
        runtimeProperties.put(key, value);
        logger.debug("Runtime property set: {} = {}", key, value);
    }
    
    /**
     * Remove a runtime property
     */
    public void removeRuntimeProperty(String key) {
        runtimeProperties.remove(key);
        logger.debug("Runtime property removed: {}", key);
    }
    
    /**
     * Get all properties as a map
     */
    public Map<String, String> getAllProperties() {
        Map<String, String> allProps = new ConcurrentHashMap<>();
        
        // Add file properties
        for (String key : properties.stringPropertyNames()) {
            allProps.put(key, properties.getProperty(key));
        }
        
        // Override with runtime properties
        allProps.putAll(runtimeProperties);
        
        return allProps;
    }
    
    /**
     * Reload configuration from file
     */
    public void reloadConfiguration() {
        properties.clear();
        runtimeProperties.clear();
        loadConfiguration();
        logger.info("Configuration reloaded");
    }
    
    /**
     * Save current configuration to file
     */
    public void saveConfiguration() {
        try (FileOutputStream fos = new FileOutputStream(configFilePath)) {
            properties.store(fos, "Inventory Management System Configuration - Updated: " + 
                java.time.LocalDateTime.now());
            logger.info("Configuration saved to: {}", configFilePath);
        } catch (IOException e) {
            logger.error("Error saving configuration file: {}", configFilePath, e);
        }
    }
    
    /**
     * Check if running in debug mode
     */
    public boolean isDebugEnabled() {
        return getBooleanProperty("app.debug.enabled", false);
    }
    
    /**
     * Check if audit logging is enabled
     */
    public boolean isAuditEnabled() {
        return getBooleanProperty("app.audit.enabled", true);
    }
    
    /**
     * Get database connection URL
     */
    public String getDatabaseUrl() {
        return getProperty("db.url", "jdbc:postgresql://localhost:5432/inventory_db");
    }
    
    /**
     * Get database username
     */
    public String getDatabaseUsername() {
        return getProperty("db.username", "inventory_user");
    }
    
    /**
     * Get database password
     */
    public String getDatabasePassword() {
        return getProperty("db.password", "inventory_pass");
    }
    
    /**
     * Get export directory path
     */
    public String getExportDirectory() {
        return getProperty("app.export.directory", "exports");
    }
    
    /**
     * Get low stock threshold for automatic inventory scheduling
     */
    public int getLowStockThreshold() {
        return getIntProperty("inventory.low.stock.threshold", 10);
    }
    
    /**
     * Check if automatic inventory scheduling is enabled
     */
    public boolean isAutoScheduleEnabled() {
        return getBooleanProperty("inventory.auto.schedule.enabled", true);
    }
    
    /**
     * Get session timeout in seconds
     */
    public int getSessionTimeoutSeconds() {
        return getIntProperty("security.session.timeout", 3600);
    }
    
    /**
     * Get maximum login attempts
     */
    public int getMaxLoginAttempts() {
        return getIntProperty("security.max.login.attempts", 3);
    }
    
    /**
     * Print current configuration (for debugging)
     */
    public void printConfiguration() {
        if (isDebugEnabled()) {
            logger.info("=== Current Configuration ===");
            getAllProperties().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    String value = entry.getKey().contains("password") ? "***" : entry.getValue();
                    logger.info("{} = {}", entry.getKey(), value);
                });
            logger.info("============================");
        }
    }
}
