package insea.neobrain.entity;

/**
 * Enumeration for client types
 */
public enum TypeClient {
    PARTICULIER("Particulier"),
    ENTREPRISE("Entreprise"),
    ADMINISTRATION("Administration");
    
    private final String label;
    
    TypeClient(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
    
    @Override
    public String toString() {
        return label;
    }
}
