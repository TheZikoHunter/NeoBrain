package insea.neobrain.util;

import insea.neobrain.entity.Personne;
import insea.neobrain.entity.Produit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^(\\+212|0)[5-7][0-9]{8}$"); // Moroccan phone format
    
    /**
     * Validation result container
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        
        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors != null ? errors : new ArrayList<>();
        }
        
        public boolean isValid() { 
            return valid; 
        }
        
        public List<String> getErrors() { 
            return errors; 
        }
        
        public String getErrorMessage() {
            return String.join(", ", errors);
        }
    }
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate phone number format (Moroccan)
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Validate a Person entity
     */
    public static ValidationResult validatePersonne(Personne personne) {
        List<String> errors = new ArrayList<>();
        
        if (personne == null) {
            errors.add("La personne ne peut pas être nulle");
            return new ValidationResult(false, errors);
        }
        
        // Required fields
        if (isNullOrEmpty(personne.getNom())) {
            errors.add("Le nom est obligatoire");
        }
        
        if (isNullOrEmpty(personne.getPrenom())) {
            errors.add("Le prénom est obligatoire");
        }
        
        if (personne.getDateNaissance() == null) {
            errors.add("La date de naissance est obligatoire");
        }
        
        // Email validation
        if (personne.getEmail() != null && !personne.getEmail().trim().isEmpty()) {
            if (!isValidEmail(personne.getEmail())) {
                errors.add("Format d'email invalide");
            }
        }
        
        // Phone validation
        if (personne.getTelephone() != null && !personne.getTelephone().trim().isEmpty()) {
            if (!isValidPhone(personne.getTelephone())) {
                errors.add("Format de téléphone invalide (format marocain attendu)");
            }
        }
        
        // Username validation
        if (isNullOrEmpty(personne.getNomUtilisateur())) {
            errors.add("Le nom d'utilisateur est obligatoire");
        } else if (personne.getNomUtilisateur().trim().length() < 3) {
            errors.add("Le nom d'utilisateur doit contenir au moins 3 caractères");
        }
        
        // Password validation
        if (isNullOrEmpty(personne.getMotDePasse())) {
            errors.add("Le mot de passe est obligatoire");
        } else if (!PasswordUtil.isValidPassword(personne.getMotDePasse())) {
            errors.add("Le mot de passe doit contenir au moins 6 caractères, une lettre et un chiffre");
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    /**
     * Validate a Product entity
     */
    public static ValidationResult validateProduit(Produit produit) {
        List<String> errors = new ArrayList<>();
        
        if (produit == null) {
            errors.add("Le produit ne peut pas être nul");
            return new ValidationResult(false, errors);
        }
        
        // Required fields
        if (isNullOrEmpty(produit.getDescription())) {
            errors.add("La description du produit est obligatoire");
        }
        
        if (produit.getPrix() == null) {
            errors.add("Le prix est obligatoire");
        } else if (produit.getPrix().doubleValue() <= 0) {
            errors.add("Le prix doit être supérieur à zéro");
        }
        
        if (produit.getQuantiteStock() == null) {
            errors.add("La quantité en stock est obligatoire");
        } else if (produit.getQuantiteStock() < 0) {
            errors.add("La quantité en stock ne peut pas être négative");
        }
        
        if (produit.getCategorie() == null) {
            errors.add("La catégorie du produit est obligatoire");
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }
    
    /**
     * Check if string is null or empty
     */
    private static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Validate positive integer
     */
    public static boolean isPositiveInteger(Integer value) {
        return value != null && value > 0;
    }
    
    /**
     * Validate non-negative integer
     */
    public static boolean isNonNegativeInteger(Integer value) {
        return value != null && value >= 0;
    }
    
    /**
     * Validate positive double
     */
    public static boolean isPositiveDouble(Double value) {
        return value != null && value > 0.0;
    }
}
