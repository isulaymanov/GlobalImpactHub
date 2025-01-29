package com.alien.security.repo;

import com.alien.security.entity.Event;
import com.alien.security.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUser(UserModel user);
    Optional<Event> findByIdAndUser(Long id, UserModel user);

}
