package com.newbies.birdy.controllers;

import com.newbies.birdy.dto.AddressDTO;
import com.newbies.birdy.dto.ReportDTO;
import com.newbies.birdy.dto.UserDTO;
import com.newbies.birdy.exceptions.ObjectException;
import com.newbies.birdy.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@SecurityRequirement(name = "Authorization")
@Tag(name = "User API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController  {

    private final AddressService addressService;
    private final UserService userService;

    private final WishlistService wishlistService;

    private final ReportService reportService;

    private final FirebaseStorageService firebaseStorageService;


    @Operation(summary = "Add product to Wishlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Can't create wihlist! Bad Request!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "201", description = "Created successfully! "),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PostMapping("/{user-id}/wishlist/{product-id}")
    public ResponseEntity<?> addWishlist(@PathVariable(name = "user-id") Integer userId,
                                         @PathVariable(name = "product-id") Integer productId){
        Boolean status = wishlistService.addWishlist(userId, productId);
        if(!status){
            return ResponseEntity.badRequest().body("Can't create wishlist!");
        }else{
            return ResponseEntity.status(HttpStatus.CREATED).body("Created successfully!");
        }
    }

    @Operation(summary = "Add product to Report")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Can't create report! Bad Request!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "201", description = "Created successfully! "),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PostMapping("/report")
    public ResponseEntity<?> addReport(@RequestBody ReportDTO reportDTO){
        Boolean status = reportService.addReport(reportDTO.getUserId(), reportDTO.getProductId(), reportDTO.getReason());
        if(!status){
            return ResponseEntity.badRequest().body("Can't create report!");
        }else{
            return ResponseEntity.status(HttpStatus.CREATED).body("Created successfully!");
        }
    }

    @Operation(summary = "Get product to Report")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Not found!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "200", description = "return successfully! "),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/{user-id}/product/{product-id}/report")
    public ResponseEntity<?> getReport(@PathVariable(name = "user-id") Integer userId,
                                       @PathVariable(name = "product-id") Integer productId){
        return ResponseEntity.ok(reportService.getReport(userId, productId));
    }

    @Operation(summary = "Delete product to Wishlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Can't create address! Bad Request!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "200", description = "Delete successfully!"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @DeleteMapping("/{user-id}/wishlist/{product-id}")
    public ResponseEntity<?> deleteWishlist(@PathVariable(name = "user-id") Integer userId,
                                         @PathVariable(name = "product-id") Integer productId){
        Boolean status = wishlistService.deleteWishlist(userId, productId);
        if(!status){
            return ResponseEntity.badRequest().body("Can't delete wishlist!");
        }else{
            return ResponseEntity.ok("Deleted successfully!");
        }
    }

    @Operation(summary = "Delete product to Wishlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Not Found!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "200", description = "return wishlistDto !"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @GetMapping("/{user-id}/wishlist/{product-id}")
    public ResponseEntity<?> getWishlist(@PathVariable(name = "user-id") Integer userId,
                                            @PathVariable(name = "product-id") Integer productId){
        return ResponseEntity.ok(wishlistService.getWishlist(userId, productId));
    }

    @Operation(summary = "Create new Address for User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Can't create address! Bad Request!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "201", description = "Created successfully! Return address id!"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    @PostMapping("/{user-id}/addresses")
    public ResponseEntity<?> createAddress(@PathVariable(name = "user-id") Integer userId, @RequestBody AddressDTO addressDTO){
        Integer aid = addressService.createAddress(userId, addressDTO);
        if(aid == 0){
            return ResponseEntity.badRequest().body("Can't create address!");
        }else{
            return new ResponseEntity<>(aid, HttpStatus.CREATED);
        }
    }


    @Operation(summary = "Get All User Address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Address not found!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "200", description = "Return list AddressDTO")
    })
    @GetMapping("/{user-id}/addresses")
    public ResponseEntity<?> getAllUserAddress(@PathVariable(name = "user-id") Integer userId){
        List<AddressDTO> list = addressService.getAllUserAddress(userId, true);
        if(list.size() > 0){
            return ResponseEntity.ok(list);
        }else{
            return new ResponseEntity<>("Can't find any address!", HttpStatus.NOT_FOUND);
        }
    }



    @Operation(summary = "Update User Information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "User not found!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "200", description = "Return true")
    })
    @PutMapping("")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO){
        return ResponseEntity.ok(userService.updateUser(userDTO));
    }

    @Operation(summary = "Get User Information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "User not found!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "200", description = "Return UserDTO")
    })
    @GetMapping("/{user-id}")
    public ResponseEntity<?> getUserInformation(@PathVariable(name = "user-id") Integer userId){
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/{user-id}/email/{email}")
    public ResponseEntity<?> isEmailExisted(@PathVariable(name = "email") String email,
                                            @PathVariable(name = "user-id") Integer userId){
        return ResponseEntity.ok(userService.isEmailExited(userId, email));
    }

    @Operation(summary = "Get User by Phone Number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Not found!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "200", description = "Return UserDTO")
    })
    @GetMapping("/phone/{phone-number}")
    public ResponseEntity<?> getUserByPhoneNumber(@PathVariable(name = "phone-number") String phoneNumber){
        UserDTO user = userService.getUserByPhoneNumber(phoneNumber, true);
        if(user == null){
            return new ResponseEntity<>("User not found!", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Get Defaul Address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Not found!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "200", description = "Return AddressDTO")
    })
    @GetMapping("/{user-id}/address-default")
    public ResponseEntity<?> getDefaultAddress(@PathVariable(name = "user-id") Integer userId){
        AddressDTO addressDTO = userService.getUserDefaultAddress(userId);
        return ResponseEntity.ok(addressDTO);
    }

    @PostMapping("/{user-id}/avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam(name = "file") MultipartFile file,
                                          @PathVariable(name = "user-id") Integer userId) {
        String fileName = null;
        String imageUrl ="";
        try{
            fileName = firebaseStorageService.uploadFile(file);
            imageUrl = firebaseStorageService.getImageUrl(fileName);
            UserDTO userDTO = userService.getUserById(userId);
            //firebaseStorageService.deleteFile(userDTO.getAvatarUrl());
            userDTO.setAvatarUrl(imageUrl);
            userService.updateUser(userDTO);
        }
        catch (IOException e){
            throw new RuntimeException("Can't upload file!");
        }

        return ResponseEntity.ok(imageUrl);
    }
}
