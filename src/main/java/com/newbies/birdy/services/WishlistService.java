package com.newbies.birdy.services;

import com.newbies.birdy.dto.WishlistDTO;

import java.util.List;

public interface WishlistService {

    Boolean addWishlist(Integer userId, Integer productId);

    List<WishlistDTO> listWishlistByUser(Integer userId, Boolean status);

    List<WishlistDTO> listWishlistByProduct(Integer productId, Boolean status);

    Boolean deleteWishlist(Integer userId, Integer productId);

    WishlistDTO getWishlist(Integer userId, Integer productId);
}
