package insea.neobrain.service;

import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.Role;
import insea.neobrain.entity.Civilite;
import insea.neobrain.entity.Nationalite;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for personnel management
 * Handles CRUD operations and business logic for personnel entities
 */
public interface PersonnelService {
    
    /**
     * Create a new personnel
     * @param personnel The personnel to create
     * @return The created personnel with generated ID
     */
    Personnel createPersonnel(Personnel personnel);
    
    /**
     * Create a new personnel with basic information
     * @param numeroPersonnel Personnel number (must be unique)
     * @param nom Last name
     * @param prenom First name
     * @param civilite Civility
     * @param nationalite Nationality
     * @param role Role
     * @param telephone Phone number
     * @param email Email address
     * @param adresse Address
     * @param dateNaissance Birth date
     * @return The created personnel
     */
    Personnel createPersonnel(String numeroPersonnel, String nom, String prenom, 
                             Civilite civilite, Nationalite nationalite, Role role,
                             String telephone, String email, String adresse, 
                             LocalDate dateNaissance);
    
    /**
     * Update an existing personnel
     * @param personnel The personnel to update
     * @return The updated personnel
     */
    Personnel updatePersonnel(Personnel personnel);
    
    /**
     * Delete a personnel by ID
     * @param id The personnel ID
     * @return true if deleted successfully, false otherwise
     */
    boolean deletePersonnel(Long id);
    
    /**
     * Find personnel by ID
     * @param id The personnel ID
     * @return Optional containing the personnel if found
     */
    Optional<Personnel> findPersonnelById(Long id);
    
    /**
     * Find personnel by personnel number
     * @param numeroPersonnel The personnel number
     * @return Optional containing the personnel if found
     */
    Optional<Personnel> findPersonnelByNumber(String numeroPersonnel);
    
    /**
     * Find all personnel
     * @return List of all personnel
     */
    List<Personnel> findAllPersonnel();
    
    /**
     * Find personnel by role
     * @param role The role to filter by
     * @return List of personnel with the specified role
     */
    List<Personnel> findPersonnelByRole(Role role);
    
    /**
     * Find personnel by name (partial match)
     * @param nom Last name (partial)
     * @param prenom First name (partial)
     * @return List of personnel matching the name criteria
     */
    List<Personnel> findPersonnelByName(String nom, String prenom);
    
    /**
     * Find personnel who can perform inventory tasks
     * @return List of personnel with inventory capabilities
     */
    List<Personnel> findInventoryCapablePersonnel();
    
    /**
     * Find active personnel (not locked accounts)
     * @return List of active personnel
     */
    List<Personnel> findActivePersonnel();
    
    /**
     * Get personnel statistics by role
     * @return List of [Role, Count] arrays
     */
    List<Object[]> getPersonnelStatisticsByRole();
    
    /**
     * Search personnel by multiple criteria
     * @param nom Last name (partial, optional)
     * @param prenom First name (partial, optional)
     * @param role Role (optional)
     * @param email Email (partial, optional)
     * @param telephone Phone (partial, optional)
     * @return List of personnel matching the criteria
     */
    List<Personnel> searchPersonnel(String nom, String prenom, Role role, 
                                   String email, String telephone);
    
    /**
     * Validate personnel data before create/update
     * @param personnel The personnel to validate
     * @return List of validation error messages, empty if valid
     */
    List<String> validatePersonnel(Personnel personnel);
    
    /**
     * Check if a personnel number is available
     * @param numeroPersonnel The personnel number to check
     * @return true if available, false if already taken
     */
    boolean isPersonnelNumberAvailable(String numeroPersonnel);
    
    /**
     * Check if a personnel number is available for update (excluding current personnel)
     * @param numeroPersonnel The personnel number to check
     * @param currentPersonnelId The ID of the current personnel being updated
     * @return true if available, false if taken by another personnel
     */
    boolean isPersonnelNumberAvailableForUpdate(String numeroPersonnel, Long currentPersonnelId);
    
    /**
     * Generate next available personnel number
     * @param rolePrefix Prefix based on role (e.g., "ADM", "SM", "SE")
     * @return Next available personnel number
     */
    String generatePersonnelNumber(String rolePrefix);
    
    /**
     * Get personnel count
     * @return Total number of personnel
     */
    long getPersonnelCount();
    
    /**
     * Get personnel count by role
     * @param role The role to count
     * @return Number of personnel with the specified role
     */
    long getPersonnelCountByRole(Role role);
    
    /**
     * Activate a personnel account
     * @param personnelId The personnel ID
     * @return true if activated successfully, false otherwise
     */
    boolean activatePersonnel(Long personnelId);
    
    /**
     * Deactivate a personnel account
     * @param personnelId The personnel ID
     * @return true if deactivated successfully, false otherwise
     */
    boolean deactivatePersonnel(Long personnelId);
    
    /**
     * Check if personnel can be deleted (no associated data)
     * @param personnelId The personnel ID
     * @return true if can be deleted, false if has associated data
     */
    boolean canDeletePersonnel(Long personnelId);
    
    /**
     * Get personnel by age range
     * @param minAge Minimum age
     * @param maxAge Maximum age
     * @return List of personnel in the age range
     */
    List<Personnel> findPersonnelByAgeRange(int minAge, int maxAge);
    
    /**
     * Export personnel data to CSV format
     * @return CSV string containing personnel data
     */
    String exportPersonnelToCSV();
    
    /**
     * Import personnel data from CSV
     * @param csvData CSV string containing personnel data
     * @return Number of personnel imported successfully
     */
    int importPersonnelFromCSV(String csvData);
    
    /**
     * Search personnel by name (first or last)
     * @param name Name to search (can be partial)
     * @return List of personnel matching the name
     */
    List<Personnel> searchByName(String name);
}
