package insea.neobrain.service;

import insea.neobrain.entity.Produit;
import insea.neobrain.entity.CategorieProduit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for product management
 * Handles CRUD operations and business logic for product entities
 */
public interface ProduitService {
    
    /**
     * Create a new product
     * @param produit The product to create
     * @return The created product with generated ID
     */
    Produit createProduit(Produit produit);
    
    /**
     * Create a new product with basic information
     * @param nom Product name
     * @param description Product description
     * @param categorie Product category
     * @param prix Unit price
     * @param quantiteStock Initial stock quantity
     * @param seuilStock Minimum stock threshold
     * @return The created product
     */
    Produit createProduit(String nom, String description, CategorieProduit categorie,
                         BigDecimal prix, int quantiteStock, int seuilStock);
    
    /**
     * Update an existing product
     * @param produit The product to update
     * @return The updated product
     */
    Produit updateProduit(Produit produit);
    
    /**
     * Delete a product by ID
     * @param id The product ID
     * @return true if deleted successfully, false otherwise
     */
    boolean deleteProduit(Long id);
    
    /**
     * Find product by ID
     * @param id The product ID
     * @return Optional containing the product if found
     */
    Optional<Produit> findProduitById(Long id);
    
    /**
     * Find all products
     * @return List of all products
     */
    List<Produit> findAllProduits();
    
    /**
     * Find products by category
     * @param categorie The product category
     * @return List of products in the specified category
     */
    List<Produit> findProduitsByCategory(CategorieProduit categorie);

    /**
     * Find product by barcode
     * @param codeBarre The product barcode
     * @return Optional containing the product if found
     */
    Optional<Produit> findProduitByCodeBarre(String codeBarre);

    /**
     * Find products by category (alias)
     * @param categorie The product category
     * @return List of products in the specified category
     */
    List<Produit> findProduitsByCategorie(CategorieProduit categorie);
    
    /**
     * Find products by name (partial match)
     * @param nom Product name (partial)
     * @return List of products matching the name
     */
    List<Produit> findProduitsByName(String nom);
    
    /**
     * Find products with low stock (below threshold)
     * @return List of products with stock below their threshold
     */
    List<Produit> findLowStockProducts();

    /**
     * Find products with low stock (alias)
     * @return List of products with stock below their threshold
     */
    List<Produit> findLowStockProduits();
    
    /**
     * Find products with no stock (zero quantity)
     * @return List of out-of-stock products
     */
    List<Produit> findOutOfStockProducts();
    
    /**
     * Find products by price range
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of products in the price range
     */
    List<Produit> findProduitsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Find products by price range (alias)
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of products in the price range
     */
    List<Produit> findProduitsInPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Search products by multiple criteria
     * @param nom Product name (partial, optional)
     * @param categorie Product category (optional)
     * @param minPrice Minimum price (optional)
     * @param maxPrice Maximum price (optional)
     * @param inStock Only products in stock (optional)
     * @return List of products matching the criteria
     */
    List<Produit> searchProduits(String nom, CategorieProduit categorie,
                                BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock);

    /**
     * Search products by multiple criteria (4-parameter version)
     * @param nom Product name (partial, optional)
     * @param categorie Product category (optional)
     * @param minPrice Minimum price (optional)
     * @param maxPrice Maximum price (optional)
     * @return List of products matching the criteria
     */
    List<Produit> searchProduits(String nom, CategorieProduit categorie,
                                BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Update product stock quantity
     * @param produitId Product ID
     * @param newQuantity New stock quantity
     * @return true if updated successfully, false otherwise
     */
    boolean updateStock(Long produitId, int newQuantity);
    
    /**
     * Add stock to a product
     * @param produitId Product ID
     * @param quantity Quantity to add
     * @return true if added successfully, false otherwise
     */
    boolean addStock(Long produitId, int quantity);
    
    /**
     * Remove stock from a product
     * @param produitId Product ID
     * @param quantity Quantity to remove
     * @return true if removed successfully, false if insufficient stock
     */
    boolean removeStock(Long produitId, int quantity);
    
    /**
     * Check if product has sufficient stock
     * @param produitId Product ID
     * @param requiredQuantity Required quantity
     * @return true if sufficient stock available, false otherwise
     */
    boolean hasSufficientStock(Long produitId, int requiredQuantity);
    
    /**
     * Update product price
     * @param produitId Product ID
     * @param newPrice New price
     * @return true if updated successfully, false otherwise
     */
    boolean updatePrice(Long produitId, BigDecimal newPrice);
    
    /**
     * Update stock threshold for a product
     * @param produitId Product ID
     * @param newThreshold New stock threshold
     * @return true if updated successfully, false otherwise
     */
    boolean updateStockThreshold(Long produitId, int newThreshold);
    
    /**
     * Get products requiring restock (stock <= threshold)
     * @return List of products that need restocking
     */
    List<Produit> getProductsRequiringRestock();
    
    /**
     * Get stock value for all products
     * @return Total value of all stock (quantity * price)
     */
    BigDecimal getTotalStockValue();
    
    /**
     * Get stock value by category
     * @param categorie Product category
     * @return Total stock value for the category
     */
    BigDecimal getStockValueByCategory(CategorieProduit categorie);
    
    /**
     * Get product statistics by category
     * @return List of [Category, Count, Total Value] arrays
     */
    List<Object[]> getProductStatisticsByCategory();
    
    /**
     * Get low stock alerts
     * @return List of products with stock alerts
     */
    List<Produit> getLowStockAlerts();

    /**
     * Adjust product stock with reason
     * @param produitId Product ID
     * @param quantity Quantity adjustment (positive or negative)
     * @param reason Reason for the adjustment
     * @return true if adjusted successfully, false otherwise
     */
    boolean adjustStock(Long produitId, int quantity, String reason);

    /**
     * Check if a barcode is available
     * @param codeBarre Barcode to check
     * @return true if available, false if already taken
     */
    boolean isBarcodeAvailable(String codeBarre);

    /**
     * Generate a unique barcode
     * @return Generated barcode string
     */
    String generateBarcode();

    /**
     * Get total number of products
     * @return Total product count
     */
    long getProduitCount();

    /**
     * Get number of low stock products
     * @return Low stock product count
     */
    long getLowStockCount();

    /**
     * Get total inventory value
     * @return Total value of all inventory
     */
    BigDecimal getTotalInventoryValue();

    /**
     * Get top selling products
     * @param limit Maximum number of products to return
     * @return List of top selling products data
     */
    List<Object[]> getTopSellingProducts(int limit);

    /**
     * Check if product can be deleted (alias)
     * @param produitId Product ID
     * @return true if can be deleted, false if has associated sales
     */
    boolean canDeleteProduit(Long produitId);

    /**
     * Export products to CSV (alias)
     * @return CSV string containing product data
     */
    String exportProduitsToCSV();

    /**
     * Import products from CSV (alias)
     * @param csvData CSV string containing product data
     * @return Number of products imported successfully
     */
    int importProduitsFromCSV(String csvData);

    /**
     * Validate product data before create/update
     * @param produit The product to validate
     * @return List of validation error messages, empty if valid
     */
    List<String> validateProduit(Produit produit);
    
    /**
     * Check if a product name is available
     * @param nom Product name to check
     * @return true if available, false if already taken
     */
    boolean isProductNameAvailable(String nom);
    
    /**
     * Check if a product name is available for update (excluding current product)
     * @param nom Product name to check
     * @param currentProductId The ID of the current product being updated
     * @return true if available, false if taken by another product
     */
    boolean isProductNameAvailableForUpdate(String nom, Long currentProductId);
    
    /**
     * Get products count
     * @return Total number of products
     */
    long getProductCount();
    
    /**
     * Get products count by category
     * @param categorie Product category
     * @return Number of products in the category
     */
    long getProductCountByCategory(CategorieProduit categorie);
    
    /**
     * Check if product can be deleted (no associated sales)
     * @param produitId Product ID
     * @return true if can be deleted, false if has associated sales
     */
    boolean canDeleteProduct(Long produitId);
    
    /**
     * Get most sold products
     * @param limit Maximum number of products to return
     * @return List of [Product, Quantity Sold] arrays
     */
    List<Object[]> getMostSoldProducts(int limit);
    
    /**
     * Get highest revenue products
     * @param limit Maximum number of products to return
     * @return List of [Product, Revenue] arrays
     */
    List<Object[]> getHighestRevenueProducts(int limit);
    
    /**
     * Export product data to CSV format
     * @return CSV string containing product data
     */
    String exportProductsToCSV();
    
    /**
     * Import product data from CSV
     * @param csvData CSV string containing product data
     * @return Number of products imported successfully
     */
    int importProductsFromCSV(String csvData);
    
    /**
     * Calculate reorder quantity based on sales history
     * @param produitId Product ID
     * @param days Number of days to analyze
     * @return Suggested reorder quantity
     */
    int calculateReorderQuantity(Long produitId, int days);
    
    /**
     * Get stock movement history for a product
     * @param produitId Product ID
     * @return List of stock movements with dates and quantities
     */
    List<Object[]> getStockMovementHistory(Long produitId);
}
