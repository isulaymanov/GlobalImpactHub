package com.alien.security.controller;

import com.alien.security.entity.FieldActivity;
import com.alien.security.entity.SocialNetwork;
import com.alien.security.service.FieldActivityService;
import com.alien.security.service.SocialNetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/field-activity")
public class FieldActivityController {

    @Autowired
    private FieldActivityService fieldActivityService;

    @PostMapping
    public ResponseEntity<FieldActivity> createFieldActivity(@RequestBody FieldActivity fieldActivity) {
        FieldActivity createdFieldActivity = fieldActivityService.createFieldActivity(fieldActivity);
        return ResponseEntity.ok(createdFieldActivity);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FieldActivity> updateFieldActivity(@PathVariable Long id, @RequestBody FieldActivity fieldActivity) {
        FieldActivity updatedFieldActivity = fieldActivityService.updateFieldActivity(id, fieldActivity);
        return ResponseEntity.ok(updatedFieldActivity);
    }

    @GetMapping("/current")
    public ResponseEntity<List<FieldActivity>> getFieldActivityForCurrentUser() {
        List<FieldActivity> fieldActivities = fieldActivityService.getFieldActivityForCurrentUser();
        return ResponseEntity.ok(fieldActivities);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFieldActivity(@PathVariable Long id) {
        fieldActivityService.deleteFieldActivity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FieldActivity> getFieldActivityById(@PathVariable Long id) {
        FieldActivity fieldActivity = fieldActivityService.getFieldActivityById(id);
        return ResponseEntity.ok(fieldActivity);
    }

    @GetMapping("/all/{userId}/admin")
    public ResponseEntity<?> getFieldActivityByUserId(@PathVariable int userId) {
        try {
            List<FieldActivity> fieldActivityList = fieldActivityService.getFieldActivityByUserId(userId);
            return ResponseEntity.ok(fieldActivityList);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, ex.getMessage()));
        }
    }

    public record ErrorResponse(int status, String message) {}
}
