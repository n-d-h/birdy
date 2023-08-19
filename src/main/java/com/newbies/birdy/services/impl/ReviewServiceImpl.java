package com.newbies.birdy.services.impl;

import com.newbies.birdy.dto.ReviewDTO;
import com.newbies.birdy.entities.OrderDetail;
import com.newbies.birdy.entities.Product;
import com.newbies.birdy.exceptions.entity.EntityNotFoundException;
import com.newbies.birdy.repositories.OrderDetailRepository;
import com.newbies.birdy.repositories.ProductRepository;
import com.newbies.birdy.services.ReviewService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public Map<List<ReviewDTO>, Long> getReviewByPageAndProductIdAndStatus( Pageable pageable, Integer productId, Boolean status) {
        Map<List<ReviewDTO>, Long> pair = new HashMap<>();
        Product product = productRepository.findByIdAndStatus(productId, true)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        Page<OrderDetail> pageList = orderDetailRepository.
                findByProductOrderDetailAndStatusAndRatingGreaterThan(product, status, 0, pageable);
        List<ReviewDTO> reviewList = new ArrayList<>();
        for (OrderDetail orderDetail : pageList) {
            reviewList.add(new ReviewDTO(orderDetail.getOrder().getPaymentMethod().getUserPaymentMethod().getFullName(),
                    orderDetail.getOrder().getPaymentMethod().getUserPaymentMethod().getAvatarUrl(),
                            orderDetail.getRating(), orderDetail.getComment()));
        }
        pair.put(reviewList, pageList.getTotalElements());
        return pair;
    }
}
