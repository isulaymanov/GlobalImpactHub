package com.alien.security.controller;

import com.alien.security.entity.SocialNetwork;
import com.alien.security.entity.UserNames;
import com.alien.security.service.SocialNetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/social-network")
public class SocialNetworkController {

    @Autowired
    private SocialNetworkService socialNetworkService;

    @PostMapping
    public ResponseEntity<SocialNetwork> createSocialNetwork(@RequestBody SocialNetwork socialNetwork) {
        SocialNetwork createdSocialNetwork = socialNetworkService.createSocialNetwork(socialNetwork);
        return ResponseEntity.ok(createdSocialNetwork);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SocialNetwork> updateSocialNetwork(@PathVariable Long id, @RequestBody SocialNetwork socialNetwork) {
        SocialNetwork updatedSocialNetwork = socialNetworkService.updateSocialNetwork(id, socialNetwork);
        return ResponseEntity.ok(updatedSocialNetwork);
    }

    @GetMapping("/current")
    public ResponseEntity<List<SocialNetwork>> getSocialNetworkForCurrentUser() {
        List<SocialNetwork> socialNetworks = socialNetworkService.getSocialNetworkForCurrentUser();
        return ResponseEntity.ok(socialNetworks);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSocialNetwork(@PathVariable Long id) {
        socialNetworkService.deleteSocialNetwork(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SocialNetwork> getSocialNetworkById(@PathVariable Long id) {
        SocialNetwork socialNetwork = socialNetworkService.getSocialNetworkById(id);
        return ResponseEntity.ok(socialNetwork);
    }

    @GetMapping("/all/{userId}/admin")
    public ResponseEntity<?> getSocialNetworkByUserId(@PathVariable int userId) {
        try {
            List<SocialNetwork> socialNetworkList = socialNetworkService.getSocialNetworkByUserId(userId);
            return ResponseEntity.ok(socialNetworkList);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, ex.getMessage()));
        }
    }

    public record ErrorResponse(int status, String message) {}
}
