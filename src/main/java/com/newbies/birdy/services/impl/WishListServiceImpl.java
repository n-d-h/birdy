package com.newbies.birdy.services.impl;

import com.newbies.birdy.dto.WishlistDTO;
import com.newbies.birdy.entities.Product;
import com.newbies.birdy.entities.User;
import com.newbies.birdy.entities.Wishlist;
import com.newbies.birdy.entities.WishlistKey;
import com.newbies.birdy.exceptions.entity.EntityNotFoundException;
import com.newbies.birdy.mapper.WishlistMapper;
import com.newbies.birdy.repositories.WishlistRepository;
import com.newbies.birdy.services.WishlistService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WishListServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;

    @Override
    public Boolean addWishlist(Integer userId, Integer productId) {
        WishlistKey wishlistKey = new WishlistKey(userId, productId);
        //Wishlist wishlist = wishlistRepository.findById(wishlistKey).orElse(null);
        User user = new User();
        Product product = new Product();
        user.setId(userId);
        product.setId(productId);
        return wishlistRepository.save(new Wishlist(wishlistKey, new Date(), true, user, product)).getId() != null;
    }

    @Override
    public List<WishlistDTO> listWishlistByUser(Integer userId, Boolean status) {
        List<Wishlist> list = wishlistRepository.findByUserId(userId, status);
        return list.stream().map(WishlistMapper.INSTANCE::toDTO).toList();
    }

    @Override
    public List<WishlistDTO> listWishlistByProduct(Integer productId, Boolean status) {
        List<Wishlist> list = wishlistRepository.findByProductId(productId, status);
        return list.stream().map(WishlistMapper.INSTANCE::toDTO).toList();
    }

    @Override
    public Boolean deleteWishlist(Integer userId, Integer productId) {
        WishlistKey wishlistKey = new WishlistKey(userId, productId);
        Wishlist wishlist = wishlistRepository
                .findById(wishlistKey)
                .orElseThrow(() -> new EntityNotFoundException("Wishlist not found"));
        wishlistRepository.delete(wishlist);
        return true;
    }

    @Override
    public WishlistDTO getWishlist(Integer userId, Integer productId) {
        WishlistKey wishlistKey = new WishlistKey(userId, productId);
        return WishlistMapper.INSTANCE.toDTO(wishlistRepository
                .findById(wishlistKey).orElseThrow(() -> new EntityNotFoundException("Wishlist not found")));
    }
}
