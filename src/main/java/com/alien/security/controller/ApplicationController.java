package com.alien.security.controller;

import com.alien.security.dto.ApplicationDTO;
import com.alien.security.entity.Application;
import com.alien.security.repo.ApplicationRepository;
import com.alien.security.repo.UserRepo;
import com.alien.security.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.stream.Collectors;

import com.alien.security.entity.UserModel;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationRepository applicationRepository;


    @Autowired
    private UserRepo userRepository;

    @PostMapping("/submit")
    public String submitApplication(@RequestParam Long eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();

        UserModel user = userRepository.findByUsername(username);

        if (user == null) {
            return "Пользователь не найден";
        }

        return applicationService.submitApplication(user, eventId);
    }

    @GetMapping("/application/{applicationNumber}")
    public Application getApplicationInfo(@PathVariable String applicationNumber) {
        Application application = applicationRepository.findByApplicationNumber(applicationNumber);

        if (application == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Заявка не найдена");
        }

        return application;
    }

    @DeleteMapping("/cancel")
    public String cancelApplication(@RequestParam Long applicationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();

        UserModel user = userRepository.findByUsername(username);

        if (user == null) {
            return "Пользователь не найден";
        }

        return applicationService.cancelApplication(applicationId, user);
    }

    @PatchMapping("/process")
    public String processApplication(
            @RequestParam Long applicationId,
            @RequestParam String status,
            @RequestParam(required = false) String rejectionReason) {
        return applicationService.processApplication(applicationId, status, rejectionReason);
    }

    @GetMapping("/event/{eventId}")
    public List<Application> getApplicationsForEvent(@PathVariable Long eventId) {
        return applicationService.getAllApplicationsForEvent(eventId);
    }

    @GetMapping("/currentuser")
    public List<ApplicationDTO> getUserApplications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();

        UserModel user = userRepository.findByUsername(username);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }

        List<Application> applications = applicationService.getUserApplications(user);

        return applications.stream()
                .map(ApplicationDTO::new)
                .collect(Collectors.toList());
    }


}
