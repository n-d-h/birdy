package com.newbies.birdy.services.impl;

import com.newbies.birdy.dto.OrderDetailDTO;
import com.newbies.birdy.entities.Order;
import com.newbies.birdy.entities.OrderDetail;
import com.newbies.birdy.entities.OrderState;
import com.newbies.birdy.entities.Product;
import com.newbies.birdy.exceptions.entity.EntityNotFoundException;
import com.newbies.birdy.mapper.OrderDetailMapper;
import com.newbies.birdy.repositories.OrderDetailRepository;
import com.newbies.birdy.repositories.OrderRepository;
import com.newbies.birdy.repositories.ProductRepository;
import com.newbies.birdy.services.OrderDetailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public List<OrderDetailDTO> getOrderDetailsByOrderIdAndStatus(Integer orderId, Boolean status) {
        Order order = orderRepository.findByIdAndStatus(orderId, status).orElseThrow(() -> new EntityNotFoundException("Order not found"));
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderAndStatus(order, status);
        return orderDetailList.stream().map(OrderDetailMapper.INSTANCE::toDTO).toList();
    }

    @Override
    public Boolean updateOrderDetails(List<OrderDetailDTO> orderDetailDTOs) {
        List<OrderDetail> orderDetailList = orderDetailDTOs.stream().map(OrderDetailMapper.INSTANCE::toEntity).toList();
        orderDetailList.forEach(orderDetail -> orderDetail.setStatus(true));
        return orderDetailRepository.saveAll(orderDetailList).size() == orderDetailDTOs.size();
    }

    @Override
    public Long countTotalRating(Integer productId) {
        Product product = productRepository.findByIdAndStatus(productId, true).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return orderDetailRepository.countByProductOrderDetailAndStatusAndRatingGreaterThan(product, true, 0);
    }

    @Override
    public Boolean checkProductIsInOrder(Integer productId) {
        return orderDetailRepository.countByProductOrderDetailAndStatus(productId) > 0;
    }

    @Override
    public Boolean checkProductIsInPendingOrder(Integer productId) {
        return orderDetailRepository.countByProductOrderDetailAndStatusAndOrderState(productId, OrderState.PENDING) > 0;
    }

    @Override
    public List<Integer> getListOrderIdProductChecked(Integer productId) {
        return orderDetailRepository.getListOrderIdProductChecked(productId, OrderState.PENDING);
    }

    @Override
    public Boolean updateOrderDetail(OrderDetailDTO orderDetailDTO) {
        OrderDetail order = OrderDetailMapper.INSTANCE.toEntity(orderDetailDTO);
        order.setStatus(true);
        return orderDetailRepository.save(order).getId() != null;
    }
}
