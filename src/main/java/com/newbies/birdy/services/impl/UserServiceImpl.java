package com.newbies.birdy.services.impl;

import com.newbies.birdy.dto.AddressDTO;
import com.newbies.birdy.dto.UserDTO;
import com.newbies.birdy.entities.Account;
import com.newbies.birdy.entities.Address;
import com.newbies.birdy.entities.User;
import com.newbies.birdy.exceptions.entity.EntityNotFoundException;
import com.newbies.birdy.mapper.AddressMapper;
import com.newbies.birdy.mapper.UserMapper;
import com.newbies.birdy.repositories.AccountRepository;
import com.newbies.birdy.repositories.UserRepository;
import com.newbies.birdy.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

    @Override
    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return UserMapper.INSTANCE.toDTO(user);
    }

    @Override
    public UserDTO getUserByPhoneNumber(String phoneNumber, Boolean status) {
        Account account = accountRepository
                .findByPhoneNumberAndStatus(phoneNumber, status)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        User user = account.getUser();
        if(user != null)
            return UserMapper.INSTANCE.toDTO(user);
        return null;
    }

    public AddressDTO getUserDefaultAddress(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Address address = user.getAddressList().stream().filter(Address::getIsDefault).findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Default address not found"));
        return AddressMapper.INSTANCE.toDTO(address);
    }

    @Override
    public List<AddressDTO> getUserAddressList(Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        List<Address> addressList = user.getAddressList();

        List<AddressDTO> result = null;

        if (addressList != null){
            result = addressList.stream().map(AddressMapper.INSTANCE::toDTO).toList();
        }
        return result;

    }

    @Override
    public Boolean updateUser(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setStatus(true);
        user.setDob(userDTO.getDob());
        user.setFullName(userDTO.getFullName());
        user.setAvatarUrl(userDTO.getAvatarUrl());
        user.setGender(userDTO.getGender());
        user.setStatus(true);
        return userRepository.save(user).getId() != null;
    }

    @Override
    public Boolean updateBalance(Integer id, Double balance) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Account account = user.getAccountUser();
        account.setBalance(balance);
        return accountRepository.save(account).getId() != null;
    }

    @Override
    public UserDTO getUserByAccount(Account account) {
        return UserMapper.INSTANCE.toDTO(userRepository.findByAccountUser(account));
    }

    @Override
    public Boolean isEmailExited(Integer userId, String email) {
        return userRepository.findByEmailAndStatusAndIdNot(email,true, userId).orElse(null) != null;
    }

}
