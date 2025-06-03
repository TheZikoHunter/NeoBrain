package insea.neobrain.repository;

import insea.neobrain.entity.Reclamation;
import insea.neobrain.entity.LigneCommande;
import insea.neobrain.entity.Client;
import insea.neobrain.entity.EtatReclamation;
import insea.neobrain.entity.TypeReclamation;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Reclamation entity
 * Provides data access methods for complaint/return management
 */
public interface ReclamationRepository extends GenericRepository<Reclamation, Long> {
    
    /**
     * Find complaints by client
     * @param client The client
     * @return List of complaints from the client
     */
    List<Reclamation> findByClient(Client client);
    
    /**
     * Find complaints by order line
     * @param ligneCommande The order line
     * @return List of complaints for the order line
     */
    List<Reclamation> findByLigneCommande(LigneCommande ligneCommande);
    
    /**
     * Find complaints by status
     * @param etat The complaint status
     * @return List of complaints with the specified status
     */
    List<Reclamation> findByEtat(EtatReclamation etat);
    
    /**
     * Find complaints by type
     * @param type The complaint type
     * @return List of complaints of the specified type
     */
    List<Reclamation> findByType(TypeReclamation type);
    
    /**
     * Find complaints by date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of complaints in the date range
     */
    List<Reclamation> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find pending complaints (not resolved)
     * @return List of pending complaints
     */
    List<Reclamation> findPendingComplaints();
    
    /**
     * Find resolved complaints in date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of resolved complaints in the date range
     */
    List<Reclamation> findResolvedInDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find complaints by numero (complaint number) pattern
     * @param numeroPattern Pattern to match complaint numbers
     * @return List of complaints with matching numero
     */
    List<Reclamation> findByNumeroLike(String numeroPattern);
    
    /**
     * Find recent complaints (within days)
     * @param days Number of days from now
     * @return List of recent complaints
     */
    List<Reclamation> findRecentComplaints(int days);
    
    /**
     * Count complaints by status
     * @param etat The complaint status
     * @return Number of complaints with the specified status
     */
    long countByEtat(EtatReclamation etat);
    
    /**
     * Count complaints by type in date range
     * @param type The complaint type
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return Number of complaints of the type in the date range
     */
    long countByTypeAndDateBetween(TypeReclamation type, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get complaint statistics by type
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of [Type, Count] arrays
     */
    List<Object[]> getComplaintStatisticsByType(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get complaint statistics by status
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of [Status, Count] arrays
     */
    List<Object[]> getComplaintStatisticsByStatus(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get complaint trends by month for a year
     * @param year The year
     * @return List of [Month, Count] arrays
     */
    List<Object[]> getMonthlyComplaintTrends(int year);
    
    /**
     * Find complaints for a client in date range
     * @param client The client
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of complaints for the client in the date range
     */
    List<Reclamation> findByClientAndDateBetween(Client client, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get clients with most complaints
     * @param limit Maximum number of clients to return
     * @return List of [Client, Complaint Count] arrays
     */
    List<Object[]> getClientsWithMostComplaints(int limit);
    
    /**
     * Get average resolution time for resolved complaints
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return Average resolution time in days
     */
    Double getAverageResolutionTime(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find overdue complaints (created more than specified days ago and still pending)
     * @param days Number of days threshold
     * @return List of overdue complaints
     */
    List<Reclamation> findOverdueComplaints(int days);
}
