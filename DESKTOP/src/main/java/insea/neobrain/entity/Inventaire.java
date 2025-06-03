package insea.neobrain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity class for Inventaire (Inventory)
 */
@Entity
@Table(name = "inventaire")
public class Inventaire {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventaire")
    private Long idInventaire;
    
    @Column(name = "numero_inventaire", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Le numéro d'inventaire est obligatoire")
    private String numeroInventaire;
    
    @Column(name = "date_debut", nullable = false)
    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;
    
    @Column(name = "date_fin")
    private LocalDate dateFin;
    
    @Column(name = "est_clos")
    private Boolean estClos = false;
    
    @Column(name = "etat_inventaire", length = 50)
    private String etatInventaire = "EN_COURS";
    
    @Column(name = "description", length = 500)
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;
    
    @Column(name = "responsable", length = 100)
    @Size(max = 100, message = "Le nom du responsable ne peut pas dépasser 100 caractères")
    private String responsable;
    
    @Column(name = "nombre_produits_total")
    private Integer nombreProduitsTotal;
    
    @Column(name = "nombre_produits_comptes")
    private Integer nombreProduitsComptes = 0;
    
    @Column(name = "ecarts_detectes")
    private Integer ecartsDetectes = 0;
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    // One-to-many relationship with TacheInventaire
    @OneToMany(mappedBy = "inventaire", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TacheInventaire> taches = new ArrayList<>();
    
    // Constructors
    public Inventaire() {
        this.dateCreation = LocalDateTime.now();
        this.estClos = false;
        this.etatInventaire = "EN_COURS";
        this.nombreProduitsComptes = 0;
        this.ecartsDetectes = 0;
    }
    
    public Inventaire(LocalDate dateDebut, String description, String responsable) {
        this();
        this.dateDebut = dateDebut;
        this.description = description;
        this.responsable = responsable;
        this.generateNumeroInventaire();
    }
    
    // Generate inventory number
    private void generateNumeroInventaire() {
        String year = String.valueOf(LocalDate.now().getYear());
        String month = String.format("%02d", LocalDate.now().getMonthValue());
        long timestamp = System.currentTimeMillis() % 10000;
        this.numeroInventaire = "INV" + year + month + timestamp;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        if (this.numeroInventaire == null) {
            generateNumeroInventaire();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
        updateStatistics();
    }
    
    // Update statistics based on tasks
    private void updateStatistics() {
        if (taches != null) {
            this.nombreProduitsComptes = (int) taches.stream()
                    .filter(t -> EtatTache.TERMINEE.equals(t.getEtatTache()))
                    .count();
            
            this.ecartsDetectes = (int) taches.stream()
                    .filter(t -> EtatTache.TERMINEE.equals(t.getEtatTache()))
                    .filter(t -> t.getQuantitePhysique() != null && 
                                t.getProduit() != null && 
                                t.getProduit().getQuantiteStock() != null &&
                                !t.getQuantitePhysique().equals(t.getProduit().getQuantiteStock()))
                    .count();
        }
    }
    
    // Getters and Setters
    public Long getIdInventaire() {
        return idInventaire;
    }
    
    public void setIdInventaire(Long idInventaire) {
        this.idInventaire = idInventaire;
    }
    
    public String getNumeroInventaire() {
        return numeroInventaire;
    }
    
    public void setNumeroInventaire(String numeroInventaire) {
        this.numeroInventaire = numeroInventaire;
    }
    
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public LocalDate getDateFin() {
        return dateFin;
    }
    
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
    
    public Boolean getEstClos() {
        return estClos;
    }
    
    public void setEstClos(Boolean estClos) {
        this.estClos = estClos;
        if (Boolean.TRUE.equals(estClos)) {
            this.etatInventaire = "CLOS";
            if (this.dateFin == null) {
                this.dateFin = LocalDate.now();
            }
        }
    }
    
    public String getEtatInventaire() {
        return etatInventaire;
    }
    
    public void setEtatInventaire(String etatInventaire) {
        this.etatInventaire = etatInventaire;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getResponsable() {
        return responsable;
    }
    
    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }
    
    public Integer getNombreProduitsTotal() {
        return nombreProduitsTotal;
    }
    
    public void setNombreProduitsTotal(Integer nombreProduitsTotal) {
        this.nombreProduitsTotal = nombreProduitsTotal;
    }
    
    public Integer getNombreProduitsComptes() {
        return nombreProduitsComptes;
    }
    
    public void setNombreProduitsComptes(Integer nombreProduitsComptes) {
        this.nombreProduitsComptes = nombreProduitsComptes;
    }
    
    public Integer getEcartsDetectes() {
        return ecartsDetectes;
    }
    
    public void setEcartsDetectes(Integer ecartsDetectes) {
        this.ecartsDetectes = ecartsDetectes;
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
    
    public List<TacheInventaire> getTaches() {
        return taches;
    }
    
    public void setTaches(List<TacheInventaire> taches) {
        this.taches = taches;
    }
    
    // Utility methods
    public boolean isEnCours() {
        return "EN_COURS".equals(etatInventaire) && !Boolean.TRUE.equals(estClos);
    }
    
    public boolean isClos() {
        return Boolean.TRUE.equals(estClos) || "CLOS".equals(etatInventaire);
    }
    
    public boolean isTermine() {
        return nombreProduitsTotal != null && nombreProduitsComptes != null && 
               nombreProduitsTotal.equals(nombreProduitsComptes);
    }
    
    public double getPourcentageAvancement() {
        if (nombreProduitsTotal == null || nombreProduitsTotal == 0) {
            return 0.0;
        }
        return (nombreProduitsComptes != null ? nombreProduitsComptes : 0) * 100.0 / nombreProduitsTotal;
    }
    
    public void cloreInventaire() {
        this.estClos = true;
        this.etatInventaire = "CLOS";
        this.dateFin = LocalDate.now();
        updateStatistics();
    }
    
    public void ajouterTache(TacheInventaire tache) {
        taches.add(tache);
        tache.setInventaire(this);
    }
    
    public void retirerTache(TacheInventaire tache) {
        taches.remove(tache);
        tache.setInventaire(null);
    }
    
    public List<TacheInventaire> getTachesParEtat(EtatTache etat) {
        return taches.stream()
                .filter(t -> etat.equals(t.getEtatTache()))
                .toList();
    }
    
    public List<TacheInventaire> getTachesAvecEcarts() {
        return taches.stream()
                .filter(t -> t.hasEcart())
                .toList();
    }
    
    // equals, hashCode, toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Inventaire that = (Inventaire) obj;
        return Objects.equals(idInventaire, that.idInventaire);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idInventaire);
    }
    
    @Override
    public String toString() {
        return "Inventaire{" +
                "idInventaire=" + idInventaire +
                ", numeroInventaire='" + numeroInventaire + '\'' +
                ", dateDebut=" + dateDebut +
                ", estClos=" + estClos +
                ", etatInventaire='" + etatInventaire + '\'' +
                '}';
    }
}
