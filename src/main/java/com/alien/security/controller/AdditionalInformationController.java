package com.alien.security.controller;

import com.alien.security.entity.AdditionalInformation;
import com.alien.security.entity.FieldActivity;
import com.alien.security.service.AdditionalInformationService;
import com.alien.security.service.FieldActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/additional-information")
public class AdditionalInformationController {

    @Autowired
    private AdditionalInformationService additionalInformationService;

    @PostMapping
    public ResponseEntity<AdditionalInformation> createAdditionalInformation(@RequestBody AdditionalInformation additionalInformation) {
        AdditionalInformation createdAdditionalInformation = additionalInformationService.createAdditionalInformation(additionalInformation);
        return ResponseEntity.ok(createdAdditionalInformation);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdditionalInformation> updateAdditionalInformation(@PathVariable Long id, @RequestBody AdditionalInformation additionalInformation) {
        AdditionalInformation updatedAdditionalInformation = additionalInformationService.updateAdditionalInformation(id, additionalInformation);
        return ResponseEntity.ok(updatedAdditionalInformation);
    }

    @GetMapping("/current")
    public ResponseEntity<List<AdditionalInformation>> getAdditionalInformationForCurrentUser() {
        List<AdditionalInformation> additionalInformations = additionalInformationService.getAdditionalInformationForCurrentUser();
        return ResponseEntity.ok(additionalInformations);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdditionalInformation(@PathVariable Long id) {
        additionalInformationService.deleteAdditionalInformation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdditionalInformation> getAdditionalInformationById(@PathVariable Long id) {
        AdditionalInformation additionalInformation = additionalInformationService.getAdditionalInformationById(id);
        return ResponseEntity.ok(additionalInformation);
    }

    @GetMapping("/all/{userId}/admin")
    public ResponseEntity<?> getAdditionalInformationByUserId(@PathVariable int userId) {
        try {
            List<AdditionalInformation> additionalInformationList = additionalInformationService.getAdditionalInformationByUserId(userId);
            return ResponseEntity.ok(additionalInformationList);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, ex.getMessage()));
        }
    }

    public record ErrorResponse(int status, String message) {}
}
