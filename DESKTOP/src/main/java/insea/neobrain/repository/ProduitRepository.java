package insea.neobrain.repository;

import insea.neobrain.entity.CategorieProduit;
import insea.neobrain.entity.Produit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository interface for Produit entity
 */
public interface ProduitRepository extends GenericRepository<Produit, Long> {
    
    /**
     * Find product by reference
     * @param reference the product reference
     * @return Optional containing the product if found
     */
    Optional<Produit> findByReference(String reference);
    
    /**
     * Find product by barcode
     * @param codeBarre the product barcode
     * @return Optional containing the product if found
     */
    Optional<Produit> findByCodeBarre(String codeBarre);
    
    /**
     * Find products by name (partial match)
     * @param nom the product name or partial name
     * @return list of products matching the name
     */
    List<Produit> findByNom(String nom);
    
    /**
     * Find products containing name
     * @param nom the product name or partial name
     * @return list of products containing the name
     */
    List<Produit> findByNomContaining(String nom);
    
    /**
     * Search products by name or reference
     * @param searchTerm the search term
     * @return list of products matching the search term
     */
    List<Produit> searchByNameOrReference(String searchTerm);
    
    /**
     * Find products by category
     * @param categorie the product category
     * @return list of products in the specified category
     */
    List<Produit> findByCategorie(CategorieProduit categorie);
    
    /**
     * Find active products
     * @return list of active products
     */
    List<Produit> findActive();
    
    /**
     * Find available products for sale
     * @return list of available products for sale
     */
    List<Produit> findAvailableForSale();
    
    /**
     * Find products with low stock
     * @return list of products with low stock
     */
    List<Produit> findWithLowStock();
    
    /**
     * Find products with low stock
     * @return list of products with low stock
     */
    List<Produit> findLowStockProducts();
    
    /**
     * Find products with no stock
     * @return list of products with no stock
     */
    List<Produit> findOutOfStock();
    
    /**
     * Find products by price range
     * @param prixMin minimum price
     * @param prixMax maximum price
     * @return list of products in the price range
     */
    List<Produit> findByPriceRange(BigDecimal prixMin, BigDecimal prixMax);
    
    /**
     * Find products needing inventory
     * @return list of products needing inventory
     */
    List<Produit> findNeedingInventory();
    
    /**
     * Find products by location
     * @param emplacement the storage location
     * @return list of products at the specified location
     */
    List<Produit> findByEmplacement(String emplacement);
    
    /**
     * Find top selling products
     * @param limit the number of top products to return
     * @return list of top selling products
     */
    List<Produit> findTopSellingProducts(int limit);
    
    /**
     * Find products with stock below threshold
     * @param threshold the stock threshold
     * @return list of products with stock below threshold
     */
    List<Produit> findWithStockBelow(Integer threshold);
    
    /**
     * Check if reference exists
     * @param reference the reference to check
     * @return true if reference exists, false otherwise
     */
    boolean existsByReference(String reference);
    
    /**
     * Count products by category
     * @param categorie the category
     * @return number of products in the category
     */
    long countByCategorie(CategorieProduit categorie);
    
    /**
     * Count active products
     * @return number of active products
     */
    long countActive();
    
    // Additional methods required by service layer
    
    /**
     * Search products with multiple criteria
     * @param nom product name filter
     * @param categorie category filter
     * @param minPrice minimum price filter
     * @param maxPrice maximum price filter
     * @return list of products matching criteria
     */
    List<Produit> searchProducts(String nom, CategorieProduit categorie, BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Count products with low stock
     * @return number of products with low stock
     */
    long countLowStockProducts();
    
    /**
     * Calculate total inventory value
     * @return total value of all products in stock
     */
    BigDecimal calculateTotalInventoryValue();
    
    /**
     * Get product count by category
     * @return map of category to product count
     */
    Map<CategorieProduit, Long> getProductCountByCategory();
    
    /**
     * Check if product can be deleted
     * @param produitId the product ID
     * @return true if product can be deleted
     */
    boolean canDeleteProduct(Long produitId);

    /**
     * Find products by active status
     * @param actif the active status
     * @return list of products with the specified active status
     */
    List<Produit> findByActif(boolean actif);
}
