package com.alien.security.service;
import com.alien.security.entity.SocialNetwork;
import com.alien.security.entity.UserModel;
import com.alien.security.entity.UserNames;
import com.alien.security.repo.SocialNetworkRepository;
import com.alien.security.repo.UserNamesRepository;
import com.alien.security.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SocialNetworkService {

    @Autowired
    private SocialNetworkRepository socialNetworkRepository;

    @Autowired
    private UserRepo userRepository;

    @Transactional
    public SocialNetwork createSocialNetwork(SocialNetwork socialNetwork) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        socialNetwork.setUser(user);
        return socialNetworkRepository.save(socialNetwork);
    }

    @Transactional
    public SocialNetwork updateSocialNetwork(Long id, SocialNetwork updatedSocialNetwork) {
        SocialNetwork existingSocialNewtwork = socialNetworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Social Network not found"));

        existingSocialNewtwork.setVkUrl(updatedSocialNetwork.getVkUrl());
        existingSocialNewtwork.setInstagramUrl(updatedSocialNetwork.getInstagramUrl());
        existingSocialNewtwork.setTelegramUrl(updatedSocialNetwork.getTelegramUrl());
        existingSocialNewtwork.setFacebook(updatedSocialNetwork.getFacebook());

        return socialNetworkRepository.save(existingSocialNewtwork);
    }

    public List<SocialNetwork> getSocialNetworkForCurrentUser() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel user = userRepository.findByUsername(currentUsername);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return socialNetworkRepository.findByUser(user);
    }

    @Transactional
    public void deleteSocialNetwork(Long id) {
        SocialNetwork socialNetwork = socialNetworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Social Network not found"));
        socialNetworkRepository.delete(socialNetwork);
    }

    public SocialNetwork getSocialNetworkById(Long id) {
        return socialNetworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Social Network not found"));
    }

    public List<SocialNetwork> getSocialNetworkByUserId(int userId) {
        Optional<UserModel> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new RuntimeException("Пользователь с id " + userId + " не найден");
        }

        List<SocialNetwork> socialNetworkList = socialNetworkRepository.findAllByUserId(userId);

        if (socialNetworkList.isEmpty()) {
            throw new RuntimeException("У пользователя с id " + userId + " нет записей");
        }

        return socialNetworkList;
    }
}
