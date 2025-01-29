package com.alien.security.service;

import com.alien.security.entity.FieldActivity;
import com.alien.security.entity.SocialNetwork;
import com.alien.security.entity.UserModel;
import com.alien.security.repo.FieldActivityRepository;
import com.alien.security.repo.SocialNetworkRepository;
import com.alien.security.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class FieldActivityService {

    @Autowired
    private FieldActivityRepository fieldActivityRepository;

    @Autowired
    private UserRepo userRepository;

    @Transactional
    public FieldActivity createFieldActivity(FieldActivity fieldActivity) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        fieldActivity.setUser(user);
        return fieldActivityRepository.save(fieldActivity);
    }

    @Transactional
    public FieldActivity updateFieldActivity(Long id, FieldActivity updatedFieldActivity) {
        FieldActivity existingFieldActivity = fieldActivityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Social Network not found"));

        existingFieldActivity.setStatus(updatedFieldActivity.getStatus());
        existingFieldActivity.setPlaceOfWorkOrStudy(updatedFieldActivity.getPlaceOfWorkOrStudy());
        existingFieldActivity.setPost(updatedFieldActivity.getPost());
        existingFieldActivity.setPlaceOfWorkOrStudyInEnglish(updatedFieldActivity.getPlaceOfWorkOrStudyInEnglish());
        existingFieldActivity.setThePositionEnglish(updatedFieldActivity.getThePositionEnglish());
        existingFieldActivity.setEnglishLanguageProficiencyLevel(updatedFieldActivity.getEnglishLanguageProficiencyLevel());


        return fieldActivityRepository.save(existingFieldActivity);
    }

    public List<FieldActivity> getFieldActivityForCurrentUser() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return fieldActivityRepository.findByUser(user);
    }

    @Transactional
    public void deleteFieldActivity(Long id) {
        FieldActivity fieldActivity = fieldActivityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Social Network not found"));
        fieldActivityRepository.delete(fieldActivity);
    }

    public FieldActivity getFieldActivityById(Long id) {
        return fieldActivityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Social Network not found"));
    }

    public List<FieldActivity> getFieldActivityByUserId(int userId) {
        Optional<UserModel> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("Пользователь с id " + userId + " не найден");
        }

        List<FieldActivity> fieldActivityList = fieldActivityRepository.findAllByUserId(userId);

        if (fieldActivityList.isEmpty()) {
            throw new RuntimeException("У пользователя с id " + userId + " нет записей");
        }

        return fieldActivityList;
    }
}