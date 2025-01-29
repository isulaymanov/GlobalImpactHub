package com.alien.security.repo;

import com.alien.security.entity.FieldActivity;
import com.alien.security.entity.SocialNetwork;
import com.alien.security.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldActivityRepository extends JpaRepository<FieldActivity, Long> {
    List<FieldActivity> findByUser(UserModel user);
    List<FieldActivity> findAllByUserId(int userId);


}