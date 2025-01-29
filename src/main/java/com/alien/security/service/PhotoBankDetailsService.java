package com.alien.security.service;

import com.alien.security.entity.News;
import com.alien.security.entity.PhotoBank;
import com.alien.security.entity.PhotoBankDetails;
import com.alien.security.entity.UserModel;
import com.alien.security.repo.PhotoBankDetailsRepository;
import com.alien.security.repo.PhotoBankRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoBankDetailsService {
    private final PhotoBankDetailsRepository photoBankDetailsRepository;
    private final PhotoBankRepository photoBankRepository;

    private final String UPLOAD_DIR = "D:/uploadsPhotoBankDetails/";

    public PhotoBankDetails createPhotoBankDetails(Long photoBankId, String title, MultipartFile file) throws IOException {
        PhotoBank photoBank = photoBankRepository.findById(photoBankId)
                .orElseThrow(() -> new EntityNotFoundException("PhotoBank не найден"));

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            boolean dirCreated = uploadDir.mkdirs(); // Создать директорию, если она не существует
            if (!dirCreated) {
                throw new IOException("Не удалось создать директорию для хранения фотографий.");
            }
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR + filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        PhotoBankDetails details = new PhotoBankDetails();
        details.setTitle(title);
        details.setPhotoUrl(filename);
        details.setCreatedAt(LocalDateTime.now().toString());
        details.setPhotoBank(photoBank);

        return photoBankDetailsRepository.save(details);
    }

    public List<PhotoBankDetails> getPhotoBankDetails(Long photoBankId) {
        PhotoBank photoBank = photoBankRepository.findById(photoBankId)
                .orElseThrow(() -> new EntityNotFoundException("PhotoBank не найден"));

        return photoBankDetailsRepository.findByPhotoBank(photoBank);
    }

    public PhotoBankDetails updatePhotoBankDetails(Long id, String title, MultipartFile file) throws IOException {
        PhotoBankDetails details = photoBankDetailsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PhotoBankDetails не найден"));

        if (title != null && !title.isEmpty()) {
            details.setTitle(title);
        }

        if (file != null && !file.isEmpty()) {
            Path oldFilePath = Paths.get(UPLOAD_DIR + details.getPhotoUrl());
            Files.deleteIfExists(oldFilePath);

            String newFilename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path newFilePath = Paths.get(UPLOAD_DIR + newFilename);
            Files.copy(file.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);

            details.setPhotoUrl(newFilename);
        }

        return photoBankDetailsRepository.save(details);
    }

    public void deletePhotoBankDetails(Long id) throws IOException {
        PhotoBankDetails details = photoBankDetailsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PhotoBankDetails не найден"));

        Path filePath = Paths.get(UPLOAD_DIR + details.getPhotoUrl());
        Files.deleteIfExists(filePath);

        photoBankDetailsRepository.delete(details);
    }

    public Optional<PhotoBankDetails> getPhotoBankDetailsById(Long id) {
        return photoBankDetailsRepository.findById(id);
    }
}
