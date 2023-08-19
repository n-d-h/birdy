package com.newbies.birdy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopIncomeDTO {
    Long totalDoneOrders;
    Double totalIncome;
    Double totalShopIncome;
    Double totalPlatformIncome;
    String firstDate;
    String lastDate;
}
