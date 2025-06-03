package insea.neobrain.service.impl;

import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.Role;
import insea.neobrain.entity.Civilite;
import insea.neobrain.entity.Nationalite;
import insea.neobrain.repository.PersonnelRepository;
import insea.neobrain.service.PersonnelService;
import insea.neobrain.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementation of PersonnelService
 * Provides personnel management functionality with business logic
 */
public class PersonnelServiceImpl implements PersonnelService {
    
    private static final Logger logger = LoggerFactory.getLogger(PersonnelServiceImpl.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9\\s\\-()]{8,15}$");
    
    private final PersonnelRepository personnelRepository;
    private final AuthenticationService authenticationService;
    
    public PersonnelServiceImpl(PersonnelRepository personnelRepository, 
                               AuthenticationService authenticationService) {
        this.personnelRepository = personnelRepository;
        this.authenticationService = authenticationService;
    }
    
    @Override
    public Personnel createPersonnel(Personnel personnel) {
        try {
            logger.debug("Creating new personnel: {}", personnel.getNumeroPersonnel());
            // Validate personnel data
            List<String> validationErrors = validatePersonnel(personnel);
            if (!validationErrors.isEmpty()) {
                throw new IllegalArgumentException(String.join(", ", validationErrors));
            }
            // Hash password if not already hashed
            if (personnel.getMotDePasse() != null && !personnel.getMotDePasse().startsWith("$2a$")) {
                String hashedPassword = authenticationService.hashPassword(personnel.getMotDePasse());
                personnel.setMotDePasse(hashedPassword);
            }
            Personnel created = personnelRepository.save(personnel);
            logger.info("Personnel created successfully: {}", created.getNumeroPersonnel());
            return created;
        } catch (Exception e) {
            logger.error("Error creating personnel: {}", personnel.getNumeroPersonnel(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    @Override
    public Personnel createPersonnel(String numeroPersonnel, String nom, String prenom,
                                   Civilite civilite, Nationalite nationalite, Role role,
                                   String telephone, String email, String adresse,
                                   LocalDate dateNaissance) {
        Personnel personnel = new Personnel();
        personnel.setNumeroPersonnel(numeroPersonnel);
        personnel.setNom(nom);
        personnel.setPrenom(prenom);
        personnel.setCivilite(civilite);
        personnel.setNationalite(nationalite);
        personnel.setRole(role);
        personnel.setTelephone(telephone);
        personnel.setEmail(email);
        personnel.setAdresse(adresse);
        personnel.setDateNaissance(dateNaissance);
        
        // Generate default password
        String defaultPassword = authenticationService.generateDefaultPassword();
        personnel.setMotDePasse(defaultPassword);
        
        return createPersonnel(personnel);
    }
    
    @Override
    public Personnel updatePersonnel(Personnel personnel) {
        try {
            logger.debug("Updating personnel: {}", personnel.getNumeroPersonnel());
            
            // Validate personnel data
            List<String> validationErrors = validatePersonnel(personnel);
            if (!validationErrors.isEmpty()) {
                throw new IllegalArgumentException("Validation errors: " + String.join(", ", validationErrors));
            }
            
            Personnel updated = personnelRepository.update(personnel);
            logger.info("Personnel updated successfully: {}", updated.getNumeroPersonnel());
            return updated;
            
        } catch (Exception e) {
            logger.error("Error updating personnel: {}", personnel.getNumeroPersonnel(), e);
            throw new RuntimeException("Error updating personnel", e);
        }
    }
    
    @Override
    public boolean deletePersonnel(Long id) {
        try {
            logger.debug("Deleting personnel with ID: {}", id);
            
            // Check if personnel can be deleted
            if (!canDeletePersonnel(id)) {
                logger.warn("Personnel deletion denied: Has associated data. ID: {}", id);
                return false;
            }
            
            personnelRepository.deleteById(id);
            logger.info("Personnel deleted successfully: ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("Error deleting personnel: ID {}", id, e);
            return false;
        }
    }
    
    @Override
    public Optional<Personnel> findPersonnelById(Long id) {
        try {
            return personnelRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error finding personnel by ID: {}", id, e);
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Personnel> findPersonnelByNumber(String numeroPersonnel) {
        try {
            return personnelRepository.findByNumeroPersonnel(numeroPersonnel);
        } catch (Exception e) {
            logger.error("Error finding personnel by number: {}", numeroPersonnel, e);
            return Optional.empty();
        }
    }
    
    @Override
    public List<Personnel> findAllPersonnel() {
        try {
            return personnelRepository.findAll();
        } catch (Exception e) {
            logger.error("Error finding all personnel", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Personnel> findPersonnelByRole(Role role) {
        try {
            return personnelRepository.findByRole(role);
        } catch (Exception e) {
            logger.error("Error finding personnel by role: {}", role, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Personnel> findPersonnelByName(String nom, String prenom) {
        try {
            return personnelRepository.findByNomAndPrenom(nom, prenom);
        } catch (Exception e) {
            logger.error("Error finding personnel by name: {} {}", nom, prenom, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Personnel> findInventoryCapablePersonnel() {
        try {
            return personnelRepository.findInventoryCapablePersonnel();
        } catch (Exception e) {
            logger.error("Error finding inventory capable personnel", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Personnel> findActivePersonnel() {
        try {
            List<Personnel> allPersonnel = personnelRepository.findAll();
            return allPersonnel.stream()
                .filter(p -> !authenticationService.isAccountLocked(p))
                .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error finding active personnel", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Object[]> getPersonnelStatisticsByRole() {
        try {
            java.util.Map<Role, Long> countByRole = personnelRepository.getPersonnelCountByRole();
            List<Object[]> statistics = new ArrayList<>();
            
            for (java.util.Map.Entry<Role, Long> entry : countByRole.entrySet()) {
                statistics.add(new Object[]{entry.getKey(), entry.getValue()});
            }
            
            return statistics;
        } catch (Exception e) {
            logger.error("Error getting personnel statistics by role", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Personnel> searchPersonnel(String nom, String prenom, Role role, 
                                          String email, String telephone) {
        try {
            return personnelRepository.searchPersonnel(nom, prenom, role, email, telephone);
        } catch (Exception e) {
            logger.error("Error searching personnel with criteria", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<String> validatePersonnel(Personnel personnel) {
        List<String> errors = new ArrayList<>();
        
        // Required fields validation
        if (personnel.getNumeroPersonnel() == null || personnel.getNumeroPersonnel().trim().isEmpty()) {
            errors.add("Personnel number is required");
        } else if (personnel.getNumeroPersonnel().length() < 3 || personnel.getNumeroPersonnel().length() > 20) {
            errors.add("Personnel number must be between 3 and 20 characters");
        }
        
        if (personnel.getNom() == null || personnel.getNom().trim().isEmpty()) {
            errors.add("Last name is required");
        } else if (personnel.getNom().length() > 50) {
            errors.add("Last name must not exceed 50 characters");
        }
        
        if (personnel.getPrenom() == null || personnel.getPrenom().trim().isEmpty()) {
            errors.add("First name is required");
        } else if (personnel.getPrenom().length() > 50) {
            errors.add("First name must not exceed 50 characters");
        }
        
        if (personnel.getCivilite() == null) {
            errors.add("Civility is required");
        }
        
        if (personnel.getNationalite() == null) {
            errors.add("Nationality is required");
        }
        
        if (personnel.getRole() == null) {
            errors.add("Role is required");
        }
        
        if (personnel.getDateNaissance() == null) {
            errors.add("Birth date is required");
        } else {
            // Check minimum age (18 years)
            int age = Period.between(personnel.getDateNaissance(), LocalDate.now()).getYears();
            if (age < 18) {
                errors.add("Personnel must be at least 18 years old");
            }
            if (age > 70) {
                errors.add("Personnel must be under 70 years old");
            }
        }
        
        // Email validation
        if (personnel.getEmail() != null && !personnel.getEmail().trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(personnel.getEmail()).matches()) {
                errors.add("Invalid email format");
            } else if (personnel.getEmail().length() > 100) {
                errors.add("Email must not exceed 100 characters");
            }
        }
        
        // Phone validation
        if (personnel.getTelephone() != null && !personnel.getTelephone().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(personnel.getTelephone()).matches()) {
                errors.add("Invalid phone number format");
            }
        }
        
        // Address validation
        if (personnel.getAdresse() != null && personnel.getAdresse().length() > 200) {
            errors.add("Address must not exceed 200 characters");
        }
        
        // Personnel number uniqueness (only for new personnel or when number changed)
        if (personnel.getId() == null) {
            // New personnel
            if (!isPersonnelNumberAvailable(personnel.getNumeroPersonnel())) {
                errors.add("Personnel number is already taken");
            }
        } else {
            // Existing personnel
            if (!isPersonnelNumberAvailableForUpdate(personnel.getNumeroPersonnel(), personnel.getId())) {
                errors.add("Personnel number is already taken by another personnel");
            }
        }
        
        return errors;
    }
    
    @Override
    public boolean isPersonnelNumberAvailable(String numeroPersonnel) {
        return authenticationService.isPersonnelNumberAvailable(numeroPersonnel);
    }
    
    @Override
    public boolean isPersonnelNumberAvailableForUpdate(String numeroPersonnel, Long currentPersonnelId) {
        try {
            Optional<Personnel> existing = personnelRepository.findByNumeroPersonnel(numeroPersonnel);
            return existing.isEmpty() || existing.get().getId().equals(currentPersonnelId);
        } catch (Exception e) {
            logger.error("Error checking personnel number availability for update: {}", numeroPersonnel, e);
            return false;
        }
    }
    
    @Override
    public String generatePersonnelNumber(String rolePrefix) {
        try {
            // Get count of personnel with this role prefix
            long count = personnelRepository.countByPersonnelNumberPrefix(rolePrefix);
            return String.format("%s%04d", rolePrefix, count + 1);
        } catch (Exception e) {
            logger.error("Error generating personnel number with prefix: {}", rolePrefix, e);
            return rolePrefix + "0001";
        }
    }
    
    @Override
    public long getPersonnelCount() {
        try {
            return personnelRepository.count();
        } catch (Exception e) {
            logger.error("Error getting personnel count", e);
            return 0;
        }
    }
    
    @Override
    public long getPersonnelCountByRole(Role role) {
        try {
            return personnelRepository.countByRole(role);
        } catch (Exception e) {
            logger.error("Error getting personnel count by role: {}", role, e);
            return 0;
        }
    }
    
    @Override
    public boolean activatePersonnel(Long personnelId) {
        try {
            Optional<Personnel> personnelOpt = personnelRepository.findById(personnelId);
            if (personnelOpt.isEmpty()) {
                return false;
            }
            
            Personnel personnel = personnelOpt.get();
            return authenticationService.unlockAccount(personnel);
        } catch (Exception e) {
            logger.error("Error activating personnel: {}", personnelId, e);
            return false;
        }
    }
    
    @Override
    public boolean deactivatePersonnel(Long personnelId) {
        try {
            Optional<Personnel> personnelOpt = personnelRepository.findById(personnelId);
            if (personnelOpt.isEmpty()) {
                return false;
            }
            
            Personnel personnel = personnelOpt.get();
            return authenticationService.lockAccount(personnel);
        } catch (Exception e) {
            logger.error("Error deactivating personnel: {}", personnelId, e);
            return false;
        }
    }
    
    @Override
    public boolean canDeletePersonnel(Long personnelId) {
        try {
            // Check if personnel has associated tasks, orders, etc.
            return personnelRepository.canDeletePersonnel(personnelId);
        } catch (Exception e) {
            logger.error("Error checking if personnel can be deleted: {}", personnelId, e);
            return false;
        }
    }
    
    @Override
    public List<Personnel> findPersonnelByAgeRange(int minAge, int maxAge) {
        try {
            LocalDate maxBirthDate = LocalDate.now().minusYears(minAge);
            LocalDate minBirthDate = LocalDate.now().minusYears(maxAge + 1);
            return personnelRepository.findByDateNaissanceBetween(minBirthDate, maxBirthDate);
        } catch (Exception e) {
            logger.error("Error finding personnel by age range: {} - {}", minAge, maxAge, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public String exportPersonnelToCSV() {
        try {
            List<Personnel> personnel = personnelRepository.findAll();
            StringBuilder csv = new StringBuilder();
            
            // Header
            csv.append("Numero Personnel,Nom,Prenom,Civilite,Nationalite,Role,Email,Telephone,Adresse,Date Naissance\n");
            
            // Data
            for (Personnel p : personnel) {
                csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,\"%s\",%s\n",
                    p.getNumeroPersonnel(),
                    p.getNom(),
                    p.getPrenom(),
                    p.getCivilite(),
                    p.getNationalite(),
                    p.getRole(),
                    p.getEmail() != null ? p.getEmail() : "",
                    p.getTelephone() != null ? p.getTelephone() : "",
                    p.getAdresse() != null ? p.getAdresse().replace("\"", "\"\"") : "",
                    p.getDateNaissance()
                ));
            }
            
            logger.info("Personnel data exported to CSV: {} records", personnel.size());
            return csv.toString();
            
        } catch (Exception e) {
            logger.error("Error exporting personnel to CSV", e);
            throw new RuntimeException("Error exporting personnel to CSV", e);
        }
    }
    
    @Override
    public int importPersonnelFromCSV(String csvData) {
        try {
            String[] lines = csvData.split("\n");
            int imported = 0;
            
            // Skip header line
            for (int i = 1; i < lines.length; i++) {
                try {
                    String[] fields = lines[i].split(",");
                    if (fields.length >= 10) {
                        Personnel personnel = new Personnel();
                        personnel.setNumeroPersonnel(fields[0].trim());
                        personnel.setNom(fields[1].trim());
                        personnel.setPrenom(fields[2].trim());
                        personnel.setCivilite(Civilite.valueOf(fields[3].trim()));
                        personnel.setNationalite(Nationalite.valueOf(fields[4].trim()));
                        personnel.setRole(Role.valueOf(fields[5].trim()));
                        personnel.setEmail(fields[6].trim().isEmpty() ? null : fields[6].trim());
                        personnel.setTelephone(fields[7].trim().isEmpty() ? null : fields[7].trim());
                        personnel.setAdresse(fields[8].trim().replace("\"", ""));
                        personnel.setDateNaissance(LocalDate.parse(fields[9].trim()));
                        
                        // Set default password
                        personnel.setMotDePasse(authenticationService.generateDefaultPassword());
                        
                        createPersonnel(personnel);
                        imported++;
                    }
                } catch (Exception e) {
                    logger.warn("Error importing personnel from line {}: {}", i + 1, e.getMessage());
                }
            }
            
            logger.info("Personnel imported from CSV: {} records", imported);
            return imported;
            
        } catch (Exception e) {
            logger.error("Error importing personnel from CSV", e);
            throw new RuntimeException("Error importing personnel from CSV", e);
        }
    }
    
    @Override
    public List<Personnel> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findAllPersonnel();
        }
        
        String searchTerm = name.trim().toLowerCase();
        
        return personnelRepository.findAll().stream()
                .filter(p -> p.getNom().toLowerCase().contains(searchTerm) || 
                           p.getPrenom().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
}
