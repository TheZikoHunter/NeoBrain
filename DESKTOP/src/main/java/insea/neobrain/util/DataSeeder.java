package insea.neobrain.util;

import insea.neobrain.entity.*;
import insea.neobrain.repository.*;
import insea.neobrain.repository.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Data Seeding Utility for creating sample test data
 * Provides comprehensive sample data generation for testing and development
 */
public class DataSeeder {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);
    private final Random random = new Random();
    
    // Repositories
    private final PersonnelRepository personnelRepository;
    private final ProduitRepository produitRepository;
    private final ClientRepository clientRepository;
    private final CommandeVenteRepository commandeRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final ReclamationRepository reclamationRepository;
    private final InventaireRepository inventaireRepository;
    private final TacheInventaireRepository tacheRepository;
    
    public DataSeeder() {
        // Initialize repositories
        this.personnelRepository = new PersonnelRepositoryImpl();
        this.produitRepository = new ProduitRepositoryImpl();
        this.clientRepository = new ClientRepositoryImpl();
        this.commandeRepository = new CommandeVenteRepositoryImpl();
        this.ligneCommandeRepository = new LigneCommandeRepositoryImpl();
        this.reclamationRepository = new ReclamationRepositoryImpl();
        this.inventaireRepository = new InventaireRepositoryImpl();
        this.tacheRepository = new TacheInventaireRepositoryImpl();
    }
    
    /**
     * Seed all sample data
     */
    public void seedAllData() {
        logger.info("Starting comprehensive data seeding...");
        
        try {
            // Check if data already exists
            if (hasExistingData()) {
                logger.info("Existing data detected. Skipping seeding to prevent duplicates.");
                return;
            }
            
            // Seed in dependency order
            List<Personnel> personnel = seedPersonnel();
            List<Produit> produits = seedProduits();
            List<Client> clients = seedClients();
            List<CommandeVente> commandes = seedCommandes(clients, produits);
            List<LigneCommande> lignesCommande = seedLignesCommande(commandes, produits);
            seedReclamations(lignesCommande);
            seedInventaires(produits, personnel);
            seedTachesInventaire(personnel, produits);
            
            logger.info("Data seeding completed successfully");
            
        } catch (Exception e) {
            logger.error("Error during data seeding", e);
            throw new RuntimeException("Failed to seed data", e);
        }
    }
    
    /**
     * Check if data already exists in the database
     */
    private boolean hasExistingData() {
        try {
            return personnelRepository.count() > 0 || 
                   produitRepository.count() > 0 || 
                   clientRepository.count() > 0;
        } catch (Exception e) {
            logger.warn("Could not check for existing data, proceeding with seeding", e);
            return false;
        }
    }
    
    /**
     * Seed sample personnel data
     */
    private List<Personnel> seedPersonnel() {
        logger.info("Seeding personnel data...");
        List<Personnel> personnelList = new ArrayList<>();
        
        // Admin user
        Personnel admin = new Personnel();
        admin.setNom("Admin");
        admin.setPrenom("System");
        admin.setEmail("admin@inventory.com");
        admin.setTelephone("0661234567");
        admin.setAdresse("123 Admin Street, Casablanca");
        admin.setDateEmbauche(LocalDate.now().minusYears(2));
        admin.setRole(Role.ADMIN);
        admin.setMotDePasse(PasswordUtil.hashPassword("admin123"));
        admin.setActif(true);
        personnelList.add(personnelRepository.save(admin));
        
        // Stock Manager
        Personnel stockManager = new Personnel();
        stockManager.setNom("Alami");
        stockManager.setPrenom("Youssef");
        stockManager.setEmail("y.alami@inventory.com");
        stockManager.setTelephone("0662345678");
        stockManager.setAdresse("456 Manager Avenue, Rabat");
        stockManager.setDateEmbauche(LocalDate.now().minusYears(1));
        stockManager.setRole(Role.RESPONSABLE_STOCK);
        stockManager.setMotDePasse(PasswordUtil.hashPassword("manager123"));
        stockManager.setActif(true);
        personnelList.add(personnelRepository.save(stockManager));
        
        // Stock Employees
        String[] employeeNames = {
            "Benali:Sara", "Idrissi:Ahmed", "Tazi:Fatima", "Hassani:Omar", "Kabbaj:Aicha"
        };
        
        for (int i = 0; i < employeeNames.length; i++) {
            String[] parts = employeeNames[i].split(":");
            Personnel employee = new Personnel();
            employee.setNom(parts[0]);
            employee.setPrenom(parts[1]);
            employee.setEmail(parts[1].toLowerCase() + "." + parts[0].toLowerCase() + "@inventory.com");
            employee.setTelephone("066" + String.format("%07d", 3000000 + i));
            employee.setAdresse((100 + i * 10) + " Employee Street, Casablanca");
            employee.setDateEmbauche(LocalDate.now().minusMonths(6 + i));
            employee.setRole(Role.EMPLOYE_STOCK);
            employee.setMotDePasse(PasswordUtil.hashPassword("employee123"));
            employee.setActif(true);
            personnelList.add(personnelRepository.save(employee));
        }
        
        logger.info("Created {} personnel records", personnelList.size());
        return personnelList;
    }
    
    /**
     * Seed sample product data
     */
    private List<Produit> seedProduits() {
        logger.info("Seeding product data...");
        List<Produit> produitList = new ArrayList<>();
        
        // Categories and products
        String[][] productsData = {
            {"Électronique", "Smartphone Samsung Galaxy", "999.99", "50", "Electronics - Mobile"},
            {"Électronique", "Laptop Dell Inspiron", "1299.99", "30", "Electronics - Computing"},
            {"Électronique", "Tablette iPad Air", "799.99", "25", "Electronics - Tablet"},
            {"Électronique", "Écouteurs Bluetooth", "99.99", "100", "Electronics - Audio"},
            {"Vêtements", "T-shirt Homme", "29.99", "200", "Clothing - Men"},
            {"Vêtements", "Jeans Femme", "79.99", "150", "Clothing - Women"},
            {"Vêtements", "Veste d'hiver", "149.99", "75", "Clothing - Outerwear"},
            {"Maison", "Aspirateur Robot", "399.99", "20", "Home - Cleaning"},
            {"Maison", "Cafetière Expresso", "199.99", "40", "Home - Kitchen"},
            {"Maison", "Lampe LED Connectée", "49.99", "80", "Home - Lighting"},
            {"Sports", "Ballon de Football", "24.99", "60", "Sports - Football"},
            {"Sports", "Raquette de Tennis", "89.99", "35", "Sports - Tennis"},
            {"Sports", "Vélo VTT", "599.99", "15", "Sports - Cycling"},
            {"Livres", "Roman Bestseller", "19.99", "120", "Books - Fiction"},
            {"Livres", "Manuel Technique", "49.99", "80", "Books - Technical"},
            {"Automobile", "Pneu Michelin", "129.99", "45", "Auto - Tires"},
            {"Automobile", "Huile Moteur 5L", "39.99", "90", "Auto - Maintenance"},
            {"Alimentaire", "Café Arabica 1kg", "15.99", "200", "Food - Beverages"},
            {"Alimentaire", "Chocolat Premium", "8.99", "150", "Food - Confectionery"},
            {"Jouets", "Puzzle 1000 pièces", "19.99", "70", "Toys - Puzzles"}
        };
        
        for (int i = 0; i < productsData.length; i++) {
            String[] data = productsData[i];
            Produit produit = new Produit();
            produit.setNom(data[1]);
            produit.setDescription(data[4]);
            // Map string categories to enum values
            switch (data[0]) {
                case "Informatique":
                    produit.setCategorie(CategorieProduit.INFORMATIQUE);
                    break;
                case "Electromenager":
                    produit.setCategorie(CategorieProduit.ELECTROMENAGER);
                    break;
                case "Vetements":
                    produit.setCategorie(CategorieProduit.VETEMENTS);
                    break;
                case "Sports":
                    produit.setCategorie(CategorieProduit.SPORTS);
                    break;
                default:
                    produit.setCategorie(CategorieProduit.INFORMATIQUE);
            }
            produit.setPrix(new BigDecimal(data[2]));
            produit.setQuantiteStock(Integer.parseInt(data[3]));
            produit.setStockMinimum(Math.max(5, Integer.parseInt(data[3]) / 10)); // 10% of stock as minimum
            produit.setDateCreation(LocalDateTime.now().minusDays(random.nextInt(180)));
            produit.setActif(true);
            
            // Add some random variation to stock levels
            if (random.nextBoolean()) {
                int variation = random.nextInt(20) - 10; // -10 to +10
                produit.setQuantiteStock(Math.max(0, produit.getQuantiteStock() + variation));
            }
            
            produitList.add(produitRepository.save(produit));
        }
        
        logger.info("Created {} product records", produitList.size());
        return produitList;
    }
    
    /**
     * Seed sample client data
     */
    private List<Client> seedClients() {
        logger.info("Seeding client data...");
        List<Client> clientList = new ArrayList<>();
        
        String[][] clientsData = {
            {"Société", "TechCorp Solutions", "tech@techcorp.ma", "0520123456", "Zone Industrielle, Casablanca"},
            {"Société", "Digital Innovations", "contact@digital-innovations.ma", "0520234567", "Technopark, Rabat"},
            {"Particulier", "Bennani:Mohammed", "m.bennani@gmail.com", "0661234567", "Hay Riad, Rabat"},
            {"Particulier", "Alaoui:Safaa", "s.alaoui@gmail.com", "0662345678", "Maarif, Casablanca"},
            {"Société", "Green Energy Co", "info@greenenergy.ma", "0520345678", "Agdal, Rabat"},
            {"Particulier", "Fassi:Karim", "k.fassi@yahoo.com", "0663456789", "Gueliz, Marrakech"},
            {"Société", "Atlas Trading", "sales@atlas-trading.ma", "0520456789", "Hay Hassani, Casablanca"},
            {"Particulier", "Mansouri:Leila", "l.mansouri@hotmail.com", "0664567890", "Agadir Centre"},
            {"Société", "Moroccan Textiles", "export@moroccan-textiles.ma", "0520567890", "Sidi Bernoussi, Casablanca"},
            {"Particulier", "Chakib:Youssef", "y.chakib@gmail.com", "0665678901", "Hay Mohammadi, Fes"}
        };
        
        for (String[] data : clientsData) {
            Client client = new Client();
            
            if ("Société".equals(data[0])) {
                client.setNom(data[1]);
                client.setPrenom(""); // Company doesn't have first name
                client.setTypeClient(TypeClient.ENTREPRISE);
            } else {
                String[] names = data[1].split(":");
                client.setNom(names[0]);
                client.setPrenom(names[1]);
                client.setTypeClient(TypeClient.PARTICULIER);
            }
            
            client.setEmail(data[2]);
            client.setTelephone(data[3]);
            client.setAdresse(data[4]);
            client.setDateCreation(LocalDate.now().minusDays(random.nextInt(365)));
            client.setActif(true);
            
            clientList.add(clientRepository.save(client));
        }
        
        logger.info("Created {} client records", clientList.size());
        return clientList;
    }
    
    /**
     * Seed sample order data
     */
    private List<CommandeVente> seedCommandes(List<Client> clients, List<Produit> produits) {
        logger.info("Seeding order data...");
        List<CommandeVente> commandeList = new ArrayList<>();
        
        for (int i = 0; i < 25; i++) {
            CommandeVente commande = new CommandeVente();
            commande.setNumeroCommande("CMD-" + String.format("%06d", 1000 + i));
            commande.setClient(clients.get(random.nextInt(clients.size())));
            commande.setDateCommandeVente(LocalDate.now().minusDays(random.nextInt(90)));
            
            // Random status distribution (using string status)
            String[] etats = {"BROUILLON", "EN_ATTENTE", "VALIDEE", "EXPEDIEE", "LIVREE", "ECHEC", "ANNULEE"};
            commande.setStatut(etats[random.nextInt(etats.length)]);
            
            if ("LIVREE".equals(commande.getStatut())) {
                commande.setDateLivraisonEffective(commande.getDateCommandeVente().plusDays(random.nextInt(7) + 1));
            }
            
            // Calculate total (will be updated when order lines are created)
            commande.setPrixTotal(BigDecimal.ZERO);
            
            commandeList.add(commandeRepository.save(commande));
        }
        
        logger.info("Created {} order records", commandeList.size());
        return commandeList;
    }
    
    /**
     * Seed sample order line data
     */
    private List<LigneCommande> seedLignesCommande(List<CommandeVente> commandes, List<Produit> produits) {
        logger.info("Seeding order line data...");
        List<LigneCommande> ligneList = new ArrayList<>();
        
        for (CommandeVente commande : commandes) {
            // Each order has 1-5 product lines
            int numberOfLines = random.nextInt(5) + 1;
            BigDecimal commandeTotal = BigDecimal.ZERO;
            
            for (int i = 0; i < numberOfLines; i++) {
                LigneCommande ligne = new LigneCommande();
                ligne.setCommandeVente(commande);
                ligne.setProduit(produits.get(random.nextInt(produits.size())));
                ligne.setQuantiteVente(random.nextInt(10) + 1);
                ligne.setPrixUnitaire(ligne.getProduit().getPrix());
                
                BigDecimal montantLigne = ligne.getPrixUnitaire().multiply(BigDecimal.valueOf(ligne.getQuantiteVente()));
                ligne.setSousTotal(montantLigne);
                commandeTotal = commandeTotal.add(montantLigne);
                
                ligneList.add(ligneCommandeRepository.save(ligne));
            }
            
            // Update order total
            commande.setPrixTotal(commandeTotal);
            commandeRepository.update(commande);
        }
        
        logger.info("Created {} order line records", ligneList.size());
        return ligneList;
    }
    
    /**
     * Seed sample reclamation data
     */
    private void seedReclamations(List<LigneCommande> lignesCommande) {
        logger.info("Seeding reclamation data...");
        
        // Create complaints for about 10% of order lines
        int numberOfReclamations = Math.max(5, lignesCommande.size() / 10);
        
        for (int i = 0; i < numberOfReclamations; i++) {
            LigneCommande ligne = lignesCommande.get(random.nextInt(lignesCommande.size()));
            
            Reclamation reclamation = new Reclamation();
            reclamation.setNumeroReclamation("REC-" + String.format("%06d", 1000 + i));
            reclamation.setLigneCommande(ligne);
            
            // Random complaint types
            TypeReclamation[] types = TypeReclamation.values();
            reclamation.setTypeReclamation(types[random.nextInt(types.length)]);
            
            String[] motifs = {
                "Produit défectueux à la réception",
                "Produit ne correspond pas à la description",
                "Livraison en retard",
                "Emballage endommagé",
                "Quantité incorrecte",
                "Qualité inférieure aux attentes",
                "Problème de fonctionnement",
                "Couleur/taille incorrecte"
            };
            reclamation.setMotif(motifs[random.nextInt(motifs.length)]);
            
            reclamation.setDateReclamation(ligne.getCommandeVente().getDateCommandeVente().atStartOfDay().plusDays(random.nextInt(30) + 1));
            
            // Random status distribution
            EtatReclamation[] etats = EtatReclamation.values();
            reclamation.setEtatReclamation(etats[random.nextInt(etats.length)]);
            
            if (reclamation.getEtatReclamation() == EtatReclamation.VALIDEE || 
                reclamation.getEtatReclamation() == EtatReclamation.REFUSEE) {
                reclamation.setDateTraitement(reclamation.getDateReclamation().plusDays(random.nextInt(7) + 1));
                reclamation.setCommentaireInterne("Traitement automatique - données de test");
            }
            
            reclamationRepository.save(reclamation);
        }
        
        logger.info("Created {} reclamation records", numberOfReclamations);
    }
    
    /**
     * Seed sample inventory data
     */
    private void seedInventaires(List<Produit> produits, List<Personnel> personnel) {
        logger.info("Seeding inventory data...");
        
        // Create inventory sessions for the last 3 months
        for (int i = 0; i < 10; i++) {
            Inventaire inventaire = new Inventaire();
            inventaire.setDateDebut(LocalDate.now().minusDays(random.nextInt(90)));
            inventaire.setDescription("Inventaire " + (i % 2 == 0 ? "complet" : "partiel") + " - Session " + (i + 1));
            
            Personnel responsable = personnel.get(random.nextInt(personnel.size()));
            inventaire.setResponsable(responsable.getNomComplet());
            
            // Random inventory state
            String[] etats = {"EN_COURS", "TERMINE", "VALIDE", "ANNULE"};
            inventaire.setEtatInventaire(etats[random.nextInt(etats.length)]);
            
            if (!"EN_COURS".equals(inventaire.getEtatInventaire())) {
                inventaire.setDateFin(inventaire.getDateDebut().plusDays(random.nextInt(3) + 1));
            }
            
            // Random statistics
            inventaire.setNombreProduitsTotal(random.nextInt(produits.size()) + 1);
            inventaire.setNombreProduitsComptes(Math.min(inventaire.getNombreProduitsTotal(), 
                inventaire.getNombreProduitsTotal() - random.nextInt(5)));
            inventaire.setEcartsDetectes(random.nextInt(10));
            
            inventaireRepository.save(inventaire);
        }
        
        logger.info("Created 10 inventory session records");
    }
    
    /**
     * Seed sample inventory task data
     */
    private void seedTachesInventaire(List<Personnel> personnel, List<Produit> produits) {
        logger.info("Seeding inventory task data...");
        
        // Create tasks for stock employees
        List<Personnel> stockEmployees = personnel.stream()
            .filter(p -> p.getRole() == Role.EMPLOYE_STOCK)
            .toList();
        
        if (stockEmployees.isEmpty()) {
            logger.warn("No stock employees found for task creation");
            return;
        }
        
        for (int i = 0; i < 20; i++) {
            TacheInventaire tache = new TacheInventaire();
            tache.setCommentaire("Vérification stock produit - Task " + (i + 1));
            tache.setProduit(produits.get(random.nextInt(produits.size())));
            tache.setPersonnel(stockEmployees.get(random.nextInt(stockEmployees.size())));
            tache.setDateTache(LocalDateTime.now().minusDays(random.nextInt(30)));
            
            // Random status distribution
            EtatTache[] etats = EtatTache.values();
            tache.setEtatTache(etats[random.nextInt(etats.length)]);
            
            if (tache.getEtatTache() == EtatTache.TERMINEE) {
                tache.setDateFin(tache.getDateTache().plusDays(random.nextInt(5) + 1));
                tache.setCommentaire("Tâche terminée avec succès - données de test");
            }
            
            // Set priority (TacheInventaire uses Integer for priority, not enum)
            tache.setPriorite(random.nextInt(5) + 1); // Priority 1-5
            
            tacheRepository.save(tache);
        }
        
        logger.info("Created 20 inventory task records");
    }
    
    /**
     * Clear all data (for testing purposes)
     */
    public void clearAllData() {
        logger.warn("Clearing all data from database...");
        
        try {
            // Delete in reverse dependency order
            tacheRepository.deleteAll();
            reclamationRepository.deleteAll();
            inventaireRepository.deleteAll();
            ligneCommandeRepository.deleteAll();
            commandeRepository.deleteAll();
            clientRepository.deleteAll();
            produitRepository.deleteAll();
            personnelRepository.deleteAll();
            
            logger.info("All data cleared successfully");
            
        } catch (Exception e) {
            logger.error("Error clearing data", e);
            throw new RuntimeException("Failed to clear data", e);
        }
    }
    
    /**
     * Generate summary report of seeded data
     */
    public void generateSeedingSummary() {
        logger.info("=== Data Seeding Summary ===");
        
        try {
            logger.info("Personnel: {} records", personnelRepository.count());
            logger.info("Products: {} records", produitRepository.count());
            logger.info("Clients: {} records", clientRepository.count());
            logger.info("Orders: {} records", commandeRepository.count());
            logger.info("Order Lines: {} records", ligneCommandeRepository.count());
            logger.info("Reclamations: {} records", reclamationRepository.count());
            logger.info("Inventories: {} records", inventaireRepository.count());
            logger.info("Tasks: {} records", tacheRepository.count());
            
        } catch (Exception e) {
            logger.error("Error generating summary", e);
        }
        
        logger.info("============================");
    }
    
    /**
     * Main method for standalone execution
     */
    public static void main(String[] args) {
        DataSeeder seeder = new DataSeeder();
        
        try {
            if (args.length > 0 && "clear".equals(args[0])) {
                seeder.clearAllData();
            } else {
                seeder.seedAllData();
                seeder.generateSeedingSummary();
            }
        } catch (Exception e) {
            System.err.println("Data seeding failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
