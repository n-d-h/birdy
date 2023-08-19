package com.newbies.birdy.repositories;

import com.newbies.birdy.entities.Category;
import com.newbies.birdy.entities.Product;
import com.newbies.birdy.entities.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findTop15ByRatingGreaterThanAndStateAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThanOrderByRatingDesc(Integer rating, Integer state, Boolean status, Boolean isDisabled, Boolean isBanned, Integer quantity);

    Page<Product> findByStatusAndStateAndIsDisabledAndIsBannedAndQuantityGreaterThanOrderByRatingDesc(Boolean status, Integer state, Boolean isDisabled, Boolean isBanned, Integer quantity, Pageable page);

    //------------- search and sort (all products) ------------------
    Page<Product> findByProductNameIgnoreCaseContainingAndStateAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThan(String name, Integer state, Boolean status, Boolean isDisabled, Boolean isBanned, Integer quantity, Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCaseAndStateAndRatingAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThan(String name, Integer state, Integer rating, Boolean status, Boolean isDisabled, Boolean isBanned, Integer quantity, Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCaseAndStateAndUnitPriceBetweenAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThan(String name, Integer state, Double from, Double to, Boolean status, Boolean isDisabled, Boolean isBanned, Integer quantity, Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCaseAndStateAndRatingAndUnitPriceBetweenAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThan(String name, Integer state, Integer rating, Double from, Double to, Boolean status, Boolean isDisabled, Boolean isBanned, Integer quantity, Pageable pageable);

    // --------------------------------------------------------------

    //------------ search and sort in a category ----------------
    Page<Product> findByProductNameContainingIgnoreCaseAndStateAndCategoryAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThan(String search, Integer state, Category category, Boolean status, Boolean isDisabled, Boolean isBanned, Integer quantity, Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCaseAndStateAndCategoryAndRatingAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThan(String name, Integer state, Category category, Integer rating, Boolean status, Boolean isDisabled, Boolean isBanned, Integer quantity, Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCaseAndStateAndCategoryAndUnitPriceBetweenAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThan(String name, Integer state, Category category, Double from, Double to, Boolean status, Boolean isDisabled, Boolean isBanned, Integer quantity, Pageable pageable);

    Page<Product> findByProductNameContainingIgnoreCaseAndStateAndCategoryAndRatingAndUnitPriceBetweenAndStatusAndIsDisabledAndIsBannedAndQuantityGreaterThan(String name, Integer state, Category category, Integer rating, Double from, Double to, Boolean status, Boolean isDisabled, Boolean isBanned, Integer quantity, Pageable pageable);

    // ----------------------------------------------------------

    Page<Product> findByCategoryAndStateAndStatus(Category category, Integer state, Boolean status, Pageable pageable);

    Page<Product> findByShopProductAndProductNameContainingIgnoreCaseAndStateAndStatusAndIsDisabledAndIsBanned(Shop shop, String search, Integer state, Boolean status, Boolean isDisabled, Boolean isBanned, Pageable pageable);

    Page<Product> findByShopProductAndProductNameContainingIgnoreCaseAndStatus(Shop shop, String search, Boolean status, Pageable pageable);

    Optional<Product> findByIdAndStatus(Integer id, Boolean status);

    Page<Product> findByShopProductAndProductNameContainingIgnoreCaseAndStateAndCategoryAndStatusAndIsDisabledAndIsBanned(Shop shop, String search, Integer state, Category category, Boolean status, Boolean isDisabled, Boolean isBanned, Pageable pageable);

    Page<Product> findByShopProductAndProductNameContainingIgnoreCaseAndCategoryAndStatus(Shop shop, String search, Category category, Boolean status, Pageable pageable);

    @Query("SELECT DISTINCT YEAR(p.createDate) FROM Product p " +
            "WHERE p.shopProduct = :shopProduct " +
            "AND p.status = TRUE " +
            "AND p.state = 1 " +
            "ORDER BY YEAR(p.createDate) DESC")
    List<Integer> findDistinctYear(@Param("shopProduct") Shop shopProduct);

    Long countByShopProductAndStateAndStatusAndCreateDateBetween(Shop shop, Integer state, Boolean status, Date start, Date end);

    Long countByShopProductAndStateAndStatusAndCreateDateBetweenAndCategory(Shop shop, Integer state, Boolean status, Date start, Date end, Category category);

    // Admin
    Page<Product> findByProductNameContainingIgnoreCaseAndStateAndStatus(String search, Integer state, Boolean status, Pageable pageable);

    Long countByStateAndStatus(Integer state, Boolean status);
}
