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
    private String name;
    private String lastName;
    private String middleName;
    private String username;
    private String role;


}
