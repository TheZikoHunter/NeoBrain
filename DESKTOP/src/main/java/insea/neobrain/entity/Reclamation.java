package insea.neobrain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity class for Reclamation (Complaint/Return)
 */
@Entity
@Table(name = "reclamation")
public class Reclamation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reclamation")
    private Long idReclamation;
    
    @Column(name = "numero_reclamation", length = 50, unique = true)
    private String numeroReclamation;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_reclamation", nullable = false)
    @NotNull(message = "Le type de réclamation est obligatoire")
    private TypeReclamation typeReclamation;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "etat_reclamation", nullable = false)
    @NotNull(message = "L'état de la réclamation est obligatoire")
    private EtatReclamation etatReclamation;
    
    @Column(name = "date_reclamation", nullable = false)
    @NotNull(message = "La date de réclamation est obligatoire")
    private LocalDateTime dateReclamation;
    
    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;
    
    @Column(name = "motif", length = 500)
    @Size(max = 500, message = "Le motif ne peut pas dépasser 500 caractères")
    private String motif;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "solution_proposee", columnDefinition = "TEXT")
    private String solutionProposee;
    
    @Column(name = "commentaire_interne", columnDefinition = "TEXT")
    private String commentaireInterne;
    
    @Column(name = "traite_par", length = 100)
    private String traitePar;
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    // Many-to-one relationship with LigneCommande
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ligne_commande", nullable = false)
    @NotNull(message = "La ligne de commande est obligatoire")
    private LigneCommande ligneCommande;
    
    // Constructors
    public Reclamation() {
        this.dateCreation = LocalDateTime.now();
        this.dateReclamation = LocalDateTime.now();
        this.etatReclamation = EtatReclamation.EN_ATTENTE;
    }
    
    public Reclamation(LigneCommande ligneCommande, TypeReclamation typeReclamation, String motif) {
        this();
        this.ligneCommande = ligneCommande;
        this.typeReclamation = typeReclamation;
        this.motif = motif;
        this.generateNumeroReclamation();
    }
    
    // Generate reclamation number
    private void generateNumeroReclamation() {
        String year = String.valueOf(java.time.LocalDate.now().getYear());
        String month = String.format("%02d", java.time.LocalDate.now().getMonthValue());
        long timestamp = System.currentTimeMillis() % 10000;
        this.numeroReclamation = "REC" + year + month + timestamp;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        if (this.numeroReclamation == null) {
            generateNumeroReclamation();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
        
        // Auto-set treatment date when status changes
        if (EtatReclamation.VALIDEE.equals(etatReclamation) || 
            EtatReclamation.REFUSEE.equals(etatReclamation)) {
            if (this.dateTraitement == null) {
                this.dateTraitement = LocalDateTime.now();
            }
        }
    }
    
    // Getters and Setters
    public Long getIdReclamation() {
        return idReclamation;
    }
    
    public void setIdReclamation(Long idReclamation) {
        this.idReclamation = idReclamation;
    }
    
    public String getNumeroReclamation() {
        return numeroReclamation;
    }
    
    public void setNumeroReclamation(String numeroReclamation) {
        this.numeroReclamation = numeroReclamation;
    }
    
    public TypeReclamation getTypeReclamation() {
        return typeReclamation;
    }
    
    public void setTypeReclamation(TypeReclamation typeReclamation) {
        this.typeReclamation = typeReclamation;
    }
    
    public EtatReclamation getEtatReclamation() {
        return etatReclamation;
    }
    
    public void setEtatReclamation(EtatReclamation etatReclamation) {
        this.etatReclamation = etatReclamation;
        
        // Auto-set treatment date when status changes to processed
        if ((EtatReclamation.VALIDEE.equals(etatReclamation) || 
             EtatReclamation.REFUSEE.equals(etatReclamation)) && 
            this.dateTraitement == null) {
            this.dateTraitement = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getDateReclamation() {
        return dateReclamation;
    }
    
    public void setDateReclamation(LocalDateTime dateReclamation) {
        this.dateReclamation = dateReclamation;
    }
    
    public LocalDateTime getDateTraitement() {
        return dateTraitement;
    }
    
    public void setDateTraitement(LocalDateTime dateTraitement) {
        this.dateTraitement = dateTraitement;
    }
    
    public String getMotif() {
        return motif;
    }
    
    public void setMotif(String motif) {
        this.motif = motif;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getSolutionProposee() {
        return solutionProposee;
    }
    
    public void setSolutionProposee(String solutionProposee) {
        this.solutionProposee = solutionProposee;
    }
    
    public String getCommentaireInterne() {
        return commentaireInterne;
    }
    
    public void setCommentaireInterne(String commentaireInterne) {
        this.commentaireInterne = commentaireInterne;
    }
    
    public String getTraitePar() {
        return traitePar;
    }
    
    public void setTraitePar(String traitePar) {
        this.traitePar = traitePar;
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
    
    public LigneCommande getLigneCommande() {
        return ligneCommande;
    }
    
    public void setLigneCommande(LigneCommande ligneCommande) {
        this.ligneCommande = ligneCommande;
    }
    
    // Utility methods
    public boolean isEnAttente() {
        return EtatReclamation.EN_ATTENTE.equals(etatReclamation);
    }
    
    public boolean isValidee() {
        return EtatReclamation.VALIDEE.equals(etatReclamation);
    }
    
    public boolean isRefusee() {
        return EtatReclamation.REFUSEE.equals(etatReclamation);
    }
    
    public boolean isTraitee() {
        return isValidee() || isRefusee();
    }
    
    public boolean isRetour() {
        return TypeReclamation.RETOUR.equals(typeReclamation);
    }
    
    public boolean isEchecReception() {
        return TypeReclamation.ECHEC_RECEPTION.equals(typeReclamation);
    }
    
    public void valider(String traitePar, String solutionProposee) {
        this.etatReclamation = EtatReclamation.VALIDEE;
        this.traitePar = traitePar;
        this.solutionProposee = solutionProposee;
        this.dateTraitement = LocalDateTime.now();
    }
    
    public void refuser(String traitePar, String motifRefus) {
        this.etatReclamation = EtatReclamation.REFUSEE;
        this.traitePar = traitePar;
        this.commentaireInterne = motifRefus;
        this.dateTraitement = LocalDateTime.now();
    }
    
    public String getResume() {
        StringBuilder resume = new StringBuilder();
        resume.append("Réclamation ").append(numeroReclamation);
        resume.append(" - ").append(typeReclamation.getLabel());
        resume.append(" (").append(etatReclamation.getLabel()).append(")");
        
        if (ligneCommande != null && ligneCommande.getProduit() != null) {
            resume.append(" - Produit: ").append(ligneCommande.getProduit().getNom());
        }
        
        return resume.toString();
    }
    
    // equals, hashCode, toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Reclamation that = (Reclamation) obj;
        return Objects.equals(idReclamation, that.idReclamation);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idReclamation);
    }
    
    @Override
    public String toString() {
        return "Reclamation{" +
                "idReclamation=" + idReclamation +
                ", numeroReclamation='" + numeroReclamation + '\'' +
                ", typeReclamation=" + typeReclamation +
                ", etatReclamation=" + etatReclamation +
                ", motif='" + motif + '\'' +
                ", dateReclamation=" + dateReclamation +
                '}';
    }
}
