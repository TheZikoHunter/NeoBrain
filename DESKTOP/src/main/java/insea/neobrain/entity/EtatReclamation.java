package insea.neobrain.entity;

/**
 * Enumeration for complaint/reclamation states
 */
public enum EtatReclamation {
    EN_ATTENTE("En attente"),
    VALIDEE("Validée"),
    REFUSEE("Refusée");
    
    private final String label;
    
    EtatReclamation(String label) {
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
