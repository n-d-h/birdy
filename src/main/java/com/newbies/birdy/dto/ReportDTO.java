package com.newbies.birdy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {

    private Integer userId;
    private String fullName;
    private Integer productId;
    private String productName;
    private String reason;
    private Date createDate;
    private Boolean status;

}
