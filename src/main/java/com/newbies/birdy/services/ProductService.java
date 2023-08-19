package com.newbies.birdy.services;

import com.newbies.birdy.dto.ProductDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ProductService {

    ProductDTO getProductById(Integer id);

    List<ProductDTO> getFirst15ProductsWithStatusTrue();

    Map<List<ProductDTO>, Integer> getAllProductsByStatusAndPaging(Boolean status, Integer quantity, Pageable pageable);

    Map<List<ProductDTO>, Integer> getProductsByCategoryAndStatusAndPaging(Integer categoryId, Boolean status, Pageable pageable);

    Map<List<ProductDTO>, Integer> getProductsByShopAndStatusAndPaging(Integer shopId, String search, Boolean status, Pageable pageable);

    Map<List<ProductDTO>, Integer> getProductsByShopAndStatusAndPagingForShop(Integer shopId, String search, Boolean status, Pageable pageable);

    Map<List<ProductDTO>, Integer> getProductsByShopInCategoryAndStatusAndPaging(Integer shopId, String search, Integer categoryId, Boolean status, Pageable pageable);

    Map<List<ProductDTO>, Integer> getProductsByShopInCategoryAndStatusAndPagingForShop(Integer shopId, String search, Integer categoryId, Boolean status, Pageable pageable);

    Map<List<ProductDTO>, Integer> searchAndSortProductsWithPaging(String search, Integer rating, Double from, Double to, Boolean status, Pageable pageable);

    Map<List<ProductDTO>, Integer> searchAndSortProductsInCategoryWithPaging(Integer categoryId, String search, Integer rating, Double from, Double to, Boolean status, Pageable pageable);

    Integer saveProduct(ProductDTO productDTO);

    Boolean deleteProduct(Integer id);

    void disabledProduct(ProductDTO productDTO);

    void hideProduct(ProductDTO productDTO);

    void showProduct(ProductDTO productDTO);

    void warningProduct(ProductDTO productDTO);

    void bannedProduct(ProductDTO productDTO);
    
    Boolean deleteReportProduct(Integer id);
}
