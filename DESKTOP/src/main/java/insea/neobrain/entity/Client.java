package insea.neobrain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class for Client
 */
@Entity
@Table(name = "client")
@PrimaryKeyJoinColumn(name = "id_personne")
public class Client extends Personne {
    
    @Column(name = "email_secondaire", length = 150)
    @Email(message = "Format d'email secondaire invalide")
    private String emailSecondaire;
    
    @Column(name = "est_fidele")
    private Boolean estFidele = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement")
    private ModePaiement modePaiement;
    
    @Column(name = "adresse", length = 500)
    @Size(max = 500, message = "L'adresse ne peut pas dépasser 500 caractères")
    private String adresse;
    
    @Column(name = "code_postal", length = 10)
    @Pattern(regexp = "^[0-9]{5}$", message = "Le code postal doit contenir 5 chiffres")
    private String codePostal;
    
    @Column(name = "ville", length = 100)
    @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
    private String ville;
    
    @Column(name = "points_fidelite")
    @Min(value = 0, message = "Les points de fidélité ne peuvent pas être négatifs")
    private Integer pointsFidelite = 0;
    
    @Column(name = "limite_credit")
    @DecimalMin(value = "0.0", message = "La limite de crédit ne peut pas être négative")
    private Double limiteCredit;
    
    @Column(name = "credit_utilise")
    @DecimalMin(value = "0.0", message = "Le crédit utilisé ne peut pas être négatif")
    private Double creditUtilise = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_client")
    private TypeClient typeClient;

    public TypeClient getTypeClient() {
        return typeClient;
    }
    public void setTypeClient(TypeClient typeClient) {
        this.typeClient = typeClient;
    }
    
    // One-to-many relationship with CommandeVente
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommandeVente> commandes = new ArrayList<>();
    
    // Constructors
    public Client() {
        super();
        this.estFidele = false;
        this.pointsFidelite = 0;
        this.creditUtilise = 0.0;
    }
    
    public Client(Civilite civilite, String nom, String prenom, String email) {
        super(civilite, nom, prenom);
        this.setEmail(email);
        this.estFidele = false;
        this.pointsFidelite = 0;
        this.creditUtilise = 0.0;
    }
    
    // Getters and Setters
    public String getEmailSecondaire() {
        return emailSecondaire;
    }
    
    public void setEmailSecondaire(String emailSecondaire) {
        this.emailSecondaire = emailSecondaire;
    }
    
    public Boolean getEstFidele() {
        return estFidele;
    }
    
    public void setEstFidele(Boolean estFidele) {
        this.estFidele = estFidele;
    }
    
    public ModePaiement getModePaiement() {
        return modePaiement;
    }
    
    public void setModePaiement(ModePaiement modePaiement) {
        this.modePaiement = modePaiement;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public String getCodePostal() {
        return codePostal;
    }
    
    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }
    
    public String getVille() {
        return ville;
    }
    
    public void setVille(String ville) {
        this.ville = ville;
    }
    
    public Integer getPointsFidelite() {
        return pointsFidelite;
    }
    
    public void setPointsFidelite(Integer pointsFidelite) {
        this.pointsFidelite = pointsFidelite;
    }
    
    public Double getLimiteCredit() {
        return limiteCredit;
    }
    
    public void setLimiteCredit(Double limiteCredit) {
        this.limiteCredit = limiteCredit;
    }
    
    public Double getCreditUtilise() {
        return creditUtilise;
    }
    
    public void setCreditUtilise(Double creditUtilise) {
        this.creditUtilise = creditUtilise;
    }
    
    public List<CommandeVente> getCommandes() {
        return commandes;
    }
    
    public void setCommandes(List<CommandeVente> commandes) {
        this.commandes = commandes;
    }
    
    // Utility methods
    public String getAdresseComplete() {
        StringBuilder adresseComplete = new StringBuilder();
        if (adresse != null && !adresse.trim().isEmpty()) {
            adresseComplete.append(adresse);
        }
        if (codePostal != null && !codePostal.trim().isEmpty()) {
            adresseComplete.append(", ").append(codePostal);
        }
        if (ville != null && !ville.trim().isEmpty()) {
            adresseComplete.append(" ").append(ville);
        }
        return adresseComplete.toString();
    }
    
    public Double getCreditDisponible() {
        if (limiteCredit == null) {
            return 0.0;
        }
        return limiteCredit - (creditUtilise != null ? creditUtilise : 0.0);
    }
    
    public boolean peutUtiliserCredit(Double montant) {
        return getCreditDisponible() >= montant;
    }
    
    public void ajouterPointsFidelite(Integer points) {
        if (points > 0) {
            this.pointsFidelite = (this.pointsFidelite != null ? this.pointsFidelite : 0) + points;
        }
    }
    
    public void utiliserPointsFidelite(Integer points) {
        if (points > 0 && this.pointsFidelite != null && this.pointsFidelite >= points) {
            this.pointsFidelite -= points;
        }
    }
    
    public void ajouterCommande(CommandeVente commande) {
        commandes.add(commande);
        commande.setClient(this);
    }
    
    public void retirerCommande(CommandeVente commande) {
        commandes.remove(commande);
        commande.setClient(null);
    }
    
    public boolean isClientFidele() {
        return Boolean.TRUE.equals(estFidele) && pointsFidelite != null && pointsFidelite > 100;
    }
    
    @Override
    public String toString() {
        return "Client{" +
                "idPersonne=" + getIdPersonne() +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", estFidele=" + estFidele +
                ", pointsFidelite=" + pointsFidelite +
                '}';
    }
}
