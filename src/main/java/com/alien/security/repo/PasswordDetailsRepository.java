package com.alien.security.repo;

import com.alien.security.entity.PasswordDetails;
import com.alien.security.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordDetailsRepository extends JpaRepository<PasswordDetails, Long> {
    List<PasswordDetails> findByUser(UserModel user);
    List<PasswordDetails> findAllByUserId(int userId);


}