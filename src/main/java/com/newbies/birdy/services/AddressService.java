package com.newbies.birdy.services;

import com.newbies.birdy.dto.AddressDTO;

import java.util.List;

public interface AddressService {

    Integer createAddress(Integer userId, AddressDTO addressDTO);

    List<AddressDTO> getAllUserAddress(Integer userId, Boolean status);

    String getAddressById(Integer addressId);

    AddressDTO getAddressDTOById(Integer addressId);

    Boolean updateAddress(AddressDTO address);
}
