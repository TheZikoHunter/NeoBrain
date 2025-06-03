package insea.neobrain.repository;

import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.Role;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Personnel entity
 */
public interface PersonnelRepository extends GenericRepository<Personnel, Long> {
    
    /**
     * Find personnel by ID personnel (employee ID)
     * @param idPersonnel the employee ID
     * @return Optional containing the personnel if found
     */
    Optional<Personnel> findByIdPersonnel(String idPersonnel);
    
    /**
     * Find personnel by email
     * @param email the email address
     * @return Optional containing the personnel if found
     */
    Optional<Personnel> findByEmail(String email);
    
    /**
     * Find personnel by role
     * @param role the role
     * @return list of personnel with the specified role
     */
    List<Personnel> findByRole(Role role);
    
    /**
     * Find active personnel
     * @return list of active personnel
     */
    List<Personnel> findActive();
    
    /**
     * Find personnel by name (nom and prenom)
     * @param nom the last name
     * @param prenom the first name
     * @return list of personnel matching the name
     */
    List<Personnel> findByName(String nom, String prenom);
    
    /**
     * Find personnel who can perform inventory tasks
     * @return list of personnel who can perform inventory tasks
     */
    List<Personnel> findInventoryCapablePersonnel();
    
    /**
     * Find personnel by partial name search
     * @param searchTerm the search term for name
     * @return list of personnel matching the search term
     */
    List<Personnel> searchByName(String searchTerm);
    
    /**
     * Authenticate personnel
     * @param email the email
     * @param motDePasse the password
     * @return Optional containing the authenticated personnel if successful
     */
    Optional<Personnel> authenticate(String email, String motDePasse);
    
    /**
     * Check if email exists
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if ID personnel exists
     * @param idPersonnel the employee ID to check
     * @return true if ID exists, false otherwise
     */
    boolean existsByIdPersonnel(String idPersonnel);
    
    /**
     * Find personnel by numero personnel (alias for idPersonnel)
     * @param numeroPersonnel the employee number
     * @return Optional containing the personnel if found
     */
    Optional<Personnel> findByNumeroPersonnel(String numeroPersonnel);
    
    /**
     * Find personnel by nom and prenom
     * @param nom the last name
     * @param prenom the first name
     * @return list of personnel matching the name
     */
    List<Personnel> findByNomAndPrenom(String nom, String prenom);
    
    /**
     * Get personnel count by role
     * @return map of role to count
     */
    java.util.Map<Role, Long> getPersonnelCountByRole();
    
    /**
     * Search personnel with multiple criteria
     * @param nom the last name (optional)
     * @param prenom the first name (optional)
     * @param role the role (optional)
     * @param email the email (optional)
     * @param telephone the telephone (optional)
     * @return list of personnel matching the criteria
     */
    List<Personnel> searchPersonnel(String nom, String prenom, Role role, String email, String telephone);
    
    /**
     * Count personnel by personnel number prefix
     * @param prefix the prefix to count
     * @return the count
     */
    long countByPersonnelNumberPrefix(String prefix);
    
    /**
     * Count personnel by role
     * @param role the role
     * @return the count
     */
    long countByRole(Role role);
    
    /**
     * Check if personnel can be deleted
     * @param personnelId the personnel ID
     * @return true if can be deleted, false otherwise
     */
    boolean canDeletePersonnel(Long personnelId);
    
    /**
     * Find personnel by date of birth range
     * @param startDate the start date
     * @param endDate the end date
     * @return list of personnel born between the dates
     */
    List<Personnel> findByDateNaissanceBetween(java.time.LocalDate startDate, java.time.LocalDate endDate);
    
    /**
     * Find personnel by username (nom_utilisateur)
     * @param nomUtilisateur the username
     * @return Optional containing the personnel if found
     */
    Optional<Personnel> findByNomUtilisateur(String nomUtilisateur);
}
