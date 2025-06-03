package insea.neobrain.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

import insea.neobrain.model.Personnel;
import insea.neobrain.model.Produit;
import insea.neobrain.model.Client;
import java.util.List;

/**
 * Unit tests for ValidationUtil class
 */
public class ValidationUtilTest {

    private Personnel validPersonnel;
    private Produit validProduit;
    private Client validClient;

    @BeforeEach
    void setUp() {
        validPersonnel = new Personnel();
        validPersonnel.setNom("Dupont");
        validPersonnel.setPrenom("Jean");
        validPersonnel.setEmail("jean.dupont@example.com");
        validPersonnel.setTelephone("0123456789");
        validPersonnel.setRole("ADMIN");
        validPersonnel.setMotDePasse("hashedPassword");

        validProduit = new Produit();
        validProduit.setNom("Laptop");
        validProduit.setDescription("High-performance laptop");
        validProduit.setPrix(999.99);
        validProduit.setQuantiteStock(10);
        validProduit.setCategorie("Electronics");

        validClient = new Client();
        validClient.setNom("Martin");
        validClient.setPrenom("Pierre");
        validClient.setEmail("pierre.martin@example.com");
        validClient.setTelephone("0987654321");
        validClient.setAdresse("123 Main St");
    }

    @Test
    @DisplayName("Should validate correct Personnel")
    public void testValidatePersonnelValid() {
        // When
        List<String> errors = ValidationUtil.validatePersonnel(validPersonnel);
        
        // Then
        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("Should detect missing required fields in Personnel")
    public void testValidatePersonnelMissingFields() {
        // Given
        Personnel personnel = new Personnel();
        
        // When
        List<String> errors = ValidationUtil.validatePersonnel(personnel);
        
        // Then
        assertThat(errors).hasSize(6);
        assertThat(errors).contains(
            "Le nom est obligatoire",
            "Le prénom est obligatoire", 
            "L'email est obligatoire",
            "Le téléphone est obligatoire",
            "Le rôle est obligatoire",
            "Le mot de passe est obligatoire"
        );
    }

    @Test
    @DisplayName("Should validate email format in Personnel")
    public void testValidatePersonnelInvalidEmail() {
        // Given
        validPersonnel.setEmail("invalid-email");
        
        // When
        List<String> errors = ValidationUtil.validatePersonnel(validPersonnel);
        
        // Then
        assertThat(errors).hasSize(1);
        assertThat(errors).contains("L'email n'a pas un format valide");
    }

    @Test
    @DisplayName("Should validate telephone format in Personnel")
    public void testValidatePersonnelInvalidTelephone() {
        // Given
        validPersonnel.setTelephone("123");
        
        // When
        List<String> errors = ValidationUtil.validatePersonnel(validPersonnel);
        
        // Then
        assertThat(errors).hasSize(1);
        assertThat(errors).contains("Le téléphone doit contenir au moins 10 chiffres");
    }

    @Test
    @DisplayName("Should validate role in Personnel")
    public void testValidatePersonnelInvalidRole() {
        // Given
        validPersonnel.setRole("INVALID_ROLE");
        
        // When
        List<String> errors = ValidationUtil.validatePersonnel(validPersonnel);
        
        // Then
        assertThat(errors).hasSize(1);
        assertThat(errors).contains("Le rôle doit être ADMIN, GESTIONNAIRE_STOCK, ou EMPLOYE");
    }

    @Test
    @DisplayName("Should validate correct Produit")
    public void testValidateProduitValid() {
        // When
        List<String> errors = ValidationUtil.validateProduit(validProduit);
        
        // Then
        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("Should detect missing required fields in Produit")
    public void testValidateProduitMissingFields() {
        // Given
        Produit produit = new Produit();
        
        // When
        List<String> errors = ValidationUtil.validateProduit(produit);
        
        // Then
        assertThat(errors).hasSize(4);
        assertThat(errors).contains(
            "Le nom est obligatoire",
            "La description est obligatoire",
            "Le prix est obligatoire",
            "La catégorie est obligatoire"
        );
    }

    @Test
    @DisplayName("Should validate price range in Produit")
    public void testValidateProduitInvalidPrice() {
        // Given
        validProduit.setPrix(-10.0);
        
        // When
        List<String> errors = ValidationUtil.validateProduit(validProduit);
        
        // Then
        assertThat(errors).hasSize(1);
        assertThat(errors).contains("Le prix doit être positif");
    }

    @Test
    @DisplayName("Should validate stock quantity in Produit")
    public void testValidateProduitNegativeStock() {
        // Given
        validProduit.setQuantiteStock(-5);
        
        // When
        List<String> errors = ValidationUtil.validateProduit(validProduit);
        
        // Then
        assertThat(errors).hasSize(1);
        assertThat(errors).contains("La quantité en stock ne peut pas être négative");
    }

    @Test
    @DisplayName("Should validate correct Client")
    public void testValidateClientValid() {
        // When
        List<String> errors = ValidationUtil.validateClient(validClient);
        
        // Then
        assertThat(errors).isEmpty();
    }

    @Test
    @DisplayName("Should detect missing required fields in Client")
    public void testValidateClientMissingFields() {
        // Given
        Client client = new Client();
        
        // When
        List<String> errors = ValidationUtil.validateClient(client);
        
        // Then
        assertThat(errors).hasSize(5);
        assertThat(errors).contains(
            "Le nom est obligatoire",
            "Le prénom est obligatoire",
            "L'email est obligatoire",
            "Le téléphone est obligatoire",
            "L'adresse est obligatoire"
        );
    }

    @Test
    @DisplayName("Should validate email format in Client")
    public void testValidateClientInvalidEmail() {
        // Given
        validClient.setEmail("not-an-email");
        
        // When
        List<String> errors = ValidationUtil.validateClient(validClient);
        
        // Then
        assertThat(errors).hasSize(1);
        assertThat(errors).contains("L'email n'a pas un format valide");
    }

    @Test
    @DisplayName("Should handle null objects gracefully")
    public void testValidateNullObjects() {
        // When/Then
        assertThat(ValidationUtil.validatePersonnel(null)).isEmpty();
        assertThat(ValidationUtil.validateProduit(null)).isEmpty();
        assertThat(ValidationUtil.validateClient(null)).isEmpty();
    }

    @Test
    @DisplayName("Should validate string field lengths")
    public void testValidateFieldLengths() {
        // Given
        validPersonnel.setNom("a".repeat(300)); // Very long name
        
        // When
        List<String> errors = ValidationUtil.validatePersonnel(validPersonnel);
        
        // Then
        assertThat(errors).hasSize(1);
        assertThat(errors).contains("Le nom ne peut pas dépasser 255 caractères");
    }

    @Test
    @DisplayName("Should handle empty strings correctly")
    public void testValidateEmptyStrings() {
        // Given
        validPersonnel.setNom("");
        validPersonnel.setPrenom("");
        validPersonnel.setEmail("");
        
        // When
        List<String> errors = ValidationUtil.validatePersonnel(validPersonnel);
        
        // Then
        assertThat(errors).contains(
            "Le nom est obligatoire",
            "Le prénom est obligatoire",
            "L'email est obligatoire"
        );
    }

    @Test
    @DisplayName("Should validate whitespace-only strings")
    public void testValidateWhitespaceStrings() {
        // Given
        validPersonnel.setNom("   ");
        validPersonnel.setPrenom("   ");
        
        // When
        List<String> errors = ValidationUtil.validatePersonnel(validPersonnel);
        
        // Then
        assertThat(errors).contains(
            "Le nom est obligatoire",
            "Le prénom est obligatoire"
        );
    }
}
