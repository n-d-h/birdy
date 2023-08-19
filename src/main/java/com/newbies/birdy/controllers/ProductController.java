package com.newbies.birdy.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbies.birdy.dto.FileImageDTO;
import com.newbies.birdy.dto.ProductDTO;
import com.newbies.birdy.dto.ReviewDTO;
import com.newbies.birdy.dto.ShopDTO;
import com.newbies.birdy.entities.OrderState;
import com.newbies.birdy.exceptions.entity.EntityNotFoundException;
import com.newbies.birdy.services.*;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Product API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products")
public class ProductController {

    private final ProductService productService;

    private final ProductImageService productImageService;

    private final ShopService shopService;

    private final FirebaseStorageService firebaseStorageService;

    private final OrderDetailService orderDetailService;

    private final OrderService orderService;

    private final ReviewService reviewService;
    private final EmailService emailService;

    @Operation(summary = "Get first 15 available products for landing page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Load Products List", content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/landing-page")
    public ResponseEntity<?> getFirst15Products() {
        List<ProductDTO> list = productService.getFirst15ProductsWithStatusTrue();
        if (list.isEmpty()) {
            return new ResponseEntity<>("No product found!!!", HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(list);
        }
    }

    @Operation(summary = "Get all available or unavailable products + paging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Load Products List", content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getAllProductsByStatusAndPaging(
            @Parameter(description = "Products status (true=available or false=unavailable)", example = "true") @PathVariable("status") Boolean status,
            @Parameter(description = "Page number (start from 0)", example = "0") @RequestParam("page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 30);
        Map<List<ProductDTO>, Integer> listMap = productService.getAllProductsByStatusAndPaging(status, -1, pageable);
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

    @Operation(summary = "View all products + paging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Load Products List", content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/view/all")
    public ResponseEntity<?> getAllProductsAndPaging(
            @Parameter(description = "Page number (start from 0)", example = "0") @RequestParam("page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 30, Sort.by("rating").descending());
        Map<List<ProductDTO>, Integer> listMap = productService.getAllProductsByStatusAndPaging(true, 0, pageable);
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

    @Operation(summary = "Get product details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Load Products List", content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductDetailById(@Parameter(description = "Product ID", example = "1") @PathVariable("id") Integer id) {
        try {
            ProductDTO product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return new ResponseEntity<>("No product found!!!", HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Search relevant available products + paging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Load Products List", content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/view")
    public ResponseEntity<?> searchRelevantProductsAndPaging(
            @Parameter(description = "Search Products", example = "bird") @RequestParam("search") Optional<String> search,
            @Parameter(description = "Products Rating", example = "4") @RequestParam("rating") Optional<Integer> rating,
            @Parameter(description = "Price range start", example = "10.45") @RequestParam("from") Optional<Double> from,
            @Parameter(description = "Price range end", example = "30.65") @RequestParam("to") Optional<Double> to,
            @Parameter(description = "Page number (start from 0)", example = "0") @RequestParam("page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 30, Sort.by("rating").descending());
        Map<List<ProductDTO>, Integer> listMap = productService.searchAndSortProductsWithPaging(search.orElse(""),
                rating.orElse(-1), from.orElse((double) -1), to.orElse((double) -1), true, pageable);
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
    @GetMapping("/view/latest")
    public ResponseEntity<?> searchLatestProductsStatusTrueAndPaging(
            @Parameter(description = "Search Products", example = "bird") @RequestParam("search") Optional<String> search,
            @Parameter(description = "Products Rating", example = "4") @RequestParam("rating") Optional<Integer> rating,
            @Parameter(description = "Price range start", example = "10.88") @RequestParam("from") Optional<Double> from,
            @Parameter(description = "Price range end", example = "30.99") @RequestParam("to") Optional<Double> to,
            @Parameter(description = "Page number (start from 0)", example = "0") @RequestParam("page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 30, Sort.by(Sort.Direction.DESC, "id"));
        Map<List<ProductDTO>, Integer> listMap = productService.searchAndSortProductsWithPaging(search.orElse(""),
                rating.orElse(-1), from.orElse((double) -1), to.orElse((double) -1), true, pageable);
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
    @GetMapping("/view/price-asc")
    public ResponseEntity<?> searchAndSortPriceAscProductsStatusTrueAndPaging(
            @Parameter(description = "Search Products", example = "bird") @RequestParam("search") Optional<String> search,
            @Parameter(description = "Products Rating", example = "4") @RequestParam("rating") Optional<Integer> rating,
            @Parameter(description = "Price range start", example = "10.23") @RequestParam("from") Optional<Double> from,
            @Parameter(description = "Price range end", example = "30.9") @RequestParam("to") Optional<Double> to,
            @Parameter(description = "Page number (start from 0)", example = "0") @RequestParam("page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 30, Sort.by(Sort.Direction.ASC, "unitPrice"));
        Map<List<ProductDTO>, Integer> listMap = productService.searchAndSortProductsWithPaging(search.orElse(""),
                rating.orElse(-1), from.orElse((double) -1), to.orElse((double) -1), true, pageable);
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
    @GetMapping("/view/price-desc")
    public ResponseEntity<?> searchAndSortPriceDescProductsStatusTrueAndPaging(
            @Parameter(description = "Search Products", example = "bird") @RequestParam("search") Optional<String> search,
            @Parameter(description = "Products Rating", example = "4") @RequestParam("rating") Optional<Integer> rating,
            @Parameter(description = "Price range start", example = "10.12") @RequestParam("from") Optional<Double> from,
            @Parameter(description = "Price range end", example = "30.9") @RequestParam("to") Optional<Double> to,
            @Parameter(description = "Page number (start from 0)", example = "0") @RequestParam("page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 30, Sort.by(Sort.Direction.DESC, "unitPrice"));
        Map<List<ProductDTO>, Integer> listMap = productService.searchAndSortProductsWithPaging(search.orElse(""),
                rating.orElse(-1), from.orElse((double) -1), to.orElse((double) -1), true, pageable);
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

    @Operation(summary = "Create new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PostMapping(value = "/create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createProduct(
            @RequestPart(value = "productDTO") String jsonString,
            @RequestPart(value = "mainImage") MultipartFile mainImage,
            @RequestPart(value = "subImages", required = false) MultipartFile[] subImages
    ) {
        Integer productId;
        try {
            String fileName = firebaseStorageService.uploadFile(mainImage);
            String mainImgUrl = firebaseStorageService.getImageUrl(fileName);

            ObjectMapper objectMapper = new ObjectMapper();
            ProductDTO productDTO = objectMapper.readValue(jsonString, ProductDTO.class);
            productDTO.setImageMain(mainImgUrl);
            productDTO.setState(1);
            productDTO.setRating(0);
            productDTO.setShopName(shopService.getShopById(productDTO.getShopId()).getShopName());

            productId = productService.saveProduct(productDTO);
            if (productId != null && subImages != null) {
//                MultipartFile[] subImages = productRequestDTO.getSubImages();
//                if (subImages != null) {
                String[] subImgUrls = new String[subImages.length];
                for (int i = 0; i < subImages.length; i++) {
                    fileName = firebaseStorageService.uploadFile(subImages[i]);
                    subImgUrls[i] = firebaseStorageService.getImageUrl(fileName);
                }
                productImageService.saveImages(subImgUrls, productId);
//                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (productId != null) {
            return new ResponseEntity<>(productId, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Product created failed!!!", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Edit product quantity")
    @PatchMapping("/edit-quantity/{id}")
    public ResponseEntity<?> editProductQuantity(@PathVariable("id") Integer id, Integer quantity) {
        try {
            ProductDTO product = productService.getProductById(id);
            product.setQuantity(quantity);
            productService.saveProduct(product);
            return new ResponseEntity<>("Product quantity updated!!!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Product quantity updated failed!!!", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "check product is in pending order")
    @GetMapping("/check-product/{id}")
    public ResponseEntity<?> checkProductIsInPendingOrder(@PathVariable("id") Integer id) {
        if (orderDetailService.checkProductIsInPendingOrder(id)) {
            return new ResponseEntity<>("Product is in pending orders!!!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Product is not in any pending order.", HttpStatus.NOT_FOUND);
        }
    }


    @Operation(summary = "Cancel all pending orders and Update existing product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PutMapping(value = "/update/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateCancelProduct(
            @PathVariable("id") Integer id,
            @RequestPart(value = "productDTO") String jsonString,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "objects", required = false) String objects,
            @RequestPart(value = "subImages", required = false) MultipartFile[] subImages
    ) {
        Integer productId;
        try {

            //  Get edit product
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDTO productDTO = objectMapper.readValue(jsonString, ProductDTO.class);

            if (Boolean.TRUE.equals(orderDetailService.checkProductIsInOrder(id))) {

                // product is in order => create a clone product

                // cancel all pending orders
                if (orderDetailService.checkProductIsInPendingOrder(id)) {
                    List<Integer> orderIds = orderDetailService.getListOrderIdProductChecked(id);
                    orderIds.forEach(o -> {
                        orderService.editOrderState(o, String.valueOf(OrderState.CANCELED), "Đơn hàng có sản phẩm không khả dụng");
                        orderService.cancelOrder(o);
                    });
                }

                productDTO.setId(null); // create a clone product with new id

                if (mainImage != null) {
                    String fileNameMain = firebaseStorageService.uploadFile(mainImage);
                    String mainImgUrl = firebaseStorageService.getImageUrl(fileNameMain);

                    // set new main image url for clone product
                    productDTO.setImageMain(mainImgUrl);
                } else {
                    // set old main image from url old product id for clone product
                    productDTO.setImageMain(productService.getProductById(id).getImageMain());
                }

                productDTO.setState(1); // set state = 1 (available)
                productDTO.setRating(0); // reset rating
                productDTO.setIsDisabled(false); // show product
                productDTO.setIsWarned(false); // reset warning
                productDTO.setIsBanned(false); // reset banned
                productId = productService.saveProduct(productDTO); // save clone product

                // Get old product images from old product id
                if (objects != null) {
                    List<FileImageDTO> fileImageDTOList = objectMapper.readValue(objects, new TypeReference<>() {
                    });

                    if (fileImageDTOList != null && !fileImageDTOList.isEmpty()) {
                        productImageService.updateImages(fileImageDTOList, productId); // create product images for clone product
                    }
                }

                // Get new product images from input
                if (productId != null && subImages != null) {
                    String[] subImgUrls = new String[subImages.length];
                    for (int i = 0; i < subImages.length; i++) {
                        String fileNameSub = firebaseStorageService.uploadFile(subImages[i]);
                        subImgUrls[i] = firebaseStorageService.getImageUrl(fileNameSub);
                    }
                    productImageService.saveImages(subImgUrls, productId);
                }

                // delete old product (set status = false)
                ProductDTO oldProduct = productService.getProductById(id);
                productService.disabledProduct(oldProduct);

            } else {

                // product is not in order => update current product

                productDTO.setId(id); // set id for current product
                productDTO.setState(1); // set state = 1 (available)
                productDTO.setRating(0); // reset rating

                // update main image
                if (mainImage != null) {
                    String oldMainImgUrl = productService.getProductById(id).getImageMain();
                    firebaseStorageService.deleteFile(oldMainImgUrl);

                    String fileNameMain = firebaseStorageService.uploadFile(mainImage);
                    String mainImgUrl = firebaseStorageService.getImageUrl(fileNameMain);

                    productDTO.setImageMain(mainImgUrl);
                } else {
                    productDTO.setImageMain(productService.getProductById(id).getImageMain());
                }

                // update old product images
                if (objects != null) {
                    List<FileImageDTO> fileImageDTOList = objectMapper.readValue(objects, new TypeReference<>() {
                    });

                    if (fileImageDTOList != null && !fileImageDTOList.isEmpty()) {
                        List<Integer> uidList = fileImageDTOList.stream().map(FileImageDTO::getUid).toList();
                        productImageService.updateImagesProduct(fileImageDTOList, uidList, id);
                    } else {
                        productImageService.deleteImages(id);
                    }

                } else {
                    productImageService.deleteImages(id);
                }

                // save product
                productId = productService.saveProduct(productDTO);

                // Get new product images from input
                if (productId != null && subImages != null) {
                    String[] subImgUrls = new String[subImages.length];
                    for (int i = 0; i < subImages.length; i++) {
                        String fileNameSub = firebaseStorageService.uploadFile(subImages[i]);
                        subImgUrls[i] = firebaseStorageService.getImageUrl(fileNameSub);
                    }
                    productImageService.saveImages(subImgUrls, productId);
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (productId != null) {
            return new ResponseEntity<>(productId, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Product created failed!!!", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Hide old product and Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PutMapping(value = "/update-new/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateNewProduct(
            @PathVariable("id") Integer id,
            @RequestPart(value = "productDTO") String jsonString,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "objects", required = false) String objects,
            @RequestPart(value = "subImages", required = false) MultipartFile[] subImages
    ) {
        Integer productId;
        try {

            //  Get edit product
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDTO productDTO = objectMapper.readValue(jsonString, ProductDTO.class);

            productDTO.setId(null); // create a clone product with new id

            if (mainImage != null) {
                String fileNameMain = firebaseStorageService.uploadFile(mainImage);
                String mainImgUrl = firebaseStorageService.getImageUrl(fileNameMain);

                // set new main image url for clone product
                productDTO.setImageMain(mainImgUrl);
            } else {
                // set old main image from url old product id for clone product
                productDTO.setImageMain(productService.getProductById(id).getImageMain());
            }

            productDTO.setState(1);
            productDTO.setRating(0); // reset rating
            productDTO.setIsDisabled(false); // show product
            productDTO.setIsWarned(false); // reset warning
            productDTO.setIsBanned(false); // reset banned
            productId = productService.saveProduct(productDTO); // save clone product

            // Get old product images from old product id
            if (objects != null) {
                List<FileImageDTO> fileImageDTOList = objectMapper.readValue(objects, new TypeReference<>() {
                });

                if (fileImageDTOList != null && !fileImageDTOList.isEmpty()) {
                    productImageService.updateImages(fileImageDTOList, productId); // create product images for clone product
                }
            }

            // Get new product images from input
            if (productId != null && subImages != null) {
                String[] subImgUrls = new String[subImages.length];
                for (int i = 0; i < subImages.length; i++) {
                    String fileNameSub = firebaseStorageService.uploadFile(subImages[i]);
                    subImgUrls[i] = firebaseStorageService.getImageUrl(fileNameSub);
                }
                productImageService.saveImages(subImgUrls, productId);
            }

            // hide old product
            ProductDTO oldProduct = productService.getProductById(id);
            productService.hideProduct(oldProduct);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (productId != null) {
            return new ResponseEntity<>(productId, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Product created failed!!!", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Delete existing product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Integer id) {

        // cancel all pending orders
        if (orderDetailService.checkProductIsInPendingOrder(id)) {
            List<Integer> orderIds = orderDetailService.getListOrderIdProductChecked(id);
            orderIds.forEach(o -> {
                orderService.editOrderState(o, String.valueOf(OrderState.CANCELED), "Đơn hàng có sản phẩm không khả dụng");
                orderService.cancelOrder(o);
            });
        }

        if (Boolean.FALSE.equals(productService.deleteProduct(id))) {
            return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Product deleted failed", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Hide product")
    @PatchMapping("/hide/{id}")
    public ResponseEntity<?> hideProduct(@PathVariable("id") Integer id) {
        ProductDTO productDTO = productService.getProductById(id);
        productService.hideProduct(productDTO);
        return new ResponseEntity<>("Product disabled successfully", HttpStatus.OK);
    }

    @Operation(summary = "Show product")
    @PatchMapping("/show/{id}")
    public ResponseEntity<?> showProduct(@PathVariable("id") Integer id) {
        ProductDTO productDTO = productService.getProductById(id);
        productService.showProduct(productDTO);
        return new ResponseEntity<>("Product enabled successfully", HttpStatus.OK);
    }

    @Operation(summary = "Get Product review by product id and paging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Load Review List", content = @Content(schema = @Schema(implementation = ReviewDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/review/{product-id}")
    public ResponseEntity<?> getProductReview(@PathVariable("product-id") Integer productId,
                                              @RequestParam("page") Optional<Integer> page) {
        System.out.println(page);
        Pageable pageable = PageRequest.of(page.orElse(0), 3);
        Map<List<ReviewDTO>, Long> listMap = reviewService
                .getReviewByPageAndProductIdAndStatus(pageable, productId, true);

        List<Object> list = new ArrayList<>();

        listMap.forEach((reviewDTOS, total) -> {
            list.add(reviewDTOS);
            list.add(total);
        });

        if (list.isEmpty()) {
            return new ResponseEntity<>("No review found!!!", HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(list);
        }
    }
}
