package com.newbies.birdy.mapper;

import com.newbies.birdy.dto.WishlistDTO;
import com.newbies.birdy.entities.Wishlist;
import com.newbies.birdy.entities.WishlistKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WishlistMapper {
    WishlistMapper INSTANCE = Mappers.getMapper(WishlistMapper.class);


    @Mapping(target = "userId", source = "userWishlist.id")
    @Mapping(target = "productName", source = "productWishlist.productName")
    @Mapping(target = "productId", source = "productWishlist.id")
    @Mapping(target = "fullName", source = "userWishlist.fullName")
    WishlistDTO toDTO(Wishlist user);

    @Mapping(target = "userWishlist", ignore = true)
    @Mapping(target = "productWishlist", ignore = true)
    @Mapping(target = "id", source = ".", qualifiedByName = "mapWishlistKey")
    Wishlist toEntity(WishlistDTO dto);

    @Named("mapWishlistKey")
    default WishlistKey mapWishlistKey(WishlistDTO dto) {
        WishlistKey a = new WishlistKey();
        a.setUserId(dto.getUserId());
        a.setProductId(dto.getProductId());
        return a;
    }
}
