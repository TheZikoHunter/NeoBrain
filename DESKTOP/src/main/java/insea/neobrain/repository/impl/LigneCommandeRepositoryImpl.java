package insea.neobrain.repository.impl;

import insea.neobrain.entity.LigneCommande;
import insea.neobrain.entity.CommandeVente;
import insea.neobrain.entity.Produit;
import insea.neobrain.repository.LigneCommandeRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of LigneCommandeRepository
 * Provides data access operations for order line management
 */
public class LigneCommandeRepositoryImpl extends GenericRepositoryImpl<LigneCommande, Long> 
        implements LigneCommandeRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(LigneCommandeRepositoryImpl.class);
    
    public LigneCommandeRepositoryImpl() {
        super();
    }
    
    @Override
    public List<LigneCommande> findByCommande(CommandeVente commande) {
        try (Session session = sessionFactory.openSession()) {
            Query<LigneCommande> query = session.createQuery(
                "FROM LigneCommande l WHERE l.commande = :commande ORDER BY l.id", 
                LigneCommande.class);
            query.setParameter("commande", commande);
            
            List<LigneCommande> result = query.getResultList();
            logger.debug("Found {} order lines for command: {}", result.size(), commande.getIdCommandeVente());
            return result;
        } catch (Exception e) {
            logger.error("Error finding order lines by command: {}", commande.getIdCommandeVente(), e);
            throw new RuntimeException("Error finding order lines by command", e);
        }
    }
    
    @Override
    public List<LigneCommande> findByProduit(Produit produit) {
        try (Session session = sessionFactory.openSession()) {
            Query<LigneCommande> query = session.createQuery(
                "FROM LigneCommande l WHERE l.produit = :produit ORDER BY l.commande.date DESC", 
                LigneCommande.class);
            query.setParameter("produit", produit);
            
            List<LigneCommande> result = query.getResultList();
            logger.debug("Found {} order lines for product: {}", result.size(), produit.getIdProduit());
            return result;
        } catch (Exception e) {
            logger.error("Error finding order lines by product: {}", produit.getIdProduit(), e);
            throw new RuntimeException("Error finding order lines by product", e);
        }
    }
    
    @Override
    public List<LigneCommande> findByProduitAndDateBetween(Produit produit, LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<LigneCommande> query = session.createQuery(
                "FROM LigneCommande l WHERE l.produit = :produit " +
                "AND l.commande.date BETWEEN :startDate AND :endDate " +
                "ORDER BY l.commande.date DESC", 
                LigneCommande.class);
            query.setParameter("produit", produit);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            List<LigneCommande> result = query.getResultList();
            logger.debug("Found {} order lines for product {} between {} and {}", 
                result.size(), produit.getIdProduit(), startDate, endDate);
            return result;
        } catch (Exception e) {            logger.error("Error finding order lines by product and date range: {} - {} to {}",
                produit.getIdProduit(), startDate, endDate, e);
            throw new RuntimeException("Error finding order lines by product and date range", e);
        }
    }
    
    @Override
    public int getTotalQuantitySold(Produit produit) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COALESCE(SUM(l.quantite), 0) FROM LigneCommande l WHERE l.produit = :produit", 
                Long.class);
            query.setParameter("produit", produit);
            
            Long result = query.getSingleResult();
            int totalQuantity = result != null ? result.intValue() : 0;
            logger.debug("Total quantity sold for product {}: {}", produit.getIdProduit(), totalQuantity);
            return totalQuantity;
        } catch (Exception e) {
            logger.error("Error getting total quantity sold for product: {}", produit.getIdProduit(), e);
            throw new RuntimeException("Error getting total quantity sold for product", e);
        }
    }
    
    @Override
    public int getTotalQuantitySoldInDateRange(Produit produit, LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COALESCE(SUM(l.quantite), 0) FROM LigneCommande l " +
                "WHERE l.produit = :produit AND l.commande.date BETWEEN :startDate AND :endDate", 
                Long.class);
            query.setParameter("produit", produit);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            Long result = query.getSingleResult();
            int totalQuantity = result != null ? result.intValue() : 0;
            logger.debug("Total quantity sold for product {} between {} and {}: {}", 
                produit.getIdProduit(), startDate, endDate, totalQuantity);
            return totalQuantity;
        } catch (Exception e) {            logger.error("Error getting total quantity sold for product in date range: {} - {} to {}",
                produit.getIdProduit(), startDate, endDate, e);
            throw new RuntimeException("Error getting total quantity sold for product in date range", e);
        }
    }
    
    @Override
    public BigDecimal getTotalRevenue(Produit produit) {
        try (Session session = sessionFactory.openSession()) {
            Query<BigDecimal> query = session.createQuery(
                "SELECT COALESCE(SUM(l.sousTotal), 0) FROM LigneCommande l WHERE l.produit = :produit", 
                BigDecimal.class);
            query.setParameter("produit", produit);
            
            BigDecimal result = query.getSingleResult();
            BigDecimal totalRevenue = result != null ? result : BigDecimal.ZERO;
            logger.debug("Total revenue for product {}: {}", produit.getIdProduit(), totalRevenue);
            return totalRevenue;
        } catch (Exception e) {
            logger.error("Error getting total revenue for product: {}", produit.getIdProduit(), e);
            throw new RuntimeException("Error getting total revenue for product", e);
        }
    }
    
    @Override
    public BigDecimal getTotalRevenueInDateRange(Produit produit, LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<BigDecimal> query = session.createQuery(
                "SELECT COALESCE(SUM(l.sousTotal), 0) FROM LigneCommande l " +
                "WHERE l.produit = :produit AND l.commande.date BETWEEN :startDate AND :endDate", 
                BigDecimal.class);
            query.setParameter("produit", produit);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            BigDecimal result = query.getSingleResult();
            BigDecimal totalRevenue = result != null ? result : BigDecimal.ZERO;
            logger.debug("Total revenue for product {} between {} and {}: {}", 
                produit.getIdProduit(), startDate, endDate, totalRevenue);
            return totalRevenue;
        } catch (Exception e) {            logger.error("Error getting total revenue for product in date range: {} - {} to {}",
                produit.getIdProduit(), startDate, endDate, e);
            throw new RuntimeException("Error getting total revenue for product in date range", e);
        }
    }
    
    @Override
    public List<Object[]> getTopSellingProductsByQuantity(int limit) {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT l.produit, SUM(l.quantite) as totalQuantity " +
                "FROM LigneCommande l GROUP BY l.produit ORDER BY totalQuantity DESC", 
                Object[].class);
            query.setMaxResults(limit);
            
            List<Object[]> result = query.getResultList();
            logger.debug("Found {} top selling products by quantity", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Error getting top selling products by quantity", e);
            throw new RuntimeException("Error getting top selling products by quantity", e);
        }
    }
    
    @Override
    public List<Object[]> getTopSellingProductsByQuantityInDateRange(LocalDate startDate, LocalDate endDate, int limit) {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT l.produit, SUM(l.quantite) as totalQuantity " +
                "FROM LigneCommande l WHERE l.commande.date BETWEEN :startDate AND :endDate " +
                "GROUP BY l.produit ORDER BY totalQuantity DESC", 
                Object[].class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            query.setMaxResults(limit);
            
            List<Object[]> result = query.getResultList();
            logger.debug("Found {} top selling products by quantity between {} and {}", 
                result.size(), startDate, endDate);
            return result;
        } catch (Exception e) {
            logger.error("Error getting top selling products by quantity in date range: {} - {}", 
                startDate, endDate, e);
            throw new RuntimeException("Error getting top selling products by quantity in date range", e);
        }
    }
    
    @Override
    public List<Object[]> getTopSellingProductsByRevenue(int limit) {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT l.produit, SUM(l.sousTotal) as totalRevenue " +
                "FROM LigneCommande l GROUP BY l.produit ORDER BY totalRevenue DESC", 
                Object[].class);
            query.setMaxResults(limit);
            
            List<Object[]> result = query.getResultList();
            logger.debug("Found {} top selling products by revenue", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Error getting top selling products by revenue", e);
            throw new RuntimeException("Error getting top selling products by revenue", e);
        }
    }
    
    @Override
    public List<Object[]> getTopSellingProductsByRevenueInDateRange(LocalDate startDate, LocalDate endDate, int limit) {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT l.produit, SUM(l.sousTotal) as totalRevenue " +
                "FROM LigneCommande l WHERE l.commande.date BETWEEN :startDate AND :endDate " +
                "GROUP BY l.produit ORDER BY totalRevenue DESC", 
                Object[].class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            query.setMaxResults(limit);
            
            List<Object[]> result = query.getResultList();
            logger.debug("Found {} top selling products by revenue between {} and {}", 
                result.size(), startDate, endDate);
            return result;
        } catch (Exception e) {
            logger.error("Error getting top selling products by revenue in date range: {} - {}", 
                startDate, endDate, e);
            throw new RuntimeException("Error getting top selling products by revenue in date range", e);
        }
    }
    
    @Override
    public List<Object[]> getSalesSummaryByCategory(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT l.produit.categorie, SUM(l.quantite), SUM(l.sousTotal) " +
                "FROM LigneCommande l WHERE l.commande.date BETWEEN :startDate AND :endDate " +
                "GROUP BY l.produit.categorie ORDER BY SUM(l.sousTotal) DESC", 
                Object[].class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            List<Object[]> result = query.getResultList();
            logger.debug("Found {} category sales summaries between {} and {}", 
                result.size(), startDate, endDate);
            return result;
        } catch (Exception e) {
            logger.error("Error getting sales summary by category: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error getting sales summary by category", e);
        }
    }
    
    @Override
    public List<LigneCommande> findByQuantityGreaterThanEqual(int minQuantity) {
        try (Session session = sessionFactory.openSession()) {
            Query<LigneCommande> query = session.createQuery(
                "FROM LigneCommande l WHERE l.quantite >= :minQuantity ORDER BY l.quantite DESC", 
                LigneCommande.class);
            query.setParameter("minQuantity", minQuantity);
            
            List<LigneCommande> result = query.getResultList();
            logger.debug("Found {} order lines with quantity >= {}", result.size(), minQuantity);
            return result;
        } catch (Exception e) {
            logger.error("Error finding order lines by minimum quantity: {}", minQuantity, e);
            throw new RuntimeException("Error finding order lines by minimum quantity", e);
        }
    }
    
    @Override
    public List<LigneCommande> findByPrixUnitaireBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        try (Session session = sessionFactory.openSession()) {
            Query<LigneCommande> query = session.createQuery(
                "FROM LigneCommande l WHERE l.prixUnitaire BETWEEN :minPrice AND :maxPrice " +
                "ORDER BY l.prixUnitaire", 
                LigneCommande.class);
            query.setParameter("minPrice", minPrice);
            query.setParameter("maxPrice", maxPrice);
            
            List<LigneCommande> result = query.getResultList();
            logger.debug("Found {} order lines with unit price between {} and {}", 
                result.size(), minPrice, maxPrice);
            return result;
        } catch (Exception e) {
            logger.error("Error finding order lines by unit price range: {} - {}", minPrice, maxPrice, e);
            throw new RuntimeException("Error finding order lines by unit price range", e);
        }
    }
    
    @Override
    public BigDecimal getAverageSellingPrice(Produit produit) {
        try (Session session = sessionFactory.openSession()) {
            Query<BigDecimal> query = session.createQuery(
                "SELECT COALESCE(AVG(l.prixUnitaire), 0) FROM LigneCommande l WHERE l.produit = :produit", 
                BigDecimal.class);
            query.setParameter("produit", produit);
            
            BigDecimal result = query.getSingleResult();
            BigDecimal avgPrice = result != null ? result : BigDecimal.ZERO;
            logger.debug("Average selling price for product {}: {}", produit.getIdProduit(), avgPrice);
            return avgPrice;
        } catch (Exception e) {
            logger.error("Error getting average selling price for product: {}", produit.getIdProduit(), e);
            throw new RuntimeException("Error getting average selling price for product", e);
        }
    }
    
    @Override
    public List<LigneCommande> findOrderLinesWithDiscounts() {
        try (Session session = sessionFactory.openSession()) {
            Query<LigneCommande> query = session.createQuery(
                "FROM LigneCommande l WHERE l.remise > 0 ORDER BY l.remise DESC", 
                LigneCommande.class);
            
            List<LigneCommande> result = query.getResultList();
            logger.debug("Found {} order lines with discounts", result.size());
            return result;
        } catch (Exception e) {
            logger.error("Error finding order lines with discounts", e);
            throw new RuntimeException("Error finding order lines with discounts", e);
        }
    }
    
    @Override
    public BigDecimal getTotalDiscountAmount(LocalDate startDate, LocalDate endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<BigDecimal> query = session.createQuery(
                "SELECT COALESCE(SUM(l.quantite * l.prixUnitaire * l.remise / 100), 0) " +
                "FROM LigneCommande l WHERE l.commande.date BETWEEN :startDate AND :endDate", 
                BigDecimal.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            
            BigDecimal result = query.getSingleResult();
            BigDecimal totalDiscount = result != null ? result : BigDecimal.ZERO;
            logger.debug("Total discount amount between {} and {}: {}", startDate, endDate, totalDiscount);
            return totalDiscount;
        } catch (Exception e) {
            logger.error("Error getting total discount amount: {} - {}", startDate, endDate, e);
            throw new RuntimeException("Error getting total discount amount", e);
        }
    }
}
