package com.newbies.birdy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

    private Integer id;

    private String productName;

    private String imageMain;

    private Double unitPrice;

    private int salePtc;

    private Integer quantity;

    private Integer rating;

    private Date createDate;

    private String species;

    private String age;

    private Integer gender;

    private String color;

    private Date expDate;

    private String madeIn;

    private Double weight;

    private String size;

    private String material;

    private String description;

    private String brandName;

    private Integer state;

    private Integer categoryId;

    private String categoryName;

    private Integer shopId;

    private String shopName;

    private Boolean isWarned;

    private Boolean isDisabled;

    private Boolean isBanned;

    private Long totalRating;

    private Boolean status;
}
