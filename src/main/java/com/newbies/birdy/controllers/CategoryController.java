package com.newbies.birdy.controllers;

import com.newbies.birdy.dto.ProductDTO;
import com.newbies.birdy.repositories.CategoryRepository;
import com.newbies.birdy.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@Tag(name = "Category API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/categories")
public class CategoryController  {

    private final CategoryRepository categoryRepository;
    private final ProductService productService;
    @Operation(summary = "Get all available or unavailable products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Load Products List", content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/{id}/products/{status}")
    public ResponseEntity<?> getAllProductsByCategoryAndStatus(
            @Parameter(description = "Products status (true=available or false=unavailable)", example = "true") @PathVariable("status") Boolean status,
            @Parameter(description = "Category ID (1=bird, 2=accessories, 3=food", example = "1") @PathVariable("id") Integer id,
            @Parameter(description = "Page number (start from 0)", example = "0") @RequestParam("page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 30);
        Map<List<ProductDTO>, Integer> listMap = productService.getProductsByCategoryAndStatusAndPaging(id, status, pageable);
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

    @Operation(summary = "Search relevant available products + paging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Load Products List", content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/{id}/products/view")
    public ResponseEntity<?> searchRelevantProductsAndPaging(
            @Parameter(description = "Category ID (1=bird, 2=accessories, 3=food", example = "1") @PathVariable("id") Integer id,
            @Parameter(description = "Search Products", example = "bird") @RequestParam("search") Optional<String> search,
            @Parameter(description = "Products Rating", example = "4") @RequestParam("rating") Optional<Integer> rating,
            @Parameter(description = "Price range start", example = "10.22") @RequestParam("from") Optional<Double> from,
            @Parameter(description = "Price range end", example = "30.9") @RequestParam("to") Optional<Double> to,
            @Parameter(description = "Page number (start from 0)", example = "0") @RequestParam("page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 30);
        Map<List<ProductDTO>, Integer> listMap = productService.searchAndSortProductsInCategoryWithPaging(id,
                search.orElse(""), rating.orElse(-1), from.orElse((double) -1), to.orElse((double) -1),
                true, pageable);
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

    @Operation(summary = "Search latest available products + paging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Load Products List", content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/{id}/products/view/latest")
    public ResponseEntity<?> searchLatestProductsAndPaging(
            @Parameter(description = "Category ID (1=bird, 2=accessories, 3=food", example = "1") @PathVariable("id") Integer id,
            @Parameter(description = "Search Products", example = "bird") @RequestParam("search") Optional<String> search,
            @Parameter(description = "Products Rating", example = "4") @RequestParam("rating") Optional<Integer> rating,
            @Parameter(description = "Price range start", example = "10.22") @RequestParam("from") Optional<Double> from,
            @Parameter(description = "Price range end", example = "30.9") @RequestParam("to") Optional<Double> to,
            @Parameter(description = "Page number (start from 0)", example = "0") @RequestParam("page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 30, Sort.by(Sort.Direction.DESC, "id"));
        Map<List<ProductDTO>, Integer> listMap = productService.searchAndSortProductsInCategoryWithPaging(id,
                search.orElse(""), rating.orElse(-1), from.orElse((double) -1), to.orElse((double) -1),
                true, pageable);
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

    @Operation(summary = "Search and sort price ascending available products + paging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Load Products List", content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/{id}/products/view/price-asc")
    public ResponseEntity<?> searchAndSortPriceAscProductsAndPaging(
            @Parameter(description = "Category ID (1=bird, 2=accessories, 3=food", example = "1") @PathVariable("id") Integer id,
            @Parameter(description = "Search Products", example = "bird") @RequestParam("search") Optional<String> search,
            @Parameter(description = "Products Rating", example = "4") @RequestParam("rating") Optional<Integer> rating,
            @Parameter(description = "Price range start", example = "10.22") @RequestParam("from") Optional<Double> from,
            @Parameter(description = "Price range end", example = "30.9") @RequestParam("to") Optional<Double> to,
            @Parameter(description = "Page number (start from 0)", example = "0") @RequestParam("page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 30, Sort.by(Sort.Direction.ASC, "unitPrice"));
        Map<List<ProductDTO>, Integer> listMap = productService.searchAndSortProductsInCategoryWithPaging(id,
                search.orElse(""), rating.orElse(-1), from.orElse((double) -1), to.orElse((double) -1),
                true, pageable);
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

    @Operation(summary = "Search and sort price descending available products + paging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Load Products List", content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/{id}/products/view/price-desc")
    public ResponseEntity<?> searchAndSortPriceDescProductsAndPaging(
            @Parameter(description = "Category ID (1=bird, 2=accessories, 3=food", example = "1") @PathVariable("id") Integer id,
            @Parameter(description = "Search Products", example = "bird") @RequestParam("search") Optional<String> search,
            @Parameter(description = "Products Rating", example = "4") @RequestParam("rating") Optional<Integer> rating,
            @Parameter(description = "Price range start", example = "10.34") @RequestParam("from") Optional<Double> from,
            @Parameter(description = "Price range end", example = "30.9") @RequestParam("to") Optional<Double> to,
            @Parameter(description = "Page number (start from 0)", example = "0") @RequestParam("page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 30, Sort.by(Sort.Direction.DESC, "unitPrice"));
        Map<List<ProductDTO>, Integer> listMap = productService.searchAndSortProductsInCategoryWithPaging(id,
                search.orElse(""), rating.orElse(-1), from.orElse((double) -1), to.orElse((double) -1),
                true, pageable);
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
}
