package com.newbies.birdy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportProductDTO {
    private ProductDTO product;
    private Long reportCount;
}
