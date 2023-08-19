package com.newbies.birdy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserRequest {

    private String email;

    private String fullName;

    private Date dob;

    private Integer gender;

    private String phoneNumber;

    private String password;
}
