package com.alien.security.controller;

import com.alien.security.entity.UserLanguages;
import com.alien.security.service.UserLanguagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/user-languages")
public class UserLanguagesController {

    @Autowired
    private UserLanguagesService userLanguagesService;

    @PostMapping
    public ResponseEntity<UserLanguages> createSocialNetwork(@RequestBody UserLanguages userLanguages) {
        UserLanguages createdUserLanguages = userLanguagesService.createUserLanguages(userLanguages);
        return ResponseEntity.ok(createdUserLanguages);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserLanguages> updateUserLanguages(@PathVariable Long id, @RequestBody UserLanguages userLanguages) {
        UserLanguages updatedUserLanguages = userLanguagesService.updateUserLanguages(id, userLanguages);
        return ResponseEntity.ok(updatedUserLanguages);
    }

    @GetMapping("/current")
    public ResponseEntity<List<UserLanguages>> getUserLanguagesForCurrentUser() {
        List<UserLanguages> userLanguages = userLanguagesService.getUserLanguagesForCurrentUser();
        return ResponseEntity.ok(userLanguages);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserLanguages(@PathVariable Long id) {
        userLanguagesService.deleteUserLanguages(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserLanguages> getUserLanguagesById(@PathVariable Long id) {
        UserLanguages userLanguages = userLanguagesService.getUserLanguagesById(id);
        return ResponseEntity.ok(userLanguages);
    }

    @GetMapping("/all/{userId}/admin")
    public ResponseEntity<?> getUserLanguagesByUserId(@PathVariable int userId) {
        try {
            List<UserLanguages> userLanguagesList = userLanguagesService.getUserLanguagesByUserId(userId);
            return ResponseEntity.ok(userLanguagesList);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, ex.getMessage()));
        }
    }

    public record ErrorResponse(int status, String message) {}
}
