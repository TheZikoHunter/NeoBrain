package insea.neobrain.service.impl;

import insea.neobrain.entity.Reclamation;
import insea.neobrain.entity.LigneCommande;
import insea.neobrain.entity.EtatReclamation;
import insea.neobrain.entity.TypeReclamation;
import insea.neobrain.repository.ReclamationRepository;
import insea.neobrain.service.ReclamationService;
import insea.neobrain.util.AuditLogger;
import insea.neobrain.util.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of ReclamationService
 */
public class ReclamationServiceImpl implements ReclamationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReclamationServiceImpl.class);
    private final ReclamationRepository reclamationRepository;
    
    public ReclamationServiceImpl(ReclamationRepository reclamationRepository) {
        this.reclamationRepository = reclamationRepository;
    }
    
    @Override
    public Reclamation createReclamation(Reclamation reclamation) throws BusinessException {
        try {
            if (reclamation == null) {
                throw new BusinessException("VALIDATION_ERROR", "Reclamation cannot be null");
            }
            
            // Set initial status
            if (reclamation.getEtatReclamation() == null) {
                reclamation.setEtatReclamation(EtatReclamation.EN_ATTENTE);
            }
            
            // Set creation date
            if (reclamation.getDateReclamation() == null) {
                reclamation.setDateReclamation(LocalDateTime.now());
            }
            
            Reclamation saved = reclamationRepository.save(reclamation);
            
            AuditLogger.logDataChange("system", "Reclamation", "CREATE", saved.getIdReclamation(), 
                "Type: " + reclamation.getTypeReclamation() + ", Status: " + reclamation.getEtatReclamation());
            
            logger.info("Created new reclamation with ID: {}", saved.getIdReclamation());
            return saved;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating reclamation", e);
            throw new BusinessException("CREATE_ERROR", "Error creating reclamation", e);
        }
    }
    
    @Override
    public Reclamation createReclamation(LigneCommande ligneCommande, TypeReclamation typeReclamation, String description) throws BusinessException {
        Reclamation reclamation = new Reclamation();
        reclamation.setLigneCommande(ligneCommande);
        reclamation.setTypeReclamation(typeReclamation);
        reclamation.setDescription(description);
        reclamation.setDateReclamation(LocalDateTime.now());
        reclamation.setEtatReclamation(EtatReclamation.EN_ATTENTE);
        
        return createReclamation(reclamation);
    }
    
    @Override
    public Reclamation updateReclamation(Reclamation reclamation) throws BusinessException {
        try {
            if (reclamation == null || reclamation.getIdReclamation() == null) {
                throw new BusinessException("VALIDATION_ERROR", "Reclamation ID cannot be null");
            }
            
            Optional<Reclamation> existing = reclamationRepository.findById(reclamation.getIdReclamation());
            if (existing.isEmpty()) {
                throw BusinessException.entityNotFound("Reclamation", reclamation.getIdReclamation());
            }
            
            Reclamation updated = reclamationRepository.update(reclamation);
            
            AuditLogger.logDataChange("system", "Reclamation", "UPDATE", updated.getIdReclamation(), 
                "Status: " + updated.getEtatReclamation());
            
            logger.info("Updated reclamation with ID: {}", updated.getIdReclamation());
            return updated;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating reclamation with ID: {}", reclamation.getIdReclamation(), e);
            throw new BusinessException("UPDATE_ERROR", "Error updating reclamation", e);
        }
    }
    
    @Override
    public boolean deleteReclamation(Long id) throws BusinessException {
        try {
            if (id == null) {
                throw new BusinessException("VALIDATION_ERROR", "Reclamation ID cannot be null");
            }
            
            Optional<Reclamation> existing = reclamationRepository.findById(id);
            if (existing.isEmpty()) {
                return false;
            }
            
            // Check if reclamation can be deleted (business rule: only pending reclamations)
            if (existing.get().getEtatReclamation() != EtatReclamation.EN_ATTENTE) {
                throw BusinessException.invalidState("Reclamation", 
                    existing.get().getEtatReclamation().name(), "DELETE");
            }
            
            reclamationRepository.delete(existing.get());
            
            AuditLogger.logDataChange("system", "Reclamation", "DELETE", id, 
                "Type: " + existing.get().getTypeReclamation());
            
            logger.info("Deleted reclamation with ID: {}", id);
            return true;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting reclamation with ID: {}", id, e);
            throw new BusinessException("DELETE_ERROR", "Error deleting reclamation", e);
        }
    }
    
    @Override
    public Optional<Reclamation> findById(Long id) {
        try {
            return reclamationRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error finding reclamation by ID: {}", id, e);
            throw new RuntimeException("Error finding reclamation", e);
        }
    }
    
    @Override
    public List<Reclamation> findAll() {
        try {
            return reclamationRepository.findAll();
        } catch (Exception e) {
            logger.error("Error finding all reclamations", e);
            throw new RuntimeException("Error finding reclamations", e);
        }
    }
    
    @Override
    public List<Reclamation> findByEtat(EtatReclamation etat) {
        try {
            return reclamationRepository.findByEtatReclamation(etat);
        } catch (Exception e) {
            logger.error("Error finding reclamations by status: {}", etat, e);
            throw new RuntimeException("Error finding reclamations by status", e);
        }
    }
    
    @Override
    public List<Reclamation> findByType(TypeReclamation type) {
        try {
            return reclamationRepository.findByTypeReclamation(type);
        } catch (Exception e) {
            logger.error("Error finding reclamations by type: {}", type, e);
            throw new RuntimeException("Error finding reclamations by type", e);
        }
    }
    
    @Override
    public List<Reclamation> findByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return reclamationRepository.findByDateRange(startDate, endDate);
        } catch (Exception e) {
            logger.error("Error finding reclamations by date range: {} to {}", startDate, endDate, e);
            throw new RuntimeException("Error finding reclamations by date range", e);
        }
    }
    
    @Override
    public List<Reclamation> findByLigneCommande(Long ligneCommandeId) {
        try {
            return reclamationRepository.findByLigneCommandeId(ligneCommandeId);
        } catch (Exception e) {
            logger.error("Error finding reclamations by order line ID: {}", ligneCommandeId, e);
            throw new RuntimeException("Error finding reclamations by order line", e);
        }
    }
    
    @Override
    public boolean validateReclamation(Long reclamationId, String username) throws BusinessException {
        try {
            Optional<Reclamation> reclamationOpt = reclamationRepository.findById(reclamationId);
            if (reclamationOpt.isEmpty()) {
                throw BusinessException.entityNotFound("Reclamation", reclamationId);
            }
            
            Reclamation reclamation = reclamationOpt.get();
            
            // Business rule: only pending reclamations can be validated
            if (reclamation.getEtatReclamation() != EtatReclamation.EN_ATTENTE) {
                throw BusinessException.invalidState("Reclamation", 
                    reclamation.getEtatReclamation().name(), "VALIDATE");
            }
            
            reclamation.setEtatReclamation(EtatReclamation.VALIDEE);
            reclamationRepository.update(reclamation);
            
            AuditLogger.logUserAction(username, "VALIDATE_RECLAMATION", 
                "Validated reclamation ID: " + reclamationId);
            
            logger.info("Validated reclamation ID: {} by user: {}", reclamationId, username);
            return true;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error validating reclamation ID: {}", reclamationId, e);
            throw new BusinessException("VALIDATION_ERROR", "Error validating reclamation", e);
        }
    }
    
    @Override
    public boolean refuseReclamation(Long reclamationId, String username, String reason) throws BusinessException {
        try {
            Optional<Reclamation> reclamationOpt = reclamationRepository.findById(reclamationId);
            if (reclamationOpt.isEmpty()) {
                throw BusinessException.entityNotFound("Reclamation", reclamationId);
            }
            
            Reclamation reclamation = reclamationOpt.get();
            
            // Business rule: only pending reclamations can be refused
            if (reclamation.getEtatReclamation() != EtatReclamation.EN_ATTENTE) {
                throw BusinessException.invalidState("Reclamation", 
                    reclamation.getEtatReclamation().name(), "REFUSE");
            }
            
            reclamation.setEtatReclamation(EtatReclamation.REFUSEE);
            // Could add a reason field to the entity
            reclamationRepository.update(reclamation);
            
            AuditLogger.logUserAction(username, "REFUSE_RECLAMATION", 
                "Refused reclamation ID: " + reclamationId + ", Reason: " + reason);
            
            logger.info("Refused reclamation ID: {} by user: {} for reason: {}", 
                       reclamationId, username, reason);
            return true;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error refusing reclamation ID: {}", reclamationId, e);
            throw new BusinessException("REFUSAL_ERROR", "Error refusing reclamation", e);
        }
    }
    
    @Override
    public ReclamationStats getReclamationStats() {
        try {
            long total = reclamationRepository.countAll();
            long pending = reclamationRepository.countByEtat(EtatReclamation.EN_ATTENTE);
            long validated = reclamationRepository.countByEtat(EtatReclamation.VALIDEE);
            long refused = reclamationRepository.countByEtat(EtatReclamation.REFUSEE);
            long returns = reclamationRepository.countByType(TypeReclamation.RETOUR);
            long failures = reclamationRepository.countByType(TypeReclamation.ECHEC_RECEPTION);
            
            return new ReclamationStats(total, pending, validated, refused, returns, failures);
            
        } catch (Exception e) {
            logger.error("Error getting reclamation statistics", e);
            throw new RuntimeException("Error getting reclamation statistics", e);
        }
    }
}
