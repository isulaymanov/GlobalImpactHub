package com.alien.security.controller;

import com.alien.security.entity.PhotoBankDetails;
import com.alien.security.service.PhotoBankDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/photo-bank-details")
@RequiredArgsConstructor
public class PhotoBankDetailsController {
    private final PhotoBankDetailsService photoBankDetailsService;

    @PostMapping("/{photoBankId}")
    public ResponseEntity<PhotoBankDetails> createPhotoBankDetails(
            @PathVariable Long photoBankId,
            @RequestParam String title,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(photoBankDetailsService.createPhotoBankDetails(photoBankId, title, file));
    }

    @GetMapping("/{photoBankId}")
    public ResponseEntity<List<PhotoBankDetails>> getPhotoBankDetails(@PathVariable Long photoBankId) {
        return ResponseEntity.ok(photoBankDetailsService.getPhotoBankDetails(photoBankId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PhotoBankDetails> updatePhotoBankDetails(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        return ResponseEntity.ok(photoBankDetailsService.updatePhotoBankDetails(id, title, file));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhotoBankDetails(@PathVariable Long id) throws IOException {
        photoBankDetailsService.deletePhotoBankDetails(id);
        return ResponseEntity.noContent().build();  // Возвращаем 204 No Content при успешном удалении
    }


    @GetMapping("/photo/{id}")
    public ResponseEntity<Resource> getPhoto(@PathVariable Long id) {
        Optional<PhotoBankDetails> photoBankDetailsOptional = photoBankDetailsService.getPhotoBankDetailsById(id);
        if (photoBankDetailsOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PhotoBankDetails photoBankDetails = photoBankDetailsOptional.get();
        String photoUrl = photoBankDetails.getPhotoUrl();

        if (photoUrl == null || photoUrl.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path path = Paths.get("D:/uploadsPhotoBankDetails").resolve(photoUrl.substring(photoUrl.lastIndexOf("/") + 1));  // Восстанавливаем путь до файла
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
