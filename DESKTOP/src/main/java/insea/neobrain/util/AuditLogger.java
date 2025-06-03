package insea.neobrain.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for audit logging
 */
public class AuditLogger {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogger.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Log user authentication events
     */
    public static void logAuthentication(String username, boolean success, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        String status = success ? "SUCCESS" : "FAILED";
        
        if (success) {
            logger.info("[AUTH] {} - User '{}' login {} - {}", timestamp, username, status, details);
        } else {
            logger.warn("[AUTH] {} - User '{}' login {} - {}", timestamp, username, status, details);
        }
    }
    
    /**
     * Log user actions
     */
    public static void logUserAction(String username, String action, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        logger.info("[ACTION] {} - User '{}' performed action: {} - Details: {}", 
                   timestamp, username, action, details);
    }
    
    /**
     * Log data changes (CRUD operations)
     */
    public static void logDataChange(String username, String entity, String operation, Long entityId, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        logger.info("[DATA] {} - User '{}' - Entity: {} - Operation: {} - ID: {} - Details: {}", 
                   timestamp, username, entity, operation, entityId, details);
    }
    
    /**
     * Log inventory operations
     */
    public static void logInventoryAction(String username, String action, Long inventoryId, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        logger.info("[INVENTORY] {} - User '{}' - Action: {} - Inventory ID: {} - Details: {}", 
                   timestamp, username, action, inventoryId, details);
    }
    
    /**
     * Log sales operations
     */
    public static void logSalesAction(String username, String action, Long orderId, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        logger.info("[SALES] {} - User '{}' - Action: {} - Order ID: {} - Details: {}", 
                   timestamp, username, action, orderId, details);
    }
    
    /**
     * Log system errors
     */
    public static void logError(String username, String operation, String error, Exception e) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        logger.error("[ERROR] {} - User '{}' - Operation: {} - Error: {}", 
                    timestamp, username, operation, error, e);
    }
    
    /**
     * Log security events
     */
    public static void logSecurityEvent(String username, String event, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        logger.warn("[SECURITY] {} - User '{}' - Event: {} - Details: {}", 
                   timestamp, username, event, details);
    }
    
    /**
     * Log report generation
     */
    public static void logReportGeneration(String username, String reportType, String parameters) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        logger.info("[REPORT] {} - User '{}' generated report: {} - Parameters: {}", 
                   timestamp, username, reportType, parameters);
    }
    
    /**
     * Log general information events
     */
    public static void logInfo(String action, String details) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        logger.info("[INFO] {} - Action: {} - Details: {}", 
                   timestamp, action, details);
    }
    
    /**
     * Log configuration changes
     */
    public static void logConfigurationChange(String username, String configKey, String oldValue, String newValue) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        logger.info("[CONFIG] {} - User '{}' changed config '{}' from '{}' to '{}'", 
                   timestamp, username, configKey, oldValue, newValue);
    }
}
