package insea.neobrain.entity;

/**
 * Enumeration for product categories
 */
public enum CategorieProduit {
    INFORMATIQUE("Informatique"),
    ELECTROMENAGER("Électroménager"),
    VETEMENTS("Vêtements"),
    SPORTS("Sports");
    
    private final String label;
    
    CategorieProduit(String label) {
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
