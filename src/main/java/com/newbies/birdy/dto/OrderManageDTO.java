package com.newbies.birdy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderManageDTO {
    private Integer id;
    private String orderDate;
    private String customer;
    private String total;
    private String shipType;
    private String paymentMethod;
    private String paymentStatus;
    private String comment;
    private String state;
}
