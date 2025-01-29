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
    private UserRepo userRepository; // Репозиторий для UserModel

    // Создание нового UserNames для текущего пользователя
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

    // Редактирование UserNames текущего пользователя
    @Transactional
    public UserNames updateUserNames(Long id, UserNames updatedUserNames) {
        UserNames existingUserNames = userNamesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserNames not found"));

        // Обновляем данные
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

    // Удаление UserNames для текущего пользователя
    @Transactional
    public void deleteUserNames(Long id) {
        UserNames userNames = userNamesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserNames not found"));
        userNamesRepository.delete(userNames);
    }

    // Получение UserNames по ID для администратора
    public UserNames getUserNamesById(Long id) {
        return userNamesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UserNames not found"));
    }

//    public List<UserNames> getUserNamesByUserId(int userId) {
//
//        return userNamesRepository.findAllByUserId(userId);
//    }
    public List<UserNames> getUserNamesByUserId(int userId) {
        // Проверяем, существует ли пользователь с таким ID
        Optional<UserModel> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("Пользователь с id " + userId + " не найден");
        }

        List<UserNames> userNamesList = userNamesRepository.findAllByUserId(userId);

        // Если у пользователя нет имен в `UserNames`, можно либо вернуть пустой массив, либо выбросить ошибку
        if (userNamesList.isEmpty()) {
            throw new RuntimeException("У пользователя с id " + userId + " нет записей");
        }

        return userNamesList;
    }
}
