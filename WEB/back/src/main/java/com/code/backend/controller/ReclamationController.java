package com.code.backend.controller;

import com.code.backend.entity.Reclamation;
import com.code.backend.repository.ReclamationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reclamations")
@CrossOrigin(origins = "*") // Allow React frontend
public class ReclamationController {

    @Autowired
    private ReclamationRepository repository;

    private static final String UPLOAD_DIR = "C:/Users/mohamed/Desktop/uploads";

    @PostMapping
    public Reclamation submitReclamation(
            @RequestPart("data") Reclamation reclamation,
            @RequestPart(value = "photos", required = false) MultipartFile[] photos
    ) throws IOException {
        if (photos != null && photos.length > 0) {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String filenames = Arrays.stream(photos)
                    .map(photo -> {
                        String filename = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
                        try {
                            photo.transferTo(new File(UPLOAD_DIR + filename));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return filename;
                    })
                    .collect(Collectors.joining(","));
            reclamation.setPhotoFilenames(filenames);
        }

        return repository.save(reclamation);
    }
}
