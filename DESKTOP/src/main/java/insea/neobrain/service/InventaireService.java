package insea.neobrain.service;

import insea.neobrain.entity.Inventaire;
import insea.neobrain.entity.Produit;
import insea.neobrain.entity.Personnel;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Inventaire management
 * Provides inventory management functionality
 */
public interface InventaireService {
    
    /**
     * Create a new inventory record
     * @param inventaire the inventory to create
     * @return the created inventory
     */
    Inventaire createInventaire(Inventaire inventaire);
    
    /**
     * Create a new inventory record
     * @param produit the product
     * @param quantitePhysique the physical quantity found
     * @param quantiteSysteme the system quantity
     * @param personnel the personnel who performed the inventory
     * @param observations any observations
     * @return the created inventory
     */
    Inventaire createInventaire(Produit produit, int quantitePhysique, int quantiteSysteme,
                               Personnel personnel, String observations);
    
    /**
     * Update an existing inventory record
     * @param inventaire the inventory to update
     * @return the updated inventory
     */
    Inventaire updateInventaire(Inventaire inventaire);
    
    /**
     * Delete an inventory record
     * @param id the inventory ID
     * @return true if deleted successfully
     */
    boolean deleteInventaire(Long id);
    
    /**
     * Find inventory by ID
     * @param id the inventory ID
     * @return Optional containing the inventory if found
     */
    Optional<Inventaire> findInventaireById(Long id);
    
    /**
     * Find all inventory records
     * @return list of all inventory records
     */
    List<Inventaire> findAllInventaires();
    
    /**
     * Search inventory records by description
     * @param searchTerm the search term to look for in descriptions
     * @return list of inventory records matching the search term
     */
    List<Inventaire> searchInventairesByDescription(String searchTerm);
    
    /**
     * Find inventory records by product
     * @param produit the product
     * @return list of inventory records for the product
     */
    List<Inventaire> findInventairesByProduit(Produit produit);
    
    /**
     * Find inventory records by personnel
     * @param personnel the personnel
     * @return list of inventory records by the personnel
     */
    List<Inventaire> findInventairesByPersonnel(Personnel personnel);
    
    /**
     * Find inventory records by date range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of inventory records in the date range
     */
    List<Inventaire> findInventairesByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find inventory records with discrepancies
     * @return list of inventory records with quantity differences
     */
    List<Inventaire> findInventairesWithDiscrepancies();
    
    /**
     * Find inventory records with significant discrepancies
     * @param threshold the minimum difference threshold
     * @return list of inventory records with differences >= threshold
     */
    List<Inventaire> findInventairesWithSignificantDiscrepancies(int threshold);
    
    /**
     * Start a new inventory session for all products
     * @param personnel the personnel responsible
     * @return number of inventory records created
     */
    int startFullInventory(Personnel personnel);
    
    /**
     * Start a new inventory session for specific products
     * @param produits the products to inventory
     * @param personnel the personnel responsible
     * @return number of inventory records created
     */
    int startPartialInventory(List<Produit> produits, Personnel personnel);
    
    /**
     * Complete an inventory session and update stock levels
     * @param inventaireIds the inventory IDs to complete
     * @param updateStock whether to update stock levels
     * @return number of records processed
     */
    int completeInventory(List<Long> inventaireIds, boolean updateStock);
    
    /**
     * Calculate total inventory variance
     * @return the total variance value
     */
    java.math.BigDecimal calculateTotalVariance();
    
    /**
     * Calculate inventory accuracy percentage
     * @return the accuracy percentage (0-100)
     */
    double calculateInventoryAccuracy();
    
    /**
     * Get inventory statistics by category
     * @return list of [category, count, total_variance]
     */
    List<Object[]> getInventoryStatisticsByCategory();
    
    /**
     * Get recent inventory activities
     * @param days the number of days to look back
     * @return list of recent inventory records
     */
    List<Inventaire> getRecentInventoryActivities(int days);
    
    /**
     * Validate inventory data
     * @param inventaire the inventory to validate
     * @return list of validation errors
     */
    List<String> validateInventaire(Inventaire inventaire);
    
    /**
     * Check if inventory can be deleted
     * @param inventaireId the inventory ID
     * @return true if can be deleted
     */
    boolean canDeleteInventaire(Long inventaireId);
    
    /**
     * Get inventory count
     * @return total number of inventory records
     */
    long getInventaireCount();
    
    /**
     * Export inventory data to CSV
     * @return CSV string
     */
    String exportInventairesToCSV();
    
    /**
     * Import inventory data from CSV
     * @param csvData the CSV data
     * @return number of records imported
     */
    int importInventairesFromCSV(String csvData);
}
