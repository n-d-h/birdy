package com.newbies.birdy.repositories;

import com.newbies.birdy.entities.Wishlist;
import com.newbies.birdy.entities.WishlistKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, WishlistKey> {

    @Query("SELECT w FROM Wishlist w WHERE w.id.userId = ?1 AND w.status = ?2")
    List<Wishlist> findByUserId(Integer userId, Boolean status);

    @Query("SELECT w FROM Wishlist w WHERE w.id.productId = ?1 AND w.status = ?2")
    List<Wishlist> findByProductId(Integer productId, Boolean status);

}
