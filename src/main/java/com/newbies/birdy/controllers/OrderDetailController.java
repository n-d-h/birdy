package com.newbies.birdy.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order Detail API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/order-details")
public class OrderDetailController {


}