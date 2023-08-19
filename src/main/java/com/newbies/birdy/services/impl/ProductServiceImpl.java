package com.newbies.birdy.services.impl;

import com.newbies.birdy.dto.ProductDTO;
import com.newbies.birdy.entities.Category;
import com.newbies.birdy.entities.Product;
import com.newbies.birdy.entities.ProductImage;
import com.newbies.birdy.entities.Shop;
import com.newbies.birdy.exceptions.entity.EntityNotFoundException;
import com.newbies.birdy.mapper.ProductMapper;
import com.newbies.birdy.repositories.CategoryRepository;
import com.newbies.birdy.repositories.ProductImageRepository;
import com.newbies.birdy.repositories.ProductRepository;
import com.newbies.birdy.repositories.ShopRepository;
import com.newbies.birdy.services.FirebaseStorageService;
import com.newbies.birdy.services.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;
    private final FirebaseStorageService firebaseStorageService;
    private final ProductImageRepository productImageRepository;


    @Override
    public ProductDTO getProductById(Integer id) {
        return ProductMapper.INSTANCE.toDTO(productRepository.findByIdAndStatus(id, true).orElseThrow(() -> new EntityNotFoundException("Product not found")));
    }

    @Override
    public List<ProductDTO> getFirst15ProductsWithStatusTrue() {
        return productRepository
                .findTop15ByRatingGreaterThanAndStateAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThanOrderByRatingDesc(3, 1, true, false, false, 0)
                .stream().map(ProductMapper.INSTANCE::toDTO).toList();
    }

    @Override
    public Map<List<ProductDTO>, Integer> getAllProductsByStatusAndPaging(Boolean status, Integer quantity, Pageable pageable) {
        Map<List<ProductDTO>, Integer> pair = new HashMap<>();
        Page<Product> pageList = productRepository.findByStatusAndStateAndIsDisabledAndIsBannedAndQuantityGreaterThanOrderByRatingDesc(status, 1, false, false, quantity, pageable);
        pair.put(pageList.stream().map(ProductMapper.INSTANCE::toDTO).toList(), pageList.getTotalPages());
        return pair;
    }

    @Override
    public Map<List<ProductDTO>, Integer> getProductsByCategoryAndStatusAndPaging(Integer categoryId, Boolean status, Pageable pageable) {
        Category category = categoryRepository.findByIdAndStatus(categoryId, true);
        Map<List<ProductDTO>, Integer> pair = new HashMap<>();
        Page<Product> pageList = productRepository.findByCategoryAndStateAndStatus(category, 1, status, pageable);
        pair.put(pageList.stream().map(ProductMapper.INSTANCE::toDTO).toList(), pageList.getTotalPages());
        return pair;
    }

    /* shop */

    @Override
    public Map<List<ProductDTO>, Integer> getProductsByShopAndStatusAndPaging(Integer shopId, String search, Boolean status, Pageable pageable) {
        Shop shop = shopRepository.findByIdAndStatus(shopId, true);
        Map<List<ProductDTO>, Integer> pair = new HashMap<>();
        Page<Product> pageList = productRepository.findByShopProductAndProductNameContainingIgnoreCaseAndStateAndStatusAndIsDisabledAndIsBanned(shop, search, 1, status, false, false, pageable);
        pair.put(pageList.stream().map(ProductMapper.INSTANCE::toDTO).toList(), pageList.getTotalPages());
        return pair;
    }


    // get all shop products for shop management
    @Override
    public Map<List<ProductDTO>, Integer> getProductsByShopAndStatusAndPagingForShop(Integer shopId, String search, Boolean status, Pageable pageable) {
        Shop shop = shopRepository.findByIdAndStatus(shopId, true);
        Map<List<ProductDTO>, Integer> pair = new HashMap<>();
        Page<Product> pageList = productRepository.findByShopProductAndProductNameContainingIgnoreCaseAndStatus(shop, search, status, pageable);
        pair.put(pageList.stream().map(ProductMapper.INSTANCE::toDTO).toList(), pageList.getTotalPages());
        return pair;
    }

    @Override
    public Map<List<ProductDTO>, Integer> getProductsByShopInCategoryAndStatusAndPaging(Integer shopId, String search, Integer categoryId, Boolean status, Pageable pageable) {
        Shop shop = shopRepository.findByIdAndStatus(shopId, true);
        Category category = categoryRepository.findByIdAndStatus(categoryId, true);
        Map<List<ProductDTO>, Integer> pair = new HashMap<>();
        Page<Product> pageList = productRepository.findByShopProductAndProductNameContainingIgnoreCaseAndStateAndCategoryAndStatusAndIsDisabledAndIsBanned(shop, search, 1, category, status, false, false, pageable);
        pair.put(pageList.stream().map(ProductMapper.INSTANCE::toDTO).toList(), pageList.getTotalPages());
        return pair;
    }

    @Override
    public Map<List<ProductDTO>, Integer> getProductsByShopInCategoryAndStatusAndPagingForShop(Integer shopId, String search, Integer categoryId, Boolean status, Pageable pageable) {
        Shop shop = shopRepository.findByIdAndStatus(shopId, true);
        Category category = categoryRepository.findByIdAndStatus(categoryId, true);
        Map<List<ProductDTO>, Integer> pair = new HashMap<>();
        Page<Product> pageList = productRepository.findByShopProductAndProductNameContainingIgnoreCaseAndCategoryAndStatus(shop, search, category, status, pageable);
        pair.put(pageList.stream().map(ProductMapper.INSTANCE::toDTO).toList(), pageList.getTotalPages());
        return pair;
    }

    /* shop */

    @Override
    public Map<List<ProductDTO>, Integer> searchAndSortProductsWithPaging(String search, Integer rating, Double from, Double to, Boolean status, Pageable pageable) {
        Map<List<ProductDTO>, Integer> pair = new HashMap<>();
        Page<Product> pageList = null;
        if (rating < 0 && from < 0 && to < 0) {
            pageList = productRepository.findByProductNameIgnoreCaseContainingAndStateAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThan(search, 1, status, false, false, 0, pageable);
        } else if (rating >= 0 && from < 0 && to < 0) {
            pageList = productRepository.findByProductNameContainingIgnoreCaseAndStateAndRatingAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThan(search, 1, rating, status, false, false, 0, pageable);
        } else if (rating < 0 && from >= 0 && to >= from) {
            pageList = productRepository.findByProductNameContainingIgnoreCaseAndStateAndUnitPriceBetweenAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThan(search, 1, from, to, status, false, false, 0, pageable);
        } else if (rating >= 0 && from >= 0 && to >= from) {
            pageList = productRepository.findByProductNameContainingIgnoreCaseAndStateAndRatingAndUnitPriceBetweenAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThan(search, 1, rating, from, to, status, false, false, 0, pageable);
        }

        pair.put(pageList.stream().map(ProductMapper.INSTANCE::toDTO).toList(), pageList.getTotalPages());
        return pair;
    }

    @Override
    public Map<List<ProductDTO>, Integer> searchAndSortProductsInCategoryWithPaging(Integer categoryId, String search, Integer rating, Double from, Double to, Boolean status, Pageable pageable) {
        Category category = categoryRepository.findByIdAndStatus(categoryId, true);
        Map<List<ProductDTO>, Integer> pair = new HashMap<>();
        Page<Product> pageList = null;
        if (rating < 0 && from < 0 && to < 0) {
            pageList = productRepository.findByProductNameContainingIgnoreCaseAndStateAndCategoryAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThan(search, 1, category, status, false, false, 0, pageable);
        } else if (rating >= 0 && from < 0 && to < 0) {
            pageList = productRepository.findByProductNameContainingIgnoreCaseAndStateAndCategoryAndRatingAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThan(search, 1, category, rating, status, false, false, 0, pageable);
        } else if (rating < 0 && from >= 0 && to >= from) {
            pageList = productRepository.findByProductNameContainingIgnoreCaseAndStateAndCategoryAndUnitPriceBetweenAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThan(search, 1, category, from, to, status, false, false, 0, pageable);
        } else if (rating >= 0 && from >= 0 && to >= from) {
            pageList = productRepository.findByProductNameContainingIgnoreCaseAndStateAndCategoryAndRatingAndUnitPriceBetweenAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThan(search, 1, category, rating, from, to, status, false, false, 0, pageable);
        }

        pair.put(pageList.stream().map(ProductMapper.INSTANCE::toDTO).toList(), pageList.getTotalPages());
        return pair;
    }

    @Override
    public Integer saveProduct(ProductDTO productDTO) {
        Product product = ProductMapper.INSTANCE.toEntity(productDTO);
        product.setStatus(true);
        return productRepository.save(product).getId();
    }

    @Override
    public Boolean deleteProduct(Integer id) {
//        try {
//            Product product = productRepository.findByIdAndStatus(id, true).orElseThrow(() -> new EntityNotFoundException("Product ID doesn't exist"));
//            List<ProductImage> images = productImageRepository.findByProductImgAndStatus(product, true);
//            for (ProductImage image : images) {
//                firebaseStorageService.deleteFile(image.getImgUrl());
//                productImageRepository.delete(image);
//            }
//            productRepository.delete(product);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
        Product product = productRepository.findByIdAndStatus(id, true).orElseThrow(() -> new EntityNotFoundException("Product ID doesn't exist"));
        List<ProductImage> images = productImageRepository.findByProductImgAndStatus(product, true);
        for (ProductImage image : images) {
            image.setStatus(false);
            productImageRepository.save(image);
        }
        product.setStatus(false);
        return productRepository.save(product).getStatus();
    }

    @Override
    public void disabledProduct(ProductDTO productDTO) {
        Product product = ProductMapper.INSTANCE.toEntity(productDTO);
        product.setStatus(false);
        productRepository.save(product);
    }

    @Override
    public void hideProduct(ProductDTO productDTO) {
        Product product = ProductMapper.INSTANCE.toEntity(productDTO);
        product.setIsDisabled(true);
        product.setStatus(true);
        productRepository.save(product);
    }

    @Override
    public void showProduct(ProductDTO productDTO) {
        Product product = ProductMapper.INSTANCE.toEntity(productDTO);
        product.setIsDisabled(false);
        product.setStatus(true);
        productRepository.save(product);
    }

    @Override
    public void warningProduct(ProductDTO productDTO) {
        Product product = ProductMapper.INSTANCE.toEntity(productDTO);
        product.setIsWarned(true);
        product.setStatus(true);
        productRepository.save(product);
    }

    @Override
    public void bannedProduct(ProductDTO productDTO) {
        Product product = ProductMapper.INSTANCE.toEntity(productDTO);
        product.setIsBanned(true);
        product.setStatus(true);
        productRepository.save(product);
    }

    @Override
    public Boolean deleteReportProduct(Integer id) {
        try {
            Product product = productRepository.findByIdAndStatus(id, true).orElseThrow(() -> new EntityNotFoundException("Product ID doesn't exist"));
            List<ProductImage> images = productImageRepository.findByProductImgAndStatus(product, true);
            for (ProductImage image : images) {
                firebaseStorageService.deleteFile(image.getImgUrl());
                productImageRepository.delete(image);
            }
            product.setStatus(false);
            productRepository.save(product).getStatus();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
