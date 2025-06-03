package insea.neobrain.entity;

/**
 * Enumeration for employee roles
 */
public enum Role {
    RESPONSABLE_STOCK("Responsable Stock"),
    EMPLOYE_STOCK("Employé Stock"),
    ADMIN("Administrateur");
    
    private final String label;
    
    Role(String label) {
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
