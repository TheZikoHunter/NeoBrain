package insea.neobrain.service;

import insea.neobrain.entity.Reclamation;
import insea.neobrain.entity.LigneCommande;
import insea.neobrain.entity.EtatReclamation;
import insea.neobrain.entity.TypeReclamation;
import insea.neobrain.util.BusinessException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Reclamation management
 * Handles complaints and returns
 */
public interface ReclamationService {
    
    /**
     * Create a new reclamation
     * @param reclamation The reclamation to create
     * @return The created reclamation
     * @throws BusinessException if creation fails
     */
    Reclamation createReclamation(Reclamation reclamation) throws BusinessException;
    
    /**
     * Create a new reclamation
     * @param ligneCommande The order line
     * @param typeReclamation Type of complaint
     * @param description Description of the issue
     * @return The created reclamation
     * @throws BusinessException if creation fails
     */
    Reclamation createReclamation(LigneCommande ligneCommande, TypeReclamation typeReclamation, String description) throws BusinessException;
    
    /**
     * Update an existing reclamation
     * @param reclamation The reclamation to update
     * @return The updated reclamation
     * @throws BusinessException if update fails
     */
    Reclamation updateReclamation(Reclamation reclamation) throws BusinessException;
    
    /**
     * Delete a reclamation
     * @param id The reclamation ID
     * @return true if deleted successfully
     * @throws BusinessException if deletion fails
     */
    boolean deleteReclamation(Long id) throws BusinessException;
    
    /**
     * Find reclamation by ID
     * @param id The reclamation ID
     * @return Optional containing the reclamation if found
     */
    Optional<Reclamation> findById(Long id);
    
    /**
     * Find all reclamations
     * @return List of all reclamations
     */
    List<Reclamation> findAll();
    
    /**
     * Find reclamations by status
     * @param etat The status to filter by
     * @return List of reclamations with the specified status
     */
    List<Reclamation> findByEtat(EtatReclamation etat);
    
    /**
     * Find reclamations by type
     * @param type The type to filter by
     * @return List of reclamations with the specified type
     */
    List<Reclamation> findByType(TypeReclamation type);
    
    /**
     * Find reclamations by date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of reclamations within the date range
     */
    List<Reclamation> findByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find reclamations for a specific order line
     * @param ligneCommandeId The order line ID
     * @return List of reclamations for the order line
     */
    List<Reclamation> findByLigneCommande(Long ligneCommandeId);
    
    /**
     * Validate a reclamation (approve)
     * @param reclamationId The reclamation ID
     * @param username The user performing the action
     * @return true if validated successfully
     * @throws BusinessException if validation fails
     */
    boolean validateReclamation(Long reclamationId, String username) throws BusinessException;
    
    /**
     * Refuse a reclamation
     * @param reclamationId The reclamation ID
     * @param username The user performing the action
     * @param reason Reason for refusal
     * @return true if refused successfully
     * @throws BusinessException if refusal fails
     */
    boolean refuseReclamation(Long reclamationId, String username, String reason) throws BusinessException;
    
    /**
     * Get reclamation statistics
     * @return Statistics about reclamations
     */
    ReclamationStats getReclamationStats();
    
    /**
     * Statistics container for reclamations
     */
    class ReclamationStats {
        private final long totalReclamations;
        private final long pendingReclamations;
        private final long validatedReclamations;
        private final long refusedReclamations;
        private final long returnRequests;
        private final long receptionFailures;
        
        public ReclamationStats(long totalReclamations, long pendingReclamations, 
                               long validatedReclamations, long refusedReclamations,
                               long returnRequests, long receptionFailures) {
            this.totalReclamations = totalReclamations;
            this.pendingReclamations = pendingReclamations;
            this.validatedReclamations = validatedReclamations;
            this.refusedReclamations = refusedReclamations;
            this.returnRequests = returnRequests;
            this.receptionFailures = receptionFailures;
        }
        
        // Getters
        public long getTotalReclamations() { return totalReclamations; }
        public long getPendingReclamations() { return pendingReclamations; }
        public long getValidatedReclamations() { return validatedReclamations; }
        public long getRefusedReclamations() { return refusedReclamations; }
        public long getReturnRequests() { return returnRequests; }
        public long getReceptionFailures() { return receptionFailures; }
    }
}
