package insea.neobrain.service.impl;

import insea.neobrain.entity.Produit;
import insea.neobrain.entity.CategorieProduit;
import insea.neobrain.repository.ProduitRepository;
import insea.neobrain.service.ProduitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementation of ProduitService
 * Provides product management functionality with business logic
 */
public class ProduitServiceImpl implements ProduitService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProduitServiceImpl.class);
    private static final Pattern BARCODE_PATTERN = Pattern.compile("^[0-9]{8,14}$");
    
    private final ProduitRepository produitRepository;
    
    public ProduitServiceImpl(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }
    
    @Override
    public Produit createProduit(Produit produit) {
        try {
            logger.debug("Creating new product: {}", produit.getNomProduit());
            
            // Validate product data
            List<String> validationErrors = validateProduit(produit);
            if (!validationErrors.isEmpty()) {
                throw new IllegalArgumentException("Validation errors: " + String.join(", ", validationErrors));
            }
            
            // Set creation date
            produit.setDateCreation(LocalDateTime.now());
            
            Produit created = produitRepository.save(produit);
            logger.info("Product created successfully: {}", created.getNomProduit());
            return created;
            
        } catch (Exception e) {
            logger.error("Error creating product: {}", produit.getNomProduit(), e);
            throw new RuntimeException("Error creating product", e);
        }
    }
    
    @Override
    public Produit createProduit(String nom, String description, CategorieProduit categorie,
                               BigDecimal prix, int quantiteStock, int seuilStock) {
        Produit produit = new Produit();
        produit.setNomProduit(nom);
        produit.setDescription(description);
        produit.setCategorieProduit(categorie);
        produit.setPrixUnitaire(prix);
        produit.setQuantiteStock(quantiteStock);
        produit.setSeuilAlerte(seuilStock);
        
        return createProduit(produit);
    }
    
    @Override
    public Produit updateProduit(Produit produit) {
        try {
            logger.debug("Updating product: {}", produit.getNomProduit());
            
            // Validate product data
            List<String> validationErrors = validateProduit(produit);
            if (!validationErrors.isEmpty()) {
                throw new IllegalArgumentException("Validation errors: " + String.join(", ", validationErrors));
            }
            
            // Set modification date
            produit.setDateModification(LocalDateTime.now());
            
            Produit updated = produitRepository.update(produit);
            logger.info("Product updated successfully: {}", updated.getNomProduit());
            return updated;
            
        } catch (Exception e) {
            logger.error("Error updating product: {}", produit.getNomProduit(), e);
            throw new RuntimeException("Error updating product", e);
        }
    }
    
    @Override
    public boolean deleteProduit(Long id) {
        try {
            logger.debug("Deleting product with ID: {}", id);
            
            // Check if product can be deleted
            if (!canDeleteProduit(id)) {
                logger.warn("Product deletion denied: Has associated data. ID: {}", id);
                return false;
            }
            
            produitRepository.deleteById(id);
            logger.info("Product deleted successfully: ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("Error deleting product: ID {}", id, e);
            return false;
        }
    }
    
    @Override
    public Optional<Produit> findProduitById(Long id) {
        try {
            return produitRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error finding product by ID: {}", id, e);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Produit> findProduitByCodeBarre(String codeBarre) {
        try {
            return produitRepository.findByCodeBarre(codeBarre);
        } catch (Exception e) {
            logger.error("Error finding product by barcode: {}", codeBarre, e);
            return Optional.empty();
        }
    }
    
    @Override
    public List<Produit> findAllProduits() {
        try {
            return produitRepository.findAll();
        } catch (Exception e) {
            logger.error("Error finding all products", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Produit> findProduitsByCategorie(CategorieProduit categorie) {
        try {
            return produitRepository.findByCategorie(categorie);
        } catch (Exception e) {
            logger.error("Error finding products by category: {}", categorie, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Produit> findProduitsByName(String nom) {
        try {
            return produitRepository.findByNomContaining(nom);
        } catch (Exception e) {
            logger.error("Error finding products by name: {}", nom, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Produit> findLowStockProduits() {
        try {
            return produitRepository.findLowStockProducts();
        } catch (Exception e) {
            logger.error("Error finding low stock products", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Produit> findProduitsInPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        try {
            return produitRepository.findByPriceRange(minPrice, maxPrice);
        } catch (Exception e) {
            logger.error("Error finding products in price range: {} - {}", minPrice, maxPrice, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Produit> searchProduits(String nom, CategorieProduit categorie, 
                                       BigDecimal minPrice, BigDecimal maxPrice) {
        try {
            return produitRepository.searchProducts(nom, categorie, minPrice, maxPrice);
        } catch (Exception e) {
            logger.error("Error searching products with criteria", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean updateStock(Long produitId, int newQuantity) {
        try {
            Optional<Produit> produitOpt = produitRepository.findById(produitId);
            if (produitOpt.isEmpty()) {
                logger.warn("Product not found for stock update: ID {}", produitId);
                return false;
            }
            
            Produit produit = produitOpt.get();
            produit.setQuantiteStock(newQuantity);
            produit.setDateModification(LocalDateTime.now());
            
            produitRepository.update(produit);
            logger.info("Stock updated for product ID {}: new quantity = {}", produitId, newQuantity);
            
            // Check if stock is below threshold
            if (newQuantity <= produit.getSeuilAlerte()) {
                logger.warn("Stock below threshold for product {}: {} <= {}", 
                    produit.getNomProduit(), newQuantity, produit.getSeuilAlerte());
            }
            
            return true;
        } catch (Exception e) {
            logger.error("Error updating stock for product ID: {}", produitId, e);
            return false;
        }
    }
    
    @Override
    public boolean adjustStock(Long produitId, int quantity, String reason) {
        try {
            Optional<Produit> produitOpt = produitRepository.findById(produitId);
            if (produitOpt.isEmpty()) {
                logger.warn("Product not found for stock adjustment: ID {}", produitId);
                return false;
            }
            
            Produit produit = produitOpt.get();
            int newQuantity = produit.getQuantiteStock() + quantity;
            
            if (newQuantity < 0) {
                logger.warn("Stock adjustment would result in negative stock: {} + {} = {}", 
                    produit.getQuantiteStock(), quantity, newQuantity);
                return false;
            }
            
            produit.setQuantiteStock(newQuantity);
            produit.setDateModification(LocalDateTime.now());
            
            produitRepository.update(produit);
            logger.info("Stock adjusted for product ID {}: {} + {} = {} (reason: {})", 
                produitId, produit.getQuantiteStock() - quantity, quantity, newQuantity, reason);
            
            return true;
        } catch (Exception e) {
            logger.error("Error adjusting stock for product ID: {}", produitId, e);
            return false;
        }
    }
    
    @Override
    public boolean updatePrice(Long produitId, BigDecimal newPrice) {
        try {
            Optional<Produit> produitOpt = produitRepository.findById(produitId);
            if (produitOpt.isEmpty()) {
                logger.warn("Product not found for price update: ID {}", produitId);
                return false;
            }
            
            Produit produit = produitOpt.get();
            BigDecimal oldPrice = produit.getPrixUnitaire();
            produit.setPrixUnitaire(newPrice);
            produit.setDateModification(LocalDateTime.now());
            
            produitRepository.update(produit);
            logger.info("Price updated for product ID {}: {} -> {}", produitId, oldPrice, newPrice);
            
            return true;
        } catch (Exception e) {
            logger.error("Error updating price for product ID: {}", produitId, e);
            return false;
        }
    }
    
    @Override
    public List<String> validateProduit(Produit produit) {
        List<String> errors = new ArrayList<>();
        
        // Required fields validation
        if (produit.getNomProduit() == null || produit.getNomProduit().trim().isEmpty()) {
            errors.add("Product name is required");
        } else if (produit.getNomProduit().length() > 100) {
            errors.add("Product name must not exceed 100 characters");
        }
        
        if (produit.getCodeBarre() == null || produit.getCodeBarre().trim().isEmpty()) {
            errors.add("Barcode is required");
        } else {
            if (!BARCODE_PATTERN.matcher(produit.getCodeBarre()).matches()) {
                errors.add("Invalid barcode format (must be 8-14 digits)");
            }
            // Check barcode uniqueness
            if (!isBarcodeAvailable(produit.getCodeBarre(), produit.getIdProduit())) {
                errors.add("Barcode is already used by another product");
            }
        }
        
        if (produit.getCategorieProduit() == null) {
            errors.add("Product category is required");
        }
        
        if (produit.getPrixUnitaire() == null) {
            errors.add("Unit price is required");
        } else if (produit.getPrixUnitaire().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Unit price must be positive");
        } else if (produit.getPrixUnitaire().compareTo(new BigDecimal("999999.99")) > 0) {
            errors.add("Unit price too high (maximum 999,999.99)");
        }
        
        if (produit.getQuantiteStock() < 0) {
            errors.add("Stock quantity cannot be negative");
        }
        
        if (produit.getSeuilAlerte() < 0) {
            errors.add("Alert threshold cannot be negative");
        }
        
        if (produit.getDescription() != null && produit.getDescription().length() > 500) {
            errors.add("Description must not exceed 500 characters");
        }
        
        return errors;
    }
    
    @Override
    public boolean isBarcodeAvailable(String codeBarre) {
        return isBarcodeAvailable(codeBarre, null);
    }
    
    private boolean isBarcodeAvailable(String codeBarre, Long excludeProductId) {
        try {
            Optional<Produit> existing = produitRepository.findByCodeBarre(codeBarre);
            if (existing.isEmpty()) {
                return true;
            }
            
            // If excludeProductId is provided, check if it's the same product
            return excludeProductId != null && existing.get().getIdProduit().equals(excludeProductId);
        } catch (Exception e) {
            logger.error("Error checking barcode availability: {}", codeBarre, e);
            return false;
        }
    }
    
    @Override
    public String generateBarcode() {
        try {
            // Generate a simple 13-digit barcode based on timestamp
            long timestamp = System.currentTimeMillis();
            String barcode = String.format("20%011d", timestamp % 100000000000L);
            
            // Ensure uniqueness
            while (!isBarcodeAvailable(barcode)) {
                timestamp++;
                barcode = String.format("20%011d", timestamp % 100000000000L);
            }
            
            return barcode;
        } catch (Exception e) {
            logger.error("Error generating barcode", e);
            return "2000000000001"; // Fallback barcode
        }
    }
    
    @Override
    public long getProduitCount() {
        try {
            return produitRepository.count();
        } catch (Exception e) {
            logger.error("Error getting product count", e);
            return 0;
        }
    }
    
    @Override
    public long getLowStockCount() {
        try {
            return produitRepository.countLowStockProducts();
        } catch (Exception e) {
            logger.error("Error getting low stock count", e);
            return 0;
        }
    }
    
    @Override
    public BigDecimal getTotalInventoryValue() {
        try {
            return produitRepository.calculateTotalInventoryValue();
        } catch (Exception e) {
            logger.error("Error calculating total inventory value", e);
            return BigDecimal.ZERO;
        }
    }
    
    @Override
    public List<Object[]> getTopSellingProducts(int limit) {
        try {
            List<Produit> topProducts = produitRepository.findTopSellingProducts(limit);
            // Convert to Object[] format: [Product, Quantity Sold]
            return topProducts.stream()
                .map(p -> new Object[]{p, 0}) // Default quantity sold to 0 for now
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting top selling products", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Object[]> getProductStatisticsByCategory() {
        try {
            Map<CategorieProduit, Long> countMap = produitRepository.getProductCountByCategory();
            // Convert to Object[] format: [Category, Count, Total Value]
            return countMap.entrySet().stream()
                .map(entry -> new Object[]{entry.getKey(), entry.getValue(), BigDecimal.ZERO})
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting product statistics by category", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean canDeleteProduit(Long produitId) {
        try {
            // Check if product has associated orders, inventory records, etc.
            return produitRepository.canDeleteProduct(produitId);
        } catch (Exception e) {
            logger.error("Error checking if product can be deleted: {}", produitId, e);
            return false;
        }
    }
    
    @Override
    public String exportProduitsToCSV() {
        try {
            List<Produit> produits = produitRepository.findAll();
            StringBuilder csv = new StringBuilder();
            
            // Header
            csv.append("Code Barre,Nom Produit,Categorie,Description,Prix Unitaire,Quantite Stock,Seuil Alerte,Date Creation\n");
            
            // Data
            for (Produit p : produits) {
                csv.append(String.format("%s,%s,%s,\"%s\",%s,%d,%d,%s\n",
                    p.getCodeBarre(),
                    p.getNomProduit(),
                    p.getCategorieProduit(),
                    p.getDescription() != null ? p.getDescription().replace("\"", "\"\"") : "",
                    p.getPrixUnitaire(),
                    p.getQuantiteStock(),
                    p.getSeuilAlerte(),
                    p.getDateCreation()
                ));
            }
            
            logger.info("Product data exported to CSV: {} records", produits.size());
            return csv.toString();
            
        } catch (Exception e) {
            logger.error("Error exporting products to CSV", e);
            throw new RuntimeException("Error exporting products to CSV", e);
        }
    }
    
    @Override
    public int importProduitsFromCSV(String csvData) {
        try {
            String[] lines = csvData.split("\n");
            int imported = 0;
            
            // Skip header line
            for (int i = 1; i < lines.length; i++) {
                try {
                    String[] fields = lines[i].split(",");
                    if (fields.length >= 8) {
                        Produit produit = new Produit();
                        produit.setCodeBarre(fields[0].trim());
                        produit.setNomProduit(fields[1].trim());
                        produit.setCategorieProduit(CategorieProduit.valueOf(fields[2].trim()));
                        produit.setDescription(fields[3].trim().replace("\"", ""));
                        produit.setPrixUnitaire(new BigDecimal(fields[4].trim()));
                        produit.setQuantiteStock(Integer.parseInt(fields[5].trim()));
                        produit.setSeuilAlerte(Integer.parseInt(fields[6].trim()));
                        produit.setDateCreation(LocalDateTime.parse(fields[7].trim() + "T00:00:00"));
                        
                        createProduit(produit);
                        imported++;
                    }
                } catch (Exception e) {
                    logger.warn("Error importing product from line {}: {}", i + 1, e.getMessage());
                }
            }
            
            logger.info("Products imported from CSV: {} records", imported);
            return imported;
            
        } catch (Exception e) {
            logger.error("Error importing products from CSV", e);
            throw new RuntimeException("Error importing products from CSV", e);
        }
    }
    
    // Missing interface method implementations
    
    @Override
    public List<Produit> findProduitsByCategory(CategorieProduit categorie) {
        return findProduitsByCategorie(categorie);
    }
    
    @Override
    public List<Produit> getProductsRequiringRestock() {
        try {
            return produitRepository.findLowStockProducts();
        } catch (Exception e) {
            logger.error("Error getting products requiring restock", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean isProductNameAvailableForUpdate(String nom, Long excludeId) {
        try {
            List<Produit> existing = produitRepository.findByNom(nom);
            return existing.stream().noneMatch(p -> !p.getIdProduit().equals(excludeId));
        } catch (Exception e) {
            logger.error("Error checking product name availability for update", e);
            return false;
        }
    }
    
    @Override
    public boolean removeStock(Long produitId, int quantity) {
        return adjustStock(produitId, -quantity, "Stock removal");
    }
    
    @Override
    public BigDecimal getTotalStockValue() {
        return getTotalInventoryValue();
    }
    
    @Override
    public boolean isProductNameAvailable(String nom) {
        try {
            List<Produit> existing = produitRepository.findByNom(nom);
            return existing.isEmpty();
        } catch (Exception e) {
            logger.error("Error checking product name availability", e);
            return false;
        }
    }
    
    @Override
    public boolean updateStockThreshold(Long produitId, int threshold) {
        try {
            Optional<Produit> produitOpt = produitRepository.findById(produitId);
            if (produitOpt.isEmpty()) {
                return false;
            }
            
            Produit produit = produitOpt.get();
            produit.setSeuilAlerte(threshold);
            produit.setDateModification(LocalDateTime.now());
            produitRepository.update(produit);
            return true;
        } catch (Exception e) {
            logger.error("Error updating stock threshold", e);
            return false;
        }
    }
    
    @Override
    public long getProductCountByCategory(CategorieProduit categorie) {
        try {
            return produitRepository.countByCategorie(categorie);
        } catch (Exception e) {
            logger.error("Error getting product count by category", e);
            return 0;
        }
    }
    
    @Override
    public boolean hasSufficientStock(Long produitId, int quantity) {
        try {
            Optional<Produit> produitOpt = produitRepository.findById(produitId);
            if (produitOpt.isEmpty()) {
                return false;
            }
            return produitOpt.get().getQuantiteStock() >= quantity;
        } catch (Exception e) {
            logger.error("Error checking sufficient stock", e);
            return false;
        }
    }
    
    @Override
    public List<Produit> findOutOfStockProducts() {
        try {
            return produitRepository.findOutOfStock();
        } catch (Exception e) {
            logger.error("Error finding out of stock products", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public int calculateReorderQuantity(Long produitId, int targetStock) {
        try {
            Optional<Produit> produitOpt = produitRepository.findById(produitId);
            if (produitOpt.isEmpty()) {
                return 0;
            }
            
            Produit produit = produitOpt.get();
            int currentStock = produit.getQuantiteStock();
            return Math.max(0, targetStock - currentStock);
        } catch (Exception e) {
            logger.error("Error calculating reorder quantity", e);
            return 0;
        }
    }
    
    @Override
    public List<Produit> searchProduits(String nom, CategorieProduit categorie, 
                                       BigDecimal minPrice, BigDecimal maxPrice, Boolean active) {
        try {
            List<Produit> results = produitRepository.searchProducts(nom, categorie, minPrice, maxPrice);
            if (active != null) {
                return results.stream()
                    .filter(p -> active.equals(p.getActif()))
                    .collect(Collectors.toList());
            }
            return results;
        } catch (Exception e) {
            logger.error("Error searching products", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Object[]> getStockMovementHistory(Long produitId) {
        logger.warn("Stock movement history not implemented yet");
        return new ArrayList<>();
    }
    
    @Override
    public String exportProductsToCSV() {
        return exportProduitsToCSV();
    }

    @Override
    public List<Produit> findProduitsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return findProduitsInPriceRange(minPrice, maxPrice);
    }
    
    @Override
    public List<Object[]> getHighestRevenueProducts(int limit) {
        try {
            List<Produit> topProducts = produitRepository.findTopSellingProducts(limit);
            return topProducts.stream()
                .map(p -> new Object[]{p.getIdProduit(), p.getNomProduit(), p.getPrixUnitaire()})
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting highest revenue products", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public BigDecimal getStockValueByCategory(CategorieProduit categorie) {
        try {
            List<Produit> products = produitRepository.findByCategorie(categorie);
            return products.stream()
                .map(p -> p.getPrixUnitaire().multiply(new BigDecimal(p.getQuantiteStock())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            logger.error("Error getting stock value by category", e);
            return BigDecimal.ZERO;
        }
    }
    
    @Override
    public int importProductsFromCSV(String csvData) {
        return importProduitsFromCSV(csvData);
    }
    
    @Override
    public List<Produit> getLowStockAlerts() {
        try {
            return findLowStockProduits();
        } catch (Exception e) {
            logger.error("Error getting low stock alerts", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Object[]> getMostSoldProducts(int limit) {
        return getTopSellingProducts(limit);
    }
    
    @Override
    public long getProductCount() {
        return getProduitCount();
    }
    
    @Override
    public boolean canDeleteProduct(Long produitId) {
        return canDeleteProduit(produitId);
    }
    
    @Override
    public List<Produit> findLowStockProducts() {
        return findLowStockProduits();
    }
    
    @Override
    public boolean addStock(Long produitId, int quantity) {
        return adjustStock(produitId, quantity, "Stock addition");
    }
}
