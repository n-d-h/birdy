package com.newbies.birdy.controllers;

import com.newbies.birdy.dto.ProductImageDTO;
import com.newbies.birdy.services.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Product Images API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/product-images")
public class ProductImageController {

    private final ProductImageService productImageService;

    @Operation(summary = "Get product image by product ID")
    @ApiResponses(value = {

    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductImagesByProductId(@Parameter(description = "Product ID", example = "1") @PathVariable("id") Integer id) {
        try {
            List<ProductImageDTO> list = productImageService.getAllImageByProductId(id);
            if (list.isEmpty()) {
                return new ResponseEntity<>("No Images found!!!", HttpStatus.NOT_FOUND);
            }
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
