package com.sparta.legendofdelivery.domain.like.repository;

import com.sparta.legendofdelivery.domain.review.entity.Review;
import com.sparta.legendofdelivery.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikeRepositoryCustom {


  Page<Review> findByReviewsByUser(User user, Pageable pageable);
}
