package com.newbies.birdy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopDTO {

    private Integer id;

    private String email;

    private String shopName;

    private String address;

    private String avatarUrl;

    private Date createDate;

    private Integer accountId;

    private String phoneNumber;

    private Integer totalProduct;

    private String rating;

    private Double balance;
}
