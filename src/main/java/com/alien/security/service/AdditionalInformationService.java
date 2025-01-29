package com.alien.security.service;

import com.alien.security.entity.AdditionalInformation;
import com.alien.security.entity.UserModel;
import com.alien.security.repo.AdditionalInformationRepository;
import com.alien.security.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdditionalInformationService {

    @Autowired
    private AdditionalInformationRepository additionalInformationRepository;

    @Autowired
    private UserRepo userRepository;

    @Transactional
    public AdditionalInformation createAdditionalInformation(AdditionalInformation additionalInformation) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        additionalInformation.setUser(user);
        return additionalInformationRepository.save(additionalInformation);
    }

    @Transactional
    public AdditionalInformation updateAdditionalInformation(Long id, AdditionalInformation updatedAdditionalInformation) {
        AdditionalInformation existingAdditionalInformation = additionalInformationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Social Network not found"));

        existingAdditionalInformation.setDirection(updatedAdditionalInformation.getDirection());
        existingAdditionalInformation.setThePresenceOfHealthRestrictions(updatedAdditionalInformation.getThePresenceOfHealthRestrictions());
        existingAdditionalInformation.setTypeOfFood(updatedAdditionalInformation.getTypeOfFood());
        existingAdditionalInformation.setClothingSize(updatedAdditionalInformation.getClothingSize());
        existingAdditionalInformation.setMemberOfTheMcd(updatedAdditionalInformation.getMemberOfTheMcd());
        existingAdditionalInformation.setWantBecomeMemberOfMcd(updatedAdditionalInformation.getWantBecomeMemberOfMcd());


        return additionalInformationRepository.save(existingAdditionalInformation);
    }

    public List<AdditionalInformation> getAdditionalInformationForCurrentUser() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new RuntimeException("Additional Information not found");
        }

        return additionalInformationRepository.findByUser(user);
    }

    @Transactional
    public void deleteAdditionalInformation(Long id) {
        AdditionalInformation additionalInformation = additionalInformationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Additional Information not found"));
        additionalInformationRepository.delete(additionalInformation);
    }

    public AdditionalInformation getAdditionalInformationById(Long id) {
        return additionalInformationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Additional Information not found"));
    }

    public List<AdditionalInformation> getAdditionalInformationByUserId(int userId) {
        Optional<UserModel> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("Пользователь с id " + userId + " не найден");
        }

        List<AdditionalInformation> additionalInformationList = additionalInformationRepository.findAllByUserId(userId);

        if (additionalInformationList.isEmpty()) {
            throw new RuntimeException("У пользователя с id " + userId + " нет записей");
        }

        return additionalInformationList;
    }
}