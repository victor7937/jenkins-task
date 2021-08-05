package com.epam.esm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    public UserDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
