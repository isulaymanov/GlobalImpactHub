package com.alien.security.service;

import com.alien.security.entity.PhotoBank;
import com.alien.security.entity.UserModel;
import com.alien.security.repo.PhotoBankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PhotoBankService {

    @Autowired
    private PhotoBankRepository photoBankRepository;

    public PhotoBank createPhotoBank(PhotoBank photoBank, UserModel user) {
        photoBank.setUser(user);
        return photoBankRepository.save(photoBank);
    }

    public List<PhotoBank> getPhotoBankByUser(UserModel user) {
        return photoBankRepository.findByUser(user);
    }

    public PhotoBank PhotoBankById(Long id) {
        return photoBankRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Photo Bank not found with id: " + id));
    }

    public Optional<PhotoBank> getPhotoBankById(Long id) {
        return photoBankRepository.findById(id);
    }
    public void deletePhotoBank(Long id, UserModel user) {
        PhotoBank photoBank = photoBankRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("PhotoBank not found or not owned by user"));
        photoBankRepository.delete(photoBank);
    }


    @Transactional
    public PhotoBank updatePhotoBank(Long id, PhotoBank updatedPhotoBank, UserModel user) {
        Optional<PhotoBank> photoBankOptional = photoBankRepository.findByIdAndUser(id, user);
        if (photoBankOptional.isPresent()) {
            PhotoBank existingPhotoBank = photoBankOptional.get();

            if (updatedPhotoBank.getTitle() != null && !updatedPhotoBank.getTitle().equals(existingPhotoBank.getTitle())) {
                existingPhotoBank.setTitle(updatedPhotoBank.getTitle());
            }
            if (updatedPhotoBank.getPhotoUrl() != null && !updatedPhotoBank.getPhotoUrl().equals(existingPhotoBank.getPhotoUrl())) {
                existingPhotoBank.setPhotoUrl(updatedPhotoBank.getPhotoUrl());
            }

            if (updatedPhotoBank.getCreated_at() != null && !updatedPhotoBank.getCreated_at().equals(existingPhotoBank.getCreated_at())) {
                existingPhotoBank.setCreated_at(updatedPhotoBank.getCreated_at());
            }

            return photoBankRepository.save(existingPhotoBank);
        }
        return null;
    }


    public String savePhotoBankPhoto(MultipartFile file) throws IOException {
        String uploadDir = "D:/uploadsPhotoBank";
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String filePath = Paths.get(uploadDir, fileName).toString();

        file.transferTo(new File(filePath));

        return "/uploadsPhotoBank/" + fileName;
    }

    public List<PhotoBank> getAllPhotoBank() {
        return photoBankRepository.findAll();
    }


}
