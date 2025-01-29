package com.alien.security.service;

import com.alien.security.entity.Application;
import com.alien.security.repo.ApplicationRepository;
import com.alien.security.repo.EventRepository;
import com.alien.security.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alien.security.entity.UserModel;
import com.alien.security.entity.Event;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Transactional
    public String submitApplication(UserModel user, Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);

        if (event == null) {
            return "Событие не найдено";
        }

        if (user.getRole().equals("ADMIN")) {
            return "Вы админ, вам нельзя подавать заявку";
        }

        Application application = new Application();
        application.setUser(user);
        application.setEvent(event);
        application.setStatus("pending");
        application.setCreatedAt(String.valueOf(System.currentTimeMillis()));

        String applicationNumber = "APP-" + System.currentTimeMillis();
        application.setApplicationNumber(applicationNumber);

        applicationRepository.save(application);

        return "Заявка успешно подана";
    }



    @Transactional
    public String cancelApplication(Long applicationId, UserModel user) {
        Application application = applicationRepository.findById(applicationId).orElse(null);

        if (application == null) {
            return "Заявка не найдена";
        }

        if (!application.getUser().equals(user)) {
            return "Вы не можете отменить чужую заявку";
        }

        applicationRepository.delete(application);
        return "Заявка успешно отменена";
    }


    @Transactional
    public String processApplication(Long applicationId, String status, String rejectionReason) {
        Application application = applicationRepository.findById(applicationId).orElse(null);
        if (application == null) {
            return "Заявка не найдена";
        }

        if (!status.equals("approved") && !status.equals("rejected")) {
            return "Неверный статус заявки";
        }

        application.setStatus(status);
        if ("rejected".equals(status)) {
            application.setRejectionReason(rejectionReason);
        }
        application.setUpdatedAt(String.valueOf(System.currentTimeMillis()));

        applicationRepository.save(application);

        return "Заявка обработана";
    }

    @Transactional
    public List<Application> getAllApplicationsForEvent(Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return new ArrayList<>();
        }
        return applicationRepository.findByEvent(event);
    }

    @Transactional
    public List<Application> getUserApplications(UserModel user) {
        return applicationRepository.findByUser(user);
    }

}
