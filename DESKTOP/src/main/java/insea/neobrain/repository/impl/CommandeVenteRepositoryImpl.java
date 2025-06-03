package insea.neobrain.repository.impl;

import insea.neobrain.entity.CommandeVente;
import insea.neobrain.entity.Client;
import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.ModePaiement;
import insea.neobrain.repository.CommandeVenteRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of CommandeVenteRepository
 * Provides data access operations for sales order management
 */
public class CommandeVenteRepositoryImpl extends GenericRepositoryImpl<CommandeVente, Long> 
        implements CommandeVenteRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(CommandeVenteRepositoryImpl.class);
    
    public CommandeVenteRepositoryImpl() {
        super();
    }
    
    @Override
    public List<CommandeVente> findByClient(Client client) {
        try (Session session = sessionFactory.openSession()) {
            Query<CommandeVente> query = session.createQuery(
                "FROM CommandeVente c WHERE c.client = :client ORDER BY c.date DESC", 
                CommandeVente.class);
            query.setParameter("client", client);
            
            List<CommandeVente> result = query.getResultList();
            logger.debug("Found {} orders for client: {}", result.size(), client.getIdPersonne());
            return result;
        } catch (Exception e) {
            logger.error("Error finding orders by client: {}", client.getIdPersonne(), e);
            throw new RuntimeException("Error finding orders by client", e);
        }
    }
    
    @Override
    public List<CommandeVente> findByPersonnel(Personnel personnel) {
        try (Session session = sessionFactory.openSession()) {
            Query<CommandeVente> query = session.createQuery(
                "FROM CommandeVente c WHERE c.personnel = :personnel ORDER BY c.date DESC", 
                CommandeVente.class);
            query.setParameter("personnel", personnel);
            
            List<CommandeVente> result = query.getResultList();
            logger.debug("Found {} orders by personnel: {}", result.size(), personnel.getNumeroPersonnel());
            return result;
        } catch (Exception e) {
            logger.error("Error finding orders by personnel: {}", personnel.getNumeroPersonnel(), e);
            throw new RuntimeException("Error finding orders by personnel", e);
        }
    }
    
    @Override
    public List<CommandeVente> findByModePaiement(ModePaiement modePaiement) {
        try (Session session = sessionFactory.openSession()) {
            Query<CommandeVente> query = session.createQuery(
                "FROM CommandeVente c WHERE c.modePaiement = :modePaiement ORDER BY c.date DESC", 
                CommandeVente.class);
            query.setParameter("modePaiement", modePaiement);
            
            List<CommandeVente> result = query.getResultList();
            logger.debug("Found {} orders with payment method: {}", result.size(), modePaiement);
            return result;
        } catch (Exception e) {
            logger.error("Error finding orders by payment method: {}", modePaiement, e);
            throw new RuntimeException("Error finding orders by payment method", e);
        }
    }
    
    @Override
    public List<CommandeVente> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<CommandeVente> query = session.createQuery(
                "FROM CommandeVente c WHERE c.date BETWEEN :startDate AND :endDate ORDER BY c.date DESC", 
                CommandeVente.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            List<CommandeVente> result = query.getResultList();
            logger.debug("Found {} orders between {} and {}", result.size(), startDate, endDate);
            return result;
        } catch (Exception e) {
            logger.error("Error finding orders by date range: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error finding orders by date range", e);
        }
    }
    
    @Override
    public List<CommandeVente> findByTotalGreaterThanEqual(BigDecimal minAmount) {
        try (Session session = sessionFactory.openSession()) {
            Query<CommandeVente> query = session.createQuery(
                "FROM CommandeVente c WHERE c.total >= :minAmount ORDER BY c.total DESC", 
                CommandeVente.class);
            query.setParameter("minAmount", minAmount);
            
            List<CommandeVente> result = query.getResultList();
            logger.debug("Found {} orders with total >= {}", result.size(), minAmount);
            return result;
        } catch (Exception e) {
            logger.error("Error finding orders by minimum amount: {}", minAmount, e);
            throw new RuntimeException("Error finding orders by minimum amount", e);
        }
    }
    
    @Override
    public List<CommandeVente> findRecentOrders(int days) {
        try (Session session = sessionFactory.openSession()) {
            LocalDate cutoffDate = LocalDate.now().minusDays(days);
            Query<CommandeVente> query = session.createQuery(
                "FROM CommandeVente c WHERE c.date >= :cutoffDate ORDER BY c.date DESC", 
                CommandeVente.class);
            query.setParameter("cutoffDate", cutoffDate);
            
            List<CommandeVente> result = query.getResultList();
            logger.debug("Found {} recent orders (within {} days)", result.size(), days);
            return result;
        } catch (Exception e) {
            logger.error("Error finding recent orders within {} days", days, e);
            throw new RuntimeException("Error finding recent orders", e);
        }
    }
    
    @Override
    public List<CommandeVente> findByClientAndDateBetween(Client client, LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<CommandeVente> query = session.createQuery(
                "FROM CommandeVente c WHERE c.client = :client " +
                "AND c.date BETWEEN :startDate AND :endDate ORDER BY c.date DESC", 
                CommandeVente.class);
            query.setParameter("client", client);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            List<CommandeVente> result = query.getResultList();
            logger.debug("Found {} orders for client {} between {} and {}", 
                result.size(), client.getIdPersonne(), startDate, endDate);
            return result;
        } catch (Exception e) {
            logger.error("Error finding orders by client and date range: {} - {} to {}", 
                client.getIdPersonne(), startDate, endDate, e);
            throw new RuntimeException("Error finding orders by client and date range", e);
        }
    }
    
    @Override
    public List<Object[]> getTopClientsByValue(LocalDate startDate, LocalDate endDate, int limit) {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT c.client, SUM(c.total) as totalValue " +
                "FROM CommandeVente c WHERE c.date BETWEEN :startDate AND :endDate " +
                "GROUP BY c.client ORDER BY totalValue DESC", 
                Object[].class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            query.setMaxResults(limit);
            
            List<Object[]> result = query.getResultList();
            logger.debug("Found {} top clients by value between {} and {}", result.size(), startDate, endDate);
            return result;
        } catch (Exception e) {
            logger.error("Error getting top clients by value: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error getting top clients by value", e);
        }
    }
    
    @Override
    public List<Object[]> getDailySalesTotals(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT c.date, SUM(c.total) " +
                "FROM CommandeVente c WHERE c.date BETWEEN :startDate AND :endDate " +
                "GROUP BY c.date ORDER BY c.date", 
                Object[].class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            List<Object[]> result = query.getResultList();
            logger.debug("Found {} daily sales totals between {} and {}", result.size(), startDate, endDate);
            return result;
        } catch (Exception e) {
            logger.error("Error getting daily sales totals: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error getting daily sales totals", e);
        }
    }
    
    @Override
    public List<Object[]> getMonthlySalesSummary(int year) {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT MONTH(c.date), SUM(c.total), COUNT(c) " +
                "FROM CommandeVente c WHERE YEAR(c.date) = :year " +
                "GROUP BY MONTH(c.date) ORDER BY MONTH(c.date)", 
                Object[].class);
            query.setParameter("year", year);
            
            List<Object[]> result = query.getResultList();
            logger.debug("Found {} monthly sales summaries for year {}", result.size(), year);
            return result;
        } catch (Exception e) {
            logger.error("Error getting monthly sales summary for year: {}", year, e);
            throw new RuntimeException("Error getting monthly sales summary", e);
        }
    }
    
    @Override
    public BigDecimal getTotalSalesAmount(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<BigDecimal> query = session.createQuery(
                "SELECT COALESCE(SUM(c.total), 0) FROM CommandeVente c " +
                "WHERE c.date BETWEEN :startDate AND :endDate", 
                BigDecimal.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            BigDecimal result = query.getSingleResult();
            logger.debug("Total sales amount between {} and {}: {}", startDate, endDate, result);
            return result != null ? result : BigDecimal.ZERO;
        } catch (Exception e) {
            logger.error("Error getting total sales amount: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error getting total sales amount", e);
        }
    }
    
    @Override
    public long countOrdersInDateRange(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(c) FROM CommandeVente c WHERE c.date BETWEEN :startDate AND :endDate", 
                Long.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            Long result = query.getSingleResult();
            logger.debug("Order count between {} and {}: {}", startDate, endDate, result);
            return result != null ? result : 0L;
        } catch (Exception e) {
            logger.error("Error counting orders in date range: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error counting orders in date range", e);
        }
    }
    
    @Override
    public BigDecimal getAverageOrderValue(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<BigDecimal> query = session.createQuery(
                "SELECT COALESCE(AVG(c.total), 0) FROM CommandeVente c " +
                "WHERE c.date BETWEEN :startDate AND :endDate", 
                BigDecimal.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            BigDecimal result = query.getSingleResult();
            logger.debug("Average order value between {} and {}: {}", startDate, endDate, result);
            return result != null ? result : BigDecimal.ZERO;
        } catch (Exception e) {
            logger.error("Error getting average order value: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error getting average order value", e);
        }
    }
    
    @Override
    public List<CommandeVente> findByNumeroLike(String numeroPattern) {
        try (Session session = sessionFactory.openSession()) {
            Query<CommandeVente> query = session.createQuery(
                "FROM CommandeVente c WHERE c.numero LIKE :numeroPattern ORDER BY c.date DESC", 
                CommandeVente.class);
            query.setParameter("numeroPattern", numeroPattern);
            
            List<CommandeVente> result = query.getResultList();
            logger.debug("Found {} orders matching numero pattern: {}", result.size(), numeroPattern);
            return result;
        } catch (Exception e) {
            logger.error("Error finding orders by numero pattern: {}", numeroPattern, e);
            throw new RuntimeException("Error finding orders by numero pattern", e);
        }
    }
    
    @Override
    public List<Object[]> getPaymentMethodDistribution(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT c.modePaiement, COUNT(c), SUM(c.total) " +
                "FROM CommandeVente c WHERE c.date BETWEEN :startDate AND :endDate " +
                "GROUP BY c.modePaiement ORDER BY COUNT(c) DESC", 
                Object[].class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            List<Object[]> result = query.getResultList();
            logger.debug("Found {} payment method distributions between {} and {}", 
                result.size(), startDate, endDate);
            return result;
        } catch (Exception e) {
            logger.error("Error getting payment method distribution: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error getting payment method distribution", e);
        }
    }
}
