package com.newbies.birdy.mapper;

import com.newbies.birdy.dto.ShipmentDTO;
import com.newbies.birdy.entities.Shipment;
import com.newbies.birdy.entities.ShipmentType;
import com.newbies.birdy.entities.Shop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShipmentMapper {

    ShipmentMapper INSTANCE = Mappers.getMapper(ShipmentMapper.class);

    @Mapping(target = "shopName", source = "shopShipment.shopName")
    @Mapping(target = "shopId", source = "shopShipment.id")
    @Mapping(target = "shipmentTypeName", source = "shipmentType.shipmentTypeName")
    @Mapping(target = "shipmentTypeId", source = "shipmentType.id")
    ShipmentDTO toDTO(Shipment shipment);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "orderList", ignore = true)
    @Mapping(target = "shopShipment", source = "shopId", qualifiedByName = "mapShop")
    @Mapping(target = "shipmentType", source = "shipmentTypeId", qualifiedByName = "mapShipmentType")
    Shipment toEntity(ShipmentDTO dto);

    @Named("mapShop")
    default Shop mapShop(Integer id) {
        Shop shop = new Shop();
        shop.setId(id);
        return shop;
    }

    @Named("mapShipmentType")
    default ShipmentType mapShipmentType(Integer id) {
        ShipmentType st = new ShipmentType();
        st.setId(id);
        return st;
    }
}
