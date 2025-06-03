package insea.neobrain.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import insea.neobrain.model.Reclamation;
import insea.neobrain.model.Personnel;
import insea.neobrain.model.Client;
import insea.neobrain.repository.ReclamationRepository;
import insea.neobrain.util.BusinessException;
import insea.neobrain.util.AuditLogger;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Unit tests for ReclamationServiceImpl class
 */
@ExtendWith(MockitoExtension.class)
public class ReclamationServiceImplTest {

    @Mock
    private ReclamationRepository reclamationRepository;

    @Mock
    private AuditLogger auditLogger;

    @InjectMocks
    private ReclamationServiceImpl reclamationService;

    private Reclamation sampleReclamation;
    private Personnel samplePersonnel;
    private Client sampleClient;

    @BeforeEach
    void setUp() {
        sampleClient = new Client();
        sampleClient.setId(1L);
        sampleClient.setNom("Dupont");
        sampleClient.setPrenom("Jean");
        sampleClient.setEmail("jean.dupont@example.com");

        samplePersonnel = new Personnel();
        samplePersonnel.setId(1L);
        samplePersonnel.setNom("Admin");
        samplePersonnel.setPrenom("System");
        samplePersonnel.setRole("ADMIN");

        sampleReclamation = new Reclamation();
        sampleReclamation.setId(1L);
        sampleReclamation.setTitre("Problème de livraison");
        sampleReclamation.setDescription("Le produit n'est pas arrivé à temps");
        sampleReclamation.setStatut("EN_ATTENTE");
        sampleReclamation.setPriorite("MOYENNE");
        sampleReclamation.setClient(sampleClient);
        sampleReclamation.setDateCreation(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create reclamation successfully")
    public void testCreateReclamation() {
        // Given
        when(reclamationRepository.save(any(Reclamation.class))).thenReturn(sampleReclamation);

        // When
        Reclamation result = reclamationService.createReclamation(sampleReclamation);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatut()).isEqualTo("EN_ATTENTE");
        assertThat(result.getDateCreation()).isNotNull();

        verify(reclamationRepository).save(sampleReclamation);
        verify(auditLogger).logDataAccess(anyString(), eq("CREATE"), eq("Reclamation"), eq("1"));
    }

    @Test
    @DisplayName("Should throw exception when creating null reclamation")
    public void testCreateNullReclamation() {
        // When/Then
        assertThatThrownBy(() -> reclamationService.createReclamation(null))
            .isInstanceOf(BusinessException.class)
            .hasMessage("La réclamation ne peut pas être nulle");

        verify(reclamationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find reclamation by id")
    public void testFindById() {
        // Given
        when(reclamationRepository.findById(1L)).thenReturn(Optional.of(sampleReclamation));

        // When
        Optional<Reclamation> result = reclamationService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);

        verify(reclamationRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when reclamation not found")
    public void testFindByIdNotFound() {
        // Given
        when(reclamationRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Reclamation> result = reclamationService.findById(999L);

        // Then
        assertThat(result).isEmpty();

        verify(reclamationRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find all reclamations")
    public void testFindAll() {
        // Given
        List<Reclamation> reclamations = Arrays.asList(sampleReclamation);
        when(reclamationRepository.findAll()).thenReturn(reclamations);

        // When
        List<Reclamation> result = reclamationService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);

        verify(reclamationRepository).findAll();
    }

    @Test
    @DisplayName("Should find reclamations by client")
    public void testFindByClient() {
        // Given
        List<Reclamation> reclamations = Arrays.asList(sampleReclamation);
        when(reclamationRepository.findByClient(sampleClient)).thenReturn(reclamations);

        // When
        List<Reclamation> result = reclamationService.findByClient(sampleClient);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClient().getId()).isEqualTo(sampleClient.getId());

        verify(reclamationRepository).findByClient(sampleClient);
    }

    @Test
    @DisplayName("Should find reclamations by status")
    public void testFindByStatut() {
        // Given
        String statut = "EN_ATTENTE";
        List<Reclamation> reclamations = Arrays.asList(sampleReclamation);
        when(reclamationRepository.findByStatut(statut)).thenReturn(reclamations);

        // When
        List<Reclamation> result = reclamationService.findByStatut(statut);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatut()).isEqualTo(statut);

        verify(reclamationRepository).findByStatut(statut);
    }

    @Test
    @DisplayName("Should find reclamations by priority")
    public void testFindByPriorite() {
        // Given
        String priorite = "HAUTE";
        List<Reclamation> reclamations = Arrays.asList(sampleReclamation);
        when(reclamationRepository.findByPriorite(priorite)).thenReturn(reclamations);

        // When
        List<Reclamation> result = reclamationService.findByPriorite(priorite);

        // Then
        assertThat(result).hasSize(1);

        verify(reclamationRepository).findByPriorite(priorite);
    }

    @Test
    @DisplayName("Should update reclamation successfully")
    public void testUpdateReclamation() {
        // Given
        sampleReclamation.setStatut("EN_COURS");
        when(reclamationRepository.save(sampleReclamation)).thenReturn(sampleReclamation);

        // When
        Reclamation result = reclamationService.updateReclamation(sampleReclamation);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatut()).isEqualTo("EN_COURS");

        verify(reclamationRepository).save(sampleReclamation);
        verify(auditLogger).logDataAccess(anyString(), eq("UPDATE"), eq("Reclamation"), eq("1"));
    }

    @Test
    @DisplayName("Should approve reclamation successfully")
    public void testApprouverReclamation() {
        // Given
        when(reclamationRepository.findById(1L)).thenReturn(Optional.of(sampleReclamation));
        when(reclamationRepository.save(any(Reclamation.class))).thenReturn(sampleReclamation);

        // When
        reclamationService.approuverReclamation(1L, samplePersonnel, "Réclamation approuvée");

        // Then
        verify(reclamationRepository).findById(1L);
        verify(reclamationRepository).save(any(Reclamation.class));
        verify(auditLogger).logDataModification(
            eq("System Admin"), 
            eq("APPROVE"), 
            eq("Reclamation"), 
            eq("1"), 
            eq("EN_ATTENTE"), 
            eq("APPROUVEE")
        );
    }

    @Test
    @DisplayName("Should throw exception when approving non-existent reclamation")
    public void testApprouverReclamationNotFound() {
        // Given
        when(reclamationRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> 
            reclamationService.approuverReclamation(999L, samplePersonnel, "Commentaire")
        )
        .isInstanceOf(BusinessException.class)
        .hasMessage("Réclamation avec l'identifiant '999' introuvable");

        verify(reclamationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should refuse reclamation successfully")
    public void testRefuserReclamation() {
        // Given
        when(reclamationRepository.findById(1L)).thenReturn(Optional.of(sampleReclamation));
        when(reclamationRepository.save(any(Reclamation.class))).thenReturn(sampleReclamation);

        // When
        reclamationService.refuserReclamation(1L, samplePersonnel, "Réclamation refusée");

        // Then
        verify(reclamationRepository).findById(1L);
        verify(reclamationRepository).save(any(Reclamation.class));
        verify(auditLogger).logDataModification(
            eq("System Admin"), 
            eq("REFUSE"), 
            eq("Reclamation"), 
            eq("1"), 
            eq("EN_ATTENTE"), 
            eq("REFUSEE")
        );
    }

    @Test
    @DisplayName("Should throw exception when refusing non-existent reclamation")
    public void testRefuserReclamationNotFound() {
        // Given
        when(reclamationRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> 
            reclamationService.refuserReclamation(999L, samplePersonnel, "Commentaire")
        )
        .isInstanceOf(BusinessException.class)
        .hasMessage("Réclamation avec l'identifiant '999' introuvable");

        verify(reclamationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should close reclamation successfully")
    public void testCloturerReclamation() {
        // Given
        when(reclamationRepository.findById(1L)).thenReturn(Optional.of(sampleReclamation));
        when(reclamationRepository.save(any(Reclamation.class))).thenReturn(sampleReclamation);

        // When
        reclamationService.cloturerReclamation(1L, samplePersonnel, "Réclamation clôturée");

        // Then
        verify(reclamationRepository).findById(1L);
        verify(reclamationRepository).save(any(Reclamation.class));
        verify(auditLogger).logDataModification(
            eq("System Admin"), 
            eq("CLOSE"), 
            eq("Reclamation"), 
            eq("1"), 
            eq("EN_ATTENTE"), 
            eq("CLOTUREE")
        );
    }

    @Test
    @DisplayName("Should delete reclamation successfully")
    public void testDeleteReclamation() {
        // Given
        when(reclamationRepository.findById(1L)).thenReturn(Optional.of(sampleReclamation));

        // When
        reclamationService.deleteReclamation(1L);

        // Then
        verify(reclamationRepository).findById(1L);
        verify(reclamationRepository).delete(sampleReclamation);
        verify(auditLogger).logDataAccess(anyString(), eq("DELETE"), eq("Reclamation"), eq("1"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent reclamation")
    public void testDeleteReclamationNotFound() {
        // Given
        when(reclamationRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> reclamationService.deleteReclamation(999L))
            .isInstanceOf(BusinessException.class)
            .hasMessage("Réclamation avec l'identifiant '999' introuvable");

        verify(reclamationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should handle null personnel in approval")
    public void testApprouverReclamationNullPersonnel() {
        // Given
        when(reclamationRepository.findById(1L)).thenReturn(Optional.of(sampleReclamation));

        // When/Then
        assertThatThrownBy(() -> 
            reclamationService.approuverReclamation(1L, null, "Commentaire")
        )
        .isInstanceOf(BusinessException.class);

        verify(reclamationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle empty comment in approval")
    public void testApprouverReclamationEmptyComment() {
        // Given
        when(reclamationRepository.findById(1L)).thenReturn(Optional.of(sampleReclamation));
        when(reclamationRepository.save(any(Reclamation.class))).thenReturn(sampleReclamation);

        // When
        reclamationService.approuverReclamation(1L, samplePersonnel, "");

        // Then
        verify(reclamationRepository).save(any(Reclamation.class));
    }

    @Test
    @DisplayName("Should find recent reclamations")
    public void testFindRecentReclamations() {
        // Given
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        List<Reclamation> reclamations = Arrays.asList(sampleReclamation);
        when(reclamationRepository.findByDateCreationAfter(since)).thenReturn(reclamations);

        // When
        List<Reclamation> result = reclamationService.findByDateCreationAfter(since);

        // Then
        assertThat(result).hasSize(1);
        verify(reclamationRepository).findByDateCreationAfter(since);
    }

    @Test
    @DisplayName("Should count reclamations by status")
    public void testCountByStatut() {
        // Given
        String statut = "EN_ATTENTE";
        when(reclamationRepository.countByStatut(statut)).thenReturn(5L);

        // When
        long count = reclamationService.countByStatut(statut);

        // Then
        assertThat(count).isEqualTo(5L);
        verify(reclamationRepository).countByStatut(statut);
    }
}
