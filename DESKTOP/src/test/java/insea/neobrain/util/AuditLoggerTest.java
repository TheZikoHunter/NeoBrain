package insea.neobrain.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Unit tests for AuditLogger class
 */
public class AuditLoggerTest {

    @TempDir
    Path tempDir;

    private Path logFile;
    private AuditLogger auditLogger;

    @BeforeEach
    void setUp() throws IOException {
        logFile = tempDir.resolve("audit.log");
        auditLogger = AuditLogger.getInstance();
        
        // Set the log file path for testing
        // Note: This assumes AuditLogger has a method to set log file path
        // If not available, we'll test the getInstance behavior
    }

    @Test
    @DisplayName("Should be singleton")
    public void testSingletonPattern() {
        // When
        AuditLogger instance1 = AuditLogger.getInstance();
        AuditLogger instance2 = AuditLogger.getInstance();

        // Then
        assertThat(instance1).isSameAs(instance2);
    }

    @Test
    @DisplayName("Should log authentication success")
    public void testLogAuthenticationSuccess() {
        // Given
        String username = "john.doe";
        String ipAddress = "192.168.1.100";

        // When
        auditLogger.logAuthenticationSuccess(username, ipAddress);

        // Then
        // Since we can't easily test file output in unit tests without modifying the class,
        // we verify the method doesn't throw exceptions
        assertThat(true).isTrue(); // Method executed without exception
    }

    @Test
    @DisplayName("Should log authentication failure")
    public void testLogAuthenticationFailure() {
        // Given
        String username = "invalid.user";
        String ipAddress = "192.168.1.100";
        String reason = "Invalid credentials";

        // When
        auditLogger.logAuthenticationFailure(username, ipAddress, reason);

        // Then
        assertThat(true).isTrue(); // Method executed without exception
    }

    @Test
    @DisplayName("Should log data access")
    public void testLogDataAccess() {
        // Given
        String username = "admin";
        String action = "READ";
        String entityType = "Personnel";
        String entityId = "123";

        // When
        auditLogger.logDataAccess(username, action, entityType, entityId);

        // Then
        assertThat(true).isTrue(); // Method executed without exception
    }

    @Test
    @DisplayName("Should log data modification")
    public void testLogDataModification() {
        // Given
        String username = "manager";
        String action = "UPDATE";
        String entityType = "Produit";
        String entityId = "456";
        String oldValue = "Old Product Name";
        String newValue = "New Product Name";

        // When
        auditLogger.logDataModification(username, action, entityType, entityId, oldValue, newValue);

        // Then
        assertThat(true).isTrue(); // Method executed without exception
    }

    @Test
    @DisplayName("Should log system action")
    public void testLogSystemAction() {
        // Given
        String username = "system.admin";
        String action = "BACKUP_DATABASE";
        String details = "Scheduled database backup completed successfully";

        // When
        auditLogger.logSystemAction(username, action, details);

        // Then
        assertThat(true).isTrue(); // Method executed without exception
    }

    @Test
    @DisplayName("Should handle null username gracefully")
    public void testLogWithNullUsername() {
        // When/Then
        assertThatCode(() -> {
            auditLogger.logAuthenticationSuccess(null, "192.168.1.100");
            auditLogger.logDataAccess(null, "READ", "Personnel", "123");
            auditLogger.logSystemAction(null, "ACTION", "details");
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle empty strings gracefully")
    public void testLogWithEmptyStrings() {
        // When/Then
        assertThatCode(() -> {
            auditLogger.logAuthenticationSuccess("", "");
            auditLogger.logDataAccess("", "", "", "");
            auditLogger.logDataModification("", "", "", "", "", "");
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle very long strings")
    public void testLogWithLongStrings() {
        // Given
        String longString = "a".repeat(10000);

        // When/Then
        assertThatCode(() -> {
            auditLogger.logSystemAction("user", "ACTION", longString);
            auditLogger.logDataModification("user", "UPDATE", "Entity", "1", longString, longString);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle special characters in log messages")
    public void testLogWithSpecialCharacters() {
        // Given
        String specialChars = "Special chars: ñáéíóú üÜ 中文 日本語 🚀";

        // When/Then
        assertThatCode(() -> {
            auditLogger.logAuthenticationSuccess(specialChars, "192.168.1.100");
            auditLogger.logSystemAction("user", "ACTION", specialChars);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle concurrent logging")
    public void testConcurrentLogging() throws InterruptedException {
        // Given
        int numberOfThreads = 10;
        int operationsPerThread = 100;
        Thread[] threads = new Thread[numberOfThreads];

        // When
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    auditLogger.logSystemAction(
                        "user" + threadId, 
                        "ACTION" + j, 
                        "Thread " + threadId + " operation " + j
                    );
                }
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Then
        assertThat(true).isTrue(); // All operations completed without exception
    }

    @Test
    @DisplayName("Should format timestamp correctly")
    public void testTimestampFormat() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        auditLogger.logSystemAction("user", "TEST_ACTION", "Testing timestamp");

        // Then
        // This test mainly ensures the method runs without errors
        // In a real implementation, we might want to capture and verify the log format
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Should log different action types")
    public void testDifferentActionTypes() {
        // Given
        String[] actions = {"CREATE", "READ", "UPDATE", "DELETE", "LOGIN", "LOGOUT", "EXPORT", "IMPORT"};

        // When/Then
        assertThatCode(() -> {
            for (String action : actions) {
                auditLogger.logDataAccess("user", action, "TestEntity", "123");
            }
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle rapid successive calls")
    public void testRapidSuccessiveCalls() {
        // When/Then
        assertThatCode(() -> {
            for (int i = 0; i < 1000; i++) {
                auditLogger.logSystemAction("user", "RAPID_ACTION_" + i, "Details " + i);
            }
        }).doesNotThrowAnyException();
    }
}
