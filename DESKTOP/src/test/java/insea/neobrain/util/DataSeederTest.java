package insea.neobrain.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import insea.neobrain.repository.PersonnelRepository;
import insea.neobrain.repository.ClientRepository;
import insea.neobrain.repository.ProduitRepository;
import insea.neobrain.repository.InventaireRepository;
import insea.neobrain.repository.ReclamationRepository;
import insea.neobrain.repository.CommandeVenteRepository;
import insea.neobrain.model.*;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for DataSeeder class
 */
@ExtendWith(MockitoExtension.class)
public class DataSeederTest {

    @Mock
    private PersonnelRepository personnelRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private InventaireRepository inventaireRepository;

    @Mock
    private ReclamationRepository reclamationRepository;

    @Mock
    private CommandeVenteRepository commandeVenteRepository;

    private DataSeeder dataSeeder;

    @BeforeEach
    void setUp() {
        dataSeeder = new DataSeeder(
            personnelRepository,
            clientRepository,
            produitRepository,
            inventaireRepository,
            reclamationRepository,
            commandeVenteRepository
        );
    }

    @Test
    @DisplayName("Should create admin user")
    public void testCreateAdminUser() {
        // Given
        when(personnelRepository.findByEmail("admin@neobrain.com")).thenReturn(java.util.Optional.empty());

        // When
        dataSeeder.createAdminUser();

        // Then
        verify(personnelRepository).findByEmail("admin@neobrain.com");
        verify(personnelRepository).save(any(Personnel.class));
    }

    @Test
    @DisplayName("Should not create admin user if already exists")
    public void testCreateAdminUserAlreadyExists() {
        // Given
        Personnel existingAdmin = new Personnel();
        existingAdmin.setEmail("admin@neobrain.com");
        when(personnelRepository.findByEmail("admin@neobrain.com")).thenReturn(java.util.Optional.of(existingAdmin));

        // When
        dataSeeder.createAdminUser();

        // Then
        verify(personnelRepository).findByEmail("admin@neobrain.com");
        verify(personnelRepository, never()).save(any(Personnel.class));
    }

    @Test
    @DisplayName("Should seed sample personnel")
    public void testSeedSamplePersonnel() {
        // Given
        when(personnelRepository.count()).thenReturn(0L);

        // When
        List<Personnel> personnel = dataSeeder.seedSamplePersonnel();

        // Then
        assertThat(personnel).hasSize(10);
        verify(personnelRepository).count();
        verify(personnelRepository, times(10)).save(any(Personnel.class));
    }

    @Test
    @DisplayName("Should not seed personnel if data already exists")
    public void testSeedSamplePersonnelDataExists() {
        // Given
        when(personnelRepository.count()).thenReturn(5L);

        // When
        List<Personnel> personnel = dataSeeder.seedSamplePersonnel();

        // Then
        assertThat(personnel).isEmpty();
        verify(personnelRepository).count();
        verify(personnelRepository, never()).save(any(Personnel.class));
    }

    @Test
    @DisplayName("Should seed sample clients")
    public void testSeedSampleClients() {
        // Given
        when(clientRepository.count()).thenReturn(0L);

        // When
        List<Client> clients = dataSeeder.seedSampleClients();

        // Then
        assertThat(clients).hasSize(15);
        verify(clientRepository).count();
        verify(clientRepository, times(15)).save(any(Client.class));
    }

    @Test
    @DisplayName("Should seed sample produits")
    public void testSeedSampleProduits() {
        // Given
        when(produitRepository.count()).thenReturn(0L);

        // When
        List<Produit> produits = dataSeeder.seedSampleProduits();

        // Then
        assertThat(produits).hasSize(20);
        verify(produitRepository).count();
        verify(produitRepository, times(20)).save(any(Produit.class));
    }

    @Test
    @DisplayName("Should seed sample inventaires")
    public void testSeedSampleInventaires() {
        // Given
        when(inventaireRepository.count()).thenReturn(0L);
        
        List<Personnel> mockPersonnel = Arrays.asList(new Personnel(), new Personnel());
        List<Produit> mockProduits = Arrays.asList(new Produit(), new Produit());

        // When
        List<Inventaire> inventaires = dataSeeder.seedSampleInventaires(mockPersonnel, mockProduits);

        // Then
        assertThat(inventaires).hasSize(3);
        verify(inventaireRepository).count();
        verify(inventaireRepository, times(3)).save(any(Inventaire.class));
    }

    @Test
    @DisplayName("Should seed sample reclamations")
    public void testSeedSampleReclamations() {
        // Given
        when(reclamationRepository.count()).thenReturn(0L);
        
        List<Client> mockClients = Arrays.asList(new Client(), new Client());

        // When
        List<Reclamation> reclamations = dataSeeder.seedSampleReclamations(mockClients);

        // Then
        assertThat(reclamations).hasSize(8);
        verify(reclamationRepository).count();
        verify(reclamationRepository, times(8)).save(any(Reclamation.class));
    }

    @Test
    @DisplayName("Should seed sample commandes")
    public void testSeedSampleCommandes() {
        // Given
        when(commandeVenteRepository.count()).thenReturn(0L);
        
        List<Client> mockClients = Arrays.asList(new Client(), new Client());
        List<Produit> mockProduits = Arrays.asList(new Produit(), new Produit());

        // When
        List<CommandeVente> commandes = dataSeeder.seedSampleCommandes(mockClients, mockProduits);

        // Then
        assertThat(commandes).hasSize(12);
        verify(commandeVenteRepository).count();
        verify(commandeVenteRepository, times(12)).save(any(CommandeVente.class));
    }

    @Test
    @DisplayName("Should seed all data in correct order")
    public void testSeedAllData() {
        // Given
        when(personnelRepository.count()).thenReturn(0L);
        when(clientRepository.count()).thenReturn(0L);
        when(produitRepository.count()).thenReturn(0L);
        when(inventaireRepository.count()).thenReturn(0L);
        when(reclamationRepository.count()).thenReturn(0L);
        when(commandeVenteRepository.count()).thenReturn(0L);

        // When
        dataSeeder.seedAllData();

        // Then
        // Verify that repositories are called in correct order
        verify(personnelRepository).findByEmail("admin@neobrain.com");
        verify(personnelRepository, atLeast(1)).save(any(Personnel.class));
        verify(clientRepository, atLeast(1)).save(any(Client.class));
        verify(produitRepository, atLeast(1)).save(any(Produit.class));
        verify(inventaireRepository, atLeast(1)).save(any(Inventaire.class));
        verify(reclamationRepository, atLeast(1)).save(any(Reclamation.class));
        verify(commandeVenteRepository, atLeast(1)).save(any(CommandeVente.class));
    }

    @Test
    @DisplayName("Should handle empty personnel list for inventaires")
    public void testSeedInventairesWithEmptyPersonnel() {
        // Given
        when(inventaireRepository.count()).thenReturn(0L);
        List<Personnel> emptyPersonnel = Arrays.asList();
        List<Produit> mockProduits = Arrays.asList(new Produit());

        // When
        List<Inventaire> inventaires = dataSeeder.seedSampleInventaires(emptyPersonnel, mockProduits);

        // Then
        assertThat(inventaires).isEmpty();
        verify(inventaireRepository).count();
        verify(inventaireRepository, never()).save(any(Inventaire.class));
    }

    @Test
    @DisplayName("Should handle empty produits list for inventaires")
    public void testSeedInventairesWithEmptyProduits() {
        // Given
        when(inventaireRepository.count()).thenReturn(0L);
        List<Personnel> mockPersonnel = Arrays.asList(new Personnel());
        List<Produit> emptyProduits = Arrays.asList();

        // When
        List<Inventaire> inventaires = dataSeeder.seedSampleInventaires(mockPersonnel, emptyProduits);

        // Then
        assertThat(inventaires).isEmpty();
        verify(inventaireRepository).count();
        verify(inventaireRepository, never()).save(any(Inventaire.class));
    }

    @Test
    @DisplayName("Should handle empty clients list for reclamations")
    public void testSeedReclamationsWithEmptyClients() {
        // Given
        when(reclamationRepository.count()).thenReturn(0L);
        List<Client> emptyClients = Arrays.asList();

        // When
        List<Reclamation> reclamations = dataSeeder.seedSampleReclamations(emptyClients);

        // Then
        assertThat(reclamations).isEmpty();
        verify(reclamationRepository).count();
        verify(reclamationRepository, never()).save(any(Reclamation.class));
    }

    @Test
    @DisplayName("Should handle empty clients list for commandes")
    public void testSeedCommandesWithEmptyClients() {
        // Given
        when(commandeVenteRepository.count()).thenReturn(0L);
        List<Client> emptyClients = Arrays.asList();
        List<Produit> mockProduits = Arrays.asList(new Produit());

        // When
        List<CommandeVente> commandes = dataSeeder.seedSampleCommandes(emptyClients, mockProduits);

        // Then
        assertThat(commandes).isEmpty();
        verify(commandeVenteRepository).count();
        verify(commandeVenteRepository, never()).save(any(CommandeVente.class));
    }

    @Test
    @DisplayName("Should handle null lists gracefully")
    public void testSeedWithNullLists() {
        // Given
        when(inventaireRepository.count()).thenReturn(0L);
        when(reclamationRepository.count()).thenReturn(0L);
        when(commandeVenteRepository.count()).thenReturn(0L);

        // When/Then
        assertThatCode(() -> {
            dataSeeder.seedSampleInventaires(null, null);
            dataSeeder.seedSampleReclamations(null);
            dataSeeder.seedSampleCommandes(null, null);
        }).doesNotThrowAnyException();

        verify(inventaireRepository, never()).save(any(Inventaire.class));
        verify(reclamationRepository, never()).save(any(Reclamation.class));
        verify(commandeVenteRepository, never()).save(any(CommandeVente.class));
    }

    @Test
    @DisplayName("Should create valid personnel data")
    public void testPersonnelDataValidity() {
        // Given
        when(personnelRepository.count()).thenReturn(0L);

        // When
        List<Personnel> personnel = dataSeeder.seedSamplePersonnel();

        // Then
        for (Personnel p : personnel) {
            assertThat(p.getNom()).isNotNull().isNotEmpty();
            assertThat(p.getPrenom()).isNotNull().isNotEmpty();
            assertThat(p.getEmail()).isNotNull().contains("@");
            assertThat(p.getTelephone()).isNotNull().isNotEmpty();
            assertThat(p.getRole()).isIn("ADMIN", "GESTIONNAIRE_STOCK", "EMPLOYE");
            assertThat(p.getMotDePasse()).isNotNull().isNotEmpty();
        }
    }

    @Test
    @DisplayName("Should create valid client data")
    public void testClientDataValidity() {
        // Given
        when(clientRepository.count()).thenReturn(0L);

        // When
        List<Client> clients = dataSeeder.seedSampleClients();

        // Then
        for (Client c : clients) {
            assertThat(c.getNom()).isNotNull().isNotEmpty();
            assertThat(c.getPrenom()).isNotNull().isNotEmpty();
            assertThat(c.getEmail()).isNotNull().contains("@");
            assertThat(c.getTelephone()).isNotNull().isNotEmpty();
            assertThat(c.getAdresse()).isNotNull().isNotEmpty();
        }
    }

    @Test
    @DisplayName("Should create valid produit data")
    public void testProduitDataValidity() {
        // Given
        when(produitRepository.count()).thenReturn(0L);

        // When
        List<Produit> produits = dataSeeder.seedSampleProduits();

        // Then
        for (Produit p : produits) {
            assertThat(p.getNom()).isNotNull().isNotEmpty();
            assertThat(p.getDescription()).isNotNull().isNotEmpty();
            assertThat(p.getPrix()).isPositive();
            assertThat(p.getQuantiteStock()).isNotNegative();
            assertThat(p.getCategorie()).isNotNull().isNotEmpty();
        }
    }

    @Test
    @DisplayName("Should handle repository save exceptions gracefully")
    public void testHandleRepositoryExceptions() {
        // Given
        when(personnelRepository.count()).thenReturn(0L);
        when(personnelRepository.save(any(Personnel.class))).thenThrow(new RuntimeException("Database error"));

        // When/Then
        assertThatCode(() -> dataSeeder.seedSamplePersonnel())
            .doesNotThrowAnyException();

        verify(personnelRepository).save(any(Personnel.class));
    }
}
