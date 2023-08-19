package com.newbies.birdy.mapper;

import com.newbies.birdy.dto.PaymentMethodDTO;
import com.newbies.birdy.entities.PaymentMethod;
import com.newbies.birdy.entities.PaymentType;
import com.newbies.birdy.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentMethodMapper {

    PaymentMethodMapper INSTANCE = Mappers.getMapper(PaymentMethodMapper.class);

    @Mapping(target = "userId", source = "userPaymentMethod.id")
    @Mapping(target = "paymentTypeName", source = "paymentType.paymentTypeName")
    @Mapping(target = "paymentTypeId", source = "paymentType.id")
    @Mapping(target = "fullName", source = "userPaymentMethod.fullName")
    PaymentMethodDTO toDTO(PaymentMethod paymentMethod);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "orderList", ignore = true)
    @Mapping(target = "userPaymentMethod", source = "userId", qualifiedByName = "mapUser")
    @Mapping(target = "paymentType", source = "paymentTypeId", qualifiedByName = "mapPaymentType")
    PaymentMethod toEntity(PaymentMethodDTO dto);

    @Named("mapUser")
    default User mapUser(Integer id) {
        User u = new User();
        u.setId(id);
        return u;
    }

    @Named("mapPaymentType")
    default PaymentType mapPaymentType(Integer id) {
        PaymentType pt = new PaymentType();
        pt.setId(id);
        return pt;
    }
}
