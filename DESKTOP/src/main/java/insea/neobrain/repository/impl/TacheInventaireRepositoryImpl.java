package insea.neobrain.repository.impl;

import insea.neobrain.entity.TacheInventaire;
import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.Inventaire;
import insea.neobrain.entity.Produit;
import insea.neobrain.entity.EtatTache;
import insea.neobrain.repository.TacheInventaireRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of TacheInventaireRepository
 * Provides data access operations for inventory task management
 */
public class TacheInventaireRepositoryImpl extends GenericRepositoryImpl<TacheInventaire, Long> 
        implements TacheInventaireRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(TacheInventaireRepositoryImpl.class);
    
    public TacheInventaireRepositoryImpl() {
        super();
    }
    
    @Override
    public List<TacheInventaire> findByPersonnel(Personnel personnel) {
        try (Session session = sessionFactory.openSession()) {
            Query<TacheInventaire> query = session.createQuery(
                "FROM TacheInventaire t WHERE t.personnel = :personnel ORDER BY t.dateAssignation DESC", 
                TacheInventaire.class);
            query.setParameter("personnel", personnel);
            
            List<TacheInventaire> result = query.getResultList();
            logger.debug("Found {} tasks for personnel: {}", result.size(), personnel.getNumeroPersonnel());
            return result;
        } catch (Exception e) {
            logger.error("Error finding tasks by personnel: {}", personnel.getNumeroPersonnel(), e);
            throw new RuntimeException("Error finding tasks by personnel", e);
        }
    }
    
    @Override
    public List<TacheInventaire> findByInventaire(Inventaire inventaire) {
        try (Session session = sessionFactory.openSession()) {
            Query<TacheInventaire> query = session.createQuery(
                "FROM TacheInventaire t WHERE t.inventaire = :inventaire ORDER BY t.dateAssignation", 
                TacheInventaire.class);
            query.setParameter("inventaire", inventaire);
            
            List<TacheInventaire> result = query.getResultList();
            logger.debug("Found {} tasks for inventory: {}", result.size(), inventaire.getIdInventaire());
            return result;
        } catch (Exception e) {
            logger.error("Error finding tasks by inventory: {}", inventaire.getIdInventaire(), e);
            throw new RuntimeException("Error finding tasks by inventory", e);
        }
    }
    
    @Override
    public List<TacheInventaire> findByEtat(EtatTache etat) {
        try (Session session = sessionFactory.openSession()) {
            Query<TacheInventaire> query = session.createQuery(
                "FROM TacheInventaire t WHERE t.etat = :etat ORDER BY t.dateAssignation", 
                TacheInventaire.class);
            query.setParameter("etat", etat);
            
            List<TacheInventaire> result = query.getResultList();
            logger.debug("Found {} tasks with status: {}", result.size(), etat);
            return result;
        } catch (Exception e) {
            logger.error("Error finding tasks by status: {}", etat, e);
            throw new RuntimeException("Error finding tasks by status", e);
        }
    }
    
    @Override
    public List<TacheInventaire> findByDateAssignationBetween(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<TacheInventaire> query = session.createQuery(
                "FROM TacheInventaire t WHERE t.dateAssignation BETWEEN :startDate AND :endDate " +
                "ORDER BY t.dateAssignation", 
                TacheInventaire.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            List<TacheInventaire> result = query.getResultList();
            logger.debug("Found {} tasks assigned between {} and {}", result.size(), startDate, endDate);
            return result;
        } catch (Exception e) {
            logger.error("Error finding tasks by assignment date range: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error finding tasks by assignment date range", e);
        }
    }
    
    @Override
    public List<TacheInventaire> findCompletedBetween(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<TacheInventaire> query = session.createQuery(
                "FROM TacheInventaire t WHERE t.etat = :completedStatus " +
                "AND t.dateRealisation BETWEEN :startDate AND :endDate " +
                "ORDER BY t.dateRealisation", 
                TacheInventaire.class);
            query.setParameter("completedStatus", EtatTache.TERMINEE);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            List<TacheInventaire> result = query.getResultList();
            logger.debug("Found {} completed tasks between {} and {}", result.size(), startDate, endDate);
            return result;
        } catch (Exception e) {
            logger.error("Error finding completed tasks by date range: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error finding completed tasks by date range", e);
        }
    }
    
    @Override
    public List<TacheInventaire> findOverdueTasks() {
        try (Session session = sessionFactory.openSession()) {
            Query<TacheInventaire> query = session.createQuery(
                "FROM TacheInventaire t WHERE t.delai < :currentDate " +
                "AND t.etat != :completedStatus ORDER BY t.delai", 
                TacheInventaire.class);
            query.setParameter("currentDate", LocalDate.now());
            query.setParameter("completedStatus", EtatTache.TERMINEE);
            
            List<TacheInventaire> result = query.getResultList();
            logger.debug("Found {} overdue tasks", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Error finding overdue tasks", e);
            throw new RuntimeException("Error finding overdue tasks", e);
        }
    }
    
    @Override
    public List<TacheInventaire> findPendingTasksByPersonnel(Personnel personnel) {
        try (Session session = sessionFactory.openSession()) {
            Query<TacheInventaire> query = session.createQuery(
                "FROM TacheInventaire t WHERE t.personnel = :personnel " +
                "AND t.etat IN (:pendingStates) ORDER BY t.delai", 
                TacheInventaire.class);
            query.setParameter("personnel", personnel);
            query.setParameterList("pendingStates", List.of(EtatTache.EN_ATTENTE, EtatTache.EN_COURS));
            
            List<TacheInventaire> result = query.getResultList();
            logger.debug("Found {} pending tasks for personnel: {}", result.size(), personnel.getNumeroPersonnel());
            return result;
        } catch (Exception e) {
            logger.error("Error finding pending tasks for personnel: {}", personnel.getNumeroPersonnel(), e);
            throw new RuntimeException("Error finding pending tasks for personnel", e);
        }
    }
    
    @Override
    public long countByInventaireAndEtat(Inventaire inventaire, EtatTache etat) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(t) FROM TacheInventaire t WHERE t.inventaire = :inventaire AND t.etat = :etat", 
                Long.class);
            query.setParameter("inventaire", inventaire);
            query.setParameter("etat", etat);
            
            Long result = query.getSingleResult();
            logger.debug("Found {} tasks with status {} for inventory: {}", result, etat, inventaire.getIdInventaire());
            return result != null ? result : 0L;
        } catch (Exception e) {
            logger.error("Error counting tasks by inventory and status: {} - {}", inventaire.getIdInventaire(), etat, e);
            throw new RuntimeException("Error counting tasks by inventory and status", e);
        }
    }
    
    @Override
    public List<TacheInventaire> findHighPriorityTasks(int days) {
        try (Session session = sessionFactory.openSession()) {
            LocalDate priorityDate = LocalDate.now().plusDays(days);
            Query<TacheInventaire> query = session.createQuery(
                "FROM TacheInventaire t WHERE t.delai <= :priorityDate " +
                "AND t.etat IN (:pendingStates) ORDER BY t.delai", 
                TacheInventaire.class);
            query.setParameter("priorityDate", priorityDate);
            query.setParameterList("pendingStates", List.of(EtatTache.EN_ATTENTE, EtatTache.EN_COURS));
            
            List<TacheInventaire> result = query.getResultList();
            logger.debug("Found {} high priority tasks (deadline within {} days)", result.size(), days);
            return result;
        } catch (Exception e) {
            logger.error("Error finding high priority tasks within {} days", days, e);
            throw new RuntimeException("Error finding high priority tasks", e);
        }
    }
    
    @Override
    public Object[] getTaskStatisticsByPersonnel(Personnel personnel) {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT " +
                "SUM(CASE WHEN t.etat = :completedStatus THEN 1 ELSE 0 END), " +
                "COUNT(t) " +
                "FROM TacheInventaire t WHERE t.personnel = :personnel", 
                Object[].class);
            query.setParameter("completedStatus", EtatTache.TERMINEE);
            query.setParameter("personnel", personnel);
            
            Object[] result = query.getSingleResult();
            logger.debug("Task statistics for personnel {}: completed={}, total={}", 
                personnel.getNumeroPersonnel(), result[0], result[1]);
            return result;
        } catch (Exception e) {
            logger.error("Error getting task statistics for personnel: {}", personnel.getNumeroPersonnel(), e);
            throw new RuntimeException("Error getting task statistics for personnel", e);
        }
    }
    
    @Override
    public List<TacheInventaire> findLatestTasksPerProduct(Inventaire inventaire) {
        try (Session session = sessionFactory.openSession()) {
            Query<TacheInventaire> query = session.createQuery(
                "FROM TacheInventaire t1 WHERE t1.inventaire = :inventaire " +
                "AND t1.dateAssignation = (" +
                "    SELECT MAX(t2.dateAssignation) FROM TacheInventaire t2 " +
                "    WHERE t2.inventaire = :inventaire AND t2.produit = t1.produit" +
                ") ORDER BY t1.produit.nom", 
                TacheInventaire.class);
            query.setParameter("inventaire", inventaire);
            
            List<TacheInventaire> result = query.getResultList();
            logger.debug("Found {} latest tasks per product for inventory: {}", result.size(), inventaire.getIdInventaire());
            return result;
        } catch (Exception e) {
            logger.error("Error finding latest tasks per product for inventory: {}", inventaire.getIdInventaire(), e);
            throw new RuntimeException("Error finding latest tasks per product for inventory", e);
        }
    }

    // Missing interface methods implementations

    @Override
    public List<TacheInventaire> findByProduit(Produit produit) {
        try (Session session = sessionFactory.openSession()) {
            Query<TacheInventaire> query = session.createQuery(
                "FROM TacheInventaire t WHERE t.produit = :produit ORDER BY t.dateAssignation DESC", 
                TacheInventaire.class);
            query.setParameter("produit", produit);
            
            List<TacheInventaire> result = query.getResultList();
            logger.debug("Found {} tasks for product: {}", result.size(), produit.getNomProduit());
            return result;
        } catch (Exception e) {
            logger.error("Error finding tasks by product: {}", produit.getNomProduit(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<TacheInventaire> findByEtatTache(EtatTache etatTache) {
        return findByEtat(etatTache);
    }

    @Override
    public List<TacheInventaire> findByDateRange(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        try (Session session = sessionFactory.openSession()) {
            Query<TacheInventaire> query = session.createQuery(
                "FROM TacheInventaire t WHERE t.dateAssignation BETWEEN :startDate AND :endDate " +
                "ORDER BY t.dateAssignation", 
                TacheInventaire.class);
            query.setParameter("startDate", startDateTime.toLocalDate());
            query.setParameter("endDate", endDateTime.toLocalDate());
            
            List<TacheInventaire> result = query.getResultList();
            logger.debug("Found {} tasks between {} and {}", result.size(), startDateTime, endDateTime);
            return result;
        } catch (Exception e) {
            logger.error("Error finding tasks by date range: {} - {}", startDateTime, endDateTime, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<TacheInventaire> findWithDiscrepancies() {
        try (Session session = sessionFactory.openSession()) {
            Query<TacheInventaire> query = session.createQuery(
                "FROM TacheInventaire t WHERE t.quantitePhysique != t.quantiteTheorique " +
                "ORDER BY t.dateAssignation DESC", 
                TacheInventaire.class);
            
            List<TacheInventaire> result = query.getResultList();
            logger.debug("Found {} tasks with discrepancies", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Error finding tasks with discrepancies", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<TacheInventaire> findWithSignificantDiscrepancies(int threshold) {
        try (Session session = sessionFactory.openSession()) {
            Query<TacheInventaire> query = session.createQuery(
                "FROM TacheInventaire t WHERE ABS(t.quantitePhysique - t.quantiteTheorique) >= :threshold " +
                "ORDER BY ABS(t.quantitePhysique - t.quantiteTheorique) DESC", 
                TacheInventaire.class);
            query.setParameter("threshold", threshold);
            
            List<TacheInventaire> result = query.getResultList();
            logger.debug("Found {} tasks with significant discrepancies (>= {})", result.size(), threshold);
            return result;
        } catch (Exception e) {
            logger.error("Error finding tasks with significant discrepancies", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<TacheInventaire> findByPersonnelAndEtatTache(Personnel personnel, EtatTache etatTache) {
        try (Session session = sessionFactory.openSession()) {
            Query<TacheInventaire> query = session.createQuery(
                "FROM TacheInventaire t WHERE t.personnel = :personnel AND t.etat = :etatTache " +
                "ORDER BY t.dateAssignation DESC", 
                TacheInventaire.class);
            query.setParameter("personnel", personnel);
            query.setParameter("etatTache", etatTache);
            
            List<TacheInventaire> result = query.getResultList();
            logger.debug("Found {} tasks for personnel {} with status {}", result.size(), 
                personnel.getNumeroPersonnel(), etatTache);
            return result;
        } catch (Exception e) {
            logger.error("Error finding tasks by personnel and status: {} - {}", 
                personnel.getNumeroPersonnel(), etatTache, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<TacheInventaire> findByInventaireAndEtatTache(Inventaire inventaire, EtatTache etatTache) {
        try (Session session = sessionFactory.openSession()) {
            Query<TacheInventaire> query = session.createQuery(
                "FROM TacheInventaire t WHERE t.inventaire = :inventaire AND t.etat = :etatTache " +
                "ORDER BY t.dateAssignation", 
                TacheInventaire.class);
            query.setParameter("inventaire", inventaire);
            query.setParameter("etatTache", etatTache);
            
            List<TacheInventaire> result = query.getResultList();
            logger.debug("Found {} tasks for inventory {} with status {}", result.size(), 
                inventaire.getIdInventaire(), etatTache);
            return result;
        } catch (Exception e) {
            logger.error("Error finding tasks by inventory and status: {} - {}", 
                inventaire.getIdInventaire(), etatTache, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<TacheInventaire> findUnassigned() {
        try (Session session = sessionFactory.openSession()) {
            Query<TacheInventaire> query = session.createQuery(
                "FROM TacheInventaire t WHERE t.personnel IS NULL " +
                "ORDER BY t.dateAssignation DESC", 
                TacheInventaire.class);
            
            List<TacheInventaire> result = query.getResultList();
            logger.debug("Found {} unassigned tasks", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Error finding unassigned tasks", e);
            return new ArrayList<>();
        }
    }
}
