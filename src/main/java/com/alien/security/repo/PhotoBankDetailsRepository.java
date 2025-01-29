package com.alien.security.repo;

import com.alien.security.entity.PhotoBank;
import com.alien.security.entity.PhotoBankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoBankDetailsRepository extends JpaRepository<PhotoBankDetails, Long> {
    List<PhotoBankDetails> findByPhotoBank(PhotoBank photoBank);

}

