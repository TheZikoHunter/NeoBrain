package insea.neobrain.repository;

import insea.neobrain.entity.CommandeVente;
import insea.neobrain.entity.Client;
import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.ModePaiement;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for CommandeVente entity
 * Provides data access methods for sales order management
 */
public interface CommandeVenteRepository extends GenericRepository<CommandeVente, Long> {
    
    /**
     * Find orders by client
     * @param client The client
     * @return List of orders for the client
     */
    List<CommandeVente> findByClient(Client client);
    
    /**
     * Find orders by personnel (who created the order)
     * @param personnel The personnel
     * @return List of orders created by the personnel
     */
    List<CommandeVente> findByPersonnel(Personnel personnel);
    
    /**
     * Find orders by payment method
     * @param modePaiement The payment method
     * @return List of orders with the specified payment method
     */
    List<CommandeVente> findByModePaiement(ModePaiement modePaiement);
    
    /**
     * Find orders by date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of orders in the date range
     */
    List<CommandeVente> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find orders by minimum total amount
     * @param minAmount Minimum total amount
     * @return List of orders with total >= minAmount
     */
    List<CommandeVente> findByTotalGreaterThanEqual(BigDecimal minAmount);
    
    /**
     * Find recent orders (within days)
     * @param days Number of days from now
     * @return List of recent orders
     */
    List<CommandeVente> findRecentOrders(int days);
    
    /**
     * Find orders for a client in date range
     * @param client The client
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of orders for the client in the date range
     */
    List<CommandeVente> findByClientAndDateBetween(Client client, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get top clients by order value in date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @param limit Maximum number of clients to return
     * @return List of [Client, Total Amount] arrays
     */
    List<Object[]> getTopClientsByValue(LocalDate startDate, LocalDate endDate, int limit);
    
    /**
     * Get daily sales totals for date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of [Date, Total Amount] arrays
     */
    List<Object[]> getDailySalesTotals(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get monthly sales summary for a year
     * @param year The year
     * @return List of [Month, Total Amount, Order Count] arrays
     */
    List<Object[]> getMonthlySalesSummary(int year);
    
    /**
     * Calculate total sales for date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return Total sales amount
     */
    BigDecimal getTotalSalesAmount(LocalDate startDate, LocalDate endDate);
    
    /**
     * Count orders for date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return Number of orders in the date range
     */
    long countOrdersInDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get average order value for date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return Average order value
     */
    BigDecimal getAverageOrderValue(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find orders by numero (order number) pattern
     * @param numeroPattern Pattern to match order numbers
     * @return List of orders with matching numero
     */
    List<CommandeVente> findByNumeroLike(String numeroPattern);
    
    /**
     * Get payment method distribution for date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of [Payment Method, Count, Total Amount] arrays
     */
    List<Object[]> getPaymentMethodDistribution(LocalDate startDate, LocalDate endDate);
}
