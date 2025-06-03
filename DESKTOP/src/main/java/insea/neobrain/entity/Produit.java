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
 * Entity class for Product (Produit)
 */
@Entity
@Table(name = "produit")
public class Produit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit")
    private Long idProduit;
    
    @Column(name = "code_produit", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Le code produit est obligatoire")
    @Size(max = 50, message = "Le code produit ne peut pas dépasser 50 caractères")
    private String codeProduit;
    
    @Column(name = "nom", nullable = false, length = 200)
    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(max = 200, message = "Le nom ne peut pas dépasser 200 caractères")
    private String nom;
    
    @Column(name = "description", length = 1000)
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;
    
    @Column(name = "prix", precision = 10, scale = 2, nullable = false)
    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être positif")
    private BigDecimal prix;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "categorie", nullable = false)
    @NotNull(message = "La catégorie est obligatoire")
    private CategorieProduit categorie;
    
    @Column(name = "quantite_stock", nullable = false)
    @NotNull(message = "La quantité en stock est obligatoire")
    @Min(value = 0, message = "La quantité en stock ne peut pas être négative")
    private Integer quantiteStock;
    
    @Column(name = "stock_minimum")
    @Min(value = 0, message = "Le stock minimum ne peut pas être négatif")
    private Integer stockMinimum = 10;
    
    @Column(name = "seuil_stock")
    @Min(value = 0, message = "Le seuil de stock ne peut pas être négatif")
    private Integer seuilStock = 5;
    
    @Column(name = "stock_maximum")
    @Min(value = 0, message = "Le stock maximum ne peut pas être négatif")
    private Integer stockMaximum;
    
    @Column(name = "disponible")
    private Boolean disponible = true;
    
    @Column(name = "besoin_inventaire")
    private Boolean besoinInventaire = false;
    
    @Column(name = "date_ajout", nullable = false)
    private LocalDate dateAjout;
    
    @Column(name = "dernier_inventaire")
    private LocalDateTime dernierInventaire;
    
    @Column(name = "unite_mesure", length = 20)
    private String uniteMesure = "pièce";
    
    @Column(name = "emplacement", length = 100)
    @Size(max = 100, message = "L'emplacement ne peut pas dépasser 100 caractères")
    private String emplacement;
    
    @Column(name = "code_barre", length = 50)
    @Size(max = 50, message = "Le code-barres ne peut pas dépasser 50 caractères")
    private String codeBarre;
    
    @Column(name = "actif")
    private Boolean actif = true;
    
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;
    
    @Column(name = "date_modification")
    private LocalDateTime dateModification;
    
    // One-to-many relationships
    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TacheInventaire> tachesInventaire = new ArrayList<>();
    
    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LigneCommande> lignesCommande = new ArrayList<>();
    
    // Constructors
    public Produit() {
        this.dateCreation = LocalDateTime.now();
        this.dateAjout = LocalDate.now();
        this.disponible = true;
        this.besoinInventaire = false;
        this.actif = true;
        this.stockMinimum = 10;
    }
    
    public Produit(String codeProduit, String nom, BigDecimal prix, CategorieProduit categorie, Integer quantiteStock) {
        this();
        this.codeProduit = codeProduit;
        this.nom = nom;
        this.prix = prix;
        this.categorie = categorie;
        this.quantiteStock = quantiteStock;
    }
    
    // Lifecycle callbacks
    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        this.dateAjout = LocalDate.now();
        if (this.codeProduit == null) {
            generateCodeProduit();
        }
    }
    
    // Generate product code if not provided
    private void generateCodeProduit() {
        String prefix;
        switch (categorie) {
            case INFORMATIQUE:
                prefix = "INF";
                break;
            case ELECTROMENAGER:
                prefix = "ELE";
                break;
            case VETEMENTS:
                prefix = "VET";
                break;
            case SPORTS:
                prefix = "SPO";
                break;
            default:
                prefix = "PRD";
                break;
        }
        this.codeProduit = prefix + System.currentTimeMillis() % 1000000;
    }
    
    // Getters and Setters
    public Long getIdProduit() {
        return idProduit;
    }
    
    public void setIdProduit(Long idProduit) {
        this.idProduit = idProduit;
    }
    
    public String getCodeProduit() {
        return codeProduit;
    }
    
    public void setCodeProduit(String codeProduit) {
        this.codeProduit = codeProduit;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrix() {
        return prix;
    }
    
    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }
    
    public CategorieProduit getCategorie() {
        return categorie;
    }
    
    public void setCategorie(CategorieProduit categorie) {
        this.categorie = categorie;
        if (this.codeProduit == null) {
            generateCodeProduit();
        }
    }
    
    public Integer getQuantiteStock() {
        return quantiteStock;
    }
    
    public void setQuantiteStock(Integer quantiteStock) {
        this.quantiteStock = quantiteStock;
        // Auto-check if inventory is needed
        checkBesoinInventaire();
    }
    
    public Integer getStockMinimum() {
        return stockMinimum;
    }
    
    public void setStockMinimum(Integer stockMinimum) {
        this.stockMinimum = stockMinimum;
    }
    
    public Integer getSeuilStock() {
        return seuilStock;
    }
    
    public void setSeuilStock(Integer seuilStock) {
        this.seuilStock = seuilStock;
    }
    
    public Integer getStockMaximum() {
        return stockMaximum;
    }
    
    public void setStockMaximum(Integer stockMaximum) {
        this.stockMaximum = stockMaximum;
    }
    
    public Boolean getDisponible() {
        return disponible;
    }
    
    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }
    
    public Boolean getBesoinInventaire() {
        return besoinInventaire;
    }
    
    public void setBesoinInventaire(Boolean besoinInventaire) {
        this.besoinInventaire = besoinInventaire;
    }
    
    public LocalDate getDateAjout() {
        return dateAjout;
    }
    
    public void setDateAjout(LocalDate dateAjout) {
        this.dateAjout = dateAjout;
    }
    
    public LocalDateTime getDernierInventaire() {
        return dernierInventaire;
    }
    
    public void setDernierInventaire(LocalDateTime dernierInventaire) {
        this.dernierInventaire = dernierInventaire;
    }
    
    public String getUniteMesure() {
        return uniteMesure;
    }
    
    public void setUniteMesure(String uniteMesure) {
        this.uniteMesure = uniteMesure;
    }
    
    public String getEmplacement() {
        return emplacement;
    }
    
    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }
    
    public String getCodeBarre() {
        return codeBarre;
    }
    
    public void setCodeBarre(String codeBarre) {
        this.codeBarre = codeBarre;
    }
    
    public Boolean getActif() {
        return actif;
    }
    
    public void setActif(Boolean actif) {
        this.actif = actif;
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
    
    public void setDerniereModification(LocalDateTime derniereModification) {
        this.dateModification = derniereModification;
    }
    
    public List<TacheInventaire> getTachesInventaire() {
        return tachesInventaire;
    }
    
    public void setTachesInventaire(List<TacheInventaire> tachesInventaire) {
        this.tachesInventaire = tachesInventaire;
    }
    
    public List<LigneCommande> getLignesCommande() {
        return lignesCommande;
    }
    
    public void setLignesCommande(List<LigneCommande> lignesCommande) {
        this.lignesCommande = lignesCommande;
    }
    
    // Alias methods for service layer compatibility
    public String getNomProduit() {
        return getNom();
    }
    
    public void setNomProduit(String nomProduit) {
        setNom(nomProduit);
    }
    
    public CategorieProduit getCategorieProduit() {
        return getCategorie();
    }
    
    public void setCategorieProduit(CategorieProduit categorieProduit) {
        setCategorie(categorieProduit);
    }
    
    public BigDecimal getPrixUnitaire() {
        return getPrix();
    }
    
    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        setPrix(prixUnitaire);
    }
    
    public int getSeuilAlerte() {
        return getStockMinimum() != null ? getStockMinimum() : 0;
    }
    
    public void setSeuilAlerte(int seuilAlerte) {
        setStockMinimum(seuilAlerte);
    }

    // Utility methods
    public boolean isStockFaible() {
        return quantiteStock != null && stockMinimum != null && quantiteStock <= stockMinimum;
    }
    
    public boolean isStockEpuise() {
        return quantiteStock == null || quantiteStock <= 0;
    }
    
    public boolean isDisponiblePourVente() {
        return Boolean.TRUE.equals(actif) && 
               Boolean.TRUE.equals(disponible) && 
               !isStockEpuise();
    }
    
    public void checkBesoinInventaire() {
        this.besoinInventaire = isStockFaible() || 
                               (dernierInventaire == null || 
                                dernierInventaire.isBefore(LocalDateTime.now().minusMonths(3)));
    }
    
    public void ajusterStock(Integer quantite) {
        if (quantite != null) {
            this.quantiteStock = (this.quantiteStock != null ? this.quantiteStock : 0) + quantite;
            if (this.quantiteStock < 0) {
                this.quantiteStock = 0;
            }
            checkBesoinInventaire();
        }
    }
    
    public boolean peutVendre(Integer quantiteDemandee) {
        return isDisponiblePourVente() && 
               quantiteDemandee != null && 
               quantiteDemandee > 0 && 
               quantiteStock != null && 
               quantiteStock >= quantiteDemandee;
    }
    
    // Relationship helper methods
    public void addTacheInventaire(TacheInventaire tache) {
        tachesInventaire.add(tache);
        tache.setProduit(this);
    }
    
    public void removeTacheInventaire(TacheInventaire tache) {
        tachesInventaire.remove(tache);
        tache.setProduit(null);
    }
    
    public void addLigneCommande(LigneCommande ligne) {
        lignesCommande.add(ligne);
        ligne.setProduit(this);
    }
    
    public void removeLigneCommande(LigneCommande ligne) {
        lignesCommande.remove(ligne);
        ligne.setProduit(null);
    }
    
    // equals, hashCode, toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Produit produit = (Produit) obj;
        return Objects.equals(idProduit, produit.idProduit);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idProduit);
    }
    
    @Override
    public String toString() {
        return "Produit{" +
                "idProduit=" + idProduit +
                ", codeProduit='" + codeProduit + '\'' +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                ", categorie=" + categorie +
                ", quantiteStock=" + quantiteStock +
                '}';
    }
}
