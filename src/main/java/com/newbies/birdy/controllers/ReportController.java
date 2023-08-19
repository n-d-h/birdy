package com.newbies.birdy.controllers;

import com.newbies.birdy.dto.ReportDTO;
import com.newbies.birdy.exceptions.ObjectException;
import com.newbies.birdy.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SecurityRequirement(name = "Authorization")
@Tag(name = "Report API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Get All Report by user Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Report not found!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "200", description = "Return list ReportlistDto")
    })
    @GetMapping("/user/{user-id}")
    public ResponseEntity<?> listAllWishlistByUser(@PathVariable(name = "user-id") Integer userId){
        List<ReportDTO> list = reportService.listReportByUser(userId, true);
        if(list.size() > 0){
            return ResponseEntity.ok(list);
        }else{
            return new ResponseEntity<>("Can't find any wishlist!", HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get All Report by product Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Report not found!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "200", description = "Return list ReportlistDto")
    })
    @GetMapping("/product/{product-id}")
    public ResponseEntity<?> listAllWishlistByProduct(@PathVariable(name = "product-id") Integer productId){
        List<ReportDTO> list = reportService.listReportByProduct(productId, true);
        if(list.size() > 0){
            return ResponseEntity.ok(list);
        }else{
            return new ResponseEntity<>("Can't find any wishlist!", HttpStatus.NOT_FOUND);
        }
    }


}
