package com.alien.security.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "field_activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;
    private String placeOfWorkOrStudy;
    private String post;
    private String placeOfWorkOrStudyInEnglish;
    private String thePositionEnglish;
    private String englishLanguageProficiencyLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserModel user;
}