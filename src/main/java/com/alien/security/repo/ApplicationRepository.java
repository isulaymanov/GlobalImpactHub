package com.alien.security.repo;

import com.alien.security.entity.Application;
import com.alien.security.entity.Event;
import com.alien.security.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUser(UserModel user);
    List<Application> findByEvent(Event event);
    Application findByApplicationNumber(String applicationNumber);
}