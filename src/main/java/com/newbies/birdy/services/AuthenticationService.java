package com.newbies.birdy.services;

import com.newbies.birdy.dto.*;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);

    UserDTO createUser(RegisterUserRequest registerUserRequest);

    ShopDTO createShop(RegisterShopRequest registerShopRequest);
}
