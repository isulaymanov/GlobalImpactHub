package com.alien.security.repo;

import com.alien.security.entity.AdditionalInformation;
import com.alien.security.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdditionalInformationRepository extends JpaRepository<AdditionalInformation, Long> {
    List<AdditionalInformation> findByUser(UserModel user);
    List<AdditionalInformation> findAllByUserId(int userId);


}