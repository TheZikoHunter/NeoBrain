package insea.neobrain.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for PasswordUtil class
 */
public class PasswordUtilTest {

    @Test
    @DisplayName("Should hash password successfully")
    public void testHashPassword() {
        // Given
        String plainPassword = "testPassword123";
        
        // When
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        
        // Then
        assertThat(hashedPassword).isNotNull();
        assertThat(hashedPassword).isNotEqualTo(plainPassword);
        assertThat(hashedPassword).startsWith("$2a$");
        assertThat(hashedPassword).hasSize(60); // BCrypt hashes are always 60 characters
    }

    @Test
    @DisplayName("Should verify correct password")
    public void testVerifyPasswordCorrect() {
        // Given
        String plainPassword = "mySecretPassword";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        
        // When
        boolean isValid = PasswordUtil.verifyPassword(plainPassword, hashedPassword);
        
        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should reject incorrect password")
    public void testVerifyPasswordIncorrect() {
        // Given
        String correctPassword = "mySecretPassword";
        String wrongPassword = "wrongPassword";
        String hashedPassword = PasswordUtil.hashPassword(correctPassword);
        
        // When
        boolean isValid = PasswordUtil.verifyPassword(wrongPassword, hashedPassword);
        
        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should handle null password gracefully")
    public void testHashPasswordNull() {
        // When/Then
        assertThatThrownBy(() -> PasswordUtil.hashPassword(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Password cannot be null");
    }

    @Test
    @DisplayName("Should handle empty password gracefully")
    public void testHashPasswordEmpty() {
        // When/Then
        assertThatThrownBy(() -> PasswordUtil.hashPassword(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Password cannot be empty");
    }

    @Test
    @DisplayName("Should handle null parameters in verification")
    public void testVerifyPasswordNullParameters() {
        // Given
        String hashedPassword = PasswordUtil.hashPassword("test");
        
        // When/Then
        assertThatThrownBy(() -> PasswordUtil.verifyPassword(null, hashedPassword))
            .isInstanceOf(IllegalArgumentException.class);
            
        assertThatThrownBy(() -> PasswordUtil.verifyPassword("test", null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should generate different hashes for same password")
    public void testHashPasswordUniqueness() {
        // Given
        String password = "samePassword";
        
        // When
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);
        
        // Then
        assertThat(hash1).isNotEqualTo(hash2);
        assertThat(PasswordUtil.verifyPassword(password, hash1)).isTrue();
        assertThat(PasswordUtil.verifyPassword(password, hash2)).isTrue();
    }

    @Test
    @DisplayName("Should handle special characters in password")
    public void testPasswordWithSpecialCharacters() {
        // Given
        String passwordWithSpecialChars = "p@ssw0rd!#$%^&*()";
        
        // When
        String hashedPassword = PasswordUtil.hashPassword(passwordWithSpecialChars);
        boolean isValid = PasswordUtil.verifyPassword(passwordWithSpecialChars, hashedPassword);
        
        // Then
        assertThat(hashedPassword).isNotNull();
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should handle very long passwords")
    public void testLongPassword() {
        // Given
        String longPassword = "a".repeat(1000);
        
        // When
        String hashedPassword = PasswordUtil.hashPassword(longPassword);
        boolean isValid = PasswordUtil.verifyPassword(longPassword, hashedPassword);
        
        // Then
        assertThat(hashedPassword).isNotNull();
        assertThat(isValid).isTrue();
    }
}
