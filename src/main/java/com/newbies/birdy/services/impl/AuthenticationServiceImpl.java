package com.newbies.birdy.services.impl;

import com.newbies.birdy.dto.*;
import com.newbies.birdy.entities.Account;
import com.newbies.birdy.entities.Role;
import com.newbies.birdy.entities.Shop;
import com.newbies.birdy.entities.User;
import com.newbies.birdy.mapper.ShopMapper;
import com.newbies.birdy.mapper.UserMapper;
import com.newbies.birdy.repositories.AccountRepository;
import com.newbies.birdy.repositories.ShopRepository;
import com.newbies.birdy.repositories.UserRepository;
import com.newbies.birdy.security.jwt.JwtService;
import com.newbies.birdy.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final static String defaultImg ="https://firebasestorage.googleapis.com/v0/b/birdy-36c81.appspot.com/o/1.png?alt=media&token=1a2b6a42-f288-47b2-873f-fff9bbfb2893";

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ShopRepository shopRepository;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword()));
        var account = accountRepository.findByPhoneNumberAndStatus(request.getPhoneNumber(), true)
                .orElseThrow();
        var jwtToken = jwtService.generateToken(account, account.getRole().name());
        var roleName = account.getRole().name();
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    @Override
    public UserDTO createUser(RegisterUserRequest userInformationDTO) {
        Account account = accountRepository
                .save(new Account(null, userInformationDTO.getPhoneNumber(),
                        passwordEncoder.encode(userInformationDTO.getPassword()) ,0.0, Role.valueOf("USER"), true, null, null));

        User user = userRepository
                .save(new User(null, userInformationDTO.getFullName(), userInformationDTO.getEmail(),
                        userInformationDTO.getDob(), userInformationDTO.getGender(), defaultImg, new Date(),
                        true, account, null, null, null,null));
        return UserMapper.INSTANCE.toDTO(user);
    }

    @Override
    public ShopDTO createShop(RegisterShopRequest info) {
        Account account = accountRepository
                .save(new Account(null, info.getPhoneNumber(),
                        passwordEncoder.encode(info.getPassword()) , 0.0, Role.valueOf("SHOP"), true, null, null));
        Shop shop = shopRepository.save(new Shop(null, info.getEmail(), info.getShopName(), info.getAddress(), defaultImg,
                new Date(), true, account, null, null));
        return ShopMapper.INSTANCE.toDTO(shop);
    }
}
