package com.newbies.birdy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Integer id;

    private String fullName;

    private String email;

    private Date dob;

    private Date createDate;

    private Integer gender;

    private String avatarUrl;

    private Integer accountId;

    private String phoneNumber;

    private Double balance;


}
