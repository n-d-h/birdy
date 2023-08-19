package com.newbies.birdy.mapper;

import com.newbies.birdy.dto.ShipmentTypeDTO;
import com.newbies.birdy.entities.ShipmentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShipmentTypeMapper {

    ShipmentTypeMapper INSTANCE = Mappers.getMapper(ShipmentTypeMapper.class);

    ShipmentTypeDTO toDTO(ShipmentType shipmentType);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "shipmentList", ignore = true)
    ShipmentType toEntity(ShipmentTypeDTO dto);
}
