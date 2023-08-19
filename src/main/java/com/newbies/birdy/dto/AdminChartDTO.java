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
public class AdminChartDTO {
    private Long totalUsers;
    private Long totalActiveProducts;
    private Long totalProductRequests;
    private Long totalShop;
    private List<Long> dataOrders;
    private List<Double> dataRevenue;
}
