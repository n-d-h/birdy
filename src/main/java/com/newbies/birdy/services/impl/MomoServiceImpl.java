package com.newbies.birdy.services.impl;

import com.newbies.birdy.dto.Response;
import com.newbies.birdy.repositories.OrderRepository;
import com.newbies.birdy.services.MomoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MomoServiceImpl implements MomoService {

    private final OrderRepository orderRepository;

    @Value("${MOMO_PARTNER_CODE}")
    private String partnerCode;
    @Value("${MOMO_ACCESS_KEY}")
    private String accessKey;
    @Value("${MOMO_SECRET_KEY}")
    private String secretKey;
    @Value("${MOMO_API_ENDPOINT}")
    private String endPoint;
    @Value("${MOMO_RETURN_URL}")
    private String returnUrl;
    @Value("${MOMO_NOTIFY_URL}")
    private String notifyUrl;

    private String orderInfo = "PAY WITH MOMO";
    private String requestId = UUID.randomUUID().toString();
    private String requestType = "captureWallet";
    private String extraData = "";
    private String lang = "en";
    private String partnerName = "CAR RENTAL";
    private String storeId = "MoMoStore";

    /**
     * @param amount
     * @return payment url
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    @Override
    public Object getPaymentUrl(Long amount, String orderId)
            throws InvalidKeyException,
            NoSuchAlgorithmException,
            IOException, UnsupportedEncodingException {
        requestId = UUID.randomUUID().toString();

        String requestRawData = new StringBuilder()
                .append("accessKey").append("=").append(accessKey).append("&")
                .append("amount").append("=").append(amount).append("&")
                .append("extraData").append("=").append(extraData).append("&")
                .append("ipnUrl").append("=").append(notifyUrl).append("&")
                .append("orderId").append("=").append(orderId).append("&")
                .append("orderInfo").append("=").append(orderInfo).append("&")
                .append("partnerCode").append("=").append(partnerCode).append("&")
                .append("redirectUrl").append("=").append(returnUrl).append("&")
                .append("requestId").append("=").append(requestId).append("&")
                .append("requestType").append("=").append(requestType)
                .toString();

        String signature = signHmacSHA256(requestRawData, secretKey);

        HashMap<String, String> values = new HashMap<String, String>() {
            {
                put("partnerCode", partnerCode);
                put("partnerName", partnerName);
                put("storeId", storeId);
                put("requestId", requestId);
                put("amount", String.valueOf(amount));
                put("orderId", orderId);
                put("orderInfo", orderInfo);
                put("redirectUrl", returnUrl);
                put("ipnUrl", notifyUrl);
                put("lang", lang);
                put("extraData", extraData);
                put("requestType", requestType);
                put("signature", signature);
            }
        };

        WebClient.Builder builder = WebClient.builder();

        WebClient webClient = builder.build();

        Mono<Object> result = webClient.post()
                .uri(endPoint)
                .bodyValue(values)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class);

        return result.block();
    }

    @Override
    public Object getPaymentUrlIndividual(Long amount, String orderId)
            throws InvalidKeyException,
            NoSuchAlgorithmException,
            IOException, UnsupportedEncodingException {
        requestId = UUID.randomUUID().toString();
        String finalOrderId = UUID.randomUUID().toString()
                + orderId.substring(orderId.length() - 2);
        String requestRawData = new StringBuilder()
                .append("accessKey").append("=").append(accessKey).append("&")
                .append("amount").append("=").append(amount).append("&")
                .append("extraData").append("=").append(extraData).append("&")
                .append("ipnUrl").append("=").append(notifyUrl).append("&")
                .append("orderId").append("=").append(finalOrderId).append("&")
                .append("orderInfo").append("=").append(orderInfo).append("&")
                .append("partnerCode").append("=").append(partnerCode).append("&")
                .append("redirectUrl").append("=").append("http://localhost/api/v1/payment/momo-info/individual").append("&")
                .append("requestId").append("=").append(requestId).append("&")
                .append("requestType").append("=").append(requestType)
                .toString();

        String signature = signHmacSHA256(requestRawData, secretKey);

        HashMap<String, String> values = new HashMap<String, String>() {
            {
                put("partnerCode", partnerCode);
                put("partnerName", partnerName);
                put("storeId", storeId);
                put("requestId", requestId);
                put("amount", String.valueOf(amount));
                put("orderId", finalOrderId);
                put("orderInfo", orderInfo);
                put("redirectUrl", "http://localhost/api/v1/payment/momo-info/individual");
                put("ipnUrl", notifyUrl);
                put("lang", lang);
                put("extraData", extraData);
                put("requestType", requestType);
                put("signature", signature);
            }
        };

        WebClient.Builder builder = WebClient.builder();

        WebClient webClient = builder.build();

        Mono<Object> result = webClient.post()
                .uri(endPoint)
                .bodyValue(values)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class);

        return result.block();
    }

    @Override
    public Object getPaymentUrlRecharge(Long amount, Integer accountId)
            throws InvalidKeyException,
            NoSuchAlgorithmException,
            IOException, UnsupportedEncodingException {
        requestId = UUID.randomUUID().toString();
        String finalOrderId = UUID.randomUUID().toString()
                + accountId.toString();
        String requestRawData = new StringBuilder()
                .append("accessKey").append("=").append(accessKey).append("&")
                .append("amount").append("=").append(amount).append("&")
                .append("extraData").append("=").append(extraData).append("&")
                .append("ipnUrl").append("=").append(notifyUrl).append("&")
                .append("orderId").append("=").append(finalOrderId).append("&")
                .append("orderInfo").append("=").append(orderInfo).append("&")
                .append("partnerCode").append("=").append(partnerCode).append("&")
                .append("redirectUrl").append("=").append("http://localhost/api/v1/payment/momo-info/recharge").append("&")
                .append("requestId").append("=").append(requestId).append("&")
                .append("requestType").append("=").append(requestType)
                .toString();

        String signature = signHmacSHA256(requestRawData, secretKey);

        HashMap<String, String> values = new HashMap<String, String>() {
            {
                put("partnerCode", partnerCode);
                put("partnerName", partnerName);
                put("storeId", storeId);
                put("requestId", requestId);
                put("amount", String.valueOf(amount));
                put("orderId", finalOrderId);
                put("orderInfo", orderInfo);
                put("redirectUrl", "http://localhost/api/v1/payment/momo-info/recharge");
                put("ipnUrl", notifyUrl);
                put("lang", lang);
                put("extraData", extraData);
                put("requestType", requestType);
                put("signature", signature);
            }
        };

        WebClient.Builder builder = WebClient.builder();

        WebClient webClient = builder.build();

        Mono<Object> result = webClient.post()
                .uri(endPoint)
                .bodyValue(values)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class);

        return result.block();
    }

    /**
     * @param partnerCode
     * @param orderId
     * @param requestId
     * @param amount
     * @param orderInfo
     * @param orderType
     * @param transId
     * @param resultCode
     * @param message
     * @param payType
     * @param responseTime
     * @param extraData
     * @param signature
     * @return Response
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    @Override
    public Response reCheckAndResponseToClient(
            String partnerCode, String orderId, String requestId,
            String amount, String orderInfo, String orderType,
            String transId, String resultCode, String message,
            String payType, String responseTime, String extraData,
            String signature
    ) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String requestRawData = new StringBuilder()
                .append("accessKey").append("=").append(accessKey).append("&")
                .append("amount").append("=").append(amount).append("&")
                .append("extraData").append("=").append(extraData).append("&")
                .append("message").append("=").append(message).append("&")
                .append("orderId").append("=").append(orderId).append("&")
                .append("orderInfo").append("=").append(orderInfo).append("&")
                .append("orderType").append("=").append(orderType).append("&")
                .append("partnerCode").append("=").append(partnerCode).append("&")
                .append("payType").append("=").append(payType).append("&")
                .append("requestId").append("=").append(requestId).append("&")
                .append("responseTime").append("=").append(responseTime).append("&")
                .append("resultCode").append("=").append(resultCode).append("&")
                .append("transId").append("=").append(transId)
                .toString();

        String signRequest = signHmacSHA256(requestRawData, secretKey);

        if (!signRequest.equals(signature)) {
            Response res = Response.builder()
                    .message("INVALID SIGNATURE")
                    .status(resultCode)
                    .build();
            return res;
        }
        return Response.builder()
                .message(message)
                .status(resultCode)
                .build();
    }

    @Override
    public Response reCheckAndResponseToClientIndividual(
            String partnerCode, String orderId, String requestId,
            String amount, String orderInfo, String orderType,
            String transId, String resultCode, String message,
            String payType, String responseTime, String extraData,
            String signature
    ) throws InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        String requestRawData = new StringBuilder()
                .append("accessKey").append("=").append(accessKey).append("&")
                .append("amount").append("=").append(amount).append("&")
                .append("extraData").append("=").append(extraData).append("&")
                .append("message").append("=").append(message).append("&")
                .append("orderId").append("=").append(orderId).append("&")
                .append("orderInfo").append("=").append(orderInfo).append("&")
                .append("orderType").append("=").append(orderType).append("&")
                .append("partnerCode").append("=").append(partnerCode).append("&")
                .append("payType").append("=").append(payType).append("&")
                .append("requestId").append("=").append(requestId).append("&")
                .append("responseTime").append("=").append(responseTime).append("&")
                .append("resultCode").append("=").append(resultCode).append("&")
                .append("transId").append("=").append(transId)
                .toString();

        String signRequest = signHmacSHA256(requestRawData, secretKey);

        if (!signRequest.equals(signature)) {

            Response res = Response.builder()
                    .message("INVALID SIGNATURE")
                    .status(resultCode)
                    .build();
            return res;
        }

        return Response.builder()
                .message(message)
                .status(resultCode)
                .build();
    }



    /**
     * @param data
     * @param secretKey
     * @return signature
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws UnsupportedEncodingException
     */
    public String signHmacSHA256(String data, String secretKey)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return toHexString(rawHmac);
    }

    /**
     * @param bytes
     * @return hex string
     */
    public String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        formatter.close();
        return sb.toString();
    }
}

