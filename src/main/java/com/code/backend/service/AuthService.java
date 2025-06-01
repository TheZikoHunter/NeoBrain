package com.code.backend.service;

import com.code.backend.dto.LoginRequest;
import com.code.backend.dto.LoginResponse;
import com.code.backend.dto.RegistrationRequest;
import com.code.backend.dto.RegistrationResponse;
import com.code.backend.entity.User;
import com.code.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/*@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(String firstName, String lastName, String email, String password) {
        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("User with this email already exists");
        }

        // Create new user
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}*/

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse login(LoginRequest request) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

            if (userOptional.isEmpty()) {
                return new LoginResponse(false, "Invalid email or password");
            }

            User user = userOptional.get();

            // Check if password matches
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return new LoginResponse(false, "Invalid email or password");
            }

            return new LoginResponse(true, "Login successful",
                    user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());

        } catch (Exception e) {
            return new LoginResponse(false, "Login failed: " + e.getMessage());
        }
    }

    public RegistrationResponse registerUser(RegistrationRequest request) {
        try {
            // Check if passwords match
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return new RegistrationResponse(false, "Passwords do not match");
            }

            // Check if user must agree to terms
            if (!request.isAgreeToTerms()) {
                return new RegistrationResponse(false, "You must agree to the terms and conditions");
            }

            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                return new RegistrationResponse(false, "Email already exists");
            }

            // Create new user
            User user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            User savedUser = userRepository.save(user);

            return new RegistrationResponse(true, "User registered successfully", savedUser.getId());

        } catch (Exception e) {
            return new RegistrationResponse(false, "Registration failed: " + e.getMessage());
        }
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
}
