package com.newbies.birdy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateRequestDTO {

    private List<OrderDTO> orderList;
    private List<OrderDetailDTO> orderDetailList;
    private Integer addressId;
}
