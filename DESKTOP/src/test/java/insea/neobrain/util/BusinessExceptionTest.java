package insea.neobrain.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for BusinessException class
 */
public class BusinessExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    public void testCreateExceptionWithMessage() {
        // Given
        String message = "Test error message";

        // When
        BusinessException exception = new BusinessException(message);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("Should create exception with message and cause")
    public void testCreateExceptionWithMessageAndCause() {
        // Given
        String message = "Test error message";
        RuntimeException cause = new RuntimeException("Root cause");

        // When
        BusinessException exception = new BusinessException(message, cause);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("Should create validation error")
    public void testCreateValidationError() {
        // Given
        String field = "email";
        String reason = "Invalid format";

        // When
        BusinessException exception = BusinessException.validationError(field, reason);

        // Then
        assertThat(exception.getMessage()).isEqualTo("Erreur de validation pour le champ 'email': Invalid format");
    }

    @Test
    @DisplayName("Should create entity not found error")
    public void testCreateEntityNotFoundError() {
        // Given
        String entityType = "Personnel";
        String identifier = "123";

        // When
        BusinessException exception = BusinessException.entityNotFound(entityType, identifier);

        // Then
        assertThat(exception.getMessage()).isEqualTo("Personnel avec l'identifiant '123' introuvable");
    }

    @Test
    @DisplayName("Should create duplicate entity error")
    public void testCreateDuplicateEntityError() {
        // Given
        String entityType = "Client";
        String field = "email";
        String value = "john@example.com";

        // When
        BusinessException exception = BusinessException.duplicateEntity(entityType, field, value);

        // Then
        assertThat(exception.getMessage()).isEqualTo("Client avec email 'john@example.com' existe déjà");
    }

    @Test
    @DisplayName("Should create insufficient stock error")
    public void testCreateInsufficientStockError() {
        // Given
        String productName = "Laptop";
        int requested = 10;
        int available = 5;

        // When
        BusinessException exception = BusinessException.insufficientStock(productName, requested, available);

        // Then
        assertThat(exception.getMessage()).isEqualTo("Stock insuffisant pour 'Laptop': demandé 10, disponible 5");
    }

    @Test
    @DisplayName("Should create unauthorized access error")
    public void testCreateUnauthorizedAccessError() {
        // Given
        String action = "DELETE_USER";

        // When
        BusinessException exception = BusinessException.unauthorizedAccess(action);

        // Then
        assertThat(exception.getMessage()).isEqualTo("Accès non autorisé pour l'action: DELETE_USER");
    }

    @Test
    @DisplayName("Should create business rule violation error")
    public void testCreateBusinessRuleViolationError() {
        // Given
        String rule = "Minimum order amount";
        String details = "Order must be at least 50€";

        // When
        BusinessException exception = BusinessException.businessRuleViolation(rule, details);

        // Then
        assertThat(exception.getMessage()).isEqualTo("Violation de règle métier 'Minimum order amount': Order must be at least 50€");
    }

    @Test
    @DisplayName("Should create data integrity error")
    public void testCreateDataIntegrityError() {
        // Given
        String details = "Foreign key constraint violation";

        // When
        BusinessException exception = BusinessException.dataIntegrityError(details);

        // Then
        assertThat(exception.getMessage()).isEqualTo("Erreur d'intégrité des données: Foreign key constraint violation");
    }

    @Test
    @DisplayName("Should handle null parameters gracefully")
    public void testHandleNullParameters() {
        // When/Then
        assertThatCode(() -> {
            BusinessException.validationError(null, null);
            BusinessException.entityNotFound(null, null);
            BusinessException.duplicateEntity(null, null, null);
            BusinessException.insufficientStock(null, 0, 0);
            BusinessException.unauthorizedAccess(null);
            BusinessException.businessRuleViolation(null, null);
            BusinessException.dataIntegrityError(null);
        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle empty string parameters")
    public void testHandleEmptyStringParameters() {
        // When
        BusinessException validationEx = BusinessException.validationError("", "");
        BusinessException entityNotFoundEx = BusinessException.entityNotFound("", "");
        BusinessException duplicateEx = BusinessException.duplicateEntity("", "", "");

        // Then
        assertThat(validationEx.getMessage()).contains("Erreur de validation");
        assertThat(entityNotFoundEx.getMessage()).contains("introuvable");
        assertThat(duplicateEx.getMessage()).contains("existe déjà");
    }

    @Test
    @DisplayName("Should handle special characters in parameters")
    public void testHandleSpecialCharacters() {
        // Given
        String specialField = "email_ñáéíóú";
        String specialReason = "Format invalide: @#$%^&*()";

        // When
        BusinessException exception = BusinessException.validationError(specialField, specialReason);

        // Then
        assertThat(exception.getMessage()).contains(specialField);
        assertThat(exception.getMessage()).contains(specialReason);
    }

    @Test
    @DisplayName("Should handle very long parameters")
    public void testHandleLongParameters() {
        // Given
        String longString = "a".repeat(1000);

        // When
        BusinessException exception = BusinessException.validationError(longString, longString);

        // Then
        assertThat(exception.getMessage()).contains(longString);
    }

    @Test
    @DisplayName("Should handle negative numbers in insufficient stock")
    public void testHandleNegativeNumbers() {
        // Given
        String productName = "Test Product";
        int requested = -5;
        int available = -10;

        // When
        BusinessException exception = BusinessException.insufficientStock(productName, requested, available);

        // Then
        assertThat(exception.getMessage()).contains("demandé -5");
        assertThat(exception.getMessage()).contains("disponible -10");
    }

    @Test
    @DisplayName("Should be throwable")
    public void testExceptionIsThrowable() {
        // When/Then
        assertThatThrownBy(() -> {
            throw BusinessException.validationError("test", "test error");
        })
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("test error");
    }

    @Test
    @DisplayName("Should maintain stack trace")
    public void testMaintainStackTrace() {
        // Given
        RuntimeException cause = new RuntimeException("Original cause");

        // When
        BusinessException exception = new BusinessException("Business error", cause);

        // Then
        assertThat(exception.getCause()).isEqualTo(cause);
        assertThat(exception.getStackTrace()).isNotEmpty();
    }

    @Test
    @DisplayName("Should support method chaining for factory methods")
    public void testFactoryMethodsReturnBusinessException() {
        // When
        BusinessException ex1 = BusinessException.validationError("field", "reason");
        BusinessException ex2 = BusinessException.entityNotFound("Entity", "123");
        BusinessException ex3 = BusinessException.duplicateEntity("Entity", "field", "value");

        // Then
        assertThat(ex1).isInstanceOf(BusinessException.class);
        assertThat(ex2).isInstanceOf(BusinessException.class);
        assertThat(ex3).isInstanceOf(BusinessException.class);
    }
}
