package insea.neobrain.repository;

import insea.neobrain.entity.Inventaire;
import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.Produit;
import insea.neobrain.entity.CategorieProduit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository interface for Inventaire entity
 */
public interface InventaireRepository extends GenericRepository<Inventaire, Long> {
    
    /**
     * Find inventory by number
     * @param numeroInventaire the inventory number
     * @return Optional containing the inventory if found
     */
    Optional<Inventaire> findByNumeroInventaire(String numeroInventaire);
    
    /**
     * Find active (open) inventories
     * @return list of active inventories
     */
    List<Inventaire> findActive();
    
    /**
     * Find closed inventories
     * @return list of closed inventories
     */
    List<Inventaire> findClosed();
    
    /**
     * Find inventories by date range
     * @param dateDebut start date
     * @param dateFin end date
     * @return list of inventories in the date range
     */
    List<Inventaire> findByDateRange(LocalDate dateDebut, LocalDate dateFin);
    
    /**
     * Find inventories by responsible person
     * @param responsable the responsible person name
     * @return list of inventories managed by the person
     */
    List<Inventaire> findByResponsable(String responsable);
    
    /**
     * Find inventories by description containing search term
     * @param searchTerm the search term to look for in descriptions
     * @return list of inventories with descriptions containing the search term
     */
    List<Inventaire> findByDescriptionContaining(String searchTerm);
    
    /**
     * Find inventories by state
     * @param etatInventaire the inventory state
     * @return list of inventories in the specified state
     */
    List<Inventaire> findByEtat(String etatInventaire);
    
    /**
     * Find inventories with discrepancies
     * @return list of inventories that have detected discrepancies
     */
    List<Inventaire> findWithDiscrepancies();
    
    /**
     * Find current year inventories
     * @return list of inventories from current year
     */
    List<Inventaire> findCurrentYear();
    
    /**
     * Find inventories by completion percentage
     * @param minPercentage minimum completion percentage
     * @return list of inventories with completion above minimum
     */
    List<Inventaire> findByCompletionAbove(double minPercentage);
    
    /**
     * Find most recent inventory
     * @return Optional containing the most recent inventory
     */
    Optional<Inventaire> findMostRecent();
    
    /**
     * Find inventories needing attention (incomplete after deadline)
     * @return list of inventories needing attention
     */
    List<Inventaire> findNeedingAttention();
    
    /**
     * Check if inventory number exists
     * @param numeroInventaire the inventory number to check
     * @return true if number exists, false otherwise
     */
    boolean existsByNumeroInventaire(String numeroInventaire);
    
    /**
     * Count inventories by year
     * @param year the year
     * @return number of inventories in the year
     */
    long countByYear(int year);
    
    /**
     * Count active inventories
     * @return number of active inventories
     */
    long countActive();
    
    // Additional methods required by service layer (working with TacheInventaire)
    
    /**
     * Find inventory tasks by product
     * @param produit the product
     * @return list of inventory tasks for the product
     */
    List<Inventaire> findByProduit(Produit produit);
    
    /**
     * Find inventory tasks by personnel
     * @param personnel the personnel
     * @return list of inventory tasks for the personnel
     */
    List<Inventaire> findByPersonnel(Personnel personnel);
    
    /**
     * Find inventory tasks with significant discrepancies
     * @param threshold the variance threshold
     * @return list of inventory tasks with discrepancies above threshold
     */
    List<Inventaire> findWithSignificantDiscrepancies(int threshold);
    
    /**
     * Calculate total variance across all inventory tasks
     * @return total variance value
     */
    BigDecimal calculateTotalVariance();
    
    /**
     * Count accurate inventory tasks (zero variance)
     * @return number of accurate inventory tasks
     */
    long countAccurateInventories();
    
    /**
     * Get inventory statistics by category
     * @return map of category to statistics
     */
    Map<CategorieProduit, Object[]> getInventoryStatisticsByCategory();
}
