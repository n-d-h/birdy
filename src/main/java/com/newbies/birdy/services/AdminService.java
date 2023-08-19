package com.newbies.birdy.services;

import com.newbies.birdy.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface AdminService {
    Map<List<ProductDTO>, Integer> getAllProductsForAdmin(String search, Pageable pageable);

    Integer approveProduct(Integer id);

    Boolean declineProduct(Integer id);
    List<Integer> getAllYearsForAdminChart();
    AdminChartDTO getAdminChart(Integer year);

    AdminIncomeDTO getAdminIncomeDefault();
    AdminIncomeDTO getAdminIncome(String start, String end);

    List<ReportProductDTO> getAllReportsProducts();

    ReportDetailDTO getReportProductById(Integer id);

    List<Object[]> getReportByProductId(Integer id);

    Boolean signIn(AdminSignInDTO adminSignInDTO);
}
