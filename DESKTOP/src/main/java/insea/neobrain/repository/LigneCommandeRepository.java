package insea.neobrain.repository;

import insea.neobrain.entity.LigneCommande;
import insea.neobrain.entity.CommandeVente;
import insea.neobrain.entity.Produit;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for LigneCommande entity
 * Provides data access methods for order line management
 */
public interface LigneCommandeRepository extends GenericRepository<LigneCommande, Long> {
    
    /**
     * Find order lines by command
     * @param commande The sales order
     * @return List of order lines for the command
     */
    List<LigneCommande> findByCommande(CommandeVente commande);
    
    /**
     * Find order lines by product
     * @param produit The product
     * @return List of order lines containing the product
     */
    List<LigneCommande> findByProduit(Produit produit);
    
    /**
     * Find order lines by product in date range
     * @param produit The product
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of order lines for the product in date range
     */
    List<LigneCommande> findByProduitAndDateBetween(Produit produit, LocalDate startDate, LocalDate endDate);
    
    /**
     * Calculate total quantity sold for a product
     * @param produit The product
     * @return Total quantity sold
     */
    int getTotalQuantitySold(Produit produit);
    
    /**
     * Calculate total quantity sold for a product in date range
     * @param produit The product
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return Total quantity sold in the date range
     */
    int getTotalQuantitySoldInDateRange(Produit produit, LocalDate startDate, LocalDate endDate);
    
    /**
     * Calculate total revenue for a product
     * @param produit The product
     * @return Total revenue from the product
     */
    BigDecimal getTotalRevenue(Produit produit);
    
    /**
     * Calculate total revenue for a product in date range
     * @param produit The product
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return Total revenue from the product in the date range
     */
    BigDecimal getTotalRevenueInDateRange(Produit produit, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get top selling products by quantity
     * @param limit Maximum number of products to return
     * @return List of [Product, Total Quantity] arrays
     */
    List<Object[]> getTopSellingProductsByQuantity(int limit);
    
    /**
     * Get top selling products by quantity in date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @param limit Maximum number of products to return
     * @return List of [Product, Total Quantity] arrays
     */
    List<Object[]> getTopSellingProductsByQuantityInDateRange(LocalDate startDate, LocalDate endDate, int limit);
    
    /**
     * Get top selling products by revenue
     * @param limit Maximum number of products to return
     * @return List of [Product, Total Revenue] arrays
     */
    List<Object[]> getTopSellingProductsByRevenue(int limit);
    
    /**
     * Get top selling products by revenue in date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @param limit Maximum number of products to return
     * @return List of [Product, Total Revenue] arrays
     */
    List<Object[]> getTopSellingProductsByRevenueInDateRange(LocalDate startDate, LocalDate endDate, int limit);
    
    /**
     * Get sales summary by product category
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of [Category, Quantity, Revenue] arrays
     */
    List<Object[]> getSalesSummaryByCategory(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find order lines with quantity greater than threshold
     * @param minQuantity Minimum quantity threshold
     * @return List of order lines with quantity >= minQuantity
     */
    List<LigneCommande> findByQuantityGreaterThanEqual(int minQuantity);
    
    /**
     * Find order lines with unit price in range
     * @param minPrice Minimum unit price
     * @param maxPrice Maximum unit price
     * @return List of order lines with unit price in range
     */
    List<LigneCommande> findByPrixUnitaireBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Get average selling price for a product
     * @param produit The product
     * @return Average selling price
     */
    BigDecimal getAverageSellingPrice(Produit produit);
    
    /**
     * Get order lines with applied discounts
     * @return List of order lines that have discounts applied
     */
    List<LigneCommande> findOrderLinesWithDiscounts();
    
    /**
     * Calculate total discount amount for date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return Total discount amount
     */
    BigDecimal getTotalDiscountAmount(LocalDate startDate, LocalDate endDate);
}
