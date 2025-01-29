package com.alien.security.service;

import com.alien.security.entity.Event;
import com.alien.security.entity.UserModel;
import com.alien.security.repo.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public Event createEvent(Event event, UserModel user) {
        event.setUser(user);
        return eventRepository.save(event);
    }

    public List<Event> getEventsByUser(UserModel user) {
        return eventRepository.findByUser(user);
    }

    public Event EventsById(Long id) {
        return eventRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Event not found with id: " + id));
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }
    public void deleteEvent(Long id, UserModel user) {
        Event event = eventRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Event not found or not owned by user"));
        eventRepository.delete(event);
    }


    @Transactional
    public Event updateEvent(Long id, Event updatedEvent, UserModel user) {
        Optional<Event> eventOptional = eventRepository.findByIdAndUser(id, user);
        if (eventOptional.isPresent()) {
            Event existingEvent = eventOptional.get();

            if (updatedEvent.getPhotoUrl() != null && !updatedEvent.getPhotoUrl().equals(existingEvent.getPhotoUrl())) {
                existingEvent.setPhotoUrl(updatedEvent.getPhotoUrl());
            }
            if (updatedEvent.getTitle() != null && !updatedEvent.getTitle().equals(existingEvent.getTitle())) {
                existingEvent.setTitle(updatedEvent.getTitle());
            }
            if (updatedEvent.getDateOfTheEvent() != null && !updatedEvent.getDateOfTheEvent().equals(existingEvent.getDateOfTheEvent())) {
                existingEvent.setDateOfTheEvent(updatedEvent.getDateOfTheEvent());
            }
            if (updatedEvent.getLocationOfTheEvent() != null && !updatedEvent.getLocationOfTheEvent().equals(existingEvent.getLocationOfTheEvent())) {
                existingEvent.setLocationOfTheEvent(updatedEvent.getLocationOfTheEvent());
            }
            if (updatedEvent.getDescription() != null && !updatedEvent.getDescription().equals(existingEvent.getDescription())) {
                existingEvent.setDescription(updatedEvent.getDescription());
            }
            if (updatedEvent.getRegistrationDeadline() != null && !updatedEvent.getRegistrationDeadline().equals(existingEvent.getRegistrationDeadline())) {
                existingEvent.setRegistrationDeadline(updatedEvent.getRegistrationDeadline());
            }

            if (updatedEvent.getAgeOfParticipants() != null && !updatedEvent.getAgeOfParticipants().equals(existingEvent.getAgeOfParticipants())) {
                existingEvent.setAgeOfParticipants(updatedEvent.getAgeOfParticipants());
            }

            return eventRepository.save(existingEvent);
        }
        return null;
    }


    public String saveEventPhoto(MultipartFile file) throws IOException {
        String uploadDir = "D:/uploadsEvent";
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String filePath = Paths.get(uploadDir, fileName).toString();

        file.transferTo(new File(filePath));

        return "/uploadsEvent/" + fileName;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }


}
