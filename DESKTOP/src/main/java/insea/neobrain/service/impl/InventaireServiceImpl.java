package insea.neobrain.service.impl;

import insea.neobrain.entity.Inventaire;
import insea.neobrain.entity.Produit;
import insea.neobrain.entity.Personnel;
import insea.neobrain.repository.InventaireRepository;
import insea.neobrain.repository.ProduitRepository;
import insea.neobrain.service.InventaireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Simple implementation of InventaireService
 * This provides basic functionality to satisfy the interface requirements
 */
public class InventaireServiceImpl implements InventaireService {
    
    private static final Logger logger = LoggerFactory.getLogger(InventaireServiceImpl.class);
    
    private final InventaireRepository inventaireRepository;
    private final ProduitRepository produitRepository;
    
    public InventaireServiceImpl(InventaireRepository inventaireRepository, 
                                ProduitRepository produitRepository) {
        this.inventaireRepository = inventaireRepository;
        this.produitRepository = produitRepository;
    }
    
    @Override
    public Inventaire createInventaire(Inventaire inventaire) {
        try {
            logger.debug("Creating new inventory record");
            return inventaireRepository.save(inventaire);
        } catch (Exception e) {
            logger.error("Error creating inventory record", e);
            throw new RuntimeException("Error creating inventory record", e);
        }
    }
    
    @Override
    public Inventaire createInventaire(Produit produit, int quantitePhysique, int quantiteSysteme,
                                     Personnel personnel, String observations) {
        try {
            logger.debug("Creating inventory record for product: {}", produit.getNom());
            
            // Create inventory session for this product
            Inventaire inventaire = new Inventaire();
            inventaire.setDateDebut(LocalDate.now());
            inventaire.setDescription("Inventory for product: " + produit.getNom());
            inventaire.setResponsable(personnel.getNomComplet());
            inventaire.setNombreProduitsTotal(1);
            inventaire.setNombreProduitsComptes(1);
            
            // Calculate variance
            int variance = Math.abs(quantitePhysique - quantiteSysteme);
            inventaire.setEcartsDetectes(variance > 0 ? 1 : 0);
            
            Inventaire saved = inventaireRepository.save(inventaire);
            logger.info("Inventory record created for product: {}", produit.getNom());
            
            return saved;
            
        } catch (Exception e) {
            logger.error("Error creating inventory record for product: {}", produit.getNom(), e);
            throw new RuntimeException("Error creating inventory record", e);
        }
    }
    
    @Override
    public Inventaire updateInventaire(Inventaire inventaire) {
        try {
            logger.debug("Updating inventory record ID: {}", inventaire.getIdInventaire());
            return inventaireRepository.update(inventaire);
        } catch (Exception e) {
            logger.error("Error updating inventory record: {}", inventaire.getIdInventaire(), e);
            throw new RuntimeException("Error updating inventory record", e);
        }
    }
    
    @Override
    public boolean deleteInventaire(Long id) {
        try {
            logger.debug("Deleting inventory record with ID: {}", id);
            inventaireRepository.deleteById(id);
            logger.info("Inventory record deleted successfully: ID {}", id);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting inventory record: ID {}", id, e);
            return false;
        }
    }
    
    @Override
    public Optional<Inventaire> findInventaireById(Long id) {
        try {
            return inventaireRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error finding inventory by ID: {}", id, e);
            return Optional.empty();
        }
    }
    
    @Override
    public List<Inventaire> findAllInventaires() {
        try {
            return inventaireRepository.findAll();
        } catch (Exception e) {
            logger.error("Error finding all inventories", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Inventaire> searchInventairesByDescription(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                logger.debug("Empty search term, returning all inventories");
                return findAllInventaires();
            }
            logger.debug("Searching inventories by description: {}", searchTerm);
            return inventaireRepository.findByDescriptionContaining(searchTerm.trim());
        } catch (Exception e) {
            logger.error("Error searching inventories by description: {}", searchTerm, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Inventaire> findInventairesByProduit(Produit produit) {
        try {
            // Since we're using session-based architecture, return relevant sessions
            return inventaireRepository.findByDateRange(
                LocalDate.now().minusMonths(1), LocalDate.now());
        } catch (Exception e) {
            logger.error("Error finding inventories by product: {}", produit.getNom(), e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Inventaire> findInventairesByPersonnel(Personnel personnel) {
        try {
            return inventaireRepository.findByResponsable(personnel.getNomComplet());
        } catch (Exception e) {
            logger.error("Error finding inventories by personnel: {}", personnel.getNomComplet(), e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Inventaire> findInventairesByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return inventaireRepository.findByDateRange(startDate, endDate);
        } catch (Exception e) {
            logger.error("Error finding inventories by date range: {} - {}", startDate, endDate, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Inventaire> findInventairesWithDiscrepancies() {
        try {
            List<Inventaire> allInventaires = inventaireRepository.findAll();
            return allInventaires.stream()
                .filter(inv -> inv.getEcartsDetectes() != null && inv.getEcartsDetectes() > 0)
                .toList();
        } catch (Exception e) {
            logger.error("Error finding inventories with discrepancies", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Inventaire> findInventairesWithSignificantDiscrepancies(int threshold) {
        try {
            List<Inventaire> allInventaires = inventaireRepository.findAll();
            return allInventaires.stream()
                .filter(inv -> inv.getEcartsDetectes() != null && inv.getEcartsDetectes() >= threshold)
                .toList();
        } catch (Exception e) {
            logger.error("Error finding inventories with significant discrepancies", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public int startFullInventory(Personnel personnel) {
        try {
            logger.info("Starting full inventory by personnel: {}", personnel.getNomComplet());
            
            List<Produit> allProducts = produitRepository.findAll();
            return startPartialInventory(allProducts, personnel);
            
        } catch (Exception e) {
            logger.error("Error starting full inventory", e);
            throw new RuntimeException("Error starting full inventory", e);
        }
    }
    
    @Override
    public int startPartialInventory(List<Produit> produits, Personnel personnel) {
        try {
            logger.info("Starting partial inventory for {} products by personnel: {}", 
                produits.size(), personnel.getNomComplet());
            
            // Create inventory session
            Inventaire session = new Inventaire();
            session.setDateDebut(LocalDate.now());
            session.setDescription("Inventory session for " + produits.size() + " products");
            session.setResponsable(personnel.getNomComplet());
            session.setNombreProduitsTotal(produits.size());
            session.setNombreProduitsComptes(0);
            session.setEcartsDetectes(0);
            
            inventaireRepository.save(session);
            
            logger.info("Partial inventory started: 1 session created for {} products", produits.size());
            return 1; // Return number of sessions created
            
        } catch (Exception e) {
            logger.error("Error starting partial inventory", e);
            throw new RuntimeException("Error starting partial inventory", e);
        }
    }
    
    @Override
    public int completeInventory(List<Long> inventaireIds, boolean updateStock) {
        try {
            logger.info("Completing inventory for {} records, update stock: {}", 
                inventaireIds.size(), updateStock);
            
            int processed = 0;
            
            for (Long inventaireId : inventaireIds) {
                try {
                    Optional<Inventaire> inventaireOpt = inventaireRepository.findById(inventaireId);
                    if (inventaireOpt.isEmpty()) {
                        logger.warn("Inventory record not found: ID {}", inventaireId);
                        continue;
                    }
                    
                    Inventaire inventaire = inventaireOpt.get();
                    
                    // Mark inventory as completed
                    inventaire.setEstClos(true);
                    inventaire.setDateFin(LocalDate.now());
                    inventaire.setEtatInventaire("CLOS");
                    
                    inventaireRepository.update(inventaire);
                    processed++;
                    
                } catch (Exception e) {
                    logger.warn("Error completing inventory record ID {}: {}", inventaireId, e.getMessage());
                }
            }
            
            logger.info("Inventory completion processed: {} records", processed);
            return processed;
            
        } catch (Exception e) {
            logger.error("Error completing inventory", e);
            throw new RuntimeException("Error completing inventory", e);
        }
    }
    
    @Override
    public BigDecimal calculateTotalVariance() {
        try {
            // Placeholder implementation
            return BigDecimal.ZERO;
        } catch (Exception e) {
            logger.error("Error calculating total variance", e);
            return BigDecimal.ZERO;
        }
    }
    
    @Override
    public double calculateInventoryAccuracy() {
        try {
            List<Inventaire> allInventaires = inventaireRepository.findAll();
            if (allInventaires.isEmpty()) {
                return 100.0;
            }
            
            long accurateCount = allInventaires.stream()
                .filter(inv -> inv.getEcartsDetectes() == null || inv.getEcartsDetectes() == 0)
                .count();
            
            return (double) accurateCount / allInventaires.size() * 100.0;
            
        } catch (Exception e) {
            logger.error("Error calculating inventory accuracy", e);
            return 0.0;
        }
    }
    
    @Override
    public List<Object[]> getInventoryStatisticsByCategory() {
        try {
            // Placeholder implementation
            return new ArrayList<>();
        } catch (Exception e) {
            logger.error("Error getting inventory statistics by category", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Inventaire> getRecentInventoryActivities(int days) {
        try {
            LocalDate cutoffDate = LocalDate.now().minusDays(days);
            return findInventairesByDateRange(cutoffDate, LocalDate.now());
        } catch (Exception e) {
            logger.error("Error getting recent inventory activities", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<String> validateInventaire(Inventaire inventaire) {
        List<String> errors = new ArrayList<>();
        
        if (inventaire.getDateDebut() == null) {
            errors.add("Inventory start date is required");
        } else if (inventaire.getDateDebut().isAfter(LocalDate.now())) {
            errors.add("Inventory start date cannot be in the future");
        }
        
        if (inventaire.getResponsable() == null || inventaire.getResponsable().trim().isEmpty()) {
            errors.add("Responsible person is required");
        }
        
        if (inventaire.getDescription() != null && inventaire.getDescription().length() > 500) {
            errors.add("Description must not exceed 500 characters");
        }
        
        return errors;
    }
    
    @Override
    public boolean canDeleteInventaire(Long inventaireId) {
        try {
            Optional<Inventaire> inventaireOpt = inventaireRepository.findById(inventaireId);
            if (inventaireOpt.isEmpty()) {
                return false;
            }
            
            Inventaire inventaire = inventaireOpt.get();
            // Allow deletion if not closed
            return !inventaire.isClos();
            
        } catch (Exception e) {
            logger.error("Error checking if inventory can be deleted: {}", inventaireId, e);
            return false;
        }
    }
    
    @Override
    public long getInventaireCount() {
        try {
            return inventaireRepository.count();
        } catch (Exception e) {
            logger.error("Error getting inventory count", e);
            return 0;
        }
    }
    
    @Override
    public String exportInventairesToCSV() {
        try {
            List<Inventaire> inventaires = inventaireRepository.findAll();
            StringBuilder csv = new StringBuilder();
            
            // Header
            csv.append("ID,Numero,Date Debut,Date Fin,Etat,Responsable,Produits Total,Produits Comptes,Ecarts\n");
            
            // Data
            for (Inventaire inv : inventaires) {
                csv.append(String.format("%d,%s,%s,%s,%s,%s,%d,%d,%d\n",
                    inv.getIdInventaire(),
                    inv.getNumeroInventaire(),
                    inv.getDateDebut(),
                    inv.getDateFin() != null ? inv.getDateFin() : "",
                    inv.getEtatInventaire(),
                    inv.getResponsable(),
                    inv.getNombreProduitsTotal() != null ? inv.getNombreProduitsTotal() : 0,
                    inv.getNombreProduitsComptes() != null ? inv.getNombreProduitsComptes() : 0,
                    inv.getEcartsDetectes() != null ? inv.getEcartsDetectes() : 0
                ));
            }
            
            logger.info("Inventory data exported to CSV: {} records", inventaires.size());
            return csv.toString();
            
        } catch (Exception e) {
            logger.error("Error exporting inventories to CSV", e);
            throw new RuntimeException("Error exporting inventories to CSV", e);
        }
    }
    
    @Override
    public int importInventairesFromCSV(String csvData) {
        try {
            String[] lines = csvData.split("\n");
            int imported = 0;
            
            // Skip header line
            for (int i = 1; i < lines.length; i++) {
                try {
                    String[] fields = lines[i].split(",");
                    if (fields.length >= 6) {
                        Inventaire inventaire = new Inventaire();
                        inventaire.setDateDebut(LocalDate.parse(fields[2].trim()));
                        inventaire.setEtatInventaire(fields[4].trim());
                        inventaire.setResponsable(fields[5].trim());
                        
                        if (fields.length > 6 && !fields[6].trim().isEmpty()) {
                            inventaire.setNombreProduitsTotal(Integer.parseInt(fields[6].trim()));
                        }
                        if (fields.length > 7 && !fields[7].trim().isEmpty()) {
                            inventaire.setNombreProduitsComptes(Integer.parseInt(fields[7].trim()));
                        }
                        if (fields.length > 8 && !fields[8].trim().isEmpty()) {
                            inventaire.setEcartsDetectes(Integer.parseInt(fields[8].trim()));
                        }
                        
                        createInventaire(inventaire);
                        imported++;
                    }
                } catch (Exception e) {
                    logger.warn("Error importing inventory from line {}: {}", i + 1, e.getMessage());
                }
            }
            
            logger.info("Inventories imported from CSV: {} records", imported);
            return imported;
            
        } catch (Exception e) {
            logger.error("Error importing inventories from CSV", e);
            throw new RuntimeException("Error importing inventories from CSV", e);
        }
    }
}
