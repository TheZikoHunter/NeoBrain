package com.code.backend.service;

import com.code.backend.dto.RegistrationRequest;
import com.code.backend.dto.RegistrationResponse;
import com.code.backend.entity.User;
import com.code.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

}
