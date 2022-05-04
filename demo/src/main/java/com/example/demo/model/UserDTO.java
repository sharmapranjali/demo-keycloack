package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private int statusCode;
    private String status;

}
