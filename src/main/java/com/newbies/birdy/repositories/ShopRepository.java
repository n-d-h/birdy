package com.newbies.birdy.repositories;

import com.newbies.birdy.entities.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Integer> {

    List<Shop> findByShopNameContainingAndStatus(String name, Boolean status);

    List<Shop> findByStatus(Boolean status);

    Shop findByIdAndStatus(Integer id, Boolean status);

    Page<Shop> findByStatusAndShopNameContaining(Boolean status, String name, Pageable pageable);

    Long countByStatus(Boolean status);

}
