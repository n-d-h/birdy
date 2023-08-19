package com.newbies.birdy.mapper;

import com.newbies.birdy.dto.OrderDetailDTO;
import com.newbies.birdy.entities.Order;
import com.newbies.birdy.entities.OrderDetail;
import com.newbies.birdy.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderDetailMapper {

    OrderDetailMapper INSTANCE = Mappers.getMapper(OrderDetailMapper.class);

    @Mapping(target = "productCategory", source = "productOrderDetail.category.categoryName")
    @Mapping(target = "productName", source = "productOrderDetail.productName")
    @Mapping(target = "productId", source = "productOrderDetail.id")
    @Mapping(target = "orderId", source = "order.id")
    OrderDetailDTO toDTO(OrderDetail orderDetail);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "productOrderDetail", source = "productId", qualifiedByName = "mapProduct")
    @Mapping(target = "order", source = "orderId", qualifiedByName = "mapOrder")
    OrderDetail toEntity(OrderDetailDTO dto);

    @Named("mapProduct")
    default Product mapProduct(Integer id) {
        Product p = new Product();
        p.setId(id);
        return p;
    }

    @Named("mapOrder")
    default Order mapOrder(Integer id) {
        Order order = new Order();
        order.setId(id);
        return order;
    }

}
