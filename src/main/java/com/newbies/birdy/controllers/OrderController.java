package com.newbies.birdy.controllers;

import com.newbies.birdy.dto.OrderCreateRequestDTO;
import com.newbies.birdy.dto.OrderDTO;
import com.newbies.birdy.dto.OrderDetailDTO;
import com.newbies.birdy.exceptions.ObjectException;
import com.newbies.birdy.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Order API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    private final OrderDetailService orderDetailService;

    private final AddressService addressService;

    private final ShopService shopService;

    private final GoogleDistantMatrixService googleDistantMatrixService;

    private final ShipmentService shipmentService;

    private final UserService userService;

    @Operation(summary = "Get All Order Detail By Order Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Order Detail not found!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "200", description = "Return List OrderDetailDTO")
    })
    @GetMapping("/order-detail/{order-id}")
    public ResponseEntity<?> getOrderDetailByOrderId(@PathVariable(name = "order-id") Integer orderId) {
        List<OrderDetailDTO> list = orderDetailService.getOrderDetailsByOrderIdAndStatus(orderId, true);
        if (list == null) {
            return new ResponseEntity<>("No order details found", HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok(list);
        }
    }

    @Operation(summary = "Get All Order By User Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Order or user not found!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "200", description = "Return List OrderDTO")
    })
    @GetMapping("/user/{user-id}")
    public ResponseEntity<?> getAllOrdersByUserId(@PathVariable(name = "user-id") Integer userId) {
        List<OrderDTO> list = orderService.getAllOrdersByUserIdAndStatus(userId, true);
        if (list.isEmpty()) {
            return new ResponseEntity<>("No orders found", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get shipment price by shop id, address id and shipment id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Not Found!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "200", description = "Return Shipment price")
    })
    @GetMapping("/shipment-price")
    public ResponseEntity<?> getShippingFee(@RequestParam(name = "shopId") Integer shopId,
                                            @RequestParam(name = "addressId") Integer addressId,
                                            @RequestParam(name = "shipmentId") Integer shipmentId) throws Exception {
        String shopAddress = shopService.getShopAddress(shopId);
        String address = addressService.getAddressById(addressId);
        if (shopAddress == null || address == null) {
            return new ResponseEntity<>("Can not find shop or address", HttpStatus.NOT_FOUND);
        }
        Double price = shipmentService.getShipmentPriceById(shipmentId, true);
        Long distance = googleDistantMatrixService.getData(shopAddress, address);
        System.out.println(distance);
        Double shipmentPrice = price * distance / 1000;
        return ResponseEntity.ok(shipmentPrice);
    }


    @Operation(summary = "Create list order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Not Found!", content = @Content(schema = @Schema(implementation = ObjectException.class))),
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "200", description = "Return Order code"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping("")
    public ResponseEntity<?> createOrder(@RequestBody OrderCreateRequestDTO orderRequest) throws Exception {
        List<OrderDTO> list = orderRequest.getOrderList();
        List<OrderDetailDTO> listDetail = orderRequest.getOrderDetailList();
//        String code = null;
//        Order parentOrder = orderService.createParentOrder(list.get(0));
//        System.out.println("tao xong order");
//        if(parentOrder != null){
//            orderService.saveDetailforOrder(parentOrder, listDetail);
//            list.remove(0);
//            list.forEach(o ->{
//                o.setCode(parentOrder.getCode());
//                Order order = orderService.createOtherOrder(o, parentOrder);
//                orderService.saveDetailforOrder(order, listDetail);
//            });
//            code = parentOrder.getCode();
//        }

        String code = orderService.createOrder(list, listDetail, orderRequest.getAddressId());

        if (!code.isEmpty()) {
            return ResponseEntity.ok(code);
        } else {
            return new ResponseEntity<>("Creat order failed", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Edit order state by order id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Not Found!", content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "200", description = "Return Order code"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PatchMapping("/edit/{order-id}")
    public ResponseEntity<?> editOrderState(@PathVariable(name = "order-id") Integer orderId,
                                            @RequestParam(name = "state") String state,
                                            @RequestParam(name = "comment") String comment) {
        Boolean edited = orderService.editOrderState(orderId, state, comment);
        switch (state.toUpperCase()) {
            case "CANCELED":
                orderService.cancelOrder(orderId);
                break;
            case "DONE":
                orderService.doneOrder(orderId);
                break;
            case "DELIVERING":

                break;
        }
        if (Boolean.TRUE.equals(edited)) {
            return new ResponseEntity<>("Edit order state successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Edit order failed", HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Edit order state by order id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Not Found!", content = @Content(schema = @Schema(implementation = OrderDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal error"),
            @ApiResponse(responseCode = "200", description = "Return Order code"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PutMapping("/order-detail/list")
    public ResponseEntity<?> updateOrderDetails(@RequestBody List<OrderDetailDTO> orderDetailDTOList) {
        Boolean edited = orderDetailService.updateOrderDetails(orderDetailDTOList);
        if (edited) {
            return new ResponseEntity<>("Update successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>(" failed", HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "User Pay Order")
    @PutMapping("/{order-id}/user/{user-id}/payment")
    public ResponseEntity<?> payOrder(@RequestParam(name = "amount") Double amount,
                                      @PathVariable(name = "order-id") Integer orderId,
                                      @PathVariable(name = "user-id") Integer userId) {
        Boolean paid = orderService.payOrder(orderId, userId, amount);
        if (paid) {
            return new ResponseEntity<>("Pay successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Pay failed", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "User Pay Order with balance")
    @PutMapping("/payment")
    public ResponseEntity<?> payOrderCheckout(@RequestParam(name = "amount") Double amount,
                                              @RequestParam(name = "code") String code,
                                              @RequestParam(name = "userId") Integer userId) {
        Boolean paid = orderService.payOrderByBalance(code, userId, amount);
        if (paid) {
            return new ResponseEntity<>("Pay successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Pay failed", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Update Order Detail")
    @PutMapping("/detail")
    public ResponseEntity<?> payOrderCheckout(@RequestBody OrderDetailDTO orderDetail) {
        Boolean status = orderDetailService.updateOrderDetail(orderDetail);
        if (status) {
            return new ResponseEntity<>("Pay successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Pay failed", HttpStatus.BAD_REQUEST);
        }
    }
}
