package insea.neobrain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity class for TacheInventaire (Inventory Task)
 */
@Entity
@Table(name = "tache_inventaire")
public class TacheInventaire {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tache_inventaire")
    private Long idTacheInventaire;
    
    @Column(name = "date_tache", nullable = false)
    @NotNull(message = "La date de la tâche est obligatoire")
    private LocalDateTime dateTache;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "etat_tache", nullable = false)
    @NotNull(message = "L'état de la tâche est obligatoire")
    private EtatTache etatTache;
    
    @Column(name = "quantite_physique")
    @Min(value = 0, message = "La quantité physique ne peut pas être négative")
    private Integer quantitePhysique;
    
    @Column(name = "quantite_theorique")
    @Min(value = 0, message = "La quantité théorique ne peut pas être négative")
    private Integer quantiteTheorique;
    
    @Column(name = "commentaire", length = 500)
    @Size(max = 500, message = "Le commentaire ne peut pas dépasser 500 caractères")
    private String commentaire;
    
    @Column(name = "date_debut")
    private LocalDateTime dateDebut;
    
    @Column(name = "date_fin")
    private LocalDateTime dateFin;
    
    @Column(name = "duree_minutes")
    private Integer dureeMinutes;
    
    @Column(name = "priorite")
    @Min(value = 1, message = "La priorité doit être au minimum 1")
    @Max(value = 5, message = "La priorité doit être au maximum 5")
    private Integer priorite = 3;
    
    @Column(name = "emplacement_verifie", length = 100)
    private String emplacementVerifie;
    
    // Many-to-one relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personnel", nullable = false)
    @NotNull(message = "Le personnel assigné est obligatoire")
    private Personnel personnel;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produit", nullable = false)
    @NotNull(message = "Le produit est obligatoire")
    private Produit produit;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_inventaire", nullable = false)
    @NotNull(message = "L'inventaire est obligatoire")
    private Inventaire inventaire;
    
    // Constructors
    public TacheInventaire() {
        this.dateTache = LocalDateTime.now();
        this.etatTache = EtatTache.EN_ATTENTE;
        this.priorite = 3;
    }
    
    public TacheInventaire(Personnel personnel, Produit produit, Inventaire inventaire) {
        this();
        this.personnel = personnel;
        this.produit = produit;
        this.inventaire = inventaire;
        if (produit != null) {
            this.quantiteTheorique = produit.getQuantiteStock();
            this.emplacementVerifie = produit.getEmplacement();
        }
    }
    
    // Lifecycle callbacks
    @PreUpdate
    protected void onUpdate() {
        // Calculate duration when task is completed
        if (EtatTache.TERMINEE.equals(etatTache) && dateDebut != null && dateFin == null) {
            this.dateFin = LocalDateTime.now();
            this.dureeMinutes = (int) java.time.Duration.between(dateDebut, dateFin).toMinutes();
        }
    }
    
    // Getters and Setters
    public Long getIdTacheInventaire() {
        return idTacheInventaire;
    }
    
    public void setIdTacheInventaire(Long idTacheInventaire) {
        this.idTacheInventaire = idTacheInventaire;
    }
    
    public LocalDateTime getDateTache() {
        return dateTache;
    }
    
    public void setDateTache(LocalDateTime dateTache) {
        this.dateTache = dateTache;
    }
    
    public EtatTache getEtatTache() {
        return etatTache;
    }
    
    public void setEtatTache(EtatTache etatTache) {
        this.etatTache = etatTache;
        
        // Auto-set start/end dates based on state
        if (EtatTache.EN_COURS.equals(etatTache) && dateDebut == null) {
            this.dateDebut = LocalDateTime.now();
        } else if (EtatTache.TERMINEE.equals(etatTache) && dateFin == null) {
            this.dateFin = LocalDateTime.now();
            if (dateDebut != null) {
                this.dureeMinutes = (int) java.time.Duration.between(dateDebut, dateFin).toMinutes();
            }
        }
    }
    
    public Integer getQuantitePhysique() {
        return quantitePhysique;
    }
    
    public void setQuantitePhysique(Integer quantitePhysique) {
        this.quantitePhysique = quantitePhysique;
    }
    
    public Integer getQuantiteTheorique() {
        return quantiteTheorique;
    }
    
    public void setQuantiteTheorique(Integer quantiteTheorique) {
        this.quantiteTheorique = quantiteTheorique;
    }
    
    public String getCommentaire() {
        return commentaire;
    }
    
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
    
    public LocalDateTime getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDateTime getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
        if (this.dateDebut != null && dateFin != null) {
            this.dureeMinutes = (int) java.time.Duration.between(dateDebut, dateFin).toMinutes();
        }
    }
    
    public Integer getDureeMinutes() {
        return dureeMinutes;
    }
    
    public void setDureeMinutes(Integer dureeMinutes) {
        this.dureeMinutes = dureeMinutes;
    }
    
    public Integer getPriorite() {
        return priorite;
    }
    
    public void setPriorite(Integer priorite) {
        this.priorite = priorite;
    }
    
    public String getEmplacementVerifie() {
        return emplacementVerifie;
    }
    
    public void setEmplacementVerifie(String emplacementVerifie) {
        this.emplacementVerifie = emplacementVerifie;
    }
    
    public Personnel getPersonnel() {
        return personnel;
    }
    
    public void setPersonnel(Personnel personnel) {
        this.personnel = personnel;
    }
    
    public Produit getProduit() {
        return produit;
    }
    
    public void setProduit(Produit produit) {
        this.produit = produit;
        if (produit != null && this.quantiteTheorique == null) {
            this.quantiteTheorique = produit.getQuantiteStock();
            this.emplacementVerifie = produit.getEmplacement();
        }
    }
    
    public Inventaire getInventaire() {
        return inventaire;
    }
    
    public void setInventaire(Inventaire inventaire) {
        this.inventaire = inventaire;
    }
    
    // Utility methods
    public boolean hasEcart() {
        return quantitePhysique != null && quantiteTheorique != null && 
               !quantitePhysique.equals(quantiteTheorique);
    }
    
    public Integer getEcart() {
        if (quantitePhysique == null || quantiteTheorique == null) {
            return null;
        }
        return quantitePhysique - quantiteTheorique;
    }
    
    public boolean isEcartPositif() {
        Integer ecart = getEcart();
        return ecart != null && ecart > 0;
    }
    
    public boolean isEcartNegatif() {
        Integer ecart = getEcart();
        return ecart != null && ecart < 0;
    }
    
    public boolean isTerminee() {
        return EtatTache.TERMINEE.equals(etatTache);
    }
    
    public boolean isEnCours() {
        return EtatTache.EN_COURS.equals(etatTache);
    }
    
    public boolean isEnAttente() {
        return EtatTache.EN_ATTENTE.equals(etatTache);
    }
    
    public boolean isAnnulee() {
        return EtatTache.ANNULEE.equals(etatTache);
    }
    
    public void commencerTache() {
        this.etatTache = EtatTache.EN_COURS;
        this.dateDebut = LocalDateTime.now();
    }
    
    public void terminerTache(Integer quantiteComptee, String commentaire) {
        this.quantitePhysique = quantiteComptee;
        this.commentaire = commentaire;
        this.etatTache = EtatTache.TERMINEE;
        this.dateFin = LocalDateTime.now();
        
        if (dateDebut != null) {
            this.dureeMinutes = (int) java.time.Duration.between(dateDebut, dateFin).toMinutes();
        }
        
        // Update product's last inventory date
        if (produit != null) {
            produit.setDernierInventaire(LocalDateTime.now());
            // If there's a discrepancy, mark product as needing inventory
            if (hasEcart()) {
                produit.setBesoinInventaire(true);
            }
        }
    }
    
    public void annulerTache(String motif) {
        this.etatTache = EtatTache.ANNULEE;
        this.commentaire = motif;
        this.dateFin = LocalDateTime.now();
    }
    
    public String getDescription() {
        if (produit != null) {
            return "Inventaire du produit: " + produit.getNom();
        }
        return "Tâche d'inventaire";
    }
    
    public String getStatutDetails() {
        StringBuilder details = new StringBuilder();
        details.append("État: ").append(etatTache.getLabel());
        
        if (hasEcart()) {
            details.append(" - Écart détecté: ").append(getEcart());
        }
        
        if (dureeMinutes != null && dureeMinutes > 0) {
            details.append(" - Durée: ").append(dureeMinutes).append(" min");
        }
        
        return details.toString();
    }
    
    // equals, hashCode, toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TacheInventaire that = (TacheInventaire) obj;
        return Objects.equals(idTacheInventaire, that.idTacheInventaire);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idTacheInventaire);
    }
    
    @Override
    public String toString() {
        return "TacheInventaire{" +
                "idTacheInventaire=" + idTacheInventaire +
                ", etatTache=" + etatTache +
                ", produit=" + (produit != null ? produit.getNom() : "null") +
                ", personnel=" + (personnel != null ? personnel.getNomComplet() : "null") +
                ", quantitePhysique=" + quantitePhysique +
                ", quantiteTheorique=" + quantiteTheorique +
                '}';
    }
}
