package com.newbies.birdy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminIncomeDTO {
    private String startDate;
    private String endDate;
    private Long totalDoneOrders;
    private Double totalIncome;
    private Double totalShopsIncome;
    private Double totalPlatformIncome;
}
