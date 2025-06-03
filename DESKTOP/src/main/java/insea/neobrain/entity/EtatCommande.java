package insea.neobrain.entity;

/**
 * Enumeration for order states
 */
public enum EtatCommande {
    BROUILLON("Brouillon"),
    EN_ATTENTE("En attente"),
    VALIDEE("Validée"),
    EXPEDIEE("Expédiée"),
    LIVREE("Livrée"),
    ECHEC("Échec"),
    ANNULEE("Annulée");
    
    private final String label;
    
    EtatCommande(String label) {
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
