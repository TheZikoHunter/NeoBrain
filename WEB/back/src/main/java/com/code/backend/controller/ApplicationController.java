package com.code.backend.controller;

import com.code.backend.entity.Application;
import com.code.backend.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*") // adjust to your frontend URL
public class ApplicationController {

    private final ApplicationRepository repository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ApplicationController(ApplicationRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<String> submitApplication(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam(required = false) String phone,
            @RequestParam String position,
            @RequestParam String experience,
            @RequestParam(required = false) String message,
            @RequestParam MultipartFile resume
    ) throws IOException {
        if (resume.isEmpty()) {
            return ResponseEntity.badRequest().body("Resume file is missing.");
        }

        // Save file
        String filename = System.currentTimeMillis() + "_" + StringUtils.cleanPath(resume.getOriginalFilename());
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) uploadPath.mkdirs();
        File dest = new File(uploadDir + File.separator + filename);
        resume.transferTo(dest);

        // Save to DB
        Application app = new Application();
        app.setFirstName(firstName);
        app.setLastName(lastName);
        app.setEmail(email);
        app.setPhone(phone);
        app.setPosition(position);
        app.setExperience(experience);
        app.setMessage(message);
        app.setResumePath(dest.getAbsolutePath());

        repository.save(app);
        return ResponseEntity.ok("Application submitted successfully.");
    }
}
