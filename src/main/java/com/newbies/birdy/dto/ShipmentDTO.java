package com.newbies.birdy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentDTO {

    private Integer id;

    private Double pricePerKm;

    private Integer shopId;

    private String shopName;

    private Integer shipmentTypeId;

    private String shipmentTypeName;
}
