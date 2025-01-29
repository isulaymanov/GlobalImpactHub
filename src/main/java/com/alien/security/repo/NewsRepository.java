package com.alien.security.repo;

import com.alien.security.entity.News;
import com.alien.security.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByUser(UserModel user);
    Optional<News> findByIdAndUser(Long id, UserModel user);

}
