package insea.neobrain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity class for LigneCommande (Order Line)
 */
@Entity
@Table(name = "ligne_commande")
public class LigneCommande {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ligne_vente")
    private Long idLigneVente;
    
    @Column(name = "quantite_vente", nullable = false)
    @NotNull(message = "La quantité de vente est obligatoire")
    @Min(value = 1, message = "La quantité doit être au moins 1")
    private Integer quantiteVente;
    
    @Column(name = "prix_unitaire", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Le prix unitaire est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix unitaire doit être positif")
    private BigDecimal prixUnitaire;
    
    @Column(name = "sous_total", precision = 10, scale = 2)
    private BigDecimal sousTotal;
    
    @Column(name = "remise_ligne", precision = 8, scale = 2)
    @DecimalMin(value = "0.0", message = "La remise ne peut pas être négative")
    private BigDecimal remiseLigne = BigDecimal.ZERO;
    
    @Column(name = "etat_echec")
    private Boolean etatEchec = false;
    
    @Column(name = "etat_retour")
    private Boolean etatRetour = false;
    
    @Column(name = "quantite_livree")
    @Min(value = 0, message = "La quantité livrée ne peut pas être négative")
    private Integer quantiteLivree = 0;
    
    @Column(name = "quantite_retournee")
    @Min(value = 0, message = "La quantité retournée ne peut pas être négative")
    private Integer quantiteRetournee = 0;
    
    @Column(name = "commentaire", length = 500)
    @Size(max = 500, message = "Le commentaire ne peut pas dépasser 500 caractères")
    private String commentaire;
    
    @Column(name = "statut", length = 50)
    private String statut = "EN_ATTENTE";
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    // Many-to-one relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_commande_vente", nullable = false)
    @NotNull(message = "La commande de vente est obligatoire")
    private CommandeVente commandeVente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produit", nullable = false)
    @NotNull(message = "Le produit est obligatoire")
    private Produit produit;
    
    // One-to-many relationship with Reclamation
    @OneToMany(mappedBy = "ligneCommande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reclamation> reclamations = new ArrayList<>();
    
    // Constructors
    public LigneCommande() {
        this.dateCreation = LocalDateTime.now();
        this.etatEchec = false;
        this.etatRetour = false;
        this.quantiteLivree = 0;
        this.quantiteRetournee = 0;
        this.remiseLigne = BigDecimal.ZERO;
        this.statut = "EN_ATTENTE";
    }
    
    public LigneCommande(CommandeVente commandeVente, Produit produit, Integer quantiteVente) {
        this();
        this.commandeVente = commandeVente;
        this.produit = produit;
        this.quantiteVente = quantiteVente;
        if (produit != null) {
            this.prixUnitaire = produit.getPrix();
        }
        calculerSousTotal();
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        calculerSousTotal();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
        calculerSousTotal();
    }
    
    // Calculate subtotal
    public void calculerSousTotal() {
        if (prixUnitaire != null && quantiteVente != null) {
            BigDecimal total = prixUnitaire.multiply(BigDecimal.valueOf(quantiteVente));
            if (remiseLigne != null && remiseLigne.compareTo(BigDecimal.ZERO) > 0) {
                total = total.subtract(remiseLigne);
            }
            this.sousTotal = total;
        } else {
            this.sousTotal = BigDecimal.ZERO;
        }
    }
    
    // Getters and Setters
    public Long getIdLigneVente() {
        return idLigneVente;
    }
    
    public void setIdLigneVente(Long idLigneVente) {
        this.idLigneVente = idLigneVente;
    }
    
    public Integer getQuantiteVente() {
        return quantiteVente;
    }
    
    public void setQuantiteVente(Integer quantiteVente) {
        this.quantiteVente = quantiteVente;
        calculerSousTotal();
    }
    
    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }
    
    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
        calculerSousTotal();
    }
    
    public BigDecimal getSousTotal() {
        return sousTotal;
    }
    
    public void setSousTotal(BigDecimal sousTotal) {
        this.sousTotal = sousTotal;
    }
    
    public BigDecimal getRemiseLigne() {
        return remiseLigne;
    }
    
    public void setRemiseLigne(BigDecimal remiseLigne) {
        this.remiseLigne = remiseLigne;
        calculerSousTotal();
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
    
    public Boolean getEtatRetour() {
        return etatRetour;
    }
    
    public void setEtatRetour(Boolean etatRetour) {
        this.etatRetour = etatRetour;
        if (Boolean.TRUE.equals(etatRetour)) {
            this.statut = "RETOUR";
        }
    }
    
    public Integer getQuantiteLivree() {
        return quantiteLivree;
    }
    
    public void setQuantiteLivree(Integer quantiteLivree) {
        this.quantiteLivree = quantiteLivree;
    }
    
    public Integer getQuantiteRetournee() {
        return quantiteRetournee;
    }
    
    public void setQuantiteRetournee(Integer quantiteRetournee) {
        this.quantiteRetournee = quantiteRetournee;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
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
    
    public CommandeVente getCommandeVente() {
        return commandeVente;
    }
    
    public void setCommandeVente(CommandeVente commandeVente) {
        this.commandeVente = commandeVente;
    }
    
    public Produit getProduit() {
        return produit;
    }
    
    public void setProduit(Produit produit) {
        this.produit = produit;
        if (produit != null && this.prixUnitaire == null) {
            this.prixUnitaire = produit.getPrix();
            calculerSousTotal();
        }
    }
    
    public List<Reclamation> getReclamations() {
        return reclamations;
    }
    
    public void setReclamations(List<Reclamation> reclamations) {
        this.reclamations = reclamations;
    }
    
    // Utility methods
    public boolean isPartiallyDelivered() {
        return quantiteLivree != null && quantiteLivree > 0 && 
               quantiteLivree < quantiteVente;
    }
    
    public boolean isFullyDelivered() {
        return quantiteLivree != null && quantiteVente != null && 
               quantiteLivree.equals(quantiteVente);
    }
    
    public boolean hasReturns() {
        return quantiteRetournee != null && quantiteRetournee > 0;
    }
    
    public Integer getQuantiteRestanteALivrer() {
        if (quantiteVente == null) return 0;
        int livree = quantiteLivree != null ? quantiteLivree : 0;
        return quantiteVente - livree;
    }
    
    public boolean canBeReturned() {
        return isFullyDelivered() && !Boolean.TRUE.equals(etatRetour) && 
               !Boolean.TRUE.equals(etatEchec);
    }
    
    public void reserverStock() {
        if (produit != null && quantiteVente != null) {
            // In a real implementation, this would reserve stock
            // For now, we just mark the status
            this.statut = "STOCK_RESERVE";
        }
    }
    
    public void libererStock() {
        if (produit != null && quantiteVente != null) {
            // In a real implementation, this would release reserved stock
            this.statut = "STOCK_LIBERE";
        }
    }
    
    public void confirmerVente() {
        if (produit != null && quantiteVente != null) {
            // Update product stock
            produit.ajusterStock(-quantiteVente);
            this.quantiteLivree = quantiteVente;
            this.statut = "LIVRE";
        }
    }
    
    public void processReturn(Integer quantiteARetourner, String motif) {
        if (quantiteARetourner != null && quantiteARetourner > 0 && 
            canBeReturned() && quantiteARetourner <= quantiteLivree) {
            
            this.quantiteRetournee = (this.quantiteRetournee != null ? this.quantiteRetournee : 0) + quantiteARetourner;
            this.etatRetour = true;
            this.commentaire = motif;
            this.statut = "RETOUR_PARTIEL";
            
            if (this.quantiteRetournee.equals(this.quantiteLivree)) {
                this.statut = "RETOUR_COMPLET";
            }
            
            // Restore stock
            if (produit != null) {
                produit.ajusterStock(quantiteARetourner);
            }
        }
    }
    
    public void marquerEchec(String motif) {
        this.etatEchec = true;
        this.statut = "ECHEC";
        this.commentaire = motif;
        libererStock(); // Release any reserved stock
    }
    
    // Relationship helper methods
    public void addReclamation(Reclamation reclamation) {
        reclamations.add(reclamation);
        reclamation.setLigneCommande(this);
    }
    
    public void removeReclamation(Reclamation reclamation) {
        reclamations.remove(reclamation);
        reclamation.setLigneCommande(null);
    }
    
    public boolean hasActiveReclamations() {
        return reclamations.stream()
                .anyMatch(r -> EtatReclamation.EN_ATTENTE.equals(r.getEtatReclamation()));
    }
    
    // equals, hashCode, toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LigneCommande that = (LigneCommande) obj;
        return Objects.equals(idLigneVente, that.idLigneVente);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idLigneVente);
    }
    
    @Override
    public String toString() {
        return "LigneCommande{" +
                "idLigneVente=" + idLigneVente +
                ", quantiteVente=" + quantiteVente +
                ", prixUnitaire=" + prixUnitaire +
                ", sousTotal=" + sousTotal +
                ", produit=" + (produit != null ? produit.getNom() : "null") +
                ", statut='" + statut + '\'' +
                '}';
    }
}
