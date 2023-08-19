package com.newbies.birdy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodDTO {

    private Integer id;

    private String paymentNumber;

    private Integer paymentTypeId;

    private String paymentTypeName;

    private Integer userId;

    private String fullName;
}
