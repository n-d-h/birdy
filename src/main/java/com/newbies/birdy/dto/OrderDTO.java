package com.newbies.birdy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Integer id;

    private String code;

    private Date createDate;

    private Date updateDate;

    private String state;

    private String comment;

    private Integer shipmentId;

    private String shipmentTypeName;

    private Integer paymentMethodId;

    private String paymentTypeName;

    private Integer orderParentId;

    private String paymentStatus;

    private Integer addressId;

    private String address;

    private Integer shopId;

    private Double total;

}
