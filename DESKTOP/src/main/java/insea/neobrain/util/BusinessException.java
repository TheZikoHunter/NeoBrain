package insea.neobrain.util;

/**
 * Custom exception for business logic errors
 */
public class BusinessException extends Exception {
    private final String errorCode;
    private final String userMessage;
    
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = message;
    }
    
    public BusinessException(String errorCode, String message, String userMessage) {
        super(message);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userMessage = message;
    }
    
    public BusinessException(String errorCode, String message, String userMessage, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getUserMessage() {
        return userMessage;
    }
    
    // Common business exception factory methods
    public static BusinessException validationError(String message) {
        return new BusinessException("VALIDATION_ERROR", message);
    }
    
    public static BusinessException entityNotFound(String entityType, Long id) {
        return new BusinessException("ENTITY_NOT_FOUND", 
            String.format("%s with ID %d not found", entityType, id),
            String.format("%s non trouvé", entityType));
    }
    
    public static BusinessException duplicateEntity(String entityType, String field, String value) {
        return new BusinessException("DUPLICATE_ENTITY", 
            String.format("%s with %s '%s' already exists", entityType, field, value),
            String.format("%s avec %s '%s' existe déjà", entityType, field, value));
    }
    
    public static BusinessException insufficientStock(String productName, int requested, int available) {
        return new BusinessException("INSUFFICIENT_STOCK", 
            String.format("Insufficient stock for product '%s': requested %d, available %d", 
                         productName, requested, available),
            String.format("Stock insuffisant pour le produit '%s': demandé %d, disponible %d", 
                         productName, requested, available));
    }
    
    public static BusinessException unauthorizedOperation(String operation, String role) {
        return new BusinessException("UNAUTHORIZED_OPERATION", 
            String.format("Operation '%s' not allowed for role '%s'", operation, role),
            String.format("Opération '%s' non autorisée pour le rôle '%s'", operation, role));
    }
    
    public static BusinessException invalidState(String entityType, String currentState, String operation) {
        return new BusinessException("INVALID_STATE", 
            String.format("Cannot perform operation '%s' on %s in state '%s'", 
                         operation, entityType, currentState),
            String.format("Impossible d'effectuer l'opération '%s' sur %s dans l'état '%s'", 
                         operation, entityType, currentState));
    }
}
