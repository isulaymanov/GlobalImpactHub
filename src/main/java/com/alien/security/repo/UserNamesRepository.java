package com.alien.security.repo;

import com.alien.security.entity.UserModel;
import com.alien.security.entity.UserNames;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserNamesRepository extends JpaRepository<UserNames, Long> {
    //Optional<UserNames> findByUser(UserModel user);
    List<UserNames> findByUser(UserModel user);
    List<UserNames> findAllByUserId(int userId);


}

