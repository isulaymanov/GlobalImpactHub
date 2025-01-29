package com.alien.security.controller;

import com.alien.security.entity.AdditionalInformation;
import com.alien.security.entity.Event;
import com.alien.security.entity.PasswordDetails;
import com.alien.security.entity.UserModel;
import com.alien.security.service.AdditionalInformationService;
import com.alien.security.service.PasswordDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/password-details")
public class PasswordDetailsController {

    @Autowired
    private PasswordDetailsService passwordDetailsService;

    @PostMapping
    public ResponseEntity<PasswordDetails> createPasswordDetails(@RequestBody PasswordDetails passwordDetails) {
        PasswordDetails createdPasswordDetails = passwordDetailsService.createPasswordDetails(passwordDetails);
        return ResponseEntity.ok(createdPasswordDetails);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PasswordDetails> updatePasswordDetails(@PathVariable Long id, @RequestBody PasswordDetails passwordDetails) {
        PasswordDetails updatedPasswordDetails = passwordDetailsService.updatePasswordDetails(id, passwordDetails);
        return ResponseEntity.ok(updatedPasswordDetails);
    }

    @GetMapping("/current")
    public ResponseEntity<List<PasswordDetails>> getPasswordDetailsForCurrentUser() {
        List<PasswordDetails> passwordDetails = passwordDetailsService.getPasswordDetailsForCurrentUser();
        return ResponseEntity.ok(passwordDetails);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePasswordDetails(@PathVariable Long id) {
        passwordDetailsService.deletePasswordDetails(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PasswordDetails> getPasswordDetailsById(@PathVariable Long id) {
        PasswordDetails passwordDetails = passwordDetailsService.getPasswordDetailsById(id);
        return ResponseEntity.ok(passwordDetails);
    }

    @GetMapping("/all/{userId}/admin")
    public ResponseEntity<?> getPasswordDetailsByUserId(@PathVariable int userId) {
        try {
            List<PasswordDetails> passwordDetailsList = passwordDetailsService.getPasswordDetailsByUserId(userId);
            return ResponseEntity.ok(passwordDetailsList);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, ex.getMessage()));
        }
    }

    public record ErrorResponse(int status, String message) {}


    @PostMapping("/uploadPhoto/{id}")
    public ResponseEntity<?> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserModel user) {
        try {
            Optional<PasswordDetails> passwordDetailsOptional = passwordDetailsService.getPasswordDetailsOpById(id);
            if (passwordDetailsOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            PasswordDetails passwordDetails = passwordDetailsOptional.get();
            String passportScanUrl = passwordDetailsService.savePasswordDetailsPhoto(file);
            passwordDetails.setPassportScanUrl(passportScanUrl);
            passwordDetailsService.updatePasswordDetails(id, passwordDetails);

            return ResponseEntity.ok("Фото загружена: " + passportScanUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Ошибка загрузки фото");
        }
    }

    @GetMapping("/photo/{id}")
    public ResponseEntity<Resource> getPhoto(@PathVariable Long id) {
        Optional<PasswordDetails> passwordDetailsOptional = passwordDetailsService.getPasswordDetailsOpById(id);
        if (passwordDetailsOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PasswordDetails passwordDetails = passwordDetailsOptional.get();
        String passportScanUrl = passwordDetails.getPassportScanUrl();

        if (passportScanUrl == null || passportScanUrl.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path path = Paths.get("D:/uploadsPasswordDetails").resolve(passportScanUrl.substring(passportScanUrl.lastIndexOf("/") + 1));  // Восстанавливаем путь до файла
            Resource resource = new FileSystemResource(path);

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
