package com.newbies.birdy.services;

import com.newbies.birdy.dto.OrderDTO;
import com.newbies.birdy.dto.OrderDetailDTO;
import com.newbies.birdy.dto.OrderManageDTO;
import com.newbies.birdy.entities.Order;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface OrderService {

    List<OrderDTO> getAllOrdersByUserIdAndStatus(Integer userId, Boolean status);

    String createOrder(List<OrderDTO> orderDTOList, List<OrderDetailDTO> orderDetail, Integer addressId) throws Exception;

    Order createParentOrder(OrderDTO orderDTO);

    void saveDetailforOrder(Order order, List<OrderDetailDTO> orderDetailDTOList, Integer addressId) throws Exception;

    Order createOtherOrder(OrderDTO orderDTO, Order parentOrder);

    Map<List<OrderManageDTO>, Long> getAllOrdersByShopId(Integer shopId, String search, List<String> payment, List<String> state, Pageable pageable);

//    Map<List<OrderManageDTO>, Long> getAllOrderByShop(Integer shopId, String search, Pageable pageable);

    List<OrderManageDTO> getAllOrderByShop(Integer shopId, String search);

    Map<List<OrderManageDTO>, Integer> getAllOrdersByShopIdAndState(Integer shopId, String state, Pageable pageable);

    Boolean editOrderState(Integer orderId, String state, String comment);

    Boolean payOrder(Integer orderId, Integer userId, Double amount);

    Boolean payOrderByBalance(String code, Integer userId, Double amount);

    void cancelOrder(Integer orderId);

    void deliveryOrder(Integer orderId);

    void doneOrder(Integer orderId);

    void updateOrder(String code);

    void updateOrderIndividual(String code);

}
