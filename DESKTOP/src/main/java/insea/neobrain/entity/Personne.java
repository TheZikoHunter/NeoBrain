package insea.neobrain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Base entity class for all persons in the system
 */
@Entity
@Table(name = "personne")
@Inheritance(strategy = InheritanceType.JOINED)
public class Personne {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_personne")
    private Long idPersonne;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "civilite", nullable = false)
    @NotNull(message = "La civilité est obligatoire")
    private Civilite civilite;
    
    @Column(name = "nom", nullable = false, length = 100)
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;
    
    @Column(name = "prenom", nullable = false, length = 100)
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    private String prenom;
    
    @Column(name = "date_naissance")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;
    
    @Column(name = "email", length = 150)
    @Email(message = "Format d'email invalide")
    @Size(max = 150, message = "L'email ne peut pas dépasser 150 caractères")
    private String email;
    
    @Column(name = "telephone", length = 20)
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\.\\(\\)]{6,20}$", message = "Format de téléphone invalide")
    private String telephone;
    
    @Column(name = "adresse", length = 200)
    @Size(max = 200, message = "L'adresse ne peut pas dépasser 200 caractères")
    private String adresse;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "nationalite")
    private Nationalite nationalite;
    
    @Column(name = "nom_utilisateur", unique = true, length = 50)
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String nomUtilisateur;
    
    @Column(name = "mot_de_passe", length = 255)
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String motDePasse;
    
    @Column(name = "actif")
    private Boolean actif = true;
    
    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation;
    
    @Column(name = "date_modification")
    private LocalDate dateModification;
    
    // Constructors
    public Personne() {
        this.dateCreation = LocalDate.now();
        this.actif = true;
    }
    
    public Personne(Civilite civilite, String nom, String prenom) {
        this();
        this.civilite = civilite;
        this.nom = nom;
        this.prenom = prenom;
    }
    
    // Lifecycle callbacks
    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDate.now();
    }
    
    // Getters and Setters
    public Long getIdPersonne() {
        return idPersonne;
    }
    
    public void setIdPersonne(Long idPersonne) {
        this.idPersonne = idPersonne;
    }
    
    public Civilite getCivilite() {
        return civilite;
    }
    
    public void setCivilite(Civilite civilite) {
        this.civilite = civilite;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public LocalDate getDateNaissance() {
        return dateNaissance;
    }
    
    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    public String getAdresse() {
        return adresse;
    }
    
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    
    public Nationalite getNationalite() {
        return nationalite;
    }
    
    public void setNationalite(Nationalite nationalite) {
        this.nationalite = nationalite;
    }
    
    public String getNomUtilisateur() {
        return nomUtilisateur;
    }
    
    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }
    
    public String getMotDePasse() {
        return motDePasse;
    }
    
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
    
    public Boolean getActif() {
        return actif;
    }
    
    public void setActif(Boolean actif) {
        this.actif = actif;
    }
    
    public LocalDate getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDate getDateModification() {
        return dateModification;
    }
    
    public void setDateModification(LocalDate dateModification) {
        this.dateModification = dateModification;
    }
    
    // Utility methods
    public String getNomComplet() {
        return prenom + " " + nom;
    }
    
    public String getDisplayName() {
        return civilite.getLabel() + " " + getNomComplet();
    }
    
    // equals, hashCode, toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Personne personne = (Personne) obj;
        return Objects.equals(idPersonne, personne.idPersonne);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(idPersonne);
    }
    
    @Override
    public String toString() {
        return "Personne{" +
                "idPersonne=" + idPersonne +
                ", civilite=" + civilite +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", nomUtilisateur='" + nomUtilisateur + '\'' +
                '}';
    }
}
