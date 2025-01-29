package com.alien.security.repo;

import com.alien.security.entity.SocialNetwork;
import com.alien.security.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocialNetworkRepository extends JpaRepository<SocialNetwork, Long> {
    List<SocialNetwork> findByUser(UserModel user);
    List<SocialNetwork> findAllByUserId(int userId);


}