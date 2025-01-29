package com.alien.security.controller;

import com.alien.security.entity.Event;
import com.alien.security.entity.UserModel;
import com.alien.security.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/event")
public class EventController {
    @Autowired
    private EventService eventService;

    @PostMapping("/create")
    public Event createEvent(@RequestBody Event event, @AuthenticationPrincipal UserModel user) {
        return eventService.createEvent(event, user);
    }

    @GetMapping("/currentuser")
    public ResponseEntity<?> getEventForCurrentUser(@AuthenticationPrincipal UserModel currentUser) {
        List<Event> events = eventService.getEventsByUser(currentUser);

        if (events == null || events.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "У вас нет записей"));
        }

        return ResponseEntity.ok(events);
    }


    @GetMapping("/all")
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();

        if (events.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(events);
    }


    @PatchMapping("/edit/{id}")
    public ResponseEntity<String> updateEvent(
            @PathVariable("id") Long id,
            @RequestBody Event updatedEvent,
            @AuthenticationPrincipal UserModel user) {
        try {
            Event updated = eventService.updateEvent(id, updatedEvent, user);
            if (updated != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Event Updated Successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found or not allowed to update");
            }
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public void deleteEvent(@PathVariable Long id, @AuthenticationPrincipal UserModel user) {
        eventService.deleteEvent(id, user);
    }


    @PostMapping("/uploadPhoto/{id}")
    public ResponseEntity<?> uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal UserModel user) {
        try {
            Optional<Event> eventOptional = eventService.getEventById(id);
            if (eventOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Event event = eventOptional.get();
            String photoUrl = eventService.saveEventPhoto(file);
            event.setPhotoUrl(photoUrl);
            eventService.updateEvent(id, event, user);

            return ResponseEntity.ok("Фото загружена: " + photoUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Ошибка загрузки фото");
        }
    }

    @GetMapping("/getPhoto/{id}")
    public ResponseEntity<Resource> getPhoto(@PathVariable Long id) {
        Optional<Event> eventOptional = eventService.getEventById(id);
        if (eventOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = eventOptional.get();
        String photoUrl = event.getPhotoUrl();

        if (photoUrl == null || photoUrl.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path path = Paths.get("D:/uploadsEvent").resolve(photoUrl.substring(photoUrl.lastIndexOf("/") + 1));  // Восстанавливаем путь до файла
            Resource resource = new FileSystemResource(path);

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{id}")
    public Event EventById(@PathVariable Long id) {
        return eventService.EventsById(id);
    }

}
