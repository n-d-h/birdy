package com.newbies.birdy.mapper;

import com.newbies.birdy.dto.AccountDTO;
import com.newbies.birdy.entities.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);


    AccountDTO toDTO(Account address);

    @Mapping(target = "role", source = "role")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "status", ignore = true)
    Account toEntity(AccountDTO dto);
}
