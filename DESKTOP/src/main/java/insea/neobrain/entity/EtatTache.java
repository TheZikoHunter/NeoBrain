package insea.neobrain.entity;

/**
 * Enumeration for inventory task states
 */
public enum EtatTache {
    EN_ATTENTE("En attente"),
    EN_COURS("En cours"),
    TERMINEE("Terminée"),
    ANNULEE("Annulée");
    
    private final String label;
    
    EtatTache(String label) {
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
