package insea.neobrain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity class for CommandeVente (Sales Order)
 */
@Entity
@Table(name = "commande_vente")
public class CommandeVente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_commande_vente")
    private Long idCommandeVente;
    
    @Column(name = "numero_commande", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Le numéro de commande est obligatoire")
    private String numeroCommande;
    
    @Column(name = "date_commande_vente", nullable = false)
    @NotNull(message = "La date de commande est obligatoire")
    private LocalDate dateCommandeVente;
    
    @Column(name = "prix_total", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Le prix total est obligatoire")
    @DecimalMin(value = "0.0", message = "Le prix total ne peut pas être négatif")
    private BigDecimal prixTotal;
    
    @Column(name = "est_valide")
    private Boolean estValide = false;
    
    @Column(name = "est_expediee")
    private Boolean estExpediee = false;
    
    @Column(name = "etat_echec")
    private Boolean etatEchec = false;
    
    @Column(name = "date_validation")
    private LocalDateTime dateValidation;
    
    @Column(name = "date_expedition")
    private LocalDateTime dateExpedition;
    
    @Column(name = "date_livraison_prevue")
    private LocalDate dateLivraisonPrevue;
    
    @Column(name = "date_livraison_effective")
    private LocalDate dateLivraisonEffective;
    
    @Column(name = "commentaire", length = 500)
    @Size(max = 500, message = "Le commentaire ne peut pas dépasser 500 caractères")
    private String commentaire;
    
    @Column(name = "adresse_livraison", length = 500)
    @Size(max = 500, message = "L'adresse de livraison ne peut pas dépasser 500 caractères")
    private String adresseLivraison;
    
    @Column(name = "frais_livraison", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "Les frais de livraison ne peuvent pas être négatifs")
    private BigDecimal fraisLivraison = BigDecimal.ZERO;
    
    @Column(name = "remise", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "La remise ne peut pas être négative")
    private BigDecimal remise = BigDecimal.ZERO;
    
    @Column(name = "tva", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "La TVA ne peut pas être négative")
    private BigDecimal tva = BigDecimal.ZERO;
    
    @Column(name = "total_ttc", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", message = "Le total TTC ne peut pas être négatif")
    private BigDecimal totalTTC;
    
    @Column(name = "statut", length = 50)
    private String statut = "BROUILLON";
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    // Many-to-one relationship with Client
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client", nullable = false)
    @NotNull(message = "Le client est obligatoire")
    private Client client;
    
    // One-to-many relationship with LigneCommande
    @OneToMany(mappedBy = "commandeVente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LigneCommande> lignesCommande = new ArrayList<>();
    
    // Constructors
    public CommandeVente() {
        this.dateCreation = LocalDateTime.now();
        this.dateCommandeVente = LocalDate.now();
        this.estValide = false;
        this.estExpediee = false;
        this.etatEchec = false;
        this.prixTotal = BigDecimal.ZERO;
        this.fraisLivraison = BigDecimal.ZERO;
        this.remise = BigDecimal.ZERO;
        this.tva = BigDecimal.ZERO;
        this.statut = "BROUILLON";
    }
    
    public CommandeVente(Client client) {
        this();
        this.client = client;
        this.generateNumeroCommande();
        if (client != null && client.getAdresseComplete() != null) {
            this.adresseLivraison = client.getAdresseComplete();
        }
    }
    
    // Generate order number
    private void generateNumeroCommande() {
        String year = String.valueOf(LocalDate.now().getYear());
        String month = String.format("%02d", LocalDate.now().getMonthValue());
        long timestamp = System.currentTimeMillis() % 100000;
        this.numeroCommande = "CMD" + year + month + timestamp;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        if (this.numeroCommande == null) {
            generateNumeroCommande();
        }
        calculerTotaux();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
        calculerTotaux();
    }
    
    // Calculate totals
    public void calculerTotaux() {
        if (lignesCommande != null && !lignesCommande.isEmpty()) {
            this.prixTotal = lignesCommande.stream()
                    .map(LigneCommande::getSousTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            this.prixTotal = BigDecimal.ZERO;
        }
        
        // Calculate total TTC
        BigDecimal montantAvantTaxes = prixTotal.subtract(remise != null ? remise : BigDecimal.ZERO);
        BigDecimal montantTVA = tva != null ? tva : BigDecimal.ZERO;
        BigDecimal frais = fraisLivraison != null ? fraisLivraison : BigDecimal.ZERO;
        this.totalTTC = montantAvantTaxes.add(montantTVA).add(frais);
    }
    
    // Getters and Setters
    public Long getIdCommandeVente() {
        return idCommandeVente;
    }
    
    public void setIdCommandeVente(Long idCommandeVente) {
        this.idCommandeVente = idCommandeVente;
    }
    
    public String getNumeroCommande() {
        return numeroCommande;
    }
    
    public void setNumeroCommande(String numeroCommande) {
        this.numeroCommande = numeroCommande;
    }
    
    public LocalDate getDateCommandeVente() {
        return dateCommandeVente;
    }
    
    public void setDateCommandeVente(LocalDate dateCommandeVente) {
        this.dateCommandeVente = dateCommandeVente;
    }
    
    public BigDecimal getPrixTotal() {
        return prixTotal;
    }
    
    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }
    
    public Boolean getEstValide() {
        return estValide;
    }
    
    public void setEstValide(Boolean estValide) {
        this.estValide = estValide;
        if (Boolean.TRUE.equals(estValide)) {
            this.dateValidation = LocalDateTime.now();
            this.statut = "VALIDEE";
        }
    }
    
    public Boolean getEstExpediee() {
        return estExpediee;
    }
    
    public void setEstExpediee(Boolean estExpediee) {
        this.estExpediee = estExpediee;
        if (Boolean.TRUE.equals(estExpediee)) {
            this.dateExpedition = LocalDateTime.now();
            this.statut = "EXPEDIEE";
        }
    }
    
    public Boolean getEtatEchec() {
        return etatEchec;
    }
    
    public void setEtatEchec(Boolean etatEchec) {
        this.etatEchec = etatEchec;
        if (Boolean.TRUE.equals(etatEchec)) {
            this.statut = "ECHEC";
        }
    }
    
    public LocalDateTime getDateValidation() {
        return dateValidation;
    }
    
    public void setDateValidation(LocalDateTime dateValidation) {
        this.dateValidation = dateValidation;
    }
    
    public LocalDateTime getDateExpedition() {
        return dateExpedition;
    }
    
    public void setDateExpedition(LocalDateTime dateExpedition) {
        this.dateExpedition = dateExpedition;
    }
    
    public LocalDate getDateLivraisonPrevue() {
        return dateLivraisonPrevue;
    }
    
    public void setDateLivraisonPrevue(LocalDate dateLivraisonPrevue) {
        this.dateLivraisonPrevue = dateLivraisonPrevue;
    }
    
    public LocalDate getDateLivraisonEffective() {
        return dateLivraisonEffective;
    }
    
    public void setDateLivraisonEffective(LocalDate dateLivraisonEffective) {
        this.dateLivraisonEffective = dateLivraisonEffective;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    public String getAdresseLivraison() {
        return adresseLivraison;
    }
    
    public void setAdresseLivraison(String adresseLivraison) {
        this.adresseLivraison = adresseLivraison;
    }
    
    public BigDecimal getFraisLivraison() {
        return fraisLivraison;
    }
    
    public void setFraisLivraison(BigDecimal fraisLivraison) {
        this.fraisLivraison = fraisLivraison;
    }
    
    public BigDecimal getRemise() {
        return remise;
    }
    
    public void setRemise(BigDecimal remise) {
        this.remise = remise;
    }
    
    public BigDecimal getTva() {
        return tva;
    }
    
    public void setTva(BigDecimal tva) {
        this.tva = tva;
    }
    
    public BigDecimal getTotalTTC() {
        return totalTTC;
    }
    
    public void setTotalTTC(BigDecimal totalTTC) {
        this.totalTTC = totalTTC;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDateTime getDateModification() {
        return dateModification;
    }
    
    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }
    
    public Client getClient() {
        return client;
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
    
    public List<LigneCommande> getLignesCommande() {
        return lignesCommande;
    }
    
    public void setLignesCommande(List<LigneCommande> lignesCommande) {
        this.lignesCommande = lignesCommande;
    }
    
    // Utility methods
    public boolean isModifiable() {
        return "BROUILLON".equals(statut) && !Boolean.TRUE.equals(estValide);
    }
    
    public boolean isPretePourValidation() {
        return lignesCommande != null && !lignesCommande.isEmpty() && 
               client != null && prixTotal != null && prixTotal.compareTo(BigDecimal.ZERO) > 0;
    }
    
    public boolean isPretePourExpedition() {
        return Boolean.TRUE.equals(estValide) && !Boolean.TRUE.equals(estExpediee) && 
               !Boolean.TRUE.equals(etatEchec);
    }
    
    public void valider() {
        if (isPretePourValidation()) {
            this.estValide = true;
            this.dateValidation = LocalDateTime.now();
            this.statut = "VALIDEE";
            
            // Reserve stock for each line
            if (lignesCommande != null) {
                for (LigneCommande ligne : lignesCommande) {
                    ligne.reserverStock();
                }
            }
        } else {
            throw new IllegalStateException("La commande n'est pas prête pour validation");
        }
    }
    
    public void expedier() {
        if (isPretePourExpedition()) {
            this.estExpediee = true;
            this.dateExpedition = LocalDateTime.now();
            this.statut = "EXPEDIEE";
            
            // Update stock for each line
            if (lignesCommande != null) {
                for (LigneCommande ligne : lignesCommande) {
                    ligne.confirmerVente();
                }
            }
        } else {
            throw new IllegalStateException("La commande n'est pas prête pour expédition");
        }
    }
    
    public void marquerEchec(String motif) {
        this.etatEchec = true;
        this.statut = "ECHEC";
        this.commentaire = motif;
        
        // Release reserved stock
        if (lignesCommande != null) {
            for (LigneCommande ligne : lignesCommande) {
                ligne.libererStock();
            }
        }
    }
    
    public void ajouterLigne(LigneCommande ligne) {
        lignesCommande.add(ligne);
        ligne.setCommandeVente(this);
        calculerTotaux();
    }
    
    public void retirerLigne(LigneCommande ligne) {
        lignesCommande.remove(ligne);
        ligne.setCommandeVente(null);
        calculerTotaux();
    }
    
    public int getNombreArticles() {
        return lignesCommande != null ? lignesCommande.size() : 0;
    }
    
    public int getQuantiteTotale() {
        return lignesCommande != null ? 
               lignesCommande.stream().mapToInt(l -> l.getQuantiteVente() != null ? l.getQuantiteVente() : 0).sum() : 0;
    }
    
    // equals, hashCode, toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CommandeVente that = (CommandeVente) obj;
        return Objects.equals(idCommandeVente, that.idCommandeVente);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idCommandeVente);
    }
    
    @Override
    public String toString() {
        return "CommandeVente{" +
                "idCommandeVente=" + idCommandeVente +
                ", numeroCommande='" + numeroCommande + '\'' +
                ", dateCommandeVente=" + dateCommandeVente +
                ", prixTotal=" + prixTotal +
                ", statut='" + statut + '\'' +
                ", client=" + (client != null ? client.getNomComplet() : "null") +
                '}';
    }
}
