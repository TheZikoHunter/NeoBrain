package insea.neobrain.entity;

/**
 * Enumeration for complaint/reclamation types
 */
public enum TypeReclamation {
    RETOUR("Retour"),
    ECHEC_RECEPTION("Échec de réception");
    
    private final String label;
    
    TypeReclamation(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String getDisplayName() {
        return label;
    }
    
    @Override
    public String toString() {
        return label;
    }
}
