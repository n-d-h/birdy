package com.newbies.birdy.controllers;

import com.newbies.birdy.dto.AddressDTO;
import com.newbies.birdy.exceptions.ObjectException;
import com.newbies.birdy.services.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name ="Address API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/addresses")
public class AddressController  {

    private final AddressService addressService;

    @Operation(summary = "Get Address by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Not found!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "200", description = "Return AddressDTO"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/{address-id}")
    public ResponseEntity<?> getAddressById(@PathVariable(name = "address-id") Integer addressId){
        return ResponseEntity.ok(addressService.getAddressDTOById(addressId));
    }

    @Operation(summary = "Update Address for User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Can't create address! Bad Request!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "200", description = "Update successfully! Return address true!"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PutMapping("/")
    public ResponseEntity<?> updateAddress(@RequestBody AddressDTO address){
        Boolean status = addressService.updateAddress(address);
        if(!status){
            return ResponseEntity.badRequest().body("Can't update address!");
        }else{
            return ResponseEntity.ok(status);
        }
    }
}
