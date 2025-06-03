package insea.neobrain.entity;

/**
 * Enumeration for nationalities
 */
public enum Nationalite {
    MAROCCAINE("Marocaine"),
    FRANCAISE("Française"),
    AMERICAINE("Américaine");
    
    private final String label;
    
    Nationalite(String label) {
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
