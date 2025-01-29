package com.alien.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserModelDTO {

    private int id;
    private String phone;
    private String email;
    private String dateOfBirth;
    private String photoUrl;
    private String gender;
    private String greeting;
    private String username;
    private String role;

}
