package com.alien.security.service;

import com.alien.security.entity.SocialNetwork;
import com.alien.security.entity.UserLanguages;
import com.alien.security.entity.UserModel;
import com.alien.security.repo.SocialNetworkRepository;
import com.alien.security.repo.UserLanguagesRepository;
import com.alien.security.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class UserLanguagesService {

    @Autowired
    private UserLanguagesRepository userLanguagesRepository;

    @Autowired
    private UserRepo userRepository;

    @Transactional
    public UserLanguages createUserLanguages(UserLanguages userLanguages) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new RuntimeException("User Languages not found");
        }

        userLanguages.setUser(user);
        return userLanguagesRepository.save(userLanguages);
    }

    @Transactional
    public UserLanguages updateUserLanguages(Long id, UserLanguages updatedUserLanguages) {
        UserLanguages existingUserLanguages = userLanguagesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Languages not found"));

        existingUserLanguages.setLanguage(updatedUserLanguages.getLanguage());

        return userLanguagesRepository.save(existingUserLanguages);
    }

    public List<UserLanguages> getUserLanguagesForCurrentUser() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new RuntimeException("User Languages not found");
        }

        return userLanguagesRepository.findByUser(user);
    }

    @Transactional
    public void deleteUserLanguages(Long id) {
        UserLanguages userLanguages = userLanguagesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Languages not found"));
        userLanguagesRepository.delete(userLanguages);
    }

    public UserLanguages getUserLanguagesById(Long id) {
        return userLanguagesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Languages not found"));
    }

    public List<UserLanguages> getUserLanguagesByUserId(int userId) {
        Optional<UserModel> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("Пользователь с id " + userId + " не найден");
        }

        List<UserLanguages> userLanguagesList = userLanguagesRepository.findAllByUserId(userId);

        if (userLanguagesList.isEmpty()) {
            throw new RuntimeException("У пользователя с id " + userId + " нет записей");
        }

        return userLanguagesList;
    }
}