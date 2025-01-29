package com.alien.security.repo;

import com.alien.security.entity.UserLanguages;
import com.alien.security.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLanguagesRepository extends JpaRepository<UserLanguages, Long> {
    List<UserLanguages> findByUser(UserModel user);
    List<UserLanguages> findAllByUserId(int userId);
}
