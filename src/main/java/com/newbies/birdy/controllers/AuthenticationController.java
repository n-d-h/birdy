package com.newbies.birdy.controllers;

import com.newbies.birdy.dto.*;
import com.newbies.birdy.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {

        return ResponseEntity.ok(service.authenticate(request));
    }

    @Operation(summary = "Create new User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Can't create user! Bad Request!"),
            @ApiResponse(responseCode = "201", description = "User created successfully!"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PostMapping("/user/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequest userInformationDTO){
        UserDTO user = service.createUser(userInformationDTO);
        if(user != null){
            return new ResponseEntity<>("User created successfully!", HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>("Creat user fail!",HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Create new shop")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Can't create Shop! Bad Request!"),
            @ApiResponse(responseCode = "201", description = "shop created successfully!"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PostMapping("/shop/register")
    public ResponseEntity<?> registerShop(@RequestBody RegisterShopRequest shopInformationDTO){
        ShopDTO shop = service.createShop(shopInformationDTO);
        if(shop != null){
            return new ResponseEntity<>("Shop created successfully!", HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>("Creat shop fail!",HttpStatus.BAD_REQUEST);
        }
    }
}
