package com.alien.security.service;

import com.alien.security.entity.Event;
import com.alien.security.entity.PasswordDetails;
import com.alien.security.entity.UserModel;
import com.alien.security.repo.PasswordDetailsRepository;
import com.alien.security.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class PasswordDetailsService {

    @Autowired
    private PasswordDetailsRepository passwordDetailsRepository;

    @Autowired
    private UserRepo userRepository;

    @Transactional
    public PasswordDetails createPasswordDetails(PasswordDetails passwordDetails) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        passwordDetails.setUser(user);
        return passwordDetailsRepository.save(passwordDetails);
    }

    @Transactional
    public PasswordDetails updatePasswordDetails(Long id, PasswordDetails updatedPasswordDetails) {
        PasswordDetails existingPasswordDetails = passwordDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Social Network not found"));



        existingPasswordDetails.setTypeDocument(updatedPasswordDetails.getTypeDocument());
        existingPasswordDetails.setSeries(updatedPasswordDetails.getSeries());
        existingPasswordDetails.setNumber(updatedPasswordDetails.getNumber());
        existingPasswordDetails.setWhoIssuedDocument(updatedPasswordDetails.getWhoIssuedDocument());
        existingPasswordDetails.setDateOfIssue(updatedPasswordDetails.getDateOfIssue());
        existingPasswordDetails.setValidityPeriod(updatedPasswordDetails.getValidityPeriod());
        existingPasswordDetails.setCitizenship(updatedPasswordDetails.getCitizenship());
        existingPasswordDetails.setPlaceOfBirth(updatedPasswordDetails.getPlaceOfBirth());
        existingPasswordDetails.setCountryRegionOfResidence(updatedPasswordDetails.getCountryRegionOfResidence());
        existingPasswordDetails.setCityOfResidence(updatedPasswordDetails.getCityOfResidence());
        existingPasswordDetails.setRegistrationAddress(updatedPasswordDetails.getRegistrationAddress());
        existingPasswordDetails.setAddressOfPlaceOfResidence(updatedPasswordDetails.getAddressOfPlaceOfResidence());
        existingPasswordDetails.setPassportScanUrl(updatedPasswordDetails.getPassportScanUrl());



        return passwordDetailsRepository.save(existingPasswordDetails);
    }

    public List<PasswordDetails> getPasswordDetailsForCurrentUser() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new RuntimeException("Additional Information not found");
        }

        return passwordDetailsRepository.findByUser(user);
    }

    @Transactional
    public void deletePasswordDetails(Long id) {
        PasswordDetails passwordDetails = passwordDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Password Details Information not found"));
        passwordDetailsRepository.delete(passwordDetails);
    }

    public PasswordDetails getPasswordDetailsById(Long id) {
        return passwordDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Password Details Information not found"));
    }

    public Optional<PasswordDetails> getPasswordDetailsOpById(Long id) {
        return passwordDetailsRepository.findById(id);
    }

    public List<PasswordDetails> getPasswordDetailsByUserId(int userId) {
        Optional<UserModel> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("Пользователь с id " + userId + " не найден");
        }

        List<PasswordDetails> passwordDetailsList = passwordDetailsRepository.findAllByUserId(userId);

        if (passwordDetailsList.isEmpty()) {
            throw new RuntimeException("У пользователя с id " + userId + " нет записей");
        }

        return passwordDetailsList;
    }


    public String savePasswordDetailsPhoto(MultipartFile file) throws IOException {
        String uploadDir = "D:/uploadsPasswordDetails";
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String filePath = Paths.get(uploadDir, fileName).toString();

        file.transferTo(new File(filePath));

        return "/uploadsPasswordDetails/" + fileName;
    }

}