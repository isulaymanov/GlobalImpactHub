package com.alien.security.controller;

import com.alien.security.entity.UserNames;
import com.alien.security.service.UserNamesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usernameslang")
public class UserNamesController {

    @Autowired
    private UserNamesService userNamesService;

    @PostMapping
    public ResponseEntity<UserNames> createUserNames(@RequestBody UserNames userNames) {
        UserNames createdUserNames = userNamesService.createUserNames(userNames);
        return ResponseEntity.ok(createdUserNames);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserNames> updateUserNames(@PathVariable Long id, @RequestBody UserNames userNames) {
        UserNames updatedUserNames = userNamesService.updateUserNames(id, userNames);
        return ResponseEntity.ok(updatedUserNames);
    }

    @GetMapping("/current")
    public ResponseEntity<List<UserNames>> getUserNamesForCurrentUser() {
        List<UserNames> userNames = userNamesService.getUserNamesForCurrentUser();
        return ResponseEntity.ok(userNames);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserNames(@PathVariable Long id) {
        userNamesService.deleteUserNames(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserNames> getUserNamesById(@PathVariable Long id) {
        UserNames userNames = userNamesService.getUserNamesById(id);
        return ResponseEntity.ok(userNames);
    }

//    @GetMapping("/all/{userId}/admin")
//    public ResponseEntity<List<UserNames>> getUserNamesByUserId(@PathVariable int userId) {
//        List<UserNames> userNamesList = userNamesService.getUserNamesByUserId(userId);
//        return ResponseEntity.ok(userNamesList);
//    }
    @GetMapping("/all/{userId}/admin")
    public ResponseEntity<?> getUserNamesByUserId(@PathVariable int userId) {
        try {
            List<UserNames> userNamesList = userNamesService.getUserNamesByUserId(userId);
            return ResponseEntity.ok(userNamesList);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, ex.getMessage()));
        }
    }

    // DTO для возврата ошибок
    public record ErrorResponse(int status, String message) {}
}
