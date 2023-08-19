package com.newbies.birdy.services;

import com.newbies.birdy.dto.ReviewDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ReviewService {

    Map<List<ReviewDTO>, Long> getReviewByPageAndProductIdAndStatus(Pageable pageable, Integer productId, Boolean status);
}
