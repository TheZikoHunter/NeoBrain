package insea.neobrain.repository.impl;

import insea.neobrain.entity.Inventaire;
import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.Produit;
import insea.neobrain.entity.CategorieProduit;
import insea.neobrain.repository.InventaireRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of InventaireRepository
 */
public class InventaireRepositoryImpl extends GenericRepositoryImpl<Inventaire, Long> implements InventaireRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(InventaireRepositoryImpl.class);
    
    @Override
    public Optional<Inventaire> findByNumeroInventaire(String numeroInventaire) {
        try (Session session = openSession()) {
            Query<Inventaire> query = session.createQuery(
                "FROM Inventaire i WHERE i.numeroInventaire = :numeroInventaire", Inventaire.class);
            query.setParameter("numeroInventaire", numeroInventaire);
            Inventaire inventaire = query.uniqueResult();
            logger.debug("Inventory found by number {}: {}", numeroInventaire, inventaire != null);
            return Optional.ofNullable(inventaire);
        } catch (Exception e) {
            logger.error("Error finding inventory by number: {}", numeroInventaire, e);
            throw new RuntimeException("Error finding inventory by number", e);
        }
    }
    
    @Override
    public List<Inventaire> findActive() {
        try (Session session = openSession()) {
            Query<Inventaire> query = session.createQuery(
                "FROM Inventaire i WHERE i.estClos = false OR i.etatInventaire = 'EN_COURS' " +
                "ORDER BY i.dateDebut DESC", Inventaire.class);
            List<Inventaire> inventaires = query.getResultList();
            logger.debug("Found {} active inventories", inventaires.size());
            return inventaires;
        } catch (Exception e) {
            logger.error("Error finding active inventories", e);
            throw new RuntimeException("Error finding active inventories", e);
        }
    }
    
    @Override
    public List<Inventaire> findClosed() {
        try (Session session = openSession()) {
            Query<Inventaire> query = session.createQuery(
                "FROM Inventaire i WHERE i.estClos = true OR i.etatInventaire = 'CLOS' " +
                "ORDER BY i.dateFin DESC", Inventaire.class);
            List<Inventaire> inventaires = query.getResultList();
            logger.debug("Found {} closed inventories", inventaires.size());
            return inventaires;
        } catch (Exception e) {
            logger.error("Error finding closed inventories", e);
            throw new RuntimeException("Error finding closed inventories", e);
        }
    }
    
    @Override
    public List<Inventaire> findByDateRange(LocalDate dateDebut, LocalDate dateFin) {
        try (Session session = openSession()) {
            Query<Inventaire> query = session.createQuery(
                "FROM Inventaire i WHERE i.dateDebut BETWEEN :dateDebut AND :dateFin " +
                "ORDER BY i.dateDebut DESC", Inventaire.class);
            query.setParameter("dateDebut", dateDebut);
            query.setParameter("dateFin", dateFin);
            List<Inventaire> inventaires = query.getResultList();
            logger.debug("Found {} inventories between {} and {}", inventaires.size(), dateDebut, dateFin);
            return inventaires;
        } catch (Exception e) {
            logger.error("Error finding inventories by date range: {} - {}", dateDebut, dateFin, e);
            throw new RuntimeException("Error finding inventories by date range", e);
        }
    }
    
    @Override
    public List<Inventaire> findByResponsable(String responsable) {
        try (Session session = openSession()) {
            Query<Inventaire> query = session.createQuery(
                "FROM Inventaire i WHERE i.responsable = :responsable ORDER BY i.dateDebut DESC", Inventaire.class);
            query.setParameter("responsable", responsable);
            List<Inventaire> inventaires = query.getResultList();
            logger.debug("Found {} inventories for responsible: {}", inventaires.size(), responsable);
            return inventaires;
        } catch (Exception e) {
            logger.error("Error finding inventories by responsible: {}", responsable, e);
            throw new RuntimeException("Error finding inventories by responsible", e);
        }
    }
    
    @Override
    public List<Inventaire> findByEtat(String etatInventaire) {
        try (Session session = openSession()) {
            Query<Inventaire> query = session.createQuery(
                "FROM Inventaire i WHERE i.etatInventaire = :etatInventaire ORDER BY i.dateDebut DESC", Inventaire.class);
            query.setParameter("etatInventaire", etatInventaire);
            List<Inventaire> inventaires = query.getResultList();
            logger.debug("Found {} inventories in state: {}", inventaires.size(), etatInventaire);
            return inventaires;
        } catch (Exception e) {
            logger.error("Error finding inventories by state: {}", etatInventaire, e);
            throw new RuntimeException("Error finding inventories by state", e);
        }
    }
    
    @Override
    public List<Inventaire> findWithDiscrepancies() {
        try (Session session = openSession()) {
            Query<Inventaire> query = session.createQuery(
                "FROM Inventaire i WHERE i.ecartsDetectes > 0 ORDER BY i.ecartsDetectes DESC", Inventaire.class);
            List<Inventaire> inventaires = query.getResultList();
            logger.debug("Found {} inventories with discrepancies", inventaires.size());
            return inventaires;
        } catch (Exception e) {
            logger.error("Error finding inventories with discrepancies", e);
            throw new RuntimeException("Error finding inventories with discrepancies", e);
        }
    }
    
    @Override
    public List<Inventaire> findCurrentYear() {
        try (Session session = openSession()) {
            int currentYear = LocalDate.now().getYear();
            Query<Inventaire> query = session.createQuery(
                "FROM Inventaire i WHERE YEAR(i.dateDebut) = :year ORDER BY i.dateDebut DESC", Inventaire.class);
            query.setParameter("year", currentYear);
            List<Inventaire> inventaires = query.getResultList();
            logger.debug("Found {} inventories for current year: {}", inventaires.size(), currentYear);
            return inventaires;
        } catch (Exception e) {
            logger.error("Error finding current year inventories", e);
            throw new RuntimeException("Error finding current year inventories", e);
        }
    }
    
    @Override
    public List<Inventaire> findByCompletionAbove(double minPercentage) {
        try (Session session = openSession()) {
            Query<Inventaire> query = session.createQuery(
                "FROM Inventaire i WHERE i.nombreProduitsTotal > 0 AND " +
                "((CAST(i.nombreProduitsComptes AS double) / CAST(i.nombreProduitsTotal AS double)) * 100) >= :minPercentage " +
                "ORDER BY ((CAST(i.nombreProduitsComptes AS double) / CAST(i.nombreProduitsTotal AS double)) * 100) DESC", 
                Inventaire.class);
            query.setParameter("minPercentage", minPercentage);
            List<Inventaire> inventaires = query.getResultList();
            logger.debug("Found {} inventories with completion above {}%", inventaires.size(), minPercentage);
            return inventaires;
        } catch (Exception e) {
            logger.error("Error finding inventories by completion above: {}%", minPercentage, e);
            throw new RuntimeException("Error finding inventories by completion percentage", e);
        }
    }
    
    @Override
    public Optional<Inventaire> findMostRecent() {
        try (Session session = openSession()) {
            Query<Inventaire> query = session.createQuery(
                "FROM Inventaire i ORDER BY i.dateCreation DESC", Inventaire.class);
            query.setMaxResults(1);
            List<Inventaire> results = query.getResultList();
            Optional<Inventaire> mostRecent = results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
            logger.debug("Most recent inventory found: {}", mostRecent.isPresent());
            return mostRecent;
        } catch (Exception e) {
            logger.error("Error finding most recent inventory", e);
            throw new RuntimeException("Error finding most recent inventory", e);
        }
    }
    
    @Override
    public List<Inventaire> findNeedingAttention() {
        try (Session session = openSession()) {
            LocalDate threeDaysAgo = LocalDate.now().minusDays(3);
            Query<Inventaire> query = session.createQuery(
                "FROM Inventaire i WHERE i.estClos = false AND i.dateDebut < :deadline AND " +
                "(i.nombreProduitsTotal IS NULL OR i.nombreProduitsComptes IS NULL OR " +
                "i.nombreProduitsComptes < i.nombreProduitsTotal) " +
                "ORDER BY i.dateDebut ASC", Inventaire.class);
            query.setParameter("deadline", threeDaysAgo);
            List<Inventaire> inventaires = query.getResultList();
            logger.debug("Found {} inventories needing attention", inventaires.size());
            return inventaires;
        } catch (Exception e) {
            logger.error("Error finding inventories needing attention", e);
            throw new RuntimeException("Error finding inventories needing attention", e);
        }
    }
    
    @Override
    public boolean existsByNumeroInventaire(String numeroInventaire) {
        try (Session session = openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(i) FROM Inventaire i WHERE i.numeroInventaire = :numeroInventaire", Long.class);
            query.setParameter("numeroInventaire", numeroInventaire);
            Long count = query.getSingleResult();
            boolean exists = count != null && count > 0;
            logger.debug("Inventory number {} exists: {}", numeroInventaire, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error checking inventory number existence: {}", numeroInventaire, e);
            throw new RuntimeException("Error checking inventory number existence", e);
        }
    }
    
    @Override
    public long countByYear(int year) {
        try (Session session = openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(i) FROM Inventaire i WHERE YEAR(i.dateDebut) = :year", Long.class);
            query.setParameter("year", year);
            Long count = query.getSingleResult();
            logger.debug("Count of inventories in year {}: {}", year, count);
            return count != null ? count : 0L;
        } catch (Exception e) {
            logger.error("Error counting inventories by year: {}", year, e);
            throw new RuntimeException("Error counting inventories by year", e);
        }
    }
    
    @Override
    public long countActive() {
        try (Session session = openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(i) FROM Inventaire i WHERE i.estClos = false", Long.class);
            Long count = query.getSingleResult();
            logger.debug("Count of active inventories: {}", count);
            return count != null ? count : 0L;
        } catch (Exception e) {
            logger.error("Error counting active inventories", e);
            throw new RuntimeException("Error counting active inventories", e);
        }
    }

    @Override
    public List<Inventaire> findByProduit(Produit produit) {
        try (Session session = openSession()) {
            Query<Inventaire> query = session.createQuery(
                "SELECT DISTINCT i FROM Inventaire i " +
                "JOIN TacheInventaire t ON t.inventaire = i " +
                "WHERE t.produit = :produit ORDER BY i.dateDebut DESC", 
                Inventaire.class);
            query.setParameter("produit", produit);
            List<Inventaire> result = query.getResultList();
            logger.debug("Found {} inventories for product: {}", result.size(), produit.getIdProduit());
            return result;
        } catch (Exception e) {
            logger.error("Error finding inventories by product: {}", produit.getIdProduit(), e);
            throw new RuntimeException("Error finding inventories by product", e);
        }
    }

    @Override
    public List<Inventaire> findByPersonnel(Personnel personnel) {
        try (Session session = openSession()) {
            Query<Inventaire> query = session.createQuery(
                "FROM Inventaire i WHERE i.responsable = :responsableName ORDER BY i.dateDebut DESC", 
                Inventaire.class);
            query.setParameter("responsableName", personnel.getNom() + " " + personnel.getPrenom());
            List<Inventaire> result = query.getResultList();
            logger.debug("Found {} inventories for personnel: {}", result.size(), personnel.getIdPersonne());
            return result;
        } catch (Exception e) {
            logger.error("Error finding inventories by personnel: {}", personnel.getIdPersonne(), e);
            throw new RuntimeException("Error finding inventories by personnel", e);
        }
    }

    @Override
    public List<Inventaire> findWithSignificantDiscrepancies(int threshold) {
        try (Session session = openSession()) {
            Query<Inventaire> query = session.createQuery(
                "SELECT DISTINCT i FROM Inventaire i " +
                "JOIN TacheInventaire t ON t.inventaire = i " +
                "WHERE ABS(t.quantiteTheorique - t.quantiteReelle) > :threshold " +
                "ORDER BY i.dateDebut DESC", 
                Inventaire.class);
            query.setParameter("threshold", threshold);
            List<Inventaire> result = query.getResultList();
            logger.debug("Found {} inventories with significant discrepancies (threshold: {})", 
                result.size(), threshold);
            return result;
        } catch (Exception e) {
            logger.error("Error finding inventories with significant discrepancies", e);
            throw new RuntimeException("Error finding inventories with significant discrepancies", e);
        }
    }

    @Override
    public BigDecimal calculateTotalVariance() {
        try (Session session = openSession()) {
            Query<BigDecimal> query = session.createQuery(
                "SELECT COALESCE(SUM(ABS(t.quantiteTheorique - t.quantiteReelle)), 0) " +
                "FROM TacheInventaire t", 
                BigDecimal.class);
            BigDecimal result = query.getSingleResult();
            logger.debug("Total variance across all inventory tasks: {}", result);
            return result != null ? result : BigDecimal.ZERO;
        } catch (Exception e) {
            logger.error("Error calculating total variance", e);
            throw new RuntimeException("Error calculating total variance", e);
        }
    }

    @Override
    public long countAccurateInventories() {
        try (Session session = openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(t) FROM TacheInventaire t " +
                "WHERE t.quantiteTheorique = t.quantiteReelle", 
                Long.class);
            Long result = query.getSingleResult();
            logger.debug("Count of accurate inventory tasks: {}", result);
            return result != null ? result : 0L;
        } catch (Exception e) {
            logger.error("Error counting accurate inventory tasks", e);
            throw new RuntimeException("Error counting accurate inventory tasks", e);
        }
    }

    @Override
    public Map<CategorieProduit, Object[]> getInventoryStatisticsByCategory() {
        try (Session session = openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT p.categorie, " +
                "COUNT(t), " +
                "SUM(ABS(t.quantiteTheorique - t.quantiteReelle)), " +
                "AVG(ABS(t.quantiteTheorique - t.quantiteReelle)) " +
                "FROM TacheInventaire t " +
                "JOIN t.produit p " +
                "GROUP BY p.categorie", 
                Object[].class);
            
            List<Object[]> results = query.getResultList();
            Map<CategorieProduit, Object[]> statisticsMap = new HashMap<>();
            
            for (Object[] result : results) {
                CategorieProduit categorie = (CategorieProduit) result[0];
                Object[] stats = new Object[]{result[1], result[2], result[3]};
                statisticsMap.put(categorie, stats);
            }
            
            logger.debug("Generated inventory statistics for {} categories", statisticsMap.size());
            return statisticsMap;
        } catch (Exception e) {
            logger.error("Error getting inventory statistics by category", e);
            throw new RuntimeException("Error getting inventory statistics by category", e);
        }
    }

    // Override the findAll method to eagerly load tasks
    @Override
    public List<Inventaire> findAll() {
        try (Session session = openSession()) {
            Query<Inventaire> query = session.createQuery(
                "SELECT DISTINCT i FROM Inventaire i LEFT JOIN FETCH i.taches " +
                "ORDER BY i.dateCreation DESC", Inventaire.class);
            List<Inventaire> inventaires = query.getResultList();
            logger.debug("Found {} inventories with tasks eagerly loaded", inventaires.size());
            return inventaires;
        } catch (Exception e) {
            logger.error("Error finding all inventories with tasks", e);
            // Fallback to regular findAll without eager loading
            return super.findAll();
        }
    }
    
    @Override
    public List<Inventaire> findByDescriptionContaining(String searchTerm) {
        try (Session session = openSession()) {
            Query<Inventaire> query = session.createQuery(
                "SELECT DISTINCT i FROM Inventaire i LEFT JOIN FETCH i.taches " +
                "WHERE LOWER(i.description) LIKE LOWER(:searchTerm) ORDER BY i.dateDebut DESC", 
                Inventaire.class);
            query.setParameter("searchTerm", "%" + searchTerm + "%");
            List<Inventaire> inventaires = query.getResultList();
            logger.debug("Found {} inventories matching search term: {}", inventaires.size(), searchTerm);
            return inventaires;
        } catch (Exception e) {
            logger.error("Error finding inventories by description containing: {}", searchTerm, e);
            throw new RuntimeException("Error finding inventories by description", e);
        }
    }
}
