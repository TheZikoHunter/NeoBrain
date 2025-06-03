package insea.neobrain.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import insea.neobrain.model.Personnel;
import insea.neobrain.repository.PersonnelRepository;
import insea.neobrain.util.PasswordUtil;
import insea.neobrain.util.AuditLogger;
import insea.neobrain.util.BusinessException;

import java.util.Optional;

/**
 * Unit tests for AuthenticationServiceImpl class
 */
@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private PersonnelRepository personnelRepository;

    @Mock
    private AuditLogger auditLogger;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private Personnel samplePersonnel;

    @BeforeEach
    void setUp() {
        samplePersonnel = new Personnel();
        samplePersonnel.setId(1L);
        samplePersonnel.setNom("Dupont");
        samplePersonnel.setPrenom("Jean");
        samplePersonnel.setEmail("jean.dupont@example.com");
        samplePersonnel.setRole("ADMIN");
        samplePersonnel.setMotDePasse(PasswordUtil.hashPassword("password123"));
    }

    @Test
    @DisplayName("Should authenticate user with correct credentials")
    public void testAuthenticateSuccess() {
        // Given
        String email = "jean.dupont@example.com";
        String password = "password123";
        String ipAddress = "192.168.1.100";

        when(personnelRepository.findByEmail(email)).thenReturn(Optional.of(samplePersonnel));

        // When
        Optional<Personnel> result = authenticationService.authenticate(email, password, ipAddress);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);

        verify(personnelRepository).findByEmail(email);
        verify(auditLogger).logAuthenticationSuccess(email, ipAddress);
    }

    @Test
    @DisplayName("Should fail authentication with incorrect password")
    public void testAuthenticateWrongPassword() {
        // Given
        String email = "jean.dupont@example.com";
        String wrongPassword = "wrongpassword";
        String ipAddress = "192.168.1.100";

        when(personnelRepository.findByEmail(email)).thenReturn(Optional.of(samplePersonnel));

        // When
        Optional<Personnel> result = authenticationService.authenticate(email, wrongPassword, ipAddress);

        // Then
        assertThat(result).isEmpty();

        verify(personnelRepository).findByEmail(email);
        verify(auditLogger).logAuthenticationFailure(email, ipAddress, "Mot de passe incorrect");
    }

    @Test
    @DisplayName("Should fail authentication with non-existent user")
    public void testAuthenticateUserNotFound() {
        // Given
        String email = "nonexistent@example.com";
        String password = "password123";
        String ipAddress = "192.168.1.100";

        when(personnelRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<Personnel> result = authenticationService.authenticate(email, password, ipAddress);

        // Then
        assertThat(result).isEmpty();

        verify(personnelRepository).findByEmail(email);
        verify(auditLogger).logAuthenticationFailure(email, ipAddress, "Utilisateur introuvable");
    }

    @Test
    @DisplayName("Should handle null email")
    public void testAuthenticateNullEmail() {
        // When/Then
        assertThatThrownBy(() -> 
            authenticationService.authenticate(null, "password", "192.168.1.100")
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("L'email ne peut pas être null");

        verify(personnelRepository, never()).findByEmail(any());
        verify(auditLogger, never()).logAuthenticationSuccess(any(), any());
    }

    @Test
    @DisplayName("Should handle null password")
    public void testAuthenticateNullPassword() {
        // When/Then
        assertThatThrownBy(() -> 
            authenticationService.authenticate("test@example.com", null, "192.168.1.100")
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Le mot de passe ne peut pas être null");

        verify(personnelRepository, never()).findByEmail(any());
        verify(auditLogger, never()).logAuthenticationSuccess(any(), any());
    }

    @Test
    @DisplayName("Should handle empty email")
    public void testAuthenticateEmptyEmail() {
        // When/Then
        assertThatThrownBy(() -> 
            authenticationService.authenticate("", "password", "192.168.1.100")
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("L'email ne peut pas être vide");

        verify(personnelRepository, never()).findByEmail(any());
    }

    @Test
    @DisplayName("Should handle empty password")
    public void testAuthenticateEmptyPassword() {
        // When/Then
        assertThatThrownBy(() -> 
            authenticationService.authenticate("test@example.com", "", "192.168.1.100")
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Le mot de passe ne peut pas être vide");

        verify(personnelRepository, never()).findByEmail(any());
    }

    @Test
    @DisplayName("Should validate user role")
    public void testValidateUserRole() {
        // Given
        String requiredRole = "ADMIN";

        // When
        boolean isValid = authenticationService.validateUserRole(samplePersonnel, requiredRole);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should reject invalid user role")
    public void testValidateUserRoleInvalid() {
        // Given
        String requiredRole = "SUPER_ADMIN";

        // When
        boolean isValid = authenticationService.validateUserRole(samplePersonnel, requiredRole);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should handle null user in role validation")
    public void testValidateUserRoleNullUser() {
        // When/Then
        assertThatThrownBy(() -> 
            authenticationService.validateUserRole(null, "ADMIN")
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("L'utilisateur ne peut pas être null");
    }

    @Test
    @DisplayName("Should handle null role in validation")
    public void testValidateUserRoleNullRole() {
        // When/Then
        assertThatThrownBy(() -> 
            authenticationService.validateUserRole(samplePersonnel, null)
        )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Le rôle requis ne peut pas être null");
    }

    @Test
    @DisplayName("Should check if user has permission")
    public void testHasPermission() {
        // Given
        String action = "DELETE_USER";

        // When
        boolean hasPermission = authenticationService.hasPermission(samplePersonnel, action);

        // Then
        // Admin should have all permissions
        assertThat(hasPermission).isTrue();
    }

    @Test
    @DisplayName("Should deny permission for non-admin users")
    public void testHasPermissionNonAdmin() {
        // Given
        samplePersonnel.setRole("EMPLOYE");
        String action = "DELETE_USER";

        // When
        boolean hasPermission = authenticationService.hasPermission(samplePersonnel, action);

        // Then
        assertThat(hasPermission).isFalse();
    }

    @Test
    @DisplayName("Should change password successfully")
    public void testChangePassword() {
        // Given
        String oldPassword = "password123";
        String newPassword = "newPassword456";
        String userEmail = samplePersonnel.getEmail();

        when(personnelRepository.findByEmail(userEmail)).thenReturn(Optional.of(samplePersonnel));
        when(personnelRepository.save(any(Personnel.class))).thenReturn(samplePersonnel);

        // When
        boolean result = authenticationService.changePassword(userEmail, oldPassword, newPassword);

        // Then
        assertThat(result).isTrue();

        verify(personnelRepository).findByEmail(userEmail);
        verify(personnelRepository).save(any(Personnel.class));
        verify(auditLogger).logDataModification(
            eq(userEmail), 
            eq("CHANGE_PASSWORD"), 
            eq("Personnel"), 
            eq("1"), 
            eq("***"), 
            eq("***")
        );
    }

    @Test
    @DisplayName("Should fail password change with wrong old password")
    public void testChangePasswordWrongOldPassword() {
        // Given
        String wrongOldPassword = "wrongPassword";
        String newPassword = "newPassword456";
        String userEmail = samplePersonnel.getEmail();

        when(personnelRepository.findByEmail(userEmail)).thenReturn(Optional.of(samplePersonnel));

        // When
        boolean result = authenticationService.changePassword(userEmail, wrongOldPassword, newPassword);

        // Then
        assertThat(result).isFalse();

        verify(personnelRepository).findByEmail(userEmail);
        verify(personnelRepository, never()).save(any());
        verify(auditLogger).logAuthenticationFailure(
            eq(userEmail), 
            eq("localhost"), 
            eq("Tentative de changement de mot de passe avec ancien mot de passe incorrect")
        );
    }

    @Test
    @DisplayName("Should fail password change for non-existent user")
    public void testChangePasswordUserNotFound() {
        // Given
        String email = "nonexistent@example.com";
        String oldPassword = "password123";
        String newPassword = "newPassword456";

        when(personnelRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> 
            authenticationService.changePassword(email, oldPassword, newPassword)
        )
        .isInstanceOf(BusinessException.class)
        .hasMessage("Personnel avec l'identifiant '" + email + "' introuvable");

        verify(personnelRepository).findByEmail(email);
        verify(personnelRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle special characters in email")
    public void testAuthenticateSpecialCharactersEmail() {
        // Given
        String specialEmail = "test+tag@example-domain.com";
        String password = "password123";
        String ipAddress = "192.168.1.100";

        samplePersonnel.setEmail(specialEmail);
        when(personnelRepository.findByEmail(specialEmail)).thenReturn(Optional.of(samplePersonnel));

        // When
        Optional<Personnel> result = authenticationService.authenticate(specialEmail, password, ipAddress);

        // Then
        assertThat(result).isPresent();

        verify(personnelRepository).findByEmail(specialEmail);
        verify(auditLogger).logAuthenticationSuccess(specialEmail, ipAddress);
    }

    @Test
    @DisplayName("Should handle case insensitive email")
    public void testAuthenticateCaseInsensitiveEmail() {
        // Given
        String upperCaseEmail = "JEAN.DUPONT@EXAMPLE.COM";
        String password = "password123";
        String ipAddress = "192.168.1.100";

        when(personnelRepository.findByEmail(upperCaseEmail.toLowerCase())).thenReturn(Optional.of(samplePersonnel));

        // When
        Optional<Personnel> result = authenticationService.authenticate(upperCaseEmail, password, ipAddress);

        // Then
        assertThat(result).isPresent();

        verify(personnelRepository).findByEmail(upperCaseEmail.toLowerCase());
    }

    @Test
    @DisplayName("Should handle whitespace in credentials")
    public void testAuthenticateWithWhitespace() {
        // Given
        String emailWithSpaces = "  jean.dupont@example.com  ";
        String passwordWithSpaces = "  password123  ";
        String ipAddress = "192.168.1.100";

        when(personnelRepository.findByEmail(emailWithSpaces.trim())).thenReturn(Optional.of(samplePersonnel));

        // When
        Optional<Personnel> result = authenticationService.authenticate(emailWithSpaces, passwordWithSpaces, ipAddress);

        // Then
        assertThat(result).isPresent();

        verify(personnelRepository).findByEmail(emailWithSpaces.trim());
    }
}
