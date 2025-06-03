package insea.neobrain.repository.impl;

import insea.neobrain.entity.Reclamation;
import insea.neobrain.entity.LigneCommande;
import insea.neobrain.entity.Client;
import insea.neobrain.entity.EtatReclamation;
import insea.neobrain.entity.TypeReclamation;
import insea.neobrain.repository.ReclamationRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Implementation of ReclamationRepository
 * Provides data access operations for complaint/return management
 */
public class ReclamationRepositoryImpl extends GenericRepositoryImpl<Reclamation, Long> 
        implements ReclamationRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(ReclamationRepositoryImpl.class);
    
    public ReclamationRepositoryImpl() {
        super();
    }
    
    @Override
    public List<Reclamation> findByClient(Client client) {
        try (Session session = sessionFactory.openSession()) {
            Query<Reclamation> query = session.createQuery(
                "FROM Reclamation r WHERE r.ligneCommande.commande.client = :client ORDER BY r.date DESC", 
                Reclamation.class);
            query.setParameter("client", client);
            
            List<Reclamation> result = query.getResultList();
            logger.debug("Found {} complaints for client: {}", result.size(), client.getIdPersonne());
            return result;
        } catch (Exception e) {
            logger.error("Error finding complaints by client: {}", client.getIdPersonne(), e);
            throw new RuntimeException("Error finding complaints by client", e);
        }
    }
    
    @Override
    public List<Reclamation> findByLigneCommande(LigneCommande ligneCommande) {
        try (Session session = sessionFactory.openSession()) {
            Query<Reclamation> query = session.createQuery(
                "FROM Reclamation r WHERE r.ligneCommande = :ligneCommande ORDER BY r.date DESC", 
                Reclamation.class);
            query.setParameter("ligneCommande", ligneCommande);
            
            List<Reclamation> result = query.getResultList();
            logger.debug("Found {} complaints for order line: {}", result.size(), ligneCommande.getIdLigneVente());
            return result;
        } catch (Exception e) {
            logger.error("Error finding complaints by order line: {}", ligneCommande.getIdLigneVente(), e);
            throw new RuntimeException("Error finding complaints by order line", e);
        }
    }
    
    @Override
    public List<Reclamation> findByEtat(EtatReclamation etat) {
        try (Session session = sessionFactory.openSession()) {
            Query<Reclamation> query = session.createQuery(
                "FROM Reclamation r WHERE r.etat = :etat ORDER BY r.date DESC", 
                Reclamation.class);
            query.setParameter("etat", etat);
            
            List<Reclamation> result = query.getResultList();
            logger.debug("Found {} complaints with status: {}", result.size(), etat);
            return result;
        } catch (Exception e) {
            logger.error("Error finding complaints by status: {}", etat, e);
            throw new RuntimeException("Error finding complaints by status", e);
        }
    }
    
    @Override
    public List<Reclamation> findByType(TypeReclamation type) {
        try (Session session = sessionFactory.openSession()) {
            Query<Reclamation> query = session.createQuery(
                "FROM Reclamation r WHERE r.type = :type ORDER BY r.date DESC", 
                Reclamation.class);
            query.setParameter("type", type);
            
            List<Reclamation> result = query.getResultList();
            logger.debug("Found {} complaints of type: {}", result.size(), type);
            return result;
        } catch (Exception e) {
            logger.error("Error finding complaints by type: {}", type, e);
            throw new RuntimeException("Error finding complaints by type", e);
        }
    }
    
    @Override
    public List<Reclamation> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Reclamation> query = session.createQuery(
                "FROM Reclamation r WHERE r.date BETWEEN :startDate AND :endDate ORDER BY r.date DESC", 
                Reclamation.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            List<Reclamation> result = query.getResultList();
            logger.debug("Found {} complaints between {} and {}", result.size(), startDate, endDate);
            return result;
        } catch (Exception e) {
            logger.error("Error finding complaints by date range: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error finding complaints by date range", e);
        }
    }
    
    @Override
    public List<Reclamation> findPendingComplaints() {
        try (Session session = sessionFactory.openSession()) {
            Query<Reclamation> query = session.createQuery(
                "FROM Reclamation r WHERE r.etat IN (:pendingStates) ORDER BY r.date", 
                Reclamation.class);
            query.setParameterList("pendingStates", 
                List.of(EtatReclamation.EN_ATTENTE, EtatReclamation.VALIDEE));
            
            List<Reclamation> result = query.getResultList();
            logger.debug("Found {} pending complaints", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Error finding pending complaints", e);
            throw new RuntimeException("Error finding pending complaints", e);
        }
    }
    
    @Override
    public List<Reclamation> findResolvedInDateRange(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Reclamation> query = session.createQuery(
                "FROM Reclamation r WHERE r.etat = :resolvedState " +
                "AND r.dateResolution BETWEEN :startDate AND :endDate ORDER BY r.dateResolution DESC", 
                Reclamation.class);
            query.setParameter("resolvedState", EtatReclamation.REFUSEE);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            List<Reclamation> result = query.getResultList();
            logger.debug("Found {} resolved complaints between {} and {}", result.size(), startDate, endDate);
            return result;
        } catch (Exception e) {
            logger.error("Error finding resolved complaints by date range: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error finding resolved complaints by date range", e);
        }
    }
    
    @Override
    public List<Reclamation> findByNumeroLike(String numeroPattern) {
        try (Session session = sessionFactory.openSession()) {
            Query<Reclamation> query = session.createQuery(
                "FROM Reclamation r WHERE r.numero LIKE :numeroPattern ORDER BY r.date DESC", 
                Reclamation.class);
            query.setParameter("numeroPattern", numeroPattern);
            
            List<Reclamation> result = query.getResultList();
            logger.debug("Found {} complaints matching numero pattern: {}", result.size(), numeroPattern);
            return result;
        } catch (Exception e) {
            logger.error("Error finding complaints by numero pattern: {}", numeroPattern, e);
            throw new RuntimeException("Error finding complaints by numero pattern", e);
        }
    }
    
    @Override
    public List<Reclamation> findRecentComplaints(int days) {
        try (Session session = sessionFactory.openSession()) {
            LocalDate cutoffDate = LocalDate.now().minusDays(days);
            Query<Reclamation> query = session.createQuery(
                "FROM Reclamation r WHERE r.date >= :cutoffDate ORDER BY r.date DESC", 
                Reclamation.class);
            query.setParameter("cutoffDate", cutoffDate);
            
            List<Reclamation> result = query.getResultList();
            logger.debug("Found {} recent complaints (within {} days)", result.size(), days);
            return result;
        } catch (Exception e) {
            logger.error("Error finding recent complaints within {} days", days, e);
            throw new RuntimeException("Error finding recent complaints", e);
        }
    }
    
    @Override
    public long countByEtat(EtatReclamation etat) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(r) FROM Reclamation r WHERE r.etat = :etat", 
                Long.class);
            query.setParameter("etat", etat);
            
            Long result = query.getSingleResult();
            logger.debug("Found {} complaints with status: {}", result, etat);
            return result != null ? result : 0L;
        } catch (Exception e) {
            logger.error("Error counting complaints by status: {}", etat, e);
            throw new RuntimeException("Error counting complaints by status", e);
        }
    }
    
    @Override
    public long countByTypeAndDateBetween(TypeReclamation type, LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(r) FROM Reclamation r WHERE r.type = :type " +
                "AND r.date BETWEEN :startDate AND :endDate", 
                Long.class);
            query.setParameter("type", type);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            Long result = query.getSingleResult();
            logger.debug("Found {} complaints of type {} between {} and {}", 
                result, type, startDate, endDate);
            return result != null ? result : 0L;
        } catch (Exception e) {
            logger.error("Error counting complaints by type and date range: {} - {} to {}", 
                type, startDate, endDate, e);
            throw new RuntimeException("Error counting complaints by type and date range", e);
        }
    }
    
    @Override
    public List<Object[]> getComplaintStatisticsByType(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT r.type, COUNT(r) " +
                "FROM Reclamation r WHERE r.date BETWEEN :startDate AND :endDate " +
                "GROUP BY r.type ORDER BY COUNT(r) DESC", 
                Object[].class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            List<Object[]> result = query.getResultList();
            logger.debug("Found {} complaint type statistics between {} and {}", 
                result.size(), startDate, endDate);
            return result;
        } catch (Exception e) {
            logger.error("Error getting complaint statistics by type: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error getting complaint statistics by type", e);
        }
    }
    
    @Override
    public List<Object[]> getComplaintStatisticsByStatus(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT r.etat, COUNT(r) " +
                "FROM Reclamation r WHERE r.date BETWEEN :startDate AND :endDate " +
                "GROUP BY r.etat ORDER BY COUNT(r) DESC", 
                Object[].class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            List<Object[]> result = query.getResultList();
            logger.debug("Found {} complaint status statistics between {} and {}", 
                result.size(), startDate, endDate);
            return result;
        } catch (Exception e) {
            logger.error("Error getting complaint statistics by status: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error getting complaint statistics by status", e);
        }
    }
    
    @Override
    public List<Object[]> getMonthlyComplaintTrends(int year) {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT MONTH(r.date), COUNT(r) " +
                "FROM Reclamation r WHERE YEAR(r.date) = :year " +
                "GROUP BY MONTH(r.date) ORDER BY MONTH(r.date)", 
                Object[].class);
            query.setParameter("year", year);
            
            List<Object[]> result = query.getResultList();
            logger.debug("Found {} monthly complaint trends for year {}", result.size(), year);
            return result;
        } catch (Exception e) {
            logger.error("Error getting monthly complaint trends for year: {}", year, e);
            throw new RuntimeException("Error getting monthly complaint trends", e);
        }
    }
    
    @Override
    public List<Reclamation> findByClientAndDateBetween(Client client, LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Reclamation> query = session.createQuery(
                "FROM Reclamation r WHERE r.ligneCommande.commande.client = :client " +
                "AND r.date BETWEEN :startDate AND :endDate ORDER BY r.date DESC", 
                Reclamation.class);
            query.setParameter("client", client);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            List<Reclamation> result = query.getResultList();
            logger.debug("Found {} complaints for client {} between {} and {}", 
                result.size(), client.getIdPersonne(), startDate, endDate);
            return result;
        } catch (Exception e) {
            logger.error("Error finding complaints by client and date range: {} - {} to {}", 
                client.getIdPersonne(), startDate, endDate, e);
            throw new RuntimeException("Error finding complaints by client and date range", e);
        }
    }
    
    @Override
    public List<Object[]> getClientsWithMostComplaints(int limit) {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT r.ligneCommande.commande.client, COUNT(r) as complaintCount " +
                "FROM Reclamation r GROUP BY r.ligneCommande.commande.client " +
                "ORDER BY complaintCount DESC", 
                Object[].class);
            query.setMaxResults(limit);
            
            List<Object[]> result = query.getResultList();
            logger.debug("Found {} clients with most complaints", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Error getting clients with most complaints", e);
            throw new RuntimeException("Error getting clients with most complaints", e);
        }
    }
    
    @Override
    public Double getAverageResolutionTime(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            // Get resolved complaints with both dates available
            Query<Reclamation> query = session.createQuery(
                "FROM Reclamation r WHERE r.etat = :resolvedState " +
                "AND r.dateResolution IS NOT NULL " +
                "AND r.dateResolution BETWEEN :startDate AND :endDate", 
                Reclamation.class);
            query.setParameter("resolvedState", EtatReclamation.REFUSEE);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            List<Reclamation> resolvedComplaints = query.getResultList();
            
            if (resolvedComplaints.isEmpty()) {
                logger.debug("No resolved complaints found for average resolution time calculation");
                return 0.0;
            }
            
            // Calculate average resolution time in days
            double totalDays = resolvedComplaints.stream()
                .mapToLong(r -> ChronoUnit.DAYS.between(r.getDateReclamation(), r.getDateTraitement()))
                .average()
                .orElse(0.0);
            
            logger.debug("Average resolution time between {} and {}: {} days", 
                startDate, endDate, totalDays);
            return totalDays;
        } catch (Exception e) {
            logger.error("Error getting average resolution time: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error getting average resolution time", e);
        }
    }
    
    @Override
    public List<Reclamation> findOverdueComplaints(int days) {
        try (Session session = sessionFactory.openSession()) {
            LocalDate cutoffDate = LocalDate.now().minusDays(days);
            Query<Reclamation> query = session.createQuery(
                "FROM Reclamation r WHERE r.date <= :cutoffDate " +
                "AND r.etat IN (:pendingStates) ORDER BY r.date", 
                Reclamation.class);
            query.setParameter("cutoffDate", cutoffDate);
            query.setParameterList("pendingStates", 
                List.of(EtatReclamation.EN_ATTENTE, EtatReclamation.VALIDEE));
            
            List<Reclamation> result = query.getResultList();
            logger.debug("Found {} overdue complaints (older than {} days)", result.size(), days);
            return result;
        } catch (Exception e) {
            logger.error("Error finding overdue complaints older than {} days", days, e);
            throw new RuntimeException("Error finding overdue complaints", e);
        }
    }
}
