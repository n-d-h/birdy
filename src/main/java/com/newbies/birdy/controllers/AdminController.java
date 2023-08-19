package com.newbies.birdy.controllers;

import com.newbies.birdy.dto.*;
import com.newbies.birdy.services.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Admin API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    private final ProductService productService;

    private final ShopService shopService;

    private final EmailService emailService;

    private final ReportService reportService;

    @GetMapping("/products")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(name = "search") Optional<String> search,
            @RequestParam(name = "page") Optional<Integer> page
    ) {
        Pageable pageable = PageRequest.of(page.orElse(0), 8, Sort.by("id").ascending());
        Map<List<ProductDTO>, Integer> listMap = adminService.getAllProductsForAdmin(search.orElse(""), pageable);
        List<Object> list = new ArrayList<>();
        listMap.forEach((productDTOS, integer) -> {
            list.add(productDTOS);
            list.add(integer);
        });
        if (list.isEmpty()) {
            return new ResponseEntity<>("No product found!!!", HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(list);
        }
    }

    @GetMapping("/product/{product-id}/approve")
    public ResponseEntity<?> approveProduct(@PathVariable("product-id") Integer productId) {
        Integer id = adminService.approveProduct(productId);
        if (id == null) {
            return new ResponseEntity<>("Failed!!!", HttpStatus.CONFLICT);
        } else {
            ProductDTO productDTO = productService.getProductById(id);
            ShopDTO shopDTO = shopService.getShopById(productDTO.getShopId());
            String body = "Dear " + shopDTO.getShopName()
                    + ",\n\nYour product has been approved by admin."
                    + "\nThank you for using Birdy!\n\n"
                    + "Best regards,\nBirdy Team";
            emailService.sendSimpleEmail(
                    shopDTO.getEmail(),
                    "Product Approved",
                    body);
            return new ResponseEntity<>("Success!!!", HttpStatus.OK);
        }
    }

    @GetMapping("/product/{product-id}/decline")
    public ResponseEntity<?> declineProduct(@PathVariable("product-id") Integer productId) {
        ProductDTO productDTO = productService.getProductById(productId);
        ShopDTO shopDTO = shopService.getShopById(productDTO.getShopId());
        Boolean del = adminService.declineProduct(productId);
        if (Boolean.FALSE.equals(del)) {
            return new ResponseEntity<>("Failed!!!", HttpStatus.CONFLICT);
        } else {
            String body = "Dear " + shopDTO.getShopName()
                    + ",\n\nYour product has been rejected by admin. Please check your product and re-submit it."
                    + "\nThank you for using Birdy!\n\n"
                    + "Best regards,\nBirdy Team";
            emailService.sendSimpleEmail(
                    shopDTO.getEmail(),
                    "Product Rejected",
                    body);
            return new ResponseEntity<>("Success!!!", HttpStatus.OK);
        }
    }

    @PatchMapping("/product/{product-id}/warning")
    public ResponseEntity<?> warningProduct(@PathVariable("product-id") Integer productId) {
        ProductDTO productDTO = productService.getProductById(productId);
        productDTO.setIsWarned(Boolean.TRUE);
        productService.saveProduct(productDTO);

        ShopDTO shopDTO = shopService.getShopById(productDTO.getShopId());
        List<Object[]> reportDetails = adminService.getReportByProductId(productId);

        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Dear ").append(shopDTO.getShopName())
                .append(",\n\nThis is a warning mail from admin. The reports on your product so far have been increasing.")
                .append("\nProduct name: ").append(productDTO.getProductName())
                .append(" will be deleted if the reports keep increasing.")
                .append("\n\nReport details:");

        for (Object[] row : reportDetails) {
            String reportContent = (String) row[1];
            Long count = (Long) row[2];
            bodyBuilder.append("\n\t - ").append(reportContent).append(" (").append(count).append(" times)");
        }

        bodyBuilder.append("\n\nThank you for using Birdy!\n\nBest regards,\nBirdy Team");

        String body = bodyBuilder.toString();
        emailService.sendSimpleEmail(
                shopDTO.getEmail(),
                "Product Report Warning",
                body);
        productService.warningProduct(productDTO);
        return new ResponseEntity<>("Product warning successfully!!!", HttpStatus.OK);
    }


    @PatchMapping("/product/{product-id}/banned")
    public ResponseEntity<?> bannedProduct(@PathVariable("product-id") Integer productId) {
        ProductDTO productDTO = productService.getProductById(productId);
        ShopDTO shopDTO = shopService.getShopById(productDTO.getShopId());
        List<Object[]> reportDetails = adminService.getReportByProductId(productId);

        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("Dear ").append(shopDTO.getShopName())
                .append(",\n\nThis is a notification mail from admin.")
                .append("\nProduct name: ").append(productDTO.getProductName())
                .append(" is banned due to Birdy policy on report for shops and users.")
                .append("\n\nReport details:");

        for (Object[] row : reportDetails) {
            String reportContent = (String) row[1];
            Long count = (Long) row[2];
            bodyBuilder.append("\n\t - ").append(reportContent).append(" (").append(count).append(" times)");
        }

        bodyBuilder.append("\n\nThank you for using Birdy!\n\nBest regards,\nBirdy Team");

        String body = bodyBuilder.toString();
        emailService.sendSimpleEmail(
                shopDTO.getEmail(),
                "Product Report Notification",
                body);
        productService.bannedProduct(productDTO);
        return new ResponseEntity<>("Product banned successfully", HttpStatus.OK);
    }

    @GetMapping("/chart/all-years")
    public ResponseEntity<?> getAllYearsForChart() {
        List<Integer> list = adminService.getAllYearsForAdminChart();
        if (list.isEmpty()) {
            return new ResponseEntity<>("No data found!!!", HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(list);
        }
    }

    @GetMapping("/chart/data/{year}")
    public ResponseEntity<?> getDataForChart(@PathVariable("year") Integer year) {
        AdminChartDTO chart = adminService.getAdminChart(year);
        if (chart == null) {
            return new ResponseEntity<>("No data found!!!", HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(chart);
        }
    }

    @GetMapping("/income/default")
    public ResponseEntity<?> getIncomeDefault() {
        AdminIncomeDTO income = adminService.getAdminIncomeDefault();
        if (income == null) {
            return new ResponseEntity<>("No data found!!!", HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(income);
        }
    }

    @GetMapping("/income")
    public ResponseEntity<?> getIncome(@RequestParam(name = "start") String start,
                                       @RequestParam(name = "end") String end) {
        AdminIncomeDTO income = adminService.getAdminIncome(start, end);
        if (income == null) {
            return new ResponseEntity<>("No data found!!!", HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(income);
        }
    }

    @GetMapping("/reports")
    public ResponseEntity<?> getAllReportsProducts() {
        List<ReportProductDTO> list = adminService.getAllReportsProducts();
        if (list.isEmpty()) {
            return new ResponseEntity<>("No data found!!!", HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(list);
        }
    }

    @GetMapping("/report/{product-id}")
    public ResponseEntity<?> getReportProductById(@PathVariable("product-id") Integer id) {
        ReportDetailDTO report = adminService.getReportProductById(id);
        if (report == null) {
            return new ResponseEntity<>("No data found!!!", HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(report);
        }
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody AdminSignInDTO adminDTO) {
        Boolean signIn = adminService.signIn(adminDTO);
        if (Boolean.TRUE.equals(signIn)) {
            return new ResponseEntity<>("Sign in success!!!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Sign in failed!!!", HttpStatus.UNAUTHORIZED);
        }
    }

//    @GetMapping("/test")
//    public ResponseEntity<?> test() {
//        return ResponseEntity.ok(adminService.test());
//    }
}
