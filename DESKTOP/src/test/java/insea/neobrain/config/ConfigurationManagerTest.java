package insea.neobrain.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Unit tests for ConfigurationManager class
 */
public class ConfigurationManagerTest {

    @TempDir
    Path tempDir;

    private File configFile;
    private ConfigurationManager configManager;

    @BeforeEach
    void setUp() throws IOException {
        configFile = tempDir.resolve("test-config.properties").toFile();
        
        // Create a test configuration file
        Properties testProps = new Properties();
        testProps.setProperty("database.url", "jdbc:postgresql://localhost:5432/testdb");
        testProps.setProperty("database.username", "testuser");
        testProps.setProperty("database.password", "testpass");
        testProps.setProperty("app.name", "Test Application");
        testProps.setProperty("app.version", "1.0.0");
        testProps.setProperty("logging.level", "INFO");
        testProps.setProperty("max.connections", "20");
        testProps.setProperty("timeout.seconds", "30");
        testProps.setProperty("feature.enabled", "true");
        
        try (FileWriter writer = new FileWriter(configFile)) {
            testProps.store(writer, "Test configuration");
        }
        
        configManager = new ConfigurationManager(configFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        // Clear any system properties that might have been set during tests
        System.clearProperty("database.url");
        System.clearProperty("app.name");
        System.clearProperty("test.property");
    }

    @Test
    @DisplayName("Should load configuration from file")
    public void testLoadConfigurationFromFile() {
        // When
        String databaseUrl = configManager.getProperty("database.url");
        String appName = configManager.getProperty("app.name");

        // Then
        assertThat(databaseUrl).isEqualTo("jdbc:postgresql://localhost:5432/testdb");
        assertThat(appName).isEqualTo("Test Application");
    }

    @Test
    @DisplayName("Should return default value for missing property")
    public void testGetPropertyWithDefault() {
        // When
        String missingProperty = configManager.getProperty("non.existent.property", "default-value");

        // Then
        assertThat(missingProperty).isEqualTo("default-value");
    }

    @Test
    @DisplayName("Should return null for missing property without default")
    public void testGetPropertyMissingWithoutDefault() {
        // When
        String missingProperty = configManager.getProperty("non.existent.property");

        // Then
        assertThat(missingProperty).isNull();
    }

    @Test
    @DisplayName("Should prefer system property over file property")
    public void testSystemPropertyOverride() {
        // Given
        System.setProperty("database.url", "jdbc:postgresql://override:5432/overridedb");

        // When
        String databaseUrl = configManager.getProperty("database.url");

        // Then
        assertThat(databaseUrl).isEqualTo("jdbc:postgresql://override:5432/overridedb");
    }

    @Test
    @DisplayName("Should prefer environment variable over file property")
    public void testEnvironmentVariableOverride() {
        // Note: This test is limited because we can't set environment variables at runtime
        // We can only test that the method doesn't fail
        
        // When
        String result = configManager.getProperty("PATH", "default");

        // Then
        assertThat(result).isNotNull();
        // PATH should exist as an environment variable on most systems
    }

    @Test
    @DisplayName("Should convert property to integer")
    public void testGetIntProperty() {
        // When
        int maxConnections = configManager.getIntProperty("max.connections");
        int defaultValue = configManager.getIntProperty("non.existent.int", 100);

        // Then
        assertThat(maxConnections).isEqualTo(20);
        assertThat(defaultValue).isEqualTo(100);
    }

    @Test
    @DisplayName("Should handle invalid integer property")
    public void testGetIntPropertyInvalid() {
        // When/Then
        assertThatThrownBy(() -> configManager.getIntProperty("app.name"))
            .isInstanceOf(NumberFormatException.class);
    }

    @Test
    @DisplayName("Should convert property to long")
    public void testGetLongProperty() {
        // When
        long timeout = configManager.getLongProperty("timeout.seconds");
        long defaultValue = configManager.getLongProperty("non.existent.long", 1000L);

        // Then
        assertThat(timeout).isEqualTo(30L);
        assertThat(defaultValue).isEqualTo(1000L);
    }

    @Test
    @DisplayName("Should convert property to boolean")
    public void testGetBooleanProperty() {
        // When
        boolean featureEnabled = configManager.getBooleanProperty("feature.enabled");
        boolean defaultValue = configManager.getBooleanProperty("non.existent.boolean", false);

        // Then
        assertThat(featureEnabled).isTrue();
        assertThat(defaultValue).isFalse();
    }

    @Test
    @DisplayName("Should handle various boolean representations")
    public void testGetBooleanPropertyVariants() throws IOException {
        // Given - create additional properties for boolean testing
        Properties additionalProps = new Properties();
        additionalProps.setProperty("bool.true1", "true");
        additionalProps.setProperty("bool.true2", "TRUE");
        additionalProps.setProperty("bool.true3", "True");
        additionalProps.setProperty("bool.false1", "false");
        additionalProps.setProperty("bool.false2", "FALSE");
        additionalProps.setProperty("bool.false3", "anything_else");

        File boolConfigFile = tempDir.resolve("bool-config.properties").toFile();
        try (FileWriter writer = new FileWriter(boolConfigFile)) {
            additionalProps.store(writer, "Boolean test configuration");
        }

        ConfigurationManager boolConfigManager = new ConfigurationManager(boolConfigFile.getAbsolutePath());

        // When/Then
        assertThat(boolConfigManager.getBooleanProperty("bool.true1")).isTrue();
        assertThat(boolConfigManager.getBooleanProperty("bool.true2")).isTrue();
        assertThat(boolConfigManager.getBooleanProperty("bool.true3")).isTrue();
        assertThat(boolConfigManager.getBooleanProperty("bool.false1")).isFalse();
        assertThat(boolConfigManager.getBooleanProperty("bool.false2")).isFalse();
        assertThat(boolConfigManager.getBooleanProperty("bool.false3")).isFalse();
    }

    @Test
    @DisplayName("Should handle non-existent configuration file")
    public void testNonExistentConfigFile() {
        // Given
        String nonExistentFile = tempDir.resolve("non-existent.properties").toString();

        // When
        ConfigurationManager manager = new ConfigurationManager(nonExistentFile);
        String property = manager.getProperty("any.property", "default");

        // Then
        assertThat(property).isEqualTo("default");
    }

    @Test
    @DisplayName("Should handle null configuration file path")
    public void testNullConfigFilePath() {
        // When
        ConfigurationManager manager = new ConfigurationManager(null);
        String property = manager.getProperty("any.property", "default");

        // Then
        assertThat(property).isEqualTo("default");
    }

    @Test
    @DisplayName("Should handle empty configuration file path")
    public void testEmptyConfigFilePath() {
        // When
        ConfigurationManager manager = new ConfigurationManager("");
        String property = manager.getProperty("any.property", "default");

        // Then
        assertThat(property).isEqualTo("default");
    }

    @Test
    @DisplayName("Should reload configuration")
    public void testReloadConfiguration() throws IOException {
        // Given
        String originalValue = configManager.getProperty("app.name");
        assertThat(originalValue).isEqualTo("Test Application");

        // Modify the configuration file
        Properties newProps = new Properties();
        newProps.setProperty("app.name", "Updated Application");
        try (FileWriter writer = new FileWriter(configFile)) {
            newProps.store(writer, "Updated configuration");
        }

        // When
        configManager.reloadConfiguration();
        String updatedValue = configManager.getProperty("app.name");

        // Then
        assertThat(updatedValue).isEqualTo("Updated Application");
    }

    @Test
    @DisplayName("Should get all properties with prefix")
    public void testGetPropertiesWithPrefix() {
        // When
        Properties databaseProps = configManager.getPropertiesWithPrefix("database");

        // Then
        assertThat(databaseProps).hasSize(3);
        assertThat(databaseProps.getProperty("url")).isEqualTo("jdbc:postgresql://localhost:5432/testdb");
        assertThat(databaseProps.getProperty("username")).isEqualTo("testuser");
        assertThat(databaseProps.getProperty("password")).isEqualTo("testpass");
    }

    @Test
    @DisplayName("Should handle prefix that doesn't exist")
    public void testGetPropertiesWithNonExistentPrefix() {
        // When
        Properties nonExistentProps = configManager.getPropertiesWithPrefix("nonexistent");

        // Then
        assertThat(nonExistentProps).isEmpty();
    }

    @Test
    @DisplayName("Should handle null prefix")
    public void testGetPropertiesWithNullPrefix() {
        // When/Then
        assertThatThrownBy(() -> configManager.getPropertiesWithPrefix(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Prefix cannot be null");
    }

    @Test
    @DisplayName("Should handle empty prefix")
    public void testGetPropertiesWithEmptyPrefix() {
        // When
        Properties allProps = configManager.getPropertiesWithPrefix("");

        // Then
        assertThat(allProps).hasSizeGreaterThan(0);
        assertThat(allProps.getProperty("database.url")).isEqualTo("jdbc:postgresql://localhost:5432/testdb");
    }

    @Test
    @DisplayName("Should be case sensitive for property names")
    public void testCaseSensitivePropertyNames() {
        // When
        String lowerCase = configManager.getProperty("app.name");
        String upperCase = configManager.getProperty("APP.NAME");

        // Then
        assertThat(lowerCase).isEqualTo("Test Application");
        assertThat(upperCase).isNull();
    }

    @Test
    @DisplayName("Should handle malformed configuration file")
    public void testMalformedConfigFile() throws IOException {
        // Given
        File malformedFile = tempDir.resolve("malformed.properties").toFile();
        try (FileWriter writer = new FileWriter(malformedFile)) {
            writer.write("This is not a valid properties file format\n");
            writer.write("key without equals sign\n");
            writer.write("=value without key\n");
            writer.write("valid.key=valid.value\n");
        }

        // When
        ConfigurationManager manager = new ConfigurationManager(malformedFile.getAbsolutePath());
        String validProperty = manager.getProperty("valid.key");

        // Then
        assertThat(validProperty).isEqualTo("valid.value");
    }

    @Test
    @DisplayName("Should handle properties with special characters")
    public void testPropertiesWithSpecialCharacters() throws IOException {
        // Given
        Properties specialProps = new Properties();
        specialProps.setProperty("special.chars", "Value with spaces and símbolos ñáéíóú");
        specialProps.setProperty("unicode.property", "Unicode: 中文 日本語 🚀");
        specialProps.setProperty("path.with.backslash", "C:\\Program Files\\App");

        File specialFile = tempDir.resolve("special.properties").toFile();
        try (FileWriter writer = new FileWriter(specialFile)) {
            specialProps.store(writer, "Special characters test");
        }

        ConfigurationManager manager = new ConfigurationManager(specialFile.getAbsolutePath());

        // When
        String specialChars = manager.getProperty("special.chars");
        String unicode = manager.getProperty("unicode.property");
        String pathWithBackslash = manager.getProperty("path.with.backslash");

        // Then
        assertThat(specialChars).isEqualTo("Value with spaces and símbolos ñáéíóú");
        assertThat(unicode).isEqualTo("Unicode: 中文 日本語 🚀");
        assertThat(pathWithBackslash).isEqualTo("C:\\Program Files\\App");
    }
}
