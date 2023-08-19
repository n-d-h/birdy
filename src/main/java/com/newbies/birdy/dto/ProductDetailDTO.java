package com.newbies.birdy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailDTO {

    private Integer id;

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

    private Integer productId;

    private String productName;


}
