package com.code.backend.controller;

import com.code.backend.dto.AuthResponse;
import com.code.backend.dto.LoginRequest;
import com.code.backend.dto.LoginResponse;
import com.code.backend.entity.User;
import com.code.backend.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request);

        if (loginResponse.isSuccess()) {
            // Create cookie with userId
            Cookie userCookie = new Cookie("userId", loginResponse.getUserId().toString());
            userCookie.setHttpOnly(true);
            userCookie.setSecure(false); // Set to true in production with HTTPS
            userCookie.setPath("/");
            userCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days

            response.addCookie(userCookie);
        }

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // Clear the userId cookie
        Cookie userCookie = new Cookie("userId", null);
        userCookie.setHttpOnly(true);
        userCookie.setSecure(false);
        userCookie.setPath("/");
        userCookie.setMaxAge(0); // Delete cookie

        response.addCookie(userCookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userId".equals(cookie.getName())) {
                    try {
                        Long userId = Long.parseLong(cookie.getValue());
                        Optional<User> user = authService.getUserById(userId);
                        if (user.isPresent()) {
                            User currentUser = user.get();
                            LoginResponse userInfo = new LoginResponse(true, "User found",
                                    currentUser.getId(), currentUser.getFirstName(),
                                    currentUser.getLastName(), currentUser.getEmail());
                            return ResponseEntity.ok(userInfo);
                        }
                    } catch (NumberFormatException e) {
                        // Invalid userId in cookie
                    }
                    break;
                }
            }
        }
        return ResponseEntity.status(401).body("Not authenticated");
    }
}
