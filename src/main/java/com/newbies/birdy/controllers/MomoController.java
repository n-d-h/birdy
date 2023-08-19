package com.newbies.birdy.controllers;

import com.newbies.birdy.credential.Momo;
import com.newbies.birdy.dto.Response;
import com.newbies.birdy.services.AccountService;
import com.newbies.birdy.services.MomoService;
import com.newbies.birdy.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class MomoController {

    private final MomoService momoService;

    private final OrderService orderService;

    private final AccountService accountService;

    @GetMapping("/momo-info")
    public ResponseEntity<?> momoInfo(
            @RequestParam String partnerCode,
            @RequestParam String orderId,
            @RequestParam String requestId,
            @RequestParam String amount,
            @RequestParam String orderInfo,
            @RequestParam String orderType,
            @RequestParam String transId,
            @RequestParam String resultCode,
            @RequestParam String message,
            @RequestParam String payType,
            @RequestParam String responseTime,
            @RequestParam Optional<String> extraData,
            @RequestParam String signature
    ) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Response res = momoService.reCheckAndResponseToClient(
                partnerCode,orderId, requestId,
                amount, orderInfo, orderType,
                transId, resultCode, message,
                payType, responseTime, extraData.orElse(""), signature);
        log.info("res: {}", res);
        log.info(Momo.builder()
                .partnerCode(partnerCode)
                .orderId(orderId)
                .requestId(requestId)
                .amount(amount)
                .orderInfo(orderInfo)
                .orderType(orderType)
                .transId(transId)
                .resultCode(resultCode)
                .message(message)
                .payType(payType)
                .responseTime(responseTime)
                .extraData(extraData.orElse(""))
                .signature(signature)
                .build().toString());
        if(res.getStatus().equals("0")) {
            orderService.updateOrder(orderId);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:3000/"))
                    .build();
        }
        return ResponseEntity.ok(res);
    }


    @GetMapping("/momo-info/individual")
    public ResponseEntity<?> momoInfoIndividual(
            @RequestParam String partnerCode,
            @RequestParam String orderId,
            @RequestParam String requestId,
            @RequestParam String amount,
            @RequestParam String orderInfo,
            @RequestParam String orderType,
            @RequestParam String transId,
            @RequestParam String resultCode,
            @RequestParam String message,
            @RequestParam String payType,
            @RequestParam String responseTime,
            @RequestParam Optional<String> extraData,
            @RequestParam String signature
    ) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Response res = momoService.reCheckAndResponseToClientIndividual(
                partnerCode,orderId, requestId,
                amount, orderInfo, orderType,
                transId, resultCode, message,
                payType, responseTime, extraData.orElse(""), signature);
        log.info("res: {}", res);
        log.info(Momo.builder()
                .partnerCode(partnerCode)
                .orderId(orderId)
                .requestId(requestId)
                .amount(amount)
                .orderInfo(orderInfo)
                .orderType(orderType)
                .transId(transId)
                .resultCode(resultCode)
                .message(message)
                .payType(payType)
                .responseTime(responseTime)
                .extraData(extraData.orElse(""))
                .signature(signature)
                .build().toString());
        if(res.getStatus().equals("0")) {
            orderService.updateOrderIndividual(orderId);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:3000/"))
                    .build();
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping("/momo")
    public ResponseEntity<?> createOrder(@RequestParam Long amount, @RequestParam String orderId)
            throws InvalidKeyException,
            NoSuchAlgorithmException,
            UnsupportedEncodingException, IOException {
        Object object = momoService.getPaymentUrl(amount, orderId);
        return ResponseEntity.ok(object);
    }

    @GetMapping("/momo/individual")
    public ResponseEntity<?> createOrderIndividual(@RequestParam Long amount, @RequestParam String orderId)
            throws InvalidKeyException,
            NoSuchAlgorithmException,
            UnsupportedEncodingException, IOException {
        Object object = momoService.getPaymentUrlIndividual(amount, orderId);
        return ResponseEntity.ok(object);
    }

    @GetMapping("/momo/recharge")
    public ResponseEntity<?> createOrderRecharge(@RequestParam Long amount, @RequestParam Integer accountId)
            throws InvalidKeyException,
            NoSuchAlgorithmException,
            UnsupportedEncodingException, IOException {
        Object object = momoService.getPaymentUrlRecharge(amount, accountId);
        return ResponseEntity.ok(object);
    }

    @GetMapping("/momo-info/recharge")
    public ResponseEntity<?> momoInfoRecharge(
            @RequestParam String partnerCode,
            @RequestParam String orderId,
            @RequestParam String requestId,
            @RequestParam String amount,
            @RequestParam String orderInfo,
            @RequestParam String orderType,
            @RequestParam String transId,
            @RequestParam String resultCode,
            @RequestParam String message,
            @RequestParam String payType,
            @RequestParam String responseTime,
            @RequestParam Optional<String> extraData,
            @RequestParam String signature
    ) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Response res = momoService.reCheckAndResponseToClient(
                partnerCode,orderId, requestId,
                amount, orderInfo, orderType,
                transId, resultCode, message,
                payType, responseTime, extraData.orElse(""), signature);
        log.info("res: {}", res);
        log.info(Momo.builder()
                .partnerCode(partnerCode)
                .orderId(orderId)
                .requestId(requestId)
                .amount(amount)
                .orderInfo(orderInfo)
                .orderType(orderType)
                .transId(transId)
                .resultCode(resultCode)
                .message(message)
                .payType(payType)
                .responseTime(responseTime)
                .extraData(extraData.orElse(""))
                .signature(signature)
                .build().toString());
        if(res.getStatus().equals("0")) {
            Double balance = Double.parseDouble(amount);
            balance = balance / 23000;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            balance = Double.parseDouble(decimalFormat.format(balance));
            Integer accountId = Integer.parseInt(orderId.substring(36));
            accountService.updateAccountBalance(accountId, balance);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:3000/user"))
                    .build();
        }
        return ResponseEntity.ok(res);
    }
}

