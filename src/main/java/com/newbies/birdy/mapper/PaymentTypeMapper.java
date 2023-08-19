package com.newbies.birdy.mapper;

import com.newbies.birdy.dto.PaymentTypeDTO;
import com.newbies.birdy.entities.PaymentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentTypeMapper {

    PaymentTypeMapper INSTANCE = Mappers.getMapper(PaymentTypeMapper.class);

    PaymentTypeDTO toDTO(PaymentType paymentType);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentMethodList", ignore = true)
    PaymentType toEntity(PaymentTypeDTO dto);
}
