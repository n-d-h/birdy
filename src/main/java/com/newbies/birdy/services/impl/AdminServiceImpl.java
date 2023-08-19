package com.newbies.birdy.services.impl;

import com.newbies.birdy.dto.*;
import com.newbies.birdy.entities.OrderState;
import com.newbies.birdy.entities.Product;
import com.newbies.birdy.entities.ProductImage;
import com.newbies.birdy.mapper.ProductMapper;
import com.newbies.birdy.repositories.*;
import com.newbies.birdy.services.AdminService;
import com.newbies.birdy.services.FirebaseStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Transactional
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final FirebaseStorageService firebaseStorageService;
    private final ReportRepository reportRepository;

    @Value("${admin.username}")
    private String userName;

    @Value("${admin.password}")
    private String password;

    @Override
    public Map<List<ProductDTO>, Integer> getAllProductsForAdmin(String search, Pageable pageable) {
        Map<List<ProductDTO>, Integer> pair = new HashMap<>();
        Page<Product> pageList = productRepository.findByProductNameContainingIgnoreCaseAndStateAndStatus(search, 0, true, pageable);
        pair.put(pageList.stream().map(ProductMapper.INSTANCE::toDTO).toList(), pageList.getTotalPages());
        return pair;
    }

    @Override
    public Integer approveProduct(Integer id) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setState(1);
        return productRepository.save(product).getId();
    }

    @Override
    public Boolean declineProduct(Integer id) {
        try {
            Product product = productRepository.findById(id).orElseThrow();
            List<ProductImage> images = productImageRepository.findByProductImgAndStatus(product, true);
            for (ProductImage image : images) {
                firebaseStorageService.deleteFile(image.getImgUrl());
                productImageRepository.delete(image);
            }
            productRepository.delete(product);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Integer> getAllYearsForAdminChart() {
        return orderRepository.getAllYearsForAdminChart();
    }

    @Override
    public AdminChartDTO getAdminChart(Integer year) {
        AdminChartDTO adminChartDTO = new AdminChartDTO();
        List<Long> orders = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            LocalDate localDate = LocalDate.of(year, month, 1);
            LocalDate endLocalDate = localDate.withDayOfMonth(localDate.lengthOfMonth());

            Date startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(endLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

            long countOrders = orderRepository.countByCreateDateBetweenAndStatus(startDate, endDate, true);
            orders.add(countOrders);

            Double sumRevenues = orderDetailRepository.calculateFormattedTotalSumByMonthAndYear(month, year);
            revenues.add(sumRevenues);
        }
        adminChartDTO.setTotalUsers(userRepository.countByStatus(true));
        adminChartDTO.setTotalActiveProducts(productRepository.countByStateAndStatus(1, true));
        adminChartDTO.setTotalProductRequests(productRepository.countByStateAndStatus(0, true));
        adminChartDTO.setTotalShop(shopRepository.countByStatus(true));
        adminChartDTO.setDataOrders(orders);
        adminChartDTO.setDataRevenue(revenues);
        return adminChartDTO;
    }

    @Override
    public AdminIncomeDTO getAdminIncomeDefault() {
        Map<String, Date> map = orderRepository.getMinMaxDatesForAdminChart();
        Date startDate = map.get("firstDate") == null ? new Date() : map.get("firstDate");
        Date endDate = map.get("lastDate");

        // Format the dates to yyyy-MM-dd format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String firstDate = sdf.format(startDate);
        String lastDate = sdf.format(endDate);

        // Create a Calendar instance
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate == null ? new Date() : endDate);

        // Add one day to end date
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        // Get the updated end date
        Date updatedEnd = calendar.getTime();

        Long countDoneOrders = orderRepository.countByCreateDateBetweenAndStateAndStatus(startDate, updatedEnd, OrderState.DONE, true);
        Double totalIncome = orderDetailRepository.calculateTotalAdminIncomeBetweenDates(startDate, updatedEnd);

        AdminIncomeDTO adminIncomeDTO = new AdminIncomeDTO();
        adminIncomeDTO.setTotalDoneOrders(countDoneOrders);
        adminIncomeDTO.setTotalIncome(totalIncome);
        adminIncomeDTO.setTotalShopsIncome(BigDecimal.valueOf(totalIncome * 0.8).setScale(2, RoundingMode.HALF_UP).doubleValue());
        adminIncomeDTO.setTotalPlatformIncome(BigDecimal.valueOf(totalIncome * 0.2).setScale(2, RoundingMode.HALF_UP).doubleValue());
        adminIncomeDTO.setStartDate(firstDate);
        adminIncomeDTO.setEndDate(lastDate);

        return adminIncomeDTO;
    }

    @Override
    public AdminIncomeDTO getAdminIncome(String start, String end) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        AdminIncomeDTO adminIncomeDTO = new AdminIncomeDTO();
        try {
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);

            // Create a Calendar instance
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);

            // Add one day to end date
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            // Get the updated end date
            Date updatedEnd = calendar.getTime();

            Long countDoneOrders = orderRepository.countByCreateDateBetweenAndStateAndStatus(startDate, updatedEnd, OrderState.DONE, true);
            Double totalIncome = orderDetailRepository.calculateTotalAdminIncomeBetweenDates(startDate, updatedEnd);

            adminIncomeDTO.setTotalDoneOrders(countDoneOrders);
            adminIncomeDTO.setTotalIncome(totalIncome);
            adminIncomeDTO.setTotalShopsIncome(BigDecimal.valueOf(totalIncome * 0.8).setScale(2, RoundingMode.HALF_UP).doubleValue());
            adminIncomeDTO.setTotalPlatformIncome(BigDecimal.valueOf(totalIncome * 0.2).setScale(2, RoundingMode.HALF_UP).doubleValue());
            adminIncomeDTO.setStartDate(start);
            adminIncomeDTO.setEndDate(end);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return adminIncomeDTO;
    }

    @Override
    public List<ReportProductDTO> getAllReportsProducts() {
        List<ReportProductDTO> list = new ArrayList<>();

        List<Object[]> result = reportRepository.countReportsByProductIdWithCounted();
        if (result.isEmpty()) {
            return list;
        }
        for (Object[] row : result) {
            Integer productId = (Integer) row[0];
            Long count = (Long) row[1];

            ReportProductDTO reportProductDTO = new ReportProductDTO();
            Product product = productRepository.findById(productId).orElseThrow();
            reportProductDTO.setProduct(ProductMapper.INSTANCE.toDTO(product));
            reportProductDTO.setReportCount(count);
            list.add(reportProductDTO);
        }
        return list;
    }

    @Override
    public ReportDetailDTO getReportProductById(Integer id) {
        ReportDetailDTO reportDetailDTO = new ReportDetailDTO();
        reportDetailDTO.setProduct(ProductMapper.INSTANCE.toDTO(productRepository.findById(id).orElseThrow()));

        List<ReportReasonDTO> list = new ArrayList<>();

        List<Object[]> result = reportRepository.countReportsByProductIdWithDistinctReasonsSorted(id);
        if (result.isEmpty()) {
            return null;
        }

        for (Object[] row : result) {
            String reason = (String) row[1];
            Long count = (Long) row[2];

            ReportReasonDTO reportReasonDTO = new ReportReasonDTO();
            reportReasonDTO.setReason(reason);
            reportReasonDTO.setReasonCount(count);
            list.add(reportReasonDTO);
        }
        reportDetailDTO.setReport(list);
        return reportDetailDTO;
    }

    @Override
    public List<Object[]> getReportByProductId(Integer id) {
        return reportRepository.countReportsByProductIdWithDistinctReasonsSorted(id);
    }

    @Override
    public Boolean signIn(AdminSignInDTO adminSignInDTO) {
        return adminSignInDTO.getUsername().equals(userName) && adminSignInDTO.getPassword().equals(password);
    }
}
