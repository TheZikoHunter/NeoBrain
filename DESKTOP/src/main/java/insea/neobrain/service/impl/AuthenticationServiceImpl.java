package insea.neobrain.service.impl;

import insea.neobrain.entity.Personnel;
import insea.neobrain.entity.Role;
import insea.neobrain.repository.PersonnelRepository;
import insea.neobrain.service.AuthenticationService;
import insea.neobrain.util.PasswordUtil;
import insea.neobrain.util.AuditLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of AuthenticationService
 * Provides authentication and user management functionality
 */
public class AuthenticationServiceImpl implements AuthenticationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private static final int SALT_LENGTH = 16;
    private static final String DEFAULT_PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int DEFAULT_PASSWORD_LENGTH = 12;
    
    private final PersonnelRepository personnelRepository;
    private Personnel currentUser;
    
    public AuthenticationServiceImpl(PersonnelRepository personnelRepository) {
        this.personnelRepository = personnelRepository;
    }
    
    @Override
    public Optional<Personnel> authenticate(String nomUtilisateur, String motDePasse) {
        try {
            logger.debug("Attempting authentication for username: {}", nomUtilisateur);
            Optional<Personnel> personnelOpt = personnelRepository.findByNomUtilisateur(nomUtilisateur);
            
            if (personnelOpt.isEmpty()) {
                logger.warn("Authentication failed: Username not found: {}", nomUtilisateur);
                AuditLogger.logAuthentication(nomUtilisateur, false, "Username not found");
                return Optional.empty();
            }
            
            Personnel personnel = personnelOpt.get();
            
            // Check if account is locked
            if (isAccountLocked(personnel)) {
                logger.warn("Authentication failed: Account is locked for username: {}", nomUtilisateur);
                AuditLogger.logAuthentication(nomUtilisateur, false, "Account is locked");
                return Optional.empty();
            }
            
            // Verify password using PasswordUtil
            if (!PasswordUtil.verifyPassword(motDePasse, personnel.getMotDePasse())) {
                logger.warn("Authentication failed: Invalid password for username: {}", nomUtilisateur);
                AuditLogger.logAuthentication(nomUtilisateur, false, "Invalid password");
                return Optional.empty();
            }
            
            logger.info("Authentication successful for username: {}", nomUtilisateur);
            AuditLogger.logAuthentication(nomUtilisateur, true, "Login successful");
            return Optional.of(personnel);
        } catch (Exception e) {
            logger.error("Error during authentication for username: {}", nomUtilisateur, e);
            return Optional.empty();
        }
    }
    
    @Override
    public Personnel getCurrentUser() {
        return currentUser;
    }
    
    @Override
    public void setCurrentUser(Personnel personnel) {
        this.currentUser = personnel;
        logger.info("Current user set to: {}", personnel != null ? personnel.getNumeroPersonnel() : "null");
    }
    
    @Override
    public void logout() {
        if (currentUser != null) {
            logger.info("User logged out: {}", currentUser.getNomUtilisateur());
            AuditLogger.logAuthentication(currentUser.getNomUtilisateur(), true, "Logout successful");
            currentUser = null;
        }
    }
    
    @Override
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    @Override
    public boolean hasRole(Role role) {
        return currentUser != null && currentUser.getRole() == role;
    }
    
    @Override
    public boolean hasAnyRole(Role... roles) {
        if (currentUser == null) {
            return false;
        }
        
        for (Role role : roles) {
            if (currentUser.getRole() == role) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean changePassword(String currentPassword, String newPassword) {
        if (currentUser == null) {
            logger.warn("Cannot change password: No user logged in");
            return false;
        }
        
        try {
            // Verify current password
            if (!verifyPassword(currentPassword, currentUser.getMotDePasse())) {
                logger.warn("Password change failed: Invalid current password for user: {}", 
                    currentUser.getNumeroPersonnel());
                return false;
            }
            
            // Hash new password and update
            String hashedNewPassword = hashPassword(newPassword);
            currentUser.setMotDePasse(hashedNewPassword);
            personnelRepository.update(currentUser);
            
            logger.info("Password changed successfully for user: {}", currentUser.getNumeroPersonnel());
            return true;
            
        } catch (Exception e) {
            logger.error("Error changing password for user: {}", currentUser.getNumeroPersonnel(), e);
            return false;
        }
    }
    
    @Override
    public boolean resetPassword(Personnel personnel, String newPassword) {
        if (currentUser == null || !hasRole(Role.ADMIN)) {
            logger.warn("Password reset denied: Insufficient privileges");
            return false;
        }
        
        try {
            String hashedNewPassword = hashPassword(newPassword);
            personnel.setMotDePasse(hashedNewPassword);
            personnelRepository.update(personnel);
            
            logger.info("Password reset successfully for personnel: {} by admin: {}", 
                personnel.getNumeroPersonnel(), currentUser.getNumeroPersonnel());
            return true;
            
        } catch (Exception e) {
            logger.error("Error resetting password for personnel: {}", personnel.getNumeroPersonnel(), e);
            return false;
        }
    }
    
    @Override
    public String hashPassword(String password) {
        return PasswordUtil.hashPassword(password);
    }
    
    @Override
    public boolean verifyPassword(String password, String hashedPassword) {
        return PasswordUtil.verifyPassword(password, hashedPassword);
    }
    
    @Override
    public List<Personnel> getUsersByRole(Role role) {
        try {
            return personnelRepository.findByRole(role);
        } catch (Exception e) {
            logger.error("Error getting users by role: {}", role, e);
            throw new RuntimeException("Error getting users by role", e);
        }
    }
    
    @Override
    public boolean isPersonnelNumberAvailable(String numeroPersonnel) {
        try {
            return personnelRepository.findByNumeroPersonnel(numeroPersonnel).isEmpty();
        } catch (Exception e) {
            logger.error("Error checking personnel number availability: {}", numeroPersonnel, e);
            return false;
        }
    }
    
    @Override
    public String generateDefaultPassword() {
        // Simple random password generator (no SecureRandom needed)
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder(12);
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return password.toString();
    }
    
    @Override
    public boolean lockAccount(Personnel personnel) {
        if (currentUser == null || !hasRole(Role.ADMIN)) {
            logger.warn("Account lock denied: Insufficient privileges");
            return false;
        }
        
        try {
            // In a real implementation, you'd have an 'active' field in Personnel entity
            // For now, we'll use a simple approach by setting a special password prefix
            String lockedPassword = "LOCKED:" + personnel.getMotDePasse();
            personnel.setMotDePasse(lockedPassword);
            personnelRepository.update(personnel);
            
            logger.info("Account locked for personnel: {} by admin: {}", 
                personnel.getNumeroPersonnel(), currentUser.getNumeroPersonnel());
            return true;
            
        } catch (Exception e) {
            logger.error("Error locking account for personnel: {}", personnel.getNumeroPersonnel(), e);
            return false;
        }
    }
    
    @Override
    public boolean unlockAccount(Personnel personnel) {
        if (currentUser == null || !hasRole(Role.ADMIN)) {
            logger.warn("Account unlock denied: Insufficient privileges");
            return false;
        }
        
        try {
            // Remove the LOCKED prefix if present
            String password = personnel.getMotDePasse();
            if (password.startsWith("LOCKED:")) {
                personnel.setMotDePasse(password.substring(7));
                personnelRepository.update(personnel);
                
                logger.info("Account unlocked for personnel: {} by admin: {}", 
                    personnel.getNumeroPersonnel(), currentUser.getNumeroPersonnel());
                return true;
            }
            
            logger.warn("Account unlock failed: Account was not locked for personnel: {}", 
                personnel.getNumeroPersonnel());
            return false;
            
        } catch (Exception e) {
            logger.error("Error unlocking account for personnel: {}", personnel.getNumeroPersonnel(), e);
            return false;
        }
    }
    
    @Override
    public boolean isAccountLocked(Personnel personnel) {
        return personnel.getMotDePasse().startsWith("LOCKED:");
    }
}
