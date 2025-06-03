package insea.neobrain.repository;

import insea.neobrain.entity.Client;
import insea.neobrain.entity.ModePaiement;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Client entity
 */
public interface ClientRepository extends GenericRepository<Client, Long> {
    
    /**
     * Find client by email
     * @param email the email address
     * @return Optional containing the client if found
     */
    Optional<Client> findByEmail(String email);
    
    /**
     * Find clients by name (nom and prenom)
     * @param nom the last name
     * @param prenom the first name
     * @return list of clients matching the name
     */
    List<Client> findByName(String nom, String prenom);
    
    /**
     * Find clients by partial name search
     * @param searchTerm the search term for name
     * @return list of clients matching the search term
     */
    List<Client> searchByName(String searchTerm);
    
    /**
     * Find loyal clients
     * @return list of loyal clients
     */
    List<Client> findLoyalClients();
    
    /**
     * Find clients by payment method
     * @param modePaiement the payment method
     * @return list of clients with the specified payment method
     */
    List<Client> findByModePaiement(ModePaiement modePaiement);
    
    /**
     * Find clients by city
     * @param ville the city
     * @return list of clients in the specified city
     */
    List<Client> findByVille(String ville);
    
    /**
     * Find clients with credit limit above amount
     * @param montant the minimum credit limit
     * @return list of clients with credit limit above the amount
     */
    List<Client> findWithCreditLimitAbove(Double montant);
    
    /**
     * Find clients with available credit above amount
     * @param montant the minimum available credit
     * @return list of clients with available credit above the amount
     */
    List<Client> findWithAvailableCreditAbove(Double montant);
    
    /**
     * Find top clients by order count
     * @param limit the number of top clients to return
     * @return list of top clients by order count
     */
    List<Client> findTopClientsByOrderCount(int limit);
    
    /**
     * Find clients with pending orders
     * @return list of clients with pending orders
     */
    List<Client> findClientsWithPendingOrders();
    
    /**
     * Check if email exists
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Count clients by city
     * @param ville the city
     * @return number of clients in the city
     */
    long countByVille(String ville);
}
