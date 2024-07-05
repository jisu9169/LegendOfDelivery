package com.sparta.legendofdelivery.domain.like.repository;

import com.sparta.legendofdelivery.domain.review.entity.Review;
import com.sparta.legendofdelivery.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LikeRepositoryCustom {

  /*
  *  내가 좋아요한 댓글 모두 조회
  * */
  Page<Review> findByReviewsByUser(User user, Pageable pageable);

/*
*
* */
  int countLikedReviewsByUser(Long userId);
}
