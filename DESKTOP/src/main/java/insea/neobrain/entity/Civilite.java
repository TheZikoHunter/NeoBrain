package insea.neobrain.entity;

/**
 * Enumeration for civility titles
 */
public enum Civilite {
    M("Monsieur"),
    MLLE("Mademoiselle"), 
    MME("Madame");
    
    private final String label;
    
    Civilite(String label) {
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
