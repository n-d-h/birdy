package com.newbies.birdy.controllers;

import com.newbies.birdy.dto.AccountDTO;
import com.newbies.birdy.dto.AuthenticationRequest;
import com.newbies.birdy.exceptions.ObjectException;
import com.newbies.birdy.services.AccountService;
import com.newbies.birdy.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Account API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    private final UserService userService;

    @Operation(summary = "Check Phone Number if existed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Can't create address! Bad Request!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "200", description = "Return true if existed!"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/check-phone-number")
    public ResponseEntity<?> isPhoneNumberExist(@RequestParam("phoneNumber") String phoneNumber){
        AccountDTO accountDTO = accountService.getByPhoneNumber(phoneNumber, true);

        return ResponseEntity.ok(accountDTO!=null);
    }

    @Operation(summary = "Create new password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Can't create address! Bad Request!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "200", description = "Return true if success!"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody AuthenticationRequest authenticationRequest){
        return ResponseEntity.ok(accountService.updatePassword(authenticationRequest.getPhoneNumber(), authenticationRequest.getPassword()));
    }


}
