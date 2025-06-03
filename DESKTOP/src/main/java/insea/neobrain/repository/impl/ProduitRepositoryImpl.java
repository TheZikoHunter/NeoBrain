package insea.neobrain.repository.impl;

import insea.neobrain.entity.CategorieProduit;
import insea.neobrain.entity.Produit;
import insea.neobrain.repository.ProduitRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of ProduitRepository
 */
public class ProduitRepositoryImpl extends GenericRepositoryImpl<Produit, Long> implements ProduitRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(ProduitRepositoryImpl.class);
    
    @Override
    public Optional<Produit> findByReference(String reference) {
        try (Session session = openSession()) {
            Query<Produit> query = session.createQuery(
                "FROM Produit p WHERE p.reference = :reference", Produit.class);
            query.setParameter("reference", reference);
            Produit produit = query.uniqueResult();
            logger.debug("Product found by reference {}: {}", reference, produit != null);
            return Optional.ofNullable(produit);
        } catch (Exception e) {
            logger.error("Error finding product by reference: {}", reference, e);
            throw new RuntimeException("Error finding product by reference", e);
        }
    }
    
    @Override
    public List<Produit> findByNom(String nom) {
        try (Session session = openSession()) {
            String searchPattern = "%" + nom.toLowerCase() + "%";
            Query<Produit> query = session.createQuery(
                "FROM Produit p WHERE LOWER(p.nom) LIKE :nom ORDER BY p.nom", Produit.class);
            query.setParameter("nom", searchPattern);
            List<Produit> produits = query.getResultList();
            logger.debug("Found {} products with name containing: {}", produits.size(), nom);
            return produits;
        } catch (Exception e) {
            logger.error("Error finding products by name: {}", nom, e);
            throw new RuntimeException("Error finding products by name", e);
        }
    }
    
    @Override
    public List<Produit> searchByNameOrReference(String searchTerm) {
        try (Session session = openSession()) {
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            Query<Produit> query = session.createQuery(
                "FROM Produit p WHERE LOWER(p.nom) LIKE :searchTerm " +
                "OR LOWER(p.reference) LIKE :searchTerm " +
                "OR LOWER(p.description) LIKE :searchTerm " +
                "ORDER BY p.nom", Produit.class);
            query.setParameter("searchTerm", searchPattern);
            List<Produit> produits = query.getResultList();
            logger.debug("Found {} products matching search term: {}", produits.size(), searchTerm);
            return produits;
        } catch (Exception e) {
            logger.error("Error searching products by name or reference: {}", searchTerm, e);
            throw new RuntimeException("Error searching products by name or reference", e);
        }
    }
    
    @Override
    public List<Produit> findByCategorie(CategorieProduit categorie) {
        try (Session session = openSession()) {
            Query<Produit> query = session.createQuery(
                "FROM Produit p WHERE p.categorie = :categorie ORDER BY p.nom", Produit.class);
            query.setParameter("categorie", categorie);
            List<Produit> produits = query.getResultList();
            logger.debug("Found {} products in category: {}", produits.size(), categorie);
            return produits;
        } catch (Exception e) {
            logger.error("Error finding products by category: {}", categorie, e);
            throw new RuntimeException("Error finding products by category", e);
        }
    }
    
    @Override
    public List<Produit> findActive() {
        try (Session session = openSession()) {
            Query<Produit> query = session.createQuery(
                "FROM Produit p WHERE p.actif = true ORDER BY p.nom", Produit.class);
            List<Produit> produits = query.getResultList();
            logger.debug("Found {} active products", produits.size());
            return produits;
        } catch (Exception e) {
            logger.error("Error finding active products", e);
            throw new RuntimeException("Error finding active products", e);
        }
    }
    
    @Override
    public List<Produit> findAvailableForSale() {
        try (Session session = openSession()) {
            Query<Produit> query = session.createQuery(
                "FROM Produit p WHERE p.actif = true AND p.disponible = true " +
                "AND p.quantiteStock > 0 ORDER BY p.nom", Produit.class);
            List<Produit> produits = query.getResultList();
            logger.debug("Found {} products available for sale", produits.size());
            return produits;
        } catch (Exception e) {
            logger.error("Error finding products available for sale", e);
            throw new RuntimeException("Error finding products available for sale", e);
        }
    }
    
    @Override
    public List<Produit> findWithLowStock() {
        try (Session session = openSession()) {
            Query<Produit> query = session.createQuery(
                "FROM Produit p WHERE p.actif = true AND p.quantiteStock <= p.stockMinimum " +
                "AND p.quantiteStock > 0 ORDER BY p.quantiteStock ASC", Produit.class);
            List<Produit> produits = query.getResultList();
            logger.debug("Found {} products with low stock", produits.size());
            return produits;
        } catch (Exception e) {
            logger.error("Error finding products with low stock", e);
            throw new RuntimeException("Error finding products with low stock", e);
        }
    }
    
    @Override
    public List<Produit> findOutOfStock() {
        try (Session session = openSession()) {
            Query<Produit> query = session.createQuery(
                "FROM Produit p WHERE p.actif = true AND " +
                "(p.quantiteStock IS NULL OR p.quantiteStock <= 0) ORDER BY p.nom", Produit.class);
            List<Produit> produits = query.getResultList();
            logger.debug("Found {} products out of stock", produits.size());
            return produits;
        } catch (Exception e) {
            logger.error("Error finding products out of stock", e);
            throw new RuntimeException("Error finding products out of stock", e);
        }
    }
    
    @Override
    public List<Produit> findByPriceRange(BigDecimal prixMin, BigDecimal prixMax) {
        try (Session session = openSession()) {
            Query<Produit> query = session.createQuery(
                "FROM Produit p WHERE p.prix BETWEEN :prixMin AND :prixMax " +
                "ORDER BY p.prix ASC", Produit.class);
            query.setParameter("prixMin", prixMin);
            query.setParameter("prixMax", prixMax);
            List<Produit> produits = query.getResultList();
            logger.debug("Found {} products in price range: {} - {}", produits.size(), prixMin, prixMax);
            return produits;
        } catch (Exception e) {
            logger.error("Error finding products by price range: {} - {}", prixMin, prixMax, e);
            throw new RuntimeException("Error finding products by price range", e);
        }
    }
    
    @Override
    public List<Produit> findNeedingInventory() {
        try (Session session = openSession()) {
            Query<Produit> query = session.createQuery(
                "FROM Produit p WHERE p.actif = true AND p.besoinInventaire = true " +
                "ORDER BY p.dernierInventaire ASC NULLS FIRST", Produit.class);
            List<Produit> produits = query.getResultList();
            logger.debug("Found {} products needing inventory", produits.size());
            return produits;
        } catch (Exception e) {
            logger.error("Error finding products needing inventory", e);
            throw new RuntimeException("Error finding products needing inventory", e);
        }
    }
    
    @Override
    public List<Produit> findByEmplacement(String emplacement) {
        try (Session session = openSession()) {
            Query<Produit> query = session.createQuery(
                "FROM Produit p WHERE p.emplacement = :emplacement ORDER BY p.nom", Produit.class);
            query.setParameter("emplacement", emplacement);
            List<Produit> produits = query.getResultList();
            logger.debug("Found {} products at location: {}", produits.size(), emplacement);
            return produits;
        } catch (Exception e) {
            logger.error("Error finding products by location: {}", emplacement, e);
            throw new RuntimeException("Error finding products by location", e);
        }
    }
    
    @Override
    public List<Produit> findTopSellingProducts(int limit) {
        try (Session session = openSession()) {
            Query<Produit> query = session.createQuery(
                "SELECT lc.produit FROM LigneCommande lc " +
                "JOIN lc.commandeVente cv WHERE cv.estValide = true " +
                "GROUP BY lc.produit ORDER BY SUM(lc.quantiteVente) DESC", Produit.class);
            query.setMaxResults(limit);
            List<Produit> produits = query.getResultList();
            logger.debug("Found {} top selling products", produits.size());
            return produits;
        } catch (Exception e) {
            logger.error("Error finding top selling products", e);
            throw new RuntimeException("Error finding top selling products", e);
        }
    }
    
    @Override
    public List<Produit> findWithStockBelow(Integer threshold) {
        try (Session session = openSession()) {
            Query<Produit> query = session.createQuery(
                "FROM Produit p WHERE p.actif = true AND " +
                "(p.quantiteStock IS NULL OR p.quantiteStock < :threshold) " +
                "ORDER BY p.quantiteStock ASC NULLS FIRST", Produit.class);
            query.setParameter("threshold", threshold);
            List<Produit> produits = query.getResultList();
            logger.debug("Found {} products with stock below: {}", produits.size(), threshold);
            return produits;
        } catch (Exception e) {
            logger.error("Error finding products with stock below: {}", threshold, e);
            throw new RuntimeException("Error finding products with stock below threshold", e);
        }
    }
    
    @Override
    public boolean existsByReference(String reference) {
        try (Session session = openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(p) FROM Produit p WHERE p.reference = :reference", Long.class);
            query.setParameter("reference", reference);
            Long count = query.getSingleResult();
            boolean exists = count != null && count > 0;
            logger.debug("Product reference {} exists: {}", reference, exists);
            return exists;
        } catch (Exception e) {
            logger.error("Error checking product reference existence: {}", reference, e);
            throw new RuntimeException("Error checking product reference existence", e);
        }
    }
    
    @Override
    public long countByCategorie(CategorieProduit categorie) {
        try (Session session = openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(p) FROM Produit p WHERE p.categorie = :categorie", Long.class);
            query.setParameter("categorie", categorie);
            Long count = query.getSingleResult();
            logger.debug("Count of products in category {}: {}", categorie, count);
            return count != null ? count : 0L;
        } catch (Exception e) {
            logger.error("Error counting products by category: {}", categorie, e);
            throw new RuntimeException("Error counting products by category", e);
        }
    }
    
    @Override
    public long countActive() {
        try (Session session = openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(p) FROM Produit p WHERE p.actif = true", Long.class);
            Long count = query.getSingleResult();
            logger.debug("Count of active products: {}", count);
            return count != null ? count : 0L;
        } catch (Exception e) {
            logger.error("Error counting active products", e);
            throw new RuntimeException("Error counting active products", e);
        }
    }

    @Override
    public Optional<Produit> findByCodeBarre(String codeBarre) {
        try (Session session = openSession()) {
            Query<Produit> query = session.createQuery(
                "FROM Produit p WHERE p.codeBarre = :codeBarre", Produit.class);
            query.setParameter("codeBarre", codeBarre);
            Produit produit = query.uniqueResult();
            logger.debug("Product found by barcode {}: {}", codeBarre, produit != null);
            return Optional.ofNullable(produit);
        } catch (Exception e) {
            logger.error("Error finding product by barcode: {}", codeBarre, e);
            throw new RuntimeException("Error finding product by barcode", e);
        }
    }

    @Override
    public List<Produit> findByNomContaining(String nom) {
        return findByNom(nom); // Delegate to existing method
    }

    @Override
    public List<Produit> findLowStockProducts() {
        return findWithLowStock(); // Delegate to existing method
    }

    @Override
    public List<Produit> searchProducts(String nom, CategorieProduit categorie, BigDecimal minPrice, BigDecimal maxPrice) {
        try (Session session = openSession()) {
            StringBuilder queryBuilder = new StringBuilder("FROM Produit p WHERE 1=1");
            
            if (nom != null && !nom.trim().isEmpty()) {
                queryBuilder.append(" AND LOWER(p.nom) LIKE :nom");
            }
            if (categorie != null) {
                queryBuilder.append(" AND p.categorie = :categorie");
            }
            if (minPrice != null) {
                queryBuilder.append(" AND p.prix >= :minPrice");
            }
            if (maxPrice != null) {
                queryBuilder.append(" AND p.prix <= :maxPrice");
            }
            
            queryBuilder.append(" ORDER BY p.nom");
            
            Query<Produit> query = session.createQuery(queryBuilder.toString(), Produit.class);
            
            if (nom != null && !nom.trim().isEmpty()) {
                query.setParameter("nom", "%" + nom.toLowerCase() + "%");
            }
            if (categorie != null) {
                query.setParameter("categorie", categorie);
            }
            if (minPrice != null) {
                query.setParameter("minPrice", minPrice);
            }
            if (maxPrice != null) {
                query.setParameter("maxPrice", maxPrice);
            }
            
            List<Produit> produits = query.getResultList();
            logger.debug("Found {} products with search criteria", produits.size());
            return produits;
        } catch (Exception e) {
            logger.error("Error searching products", e);
            throw new RuntimeException("Error searching products", e);
        }
    }

    @Override
    public long countLowStockProducts() {
        try (Session session = openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(p) FROM Produit p WHERE p.quantiteStock <= p.stockMinimum", Long.class);
            Long count = query.getSingleResult();
            logger.debug("Count of low stock products: {}", count);
            return count != null ? count : 0L;
        } catch (Exception e) {
            logger.error("Error counting low stock products", e);
            throw new RuntimeException("Error counting low stock products", e);
        }
    }

    @Override
    public BigDecimal calculateTotalInventoryValue() {
        try (Session session = openSession()) {
            Query<BigDecimal> query = session.createQuery(
                "SELECT SUM(p.prix * p.quantiteStock) FROM Produit p WHERE p.actif = true", BigDecimal.class);
            BigDecimal total = query.getSingleResult();
            logger.debug("Total inventory value: {}", total);
            return total != null ? total : BigDecimal.ZERO;
        } catch (Exception e) {
            logger.error("Error calculating total inventory value", e);
            throw new RuntimeException("Error calculating total inventory value", e);
        }
    }

    @Override
    public java.util.Map<CategorieProduit, Long> getProductCountByCategory() {
        try (Session session = openSession()) {
            Query<Object[]> query = session.createQuery(
                "SELECT p.categorie, COUNT(p) FROM Produit p WHERE p.actif = true GROUP BY p.categorie", Object[].class);
            List<Object[]> results = query.getResultList();
            
            java.util.Map<CategorieProduit, Long> map = new java.util.HashMap<>();
            for (Object[] result : results) {
                map.put((CategorieProduit) result[0], (Long) result[1]);
            }
            
            logger.debug("Product count by category: {}", map);
            return map;
        } catch (Exception e) {
            logger.error("Error getting product count by category", e);
            throw new RuntimeException("Error getting product count by category", e);
        }
    }

    @Override
    public boolean canDeleteProduct(Long produitId) {
        try (Session session = openSession()) {
            // Check if product has any inventory tasks
            Query<Long> inventoryTaskQuery = session.createQuery(
                "SELECT COUNT(t) FROM TacheInventaire t WHERE t.produit.idProduit = :produitId", Long.class);
            inventoryTaskQuery.setParameter("produitId", produitId);
            Long inventoryTaskCount = inventoryTaskQuery.getSingleResult();
            
            // Check if product has any order lines
            Query<Long> orderLineQuery = session.createQuery(
                "SELECT COUNT(l) FROM LigneCommande l WHERE l.produit.idProduit = :produitId", Long.class);
            orderLineQuery.setParameter("produitId", produitId);
            Long orderLineCount = orderLineQuery.getSingleResult();
            
            boolean canDelete = (inventoryTaskCount == 0) && (orderLineCount == 0);
            logger.debug("Can delete product {}: {}", produitId, canDelete);
            return canDelete;
        } catch (Exception e) {
            logger.error("Error checking if product can be deleted: {}", produitId, e);
            return false;
        }
    }

    @Override
    public List<Produit> findByActif(boolean actif) {
        logger.debug("Finding products by active status: {}", actif);
        try {
            Session session = sessionFactory.getCurrentSession();
            Query<Produit> query = session.createQuery(
                "FROM Produit p WHERE p.actif = :actif", Produit.class);
            query.setParameter("actif", actif);
            List<Produit> results = query.getResultList();
            logger.debug("Found {} products with active status: {}", results.size(), actif);
            return results;
        } catch (Exception e) {
            logger.error("Error finding products by active status: {}", actif, e);
            return new ArrayList<>();
        }
    }
}
