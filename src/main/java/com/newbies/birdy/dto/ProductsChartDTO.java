package com.newbies.birdy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductsChartDTO {
    private List<Long> chartData;
    private Long allInYear;
    private Long allBirds;
    private Long allAccessories;
    private Long allFood;

}
