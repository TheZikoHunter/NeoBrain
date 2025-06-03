package insea.neobrain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class for Personnel (employees)
 */
@Entity
@Table(name = "personnel")
@PrimaryKeyJoinColumn(name = "id_personne")
public class Personnel extends Personne {
    
    @Column(name = "id_personnel", unique = true)
    private String idPersonnel;
    
    @Column(name = "date_embauche", nullable = false)
    @NotNull(message = "La date d'embauche est obligatoire")
    @PastOrPresent(message = "La date d'embauche ne peut pas être dans le futur")
    private LocalDate dateEmbauche;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @NotNull(message = "Le rôle est obligatoire")
    private Role role;
    
    @Column(name = "salaire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le salaire doit être positif")
    private Double salaire;
    
    @Column(name = "actif_emploi")
    private Boolean actifEmploi = true;
    
    @Column(name = "date_fin_contrat")
    private LocalDate dateFinContrat;
    
    // One-to-many relationship with TacheInventaire
    @OneToMany(mappedBy = "personnel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TacheInventaire> tachesInventaire = new ArrayList<>();
    
    // Constructors
    public Personnel() {
        super();
        this.actifEmploi = true;
    }
    
    public Personnel(Civilite civilite, String nom, String prenom, Role role, LocalDate dateEmbauche) {
        super(civilite, nom, prenom);
        this.role = role;
        this.dateEmbauche = dateEmbauche;
        this.generateIdPersonnel();
    }
    
    // Generate personnel ID based on role and timestamp
    private void generateIdPersonnel() {
        String prefix;
        switch (role) {
            case ADMIN:
                prefix = "ADM";
                break;
            case RESPONSABLE_STOCK:
                prefix = "RST";
                break;
            case EMPLOYE_STOCK:
                prefix = "EST";
                break;
            default:
                prefix = "PER";
                break;
        }
        this.idPersonnel = prefix + System.currentTimeMillis() % 100000;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        super.setDateCreation(LocalDate.now());
        if (this.idPersonnel == null && this.role != null) {
            generateIdPersonnel();
        }
    }
    
    // Getters and Setters
    public String getIdPersonnel() {
        return idPersonnel;
    }
    
    public void setIdPersonnel(String idPersonnel) {
        this.idPersonnel = idPersonnel;
    }
    
    // Alias methods for compatibility
    public String getNumeroPersonnel() {
        return idPersonnel;
    }
    
    public void setNumeroPersonnel(String numeroPersonnel) {
        this.idPersonnel = numeroPersonnel;
    }
    
    public Long getId() {
        return getIdPersonne();
    }
    
    public void setId(Long id) {
        setIdPersonne(id);
    }
    
    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }
    
    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
        if (this.idPersonnel == null) {
            generateIdPersonnel();
        }
    }
    
    public Double getSalaire() {
        return salaire;
    }
    
    public void setSalaire(Double salaire) {
        this.salaire = salaire;
    }
    
    public Boolean getActifEmploi() {
        return actifEmploi;
    }
    
    public void setActifEmploi(Boolean actifEmploi) {
        this.actifEmploi = actifEmploi;
    }
    
    public LocalDate getDateFinContrat() {
        return dateFinContrat;
    }
    
    public void setDateFinContrat(LocalDate dateFinContrat) {
        this.dateFinContrat = dateFinContrat;
    }
    
    public List<TacheInventaire> getTachesInventaire() {
        return tachesInventaire;
    }
    
    public void setTachesInventaire(List<TacheInventaire> tachesInventaire) {
        this.tachesInventaire = tachesInventaire;
    }
    
    // Utility methods
    public boolean isAdmin() {
        return Role.ADMIN.equals(this.role);
    }
    
    public boolean isResponsableStock() {
        return Role.RESPONSABLE_STOCK.equals(this.role);
    }
    
    public boolean isEmployeStock() {
        return Role.EMPLOYE_STOCK.equals(this.role);
    }
    
    public boolean canManagePersonnel() {
        return isAdmin();
    }
    
    public boolean canManageProducts() {
        return isAdmin() || isResponsableStock();
    }
    
    public boolean canManageInventory() {
        return isAdmin() || isResponsableStock();
    }
    
    public boolean canViewSalesOrders() {
        return isAdmin() || isResponsableStock();
    }
    
    public boolean canPerformInventoryTasks() {
        return isEmployeStock() || isResponsableStock() || isAdmin();
    }
    
    // Add inventory task
    public void addTacheInventaire(TacheInventaire tache) {
        tachesInventaire.add(tache);
        tache.setPersonnel(this);
    }
    
    // Remove inventory task
    public void removeTacheInventaire(TacheInventaire tache) {
        tachesInventaire.remove(tache);
        tache.setPersonnel(null);
    }
    
    @Override
    public String toString() {
        return "Personnel{" +
                "idPersonnel='" + idPersonnel + '\'' +
                ", role=" + role +
                ", dateEmbauche=" + dateEmbauche +
                ", nom='" + getNom() + '\'' +
                ", prenom='" + getPrenom() + '\'' +
                '}';
    }
}
