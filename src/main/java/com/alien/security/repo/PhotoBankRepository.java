package com.alien.security.repo;

import com.alien.security.entity.PhotoBank;
import com.alien.security.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoBankRepository extends JpaRepository<PhotoBank, Long> {
    List<PhotoBank> findByUser(UserModel user);
    Optional<PhotoBank> findByIdAndUser(Long id, UserModel user);
}
