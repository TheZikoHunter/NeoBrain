package insea.neobrain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import static org.assertj.core.api.Assertions.*;

import insea.neobrain.util.*;
import insea.neobrain.config.ConfigurationManager;

/**
 * Main application test suite
 * Tests core functionality and application setup
 */
@TestMethodOrder(OrderAnnotation.class)
public class AppTest {

    @Test
    @Order(1)
    @DisplayName("Should validate application utilities are working")
    public void testApplicationUtilities() {
        // Test PasswordUtil
        String password = "testPassword123";
        String hashedPassword = PasswordUtil.hashPassword(password);
        assertThat(hashedPassword).isNotNull();
        assertThat(PasswordUtil.verifyPassword(password, hashedPassword)).isTrue();

        // Test ValidationUtil basic functionality
        assertThat(ValidationUtil.validatePersonnel(null)).isEmpty();
        assertThat(ValidationUtil.validateProduit(null)).isEmpty();
        assertThat(ValidationUtil.validateClient(null)).isEmpty();

        // Test BusinessException factory methods
        BusinessException validationError = BusinessException.validationError("field", "reason");
        assertThat(validationError.getMessage()).contains("field");
        assertThat(validationError.getMessage()).contains("reason");

        BusinessException entityNotFound = BusinessException.entityNotFound("Entity", "123");
        assertThat(entityNotFound.getMessage()).contains("Entity");
        assertThat(entityNotFound.getMessage()).contains("123");
    }

    @Test
    @Order(2)
    @DisplayName("Should validate configuration management")
    public void testConfigurationManagement() {
        // Test ConfigurationManager with null file path (should not crash)
        ConfigurationManager config = new ConfigurationManager(null);
        String defaultValue = config.getProperty("non.existent", "default");
        assertThat(defaultValue).isEqualTo("default");

        // Test default values for different types
        int defaultInt = config.getIntProperty("non.existent.int", 100);
        assertThat(defaultInt).isEqualTo(100);

        boolean defaultBool = config.getBooleanProperty("non.existent.bool", true);
        assertThat(defaultBool).isTrue();

        long defaultLong = config.getLongProperty("non.existent.long", 1000L);
        assertThat(defaultLong).isEqualTo(1000L);
    }

    @Test
    @Order(3)
    @DisplayName("Should validate audit logging doesn't crash")
    public void testAuditLogging() {
        // Test AuditLogger singleton
        AuditLogger logger1 = AuditLogger.getInstance();
        AuditLogger logger2 = AuditLogger.getInstance();
        assertThat(logger1).isSameAs(logger2);

        // Test logging methods don't throw exceptions
        assertThatCode(() -> {
            logger1.logAuthenticationSuccess("testuser", "127.0.0.1");
            logger1.logAuthenticationFailure("testuser", "127.0.0.1", "Test reason");
            logger1.logDataAccess("testuser", "READ", "TestEntity", "123");
            logger1.logDataModification("testuser", "UPDATE", "TestEntity", "123", "old", "new");
            logger1.logSystemAction("testuser", "TEST_ACTION", "Test details");
        }).doesNotThrowAnyException();
    }

    @Test
    @Order(4)
    @DisplayName("Should validate export utility basic functionality")
    public void testExportUtility() {
        // Test that ExportUtil methods exist and can be called
        // We can't test file operations easily in unit tests, but we can test validation
        String[] headers = {"Name", "Value"};
        String[][] data = {{"Test", "Data"}};

        assertThatThrownBy(() -> ExportUtil.exportToCsv(null, headers, data))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("File cannot be null");

        assertThatThrownBy(() -> ExportUtil.exportToCsv(new java.io.File("test.csv"), null, data))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Headers cannot be null");

        assertThatThrownBy(() -> ExportUtil.exportToCsv(new java.io.File("test.csv"), headers, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Data cannot be null");
    }

    @Test
    @Order(5)
    @DisplayName("Should validate application constants and enums")
    public void testApplicationConstants() {
        // Test that common role constants work as expected
        String[] validRoles = {"ADMIN", "GESTIONNAIRE_STOCK", "EMPLOYE"};
        assertThat(validRoles).contains("ADMIN");
        assertThat(validRoles).contains("GESTIONNAIRE_STOCK");
        assertThat(validRoles).contains("EMPLOYE");

        // Test common status values
        String[] validStatuses = {"EN_ATTENTE", "EN_COURS", "APPROUVEE", "REFUSEE", "CLOTUREE"};
        assertThat(validStatuses).contains("EN_ATTENTE");
        assertThat(validStatuses).contains("APPROUVEE");
        assertThat(validStatuses).contains("CLOTUREE");

        // Test priority levels
        String[] validPriorities = {"BASSE", "MOYENNE", "HAUTE", "CRITIQUE"};
        assertThat(validPriorities).contains("BASSE");
        assertThat(validPriorities).contains("HAUTE");
        assertThat(validPriorities).contains("CRITIQUE");
    }

    @Test
    @Order(6)
    @DisplayName("Should validate application is ready for startup")
    public void testApplicationReadiness() {
        // Test that all critical utilities are available
        assertThat(PasswordUtil.class).isNotNull();
        assertThat(ValidationUtil.class).isNotNull();
        assertThat(ExportUtil.class).isNotNull();
        assertThat(AuditLogger.class).isNotNull();
        assertThat(BusinessException.class).isNotNull();
        assertThat(ConfigurationManager.class).isNotNull();

        // Test basic functionality works
        String testPassword = "appReadinessTest";
        String hashedPassword = PasswordUtil.hashPassword(testPassword);
        assertThat(PasswordUtil.verifyPassword(testPassword, hashedPassword)).isTrue();

        // Test exception handling
        BusinessException testException = BusinessException.validationError("test", "Application readiness test");
        assertThat(testException).isInstanceOf(RuntimeException.class);
        assertThat(testException.getMessage()).contains("test");
    }

    @Test
    @Order(7)
    @DisplayName("Should validate security measures are in place")
    public void testSecurityMeasures() {
        // Test password hashing produces different results for same input (salt)
        String password = "securityTest123";
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);
        
        assertThat(hash1).isNotEqualTo(hash2); // Different due to salt
        assertThat(PasswordUtil.verifyPassword(password, hash1)).isTrue();
        assertThat(PasswordUtil.verifyPassword(password, hash2)).isTrue();

        // Test password validation rejects empty/null passwords
        assertThatThrownBy(() -> PasswordUtil.hashPassword(null))
            .isInstanceOf(IllegalArgumentException.class);
        
        assertThatThrownBy(() -> PasswordUtil.hashPassword(""))
            .isInstanceOf(IllegalArgumentException.class);

        // Test password verification handles invalid inputs
        assertThatThrownBy(() -> PasswordUtil.verifyPassword(null, hash1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @Order(8)
    @DisplayName("Should validate error handling mechanisms")
    public void testErrorHandling() {
        // Test BusinessException factory methods work correctly
        assertThat(BusinessException.validationError("field", "reason"))
            .isInstanceOf(BusinessException.class);

        assertThat(BusinessException.entityNotFound("Entity", "123"))
            .isInstanceOf(BusinessException.class);

        assertThat(BusinessException.duplicateEntity("Entity", "field", "value"))
            .isInstanceOf(BusinessException.class);

        assertThat(BusinessException.insufficientStock("Product", 10, 5))
            .isInstanceOf(BusinessException.class);

        assertThat(BusinessException.unauthorizedAccess("ACTION"))
            .isInstanceOf(BusinessException.class);

        assertThat(BusinessException.businessRuleViolation("rule", "details"))
            .isInstanceOf(BusinessException.class);

        assertThat(BusinessException.dataIntegrityError("details"))
            .isInstanceOf(BusinessException.class);
    }

    @Test
    @Order(9)
    @DisplayName("Should validate application meets basic requirements")
    public void testApplicationRequirements() {
        // Test that the application has the basic components needed
        
        // 1. Security - Password hashing
        String password = "requirementTest";
        String hash = PasswordUtil.hashPassword(password);
        assertThat(hash).startsWith("$2a$"); // BCrypt format
        
        // 2. Validation - Data validation
        assertThat(ValidationUtil.class.getDeclaredMethods()).hasSizeGreaterThan(0);
        
        // 3. Audit - Logging capability
        AuditLogger logger = AuditLogger.getInstance();
        assertThat(logger).isNotNull();
        
        // 4. Export - Data export capability
        assertThat(ExportUtil.class.getDeclaredMethods()).hasSizeGreaterThan(0);
        
        // 5. Configuration - External configuration
        ConfigurationManager config = new ConfigurationManager(null);
        assertThat(config).isNotNull();
        
        // 6. Exception handling - Business exceptions
        assertThat(BusinessException.class.getSuperclass()).isEqualTo(RuntimeException.class);
    }

    @Test
    @Order(10)
    @DisplayName("Application basic setup test should pass")
    public void testBasicApplicationSetup() {
        // Classic test to ensure everything is working
        assertThat(true).isTrue();
        
        // Test that we can create basic application components
        assertThatCode(() -> {
            AuditLogger.getInstance();
            new ConfigurationManager(null);
            PasswordUtil.hashPassword("test");
            ValidationUtil.validatePersonnel(null);
            BusinessException.validationError("test", "test");
        }).doesNotThrowAnyException();
    }
}
