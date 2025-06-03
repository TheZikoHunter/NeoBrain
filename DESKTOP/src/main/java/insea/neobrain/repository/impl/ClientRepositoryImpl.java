package insea.neobrain.repository.impl;

import insea.neobrain.entity.Client;
import insea.neobrain.entity.ModePaiement;
import insea.neobrain.repository.ClientRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of ClientRepository
 */
public class ClientRepositoryImpl extends GenericRepositoryImpl<Client, Long> implements ClientRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientRepositoryImpl.class);
    
    @Override
    public Optional<Client> findByEmail(String email) {
        try (Session session = openSession()) {
            Query<Client> query = session.createQuery(
                "FROM Client c WHERE c.email = :email", Client.class);
            query.setParameter("email", email);
            Client client = query.uniqueResult();
            logger.debug("Client found by email {}: {}", email, client != null);
            return Optional.ofNullable(client);
        } catch (Exception e) {
            logger.error("Error finding client by email: {}", email, e);
            throw new RuntimeException("Error finding client by email", e);
        }
    }
    
    @Override
    public List<Client> findByName(String nom, String prenom) {
        try (Session session = openSession()) {
            Query<Client> query = session.createQuery(
                "FROM Client c WHERE c.nom = :nom AND c.prenom = :prenom", Client.class);
            query.setParameter("nom", nom);
            query.setParameter("prenom", prenom);
            List<Client> clients = query.getResultList();
            logger.debug("Found {} clients with name: {} {}", clients.size(), nom, prenom);
            return clients;
        } catch (Exception e) {
            logger.error("Error finding clients by name: {} {}", nom, prenom, e);
            throw new RuntimeException("Error finding clients by name", e);
        }
    }
    
    @Override
    public List<Client> searchByName(String searchTerm) {
        try (Session session = openSession()) {
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            Query<Client> query = session.createQuery(
                "FROM Client c WHERE LOWER(CONCAT(c.nom, ' ', c.prenom)) LIKE :searchTerm " +
                "OR LOWER(CONCAT(c.prenom, ' ', c.nom)) LIKE :searchTerm " +
                "ORDER BY c.nom, c.prenom", Client.class);
            query.setParameter("searchTerm", searchPattern);
            List<Client> clients = query.getResultList();
            logger.debug("Found {} clients matching search term: {}", clients.size(), searchTerm);
            return clients;
        } catch (Exception e) {
            logger.error("Error searching clients by name: {}", searchTerm, e);
            throw new RuntimeException("Error searching clients by name", e);
        }
    }
    
    @Override
    public List<Client> findLoyalClients() {
        try (Session session = openSession()) {
            Query<Client> query = session.createQuery(
                "FROM Client c WHERE c.estFidele = true ORDER BY c.pointsFidelite DESC", Client.class);
            List<Client> clients = query.getResultList();
            logger.debug("Found {} loyal clients", clients.size());
            return clients;
        } catch (Exception e) {
            logger.error("Error finding loyal clients", e);
            throw new RuntimeException("Error finding loyal clients", e);
        }
    }
    
    @Override
    public List<Client> findByModePaiement(ModePaiement modePaiement) {
        try (Session session = openSession()) {
            Query<Client> query = session.createQuery(
                "FROM Client c WHERE c.modePaiement = :modePaiement ORDER BY c.nom, c.prenom", Client.class);
            query.setParameter("modePaiement", modePaiement);
            List<Client> clients = query.getResultList();
            logger.debug("Found {} clients with payment method: {}", clients.size(), modePaiement);
            return clients;
        } catch (Exception e) {
            logger.error("Error finding clients by payment method: {}", modePaiement, e);
            throw new RuntimeException("Error finding clients by payment method", e);
        }
    }
    
    @Override
    public List<Client> findByVille(String ville) {
        try (Session session = openSession()) {
            Query<Client> query = session.createQuery(
                "FROM Client c WHERE c.ville = :ville ORDER BY c.nom, c.prenom", Client.class);
            query.setParameter("ville", ville);
            List<Client> clients = query.getResultList();
            logger.debug("Found {} clients in city: {}", clients.size(), ville);
            return clients;
        } catch (Exception e) {
            logger.error("Error finding clients by city: {}", ville, e);
            throw new RuntimeException("Error finding clients by city", e);
        }
    }
    
    @Override
    public List<Client> findWithCreditLimitAbove(Double montant) {
        try (Session session = openSession()) {
            Query<Client> query = session.createQuery(
                "FROM Client c WHERE c.limiteCredit > :montant ORDER BY c.limiteCredit DESC", Client.class);
            query.setParameter("montant", montant);
            List<Client> clients = query.getResultList();
            logger.debug("Found {} clients with credit limit above: {}", clients.size(), montant);
            return clients;
        } catch (Exception e) {
            logger.error("Error finding clients with credit limit above: {}", montant, e);
            throw new RuntimeException("Error finding clients with credit limit above amount", e);
        }
    }
    
    @Override
    public List<Client> findWithAvailableCreditAbove(Double montant) {
        try (Session session = openSession()) {
            Query<Client> query = session.createQuery(
                "FROM Client c WHERE (c.limiteCredit - COALESCE(c.creditUtilise, 0)) > :montant " +
                "ORDER BY (c.limiteCredit - COALESCE(c.creditUtilise, 0)) DESC", Client.class);
            query.setParameter("montant", montant);
            List<Client> clients = query.getResultList();
            logger.debug("Found {} clients with available credit above: {}", clients.size(), montant);
            return clients;
        } catch (Exception e) {
            logger.error("Error finding clients with available credit above: {}", montant, e);
            throw new RuntimeException("Error finding clients with available credit above amount", e);
        }
    }
    
    @Override
    public List<Client> findTopClientsByOrderCount(int limit) {
        try (Session session = openSession()) {
            Query<Client> query = session.createQuery(
                "SELECT c FROM Client c LEFT JOIN c.commandes cmd " +
                "GROUP BY c ORDER BY COUNT(cmd) DESC", Client.class);
            query.setMaxResults(limit);
            List<Client> clients = query.getResultList();
            logger.debug("Found {} top clients by order count", clients.size());
            return clients;
        } catch (Exception e) {
            logger.error("Error finding top clients by order count", e);
            throw new RuntimeException("Error finding top clients by order count", e);
        }
    }
    
    @Override
    public List<Client> findClientsWithPendingOrders() {
        try (Session session = openSession()) {
            Query<Client> query = session.createQuery(
                "SELECT DISTINCT c FROM Client c JOIN c.commandes cmd " +
                "WHERE cmd.estValide = false OR (cmd.estValide = true AND cmd.estExpediee = false) " +
                "ORDER BY c.nom, c.prenom", Client.class);
            List<Client> clients = query.getResultList();
            logger.debug("Found {} clients with pending orders", clients.size());
            return clients;
        } catch (Exception e) {
            logger.error("Error finding clients with pending orders", e);
            throw new RuntimeException("Error finding clients with pending orders", e);
        }
    }
    
    @Override
    public boolean existsByEmail(String email) {
        try (Session session = openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(c) FROM Client c WHERE c.email = :email", Long.class);
            query.setParameter("email", email);
            Long count = query.getSingleResult();
            boolean exists = count != null && count > 0;
            logger.debug("Client email {} exists: {}", email, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error checking client email existence: {}", email, e);
            throw new RuntimeException("Error checking client email existence", e);
        }
    }
    
    @Override
    public long countByVille(String ville) {
        try (Session session = openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(c) FROM Client c WHERE c.ville = :ville", Long.class);
            query.setParameter("ville", ville);
            Long count = query.getSingleResult();
            logger.debug("Count of clients in city {}: {}", ville, count);
            return count != null ? count : 0L;
        } catch (Exception e) {
            logger.error("Error counting clients by city: {}", ville, e);
            throw new RuntimeException("Error counting clients by city", e);
        }
    }
}
