package com.newbies.birdy.mapper;

import com.newbies.birdy.dto.AddressDTO;
import com.newbies.birdy.entities.Address;
import com.newbies.birdy.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressMapper {


    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    @Mapping(target = "userId", source = "userAddress.id")
    @Mapping(target = "fullName", source = "userAddress.fullName")
    AddressDTO toDTO(Address address);

    @Mapping(target = "orderList", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "userAddress", source = "userId",qualifiedByName = "mapUser")
    Address toEntity(AddressDTO dto);

    @Named("mapUser")
    default User mapUser(Integer id) {
        User user = new User();
        user.setId(id);
        return user;
    }

}
