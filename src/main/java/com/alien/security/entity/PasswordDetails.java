package com.alien.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "password_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String typeDocument;
    private String series;
    private String number;
    private String whoIssuedDocument;
    private String dateOfIssue;
    private String validityPeriod;
    private String citizenship;
    private String placeOfBirth;
    private String countryRegionOfResidence;
    private String cityOfResidence;
    private String registrationAddress;
    private String addressOfPlaceOfResidence;
    private String passportScanUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserModel user;
}