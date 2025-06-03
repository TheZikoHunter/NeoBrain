package insea.neobrain.service;

import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.Role;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for authentication and user management
 * Handles login, logout, session management, and security operations
 */
public interface AuthenticationService {
    
    /**
     * Authenticate a user with username and password
     * @param numeroPersonnel The personnel number (username)
     * @param motDePasse The password
     * @return Optional containing the authenticated personnel, empty if authentication fails
     */
    Optional<Personnel> authenticate(String numeroPersonnel, String motDePasse);
    
    /**
     * Get the currently logged-in user
     * @return The current user, null if no user is logged in
     */
    Personnel getCurrentUser();
    
    /**
     * Set the current user (after successful authentication)
     * @param personnel The authenticated personnel
     */
    void setCurrentUser(Personnel personnel);
    
    /**
     * Log out the current user
     * Clears the current session
     */
    void logout();
    
    /**
     * Check if a user is currently logged in
     * @return true if a user is logged in, false otherwise
     */
    boolean isLoggedIn();
    
    /**
     * Check if the current user has a specific role
     * @param role The role to check
     * @return true if the current user has the role, false otherwise
     */
    boolean hasRole(Role role);
    
    /**
     * Check if the current user has any of the specified roles
     * @param roles The roles to check
     * @return true if the current user has at least one of the roles, false otherwise
     */
    boolean hasAnyRole(Role... roles);
    
    /**
     * Change password for the current user
     * @param currentPassword The current password
     * @param newPassword The new password
     * @return true if password was changed successfully, false otherwise
     */
    boolean changePassword(String currentPassword, String newPassword);
    
    /**
     * Reset password for a personnel (admin function)
     * @param personnel The personnel whose password to reset
     * @param newPassword The new password
     * @return true if password was reset successfully, false otherwise
     */
    boolean resetPassword(Personnel personnel, String newPassword);
    
    /**
     * Hash a password using BCrypt
     * @param password The plain text password
     * @return The hashed password
     */
    String hashPassword(String password);
    
    /**
     * Verify a password against its hash
     * @param password The plain text password
     * @param hashedPassword The hashed password
     * @return true if the password matches, false otherwise
     */
    boolean verifyPassword(String password, String hashedPassword);
    
    /**
     * Get all users with a specific role
     * @param role The role to filter by
     * @return List of personnel with the specified role
     */
    List<Personnel> getUsersByRole(Role role);
    
    /**
     * Check if a personnel number is available (not already taken)
     * @param numeroPersonnel The personnel number to check
     * @return true if available, false if already taken
     */
    boolean isPersonnelNumberAvailable(String numeroPersonnel);
    
    /**
     * Generate a default password for new personnel
     * @return A generated password
     */
    String generateDefaultPassword();
    
    /**
     * Lock a user account (admin function)
     * @param personnel The personnel to lock
     * @return true if account was locked successfully, false otherwise
     */
    boolean lockAccount(Personnel personnel);
    
    /**
     * Unlock a user account (admin function)
     * @param personnel The personnel to unlock
     * @return true if account was unlocked successfully, false otherwise
     */
    boolean unlockAccount(Personnel personnel);
    
    /**
     * Check if an account is locked
     * @param personnel The personnel to check
     * @return true if account is locked, false otherwise
     */
    boolean isAccountLocked(Personnel personnel);
}
