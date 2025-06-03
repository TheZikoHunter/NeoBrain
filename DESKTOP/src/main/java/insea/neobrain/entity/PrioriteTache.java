package insea.neobrain.entity;

/**
 * Enumeration for task priorities
 */
public enum PrioriteTache {
    BASSE("Basse"),
    NORMALE("Normale"),
    HAUTE("Haute"),
    URGENTE("Urgente");
    
    private final String label;
    
    PrioriteTache(String label) {
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
