package insea.neobrain.entity;

/**
 * Enumeration for payment methods
 */
public enum ModePaiement {
    CHEQUE("Chèque"),
    ESPECE("Espèce"),
    CARTE("Carte bancaire");
    
    private final String label;
    
    ModePaiement(String label) {
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
