package com.newbies.birdy.repositories;

import com.newbies.birdy.entities.Product;
import com.newbies.birdy.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    List<ProductImage> findByProductImgAndStatus(Product product, Boolean status);

    List<ProductImage> findByProductImgAndIdNotIn(Product product, List<Integer> ids);
}
