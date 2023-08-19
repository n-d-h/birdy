package com.newbies.birdy.mapper;

import com.newbies.birdy.dto.ProductImageDTO;
import com.newbies.birdy.entities.Product;
import com.newbies.birdy.entities.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductImageMapper {

    ProductImageMapper INSTANCE = Mappers.getMapper(ProductImageMapper.class);

    @Mapping(target = "productName", source = "productImg.productName")
    @Mapping(target = "productId", source = "productImg.id")
    ProductImageDTO toDTO(ProductImage productImage);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "productImg", source = "productId", qualifiedByName = "mapProduct")
    ProductImage toEntity(ProductImageDTO dto);

    @Named("mapProduct")
    default Product mapProduct(Integer id) {
        Product p = new Product();
        p.setId(id);
        return p;
    }
}
