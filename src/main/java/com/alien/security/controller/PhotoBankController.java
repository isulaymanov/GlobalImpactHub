package com.alien.security.controller;

import com.alien.security.entity.PhotoBank;
import com.alien.security.entity.UserModel;
import com.alien.security.service.PhotoBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/photo-bank")
public class PhotoBankController {
    @Autowired
    private PhotoBankService photoBankService;

    @PostMapping("/create")
    public PhotoBank createPhotoBank(@RequestBody PhotoBank photoBank, @AuthenticationPrincipal UserModel user) {
        return photoBankService.createPhotoBank(photoBank, user);
    }

    @GetMapping("/currentuser")
    public ResponseEntity<?> getPhotoBankForCurrentUser(@AuthenticationPrincipal UserModel currentUser) {
        List<PhotoBank> photoBanks = photoBankService.getPhotoBankByUser(currentUser);

        if (photoBanks == null || photoBanks.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "У вас нет записей"));
        }

        return ResponseEntity.ok(photoBanks);
    }


    @GetMapping("/all")
    public ResponseEntity<List<PhotoBank>> getAllPhotoBank() {
        List<PhotoBank> photoBanks = photoBankService.getAllPhotoBank();

        if (photoBanks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(photoBanks);
    }


    @PatchMapping("/edit/{id}")
    public ResponseEntity<String> updatePhotoBank(
            @PathVariable("id") Long id,
            @RequestBody PhotoBank updatedPhotoBank,
            @AuthenticationPrincipal UserModel user) {
        try {
            PhotoBank updated = photoBankService.updatePhotoBank(id, updatedPhotoBank, user);
            if (updated != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body("PhotoBank Updated Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("PhotoBank not found or not allowed to update");
            }
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public void deletePhotoBank(@PathVariable Long id, @AuthenticationPrincipal UserModel user) {
        photoBankService.deletePhotoBank(id, user);
    }

    @PostMapping("/uploadPhoto/{id}")
    public ResponseEntity<?> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserModel user) {
        try {
            Optional<PhotoBank> photoBankOptional = photoBankService.getPhotoBankById(id);
            if (photoBankOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            PhotoBank photoBank = photoBankOptional.get();
            String photoUrl = photoBankService.savePhotoBankPhoto(file);
            photoBank.setPhotoUrl(photoUrl);
            photoBankService.updatePhotoBank(id, photoBank, user);

            return ResponseEntity.ok("Фото загружена: " + photoUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Ошибка загрузки фото");
        }
    }

    @GetMapping("/photo/{id}")
    public ResponseEntity<Resource> getPhoto(@PathVariable Long id) {
        Optional<PhotoBank> photoBankOptional = photoBankService.getPhotoBankById(id);
        if (photoBankOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        PhotoBank photoBank = photoBankOptional.get();
        String photoUrl = photoBank.getPhotoUrl();

        if (photoUrl == null || photoUrl.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path path = Paths.get("D:/uploadsPhotoBank").resolve(photoUrl.substring(photoUrl.lastIndexOf("/") + 1));  // Восстанавливаем путь до файла
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

    @GetMapping("/{id}")
    public PhotoBank PhotoBankById(@PathVariable Long id) {
        return photoBankService.PhotoBankById(id);
    }

}