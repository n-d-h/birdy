package com.newbies.birdy.services;

import com.newbies.birdy.dto.AddressDTO;
import com.newbies.birdy.dto.UserDTO;
import com.newbies.birdy.entities.Account;

import java.util.List;

public interface UserService {

    public UserDTO getUserById(Integer id);

    UserDTO getUserByAccount(Account account);
    UserDTO getUserByPhoneNumber(String phoneNumber, Boolean status);

    AddressDTO getUserDefaultAddress(Integer id);

    List<AddressDTO> getUserAddressList(Integer id);

    Boolean updateUser(UserDTO userDTO);

    Boolean updateBalance(Integer id, Double balance);

    Boolean isEmailExited(Integer userId, String email);
}
