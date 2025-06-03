package insea.neobrain.service;

import insea.neobrain.entity.CommandeVente;
import insea.neobrain.entity.LigneCommande;
import insea.neobrain.entity.Client;
import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.ModePaiement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for CommandeVente management
 * Provides sales order management functionality
 */
public interface CommandeVenteService {
    
    /**
     * Create a new sales order
     * @param commande the sales order to create
     * @return the created sales order
     */
    CommandeVente createCommande(CommandeVente commande);
    
    /**
     * Create a new sales order with details
     * @param client the client
     * @param personnel the personnel
     * @param lignesCommande the order lines
     * @param modePaiement the payment method
     * @return the created sales order
     */
    CommandeVente createCommande(Client client, Personnel personnel, 
                               List<LigneCommande> lignesCommande, ModePaiement modePaiement);
    
    /**
     * Update an existing sales order
     * @param commande the sales order to update
     * @return the updated sales order
     */
    CommandeVente updateCommande(CommandeVente commande);
    
    /**
     * Delete a sales order
     * @param id the sales order ID
     * @return true if deleted successfully
     */
    boolean deleteCommande(Long id);
    
    /**
     * Find sales order by ID
     * @param id the sales order ID
     * @return Optional containing the sales order if found
     */
    Optional<CommandeVente> findCommandeById(Long id);
    
    /**
     * Find sales order by order number
     * @param numeroCommande the order number
     * @return Optional containing the sales order if found
     */
    Optional<CommandeVente> findCommandeByNumero(String numeroCommande);
    
    /**
     * Find all sales orders
     * @return list of all sales orders
     */
    List<CommandeVente> findAllCommandes();
    
    /**
     * Find sales orders by client
     * @param client the client
     * @return list of sales orders for the client
     */
    List<CommandeVente> findCommandesByClient(Client client);
    
    /**
     * Find sales orders by personnel
     * @param personnel the personnel
     * @return list of sales orders by the personnel
     */
    List<CommandeVente> findCommandesByPersonnel(Personnel personnel);
    
    /**
     * Find sales orders by date range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of sales orders in the date range
     */
    List<CommandeVente> findCommandesByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find sales orders by payment method
     * @param modePaiement the payment method
     * @return list of sales orders with the payment method
     */
    List<CommandeVente> findCommandesByModePaiement(ModePaiement modePaiement);
    
    /**
     * Find sales orders by total amount range
     * @param minAmount the minimum amount
     * @param maxAmount the maximum amount
     * @return list of sales orders in the amount range
     */
    List<CommandeVente> findCommandesByAmountRange(BigDecimal minAmount, BigDecimal maxAmount);
    
    /**
     * Search sales orders with multiple criteria
     * @param clientNom the client name (optional)
     * @param personnelNom the personnel name (optional)
     * @param startDate the start date (optional)
     * @param endDate the end date (optional)
     * @param modePaiement the payment method (optional)
     * @return list of sales orders matching the criteria
     */
    List<CommandeVente> searchCommandes(String clientNom, String personnelNom,
                                       LocalDate startDate, LocalDate endDate,
                                       ModePaiement modePaiement);
    
    /**
     * Add line to sales order
     * @param commandeId the sales order ID
     * @param ligneCommande the line to add
     * @return the updated sales order
     */
    CommandeVente addLigneCommande(Long commandeId, LigneCommande ligneCommande);
    
    /**
     * Remove line from sales order
     * @param commandeId the sales order ID
     * @param ligneCommandeId the line ID to remove
     * @return the updated sales order
     */
    CommandeVente removeLigneCommande(Long commandeId, Long ligneCommandeId);
    
    /**
     * Update line in sales order
     * @param commandeId the sales order ID
     * @param ligneCommande the line to update
     * @return the updated sales order
     */
    CommandeVente updateLigneCommande(Long commandeId, LigneCommande ligneCommande);
    
    /**
     * Calculate order total
     * @param commande the sales order
     * @return the total amount
     */
    BigDecimal calculateOrderTotal(CommandeVente commande);
    
    /**
     * Apply discount to order
     * @param commandeId the sales order ID
     * @param discountPercentage the discount percentage
     * @return the updated sales order
     */
    CommandeVente applyDiscount(Long commandeId, BigDecimal discountPercentage);
    
    /**
     * Validate sales order
     * @param commande the sales order to validate
     * @return list of validation errors
     */
    List<String> validateCommande(CommandeVente commande);
    
    /**
     * Check if order can be deleted
     * @param commandeId the sales order ID
     * @return true if can be deleted
     */
    boolean canDeleteCommande(Long commandeId);
    
    /**
     * Generate order number
     * @return a unique order number
     */
    String generateOrderNumber();
    
    /**
     * Get sales order count
     * @return total number of sales orders
     */
    long getCommandeCount();
    
    /**
     * Get total sales amount
     * @return total sales amount
     */
    BigDecimal getTotalSalesAmount();
    
    /**
     * Get sales statistics by date range
     * @param startDate the start date
     * @param endDate the end date
     * @return sales statistics [total_orders, total_amount, average_amount]
     */
    Object[] getSalesStatistics(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get top clients by sales amount
     * @param limit the number of clients to return
     * @return list of [client, total_amount]
     */
    List<Object[]> getTopClientsBySales(int limit);
    
    /**
     * Get sales by payment method
     * @return list of [payment_method, count, total_amount]
     */
    List<Object[]> getSalesByPaymentMethod();
    
    /**
     * Get recent sales orders
     * @param days the number of days to look back
     * @return list of recent sales orders
     */
    List<CommandeVente> getRecentOrders(int days);
    
    /**
     * Export sales orders to CSV
     * @return CSV string
     */
    String exportCommandesToCSV();
    
    /**
     * Import sales orders from CSV
     * @param csvData the CSV data
     * @return number of records imported
     */
    int importCommandesFromCSV(String csvData);
}
