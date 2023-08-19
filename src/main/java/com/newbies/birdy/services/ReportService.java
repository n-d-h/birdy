package com.newbies.birdy.services;

import com.newbies.birdy.dto.ReportDTO;

import java.util.List;

public interface ReportService {

    Boolean addReport(Integer userId, Integer productId, String reason);

    List<ReportDTO> listReportByUser(Integer userId, Boolean status);

    List<ReportDTO> listReportByProduct(Integer productId, Boolean status);

    Boolean deleteReport(Integer userId, Integer productId);

    ReportDTO getReport(Integer userId, Integer productId);

    Boolean deleteReportByProduct(Integer productId);
}
