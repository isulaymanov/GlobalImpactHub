package com.alien.security.service;

import com.alien.security.entity.UserModel;
import com.alien.security.entity.UserNames;
import com.alien.security.repo.UserNamesRepository;
import com.alien.security.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserNamesService {

    @Autowired
    private UserNamesRepository userNamesRepository;

    @Autowired
    private UserRepo userRepository;

    @Transactional
    public UserNames createUserNames(UserNames userNames) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        userNames.setUser(user);
        return userNamesRepository.save(userNames);
    }

    @Transactional
    public UserNames updateUserNames(Long id, UserNames updatedUserNames) {
        UserNames existingUserNames = userNamesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserNames not found"));

        existingUserNames.setLanguage(updatedUserNames.getLanguage());
        existingUserNames.setFirstname(updatedUserNames.getFirstname());
        existingUserNames.setLastname(updatedUserNames.getLastname());
        existingUserNames.setMiddlename(updatedUserNames.getMiddlename());

        return userNamesRepository.save(existingUserNames);
    }

    public List<UserNames> getUserNamesForCurrentUser() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return userNamesRepository.findByUser(user);
    }

    @Transactional
    public void deleteUserNames(Long id) {
        UserNames userNames = userNamesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserNames not found"));
        userNamesRepository.delete(userNames);
    }

    public UserNames getUserNamesById(Long id) {
        return userNamesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserNames not found"));
    }

    public List<UserNames> getUserNamesByUserId(int userId) {
        Optional<UserModel> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("Пользователь с id " + userId + " не найден");
        }

        List<UserNames> userNamesList = userNamesRepository.findAllByUserId(userId);

        if (userNamesList.isEmpty()) {
            throw new RuntimeException("У пользователя с id " + userId + " нет записей");
        }

        return userNamesList;
    }
}
