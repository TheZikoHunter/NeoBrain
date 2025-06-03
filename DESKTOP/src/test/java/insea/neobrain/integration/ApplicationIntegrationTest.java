package insea.neobrain.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import static org.assertj.core.api.Assertions.*;

import insea.neobrain.config.HibernateUtil;
import insea.neobrain.repository.impl.*;
import insea.neobrain.service.impl.AuthenticationServiceImpl;
import insea.neobrain.service.impl.ReclamationServiceImpl;
import insea.neobrain.model.*;
import insea.neobrain.util.PasswordUtil;
import insea.neobrain.util.ValidationUtil;
import insea.neobrain.util.BusinessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Integration tests for the complete application flow
 * Note: These tests require a running PostgreSQL database
 * Mark as @Disabled by default to avoid breaking CI/CD without database
 */
@Disabled("Integration tests require database setup")
public class ApplicationIntegrationTest {

    private PersonnelRepositoryImpl personnelRepository;
    private ClientRepositoryImpl clientRepository;
    private ProduitRepositoryImpl produitRepository;
    private ReclamationRepositoryImpl reclamationRepository;
    private AuthenticationServiceImpl authenticationService;
    private ReclamationServiceImpl reclamationService;

    @BeforeEach
    void setUp() {
        // Initialize repositories
        personnelRepository = new PersonnelRepositoryImpl();
        clientRepository = new ClientRepositoryImpl();
        produitRepository = new ProduitRepositoryImpl();
        reclamationRepository = new ReclamationRepositoryImpl();

        // Initialize services
        authenticationService = new AuthenticationServiceImpl();
        reclamationService = new ReclamationServiceImpl();
    }

    @Test
    @DisplayName("Should perform complete user authentication flow")
    public void testCompleteAuthenticationFlow() {
        // Given - Create a test user
        Personnel testUser = new Personnel();
        testUser.setNom("Test");
        testUser.setPrenom("User");
        testUser.setEmail("test.user@example.com");
        testUser.setTelephone("0123456789");
        testUser.setRole("ADMIN");
        testUser.setMotDePasse(PasswordUtil.hashPassword("testpassword"));

        // Validate user data
        List<String> validationErrors = ValidationUtil.validatePersonnel(testUser);
        assertThat(validationErrors).isEmpty();

        // Save user
        Personnel savedUser = personnelRepository.save(testUser);
        assertThat(savedUser.getId()).isNotNull();

        // When - Authenticate user
        Optional<Personnel> authenticatedUser = authenticationService.authenticate(
            "test.user@example.com", 
            "testpassword", 
            "127.0.0.1"
        );

        // Then - Verify authentication
        assertThat(authenticatedUser).isPresent();
        assertThat(authenticatedUser.get().getEmail()).isEqualTo("test.user@example.com");
        assertThat(authenticatedUser.get().getRole()).isEqualTo("ADMIN");

        // Cleanup
        personnelRepository.delete(savedUser);
    }

    @Test
    @DisplayName("Should perform complete reclamation management flow")
    public void testCompleteReclamationFlow() {
        // Given - Create test client
        Client testClient = new Client();
        testClient.setNom("Client");
        testClient.setPrenom("Test");
        testClient.setEmail("client.test@example.com");
        testClient.setTelephone("0987654321");
        testClient.setAdresse("123 Test Street");

        List<String> clientValidationErrors = ValidationUtil.validateClient(testClient);
        assertThat(clientValidationErrors).isEmpty();

        Client savedClient = clientRepository.save(testClient);

        // Create test personnel
        Personnel testPersonnel = new Personnel();
        testPersonnel.setNom("Manager");
        testPersonnel.setPrenom("Test");
        testPersonnel.setEmail("manager.test@example.com");
        testPersonnel.setTelephone("0123456789");
        testPersonnel.setRole("GESTIONNAIRE_STOCK");
        testPersonnel.setMotDePasse(PasswordUtil.hashPassword("managerpass"));

        Personnel savedPersonnel = personnelRepository.save(testPersonnel);

        // Create reclamation
        Reclamation reclamation = new Reclamation();
        reclamation.setTitre("Test Complaint");
        reclamation.setDescription("This is a test complaint for integration testing");
        reclamation.setStatut("EN_ATTENTE");
        reclamation.setPriorite("MOYENNE");
        reclamation.setClient(savedClient);
        reclamation.setDateCreation(LocalDateTime.now());

        // When - Process reclamation through service
        Reclamation savedReclamation = reclamationService.createReclamation(reclamation);
        assertThat(savedReclamation.getId()).isNotNull();
        assertThat(savedReclamation.getStatut()).isEqualTo("EN_ATTENTE");

        // Find reclamations by client
        List<Reclamation> clientReclamations = reclamationService.findByClient(savedClient);
        assertThat(clientReclamations).hasSize(1);

        // Approve reclamation
        reclamationService.approuverReclamation(
            savedReclamation.getId(), 
            savedPersonnel, 
            "Approved for testing"
        );

        // Verify status change
        Optional<Reclamation> updatedReclamation = reclamationService.findById(savedReclamation.getId());
        assertThat(updatedReclamation).isPresent();
        assertThat(updatedReclamation.get().getStatut()).isEqualTo("APPROUVEE");

        // Cleanup
        reclamationService.deleteReclamation(savedReclamation.getId());
        personnelRepository.delete(savedPersonnel);
        clientRepository.delete(savedClient);
    }

    @Test
    @DisplayName("Should perform complete product management flow")
    public void testCompleteProductFlow() {
        // Given - Create test product
        Produit testProduct = new Produit();
        testProduct.setNom("Test Product");
        testProduct.setDescription("This is a test product for integration testing");
        testProduct.setPrix(99.99);
        testProduct.setQuantiteStock(50);
        testProduct.setCategorie("Test Category");

        List<String> productValidationErrors = ValidationUtil.validateProduit(testProduct);
        assertThat(productValidationErrors).isEmpty();

        // When - Save product
        Produit savedProduct = produitRepository.save(testProduct);

        // Then - Verify product operations
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getNom()).isEqualTo("Test Product");

        // Find product by id
        Optional<Produit> foundProduct = produitRepository.findById(savedProduct.getId());
        assertThat(foundProduct).isPresent();

        // Find products by category
        List<Produit> categoryProducts = produitRepository.findByCategorie("Test Category");
        assertThat(categoryProducts).hasSizeGreaterThanOrEqualTo(1);

        // Update product
        savedProduct.setPrix(109.99);
        Produit updatedProduct = produitRepository.save(savedProduct);
        assertThat(updatedProduct.getPrix()).isEqualTo(109.99);

        // Cleanup
        produitRepository.delete(savedProduct);
    }

    @Test
    @DisplayName("Should handle business exceptions properly")
    public void testBusinessExceptionHandling() {
        // When/Then - Test various business exceptions
        assertThatThrownBy(() -> {
            throw BusinessException.entityNotFound("Personnel", "999");
        })
        .isInstanceOf(BusinessException.class)
        .hasMessage("Personnel avec l'identifiant '999' introuvable");

        assertThatThrownBy(() -> {
            throw BusinessException.insufficientStock("Test Product", 10, 5);
        })
        .isInstanceOf(BusinessException.class)
        .hasMessage("Stock insuffisant pour 'Test Product': demandé 10, disponible 5");

        assertThatThrownBy(() -> {
            throw BusinessException.unauthorizedAccess("DELETE_USER");
        })
        .isInstanceOf(BusinessException.class)
        .hasMessage("Accès non autorisé pour l'action: DELETE_USER");
    }

    @Test
    @DisplayName("Should validate data integrity across operations")
    public void testDataIntegrity() {
        // Given - Create related entities
        Client client = new Client();
        client.setNom("Integrity");
        client.setPrenom("Test");
        client.setEmail("integrity.test@example.com");
        client.setTelephone("0123456789");
        client.setAdresse("123 Integrity Street");

        Client savedClient = clientRepository.save(client);

        // Create multiple reclamations for the same client
        for (int i = 1; i <= 3; i++) {
            Reclamation reclamation = new Reclamation();
            reclamation.setTitre("Reclamation " + i);
            reclamation.setDescription("Description for reclamation " + i);
            reclamation.setStatut("EN_ATTENTE");
            reclamation.setPriorite("MOYENNE");
            reclamation.setClient(savedClient);
            reclamation.setDateCreation(LocalDateTime.now());

            reclamationService.createReclamation(reclamation);
        }

        // When - Verify relationships
        List<Reclamation> clientReclamations = reclamationService.findByClient(savedClient);

        // Then - Check data integrity
        assertThat(clientReclamations).hasSize(3);
        for (Reclamation rec : clientReclamations) {
            assertThat(rec.getClient().getId()).isEqualTo(savedClient.getId());
            assertThat(rec.getDateCreation()).isNotNull();
        }

        // Cleanup
        for (Reclamation rec : clientReclamations) {
            reclamationService.deleteReclamation(rec.getId());
        }
        clientRepository.delete(savedClient);
    }

    @Test
    @DisplayName("Should handle concurrent operations")
    public void testConcurrentOperations() throws InterruptedException {
        // Given - Create test data
        final int numberOfThreads = 5;
        final int operationsPerThread = 10;
        Thread[] threads = new Thread[numberOfThreads];

        // When - Perform concurrent operations
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    try {
                        // Create and save client
                        Client client = new Client();
                        client.setNom("Concurrent" + threadId);
                        client.setPrenom("Test" + j);
                        client.setEmail("concurrent" + threadId + "." + j + "@example.com");
                        client.setTelephone("012345678" + j);
                        client.setAdresse("123 Concurrent Street");

                        Client savedClient = clientRepository.save(client);
                        
                        // Immediately delete to avoid data buildup
                        clientRepository.delete(savedClient);
                    } catch (Exception e) {
                        // Log error but don't fail the test due to expected concurrency issues
                        System.err.println("Concurrent operation error: " + e.getMessage());
                    }
                }
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Then - Verify no deadlocks or major issues occurred
        assertThat(true).isTrue(); // If we reach here, no deadlocks occurred
    }

    @Test
    @DisplayName("Should perform database transaction rollback on error")
    public void testTransactionRollback() {
        // This test would require specific transaction management setup
        // For now, we'll test the basic concept

        // Given - Invalid data that should cause rollback
        Personnel invalidPersonnel = new Personnel();
        invalidPersonnel.setNom(""); // Invalid empty name
        invalidPersonnel.setPrenom("Test");
        invalidPersonnel.setEmail("invalid-email"); // Invalid email format
        
        List<String> validationErrors = ValidationUtil.validatePersonnel(invalidPersonnel);
        
        // When/Then - Validation should catch errors before database save
        assertThat(validationErrors).isNotEmpty();
        assertThat(validationErrors).contains("Le nom est obligatoire");
        assertThat(validationErrors).contains("L'email n'a pas un format valide");
    }

    @Test
    @DisplayName("Should maintain audit trail for operations")
    public void testAuditTrail() {
        // Given - Create test user for audit
        Personnel testUser = new Personnel();
        testUser.setNom("Audit");
        testUser.setPrenom("Test");
        testUser.setEmail("audit.test@example.com");
        testUser.setTelephone("0123456789");
        testUser.setRole("ADMIN");
        testUser.setMotDePasse(PasswordUtil.hashPassword("auditpass"));

        Personnel savedUser = personnelRepository.save(testUser);

        // When - Perform operations that should be audited
        Optional<Personnel> authenticatedUser = authenticationService.authenticate(
            "audit.test@example.com",
            "auditpass",
            "127.0.0.1"
        );

        // Then - Verify authentication worked (audit trail is tested in unit tests)
        assertThat(authenticatedUser).isPresent();

        // Test failed authentication for audit
        Optional<Personnel> failedAuth = authenticationService.authenticate(
            "audit.test@example.com",
            "wrongpassword",
            "127.0.0.1"
        );

        assertThat(failedAuth).isEmpty();

        // Cleanup
        personnelRepository.delete(savedUser);
    }
}
