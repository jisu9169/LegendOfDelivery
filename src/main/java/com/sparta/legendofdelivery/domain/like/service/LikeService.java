package com.sparta.legendofdelivery.domain.like.service;

import static com.sparta.legendofdelivery.global.entity.ErrorCode.ALREADY_LIKED_REVIEW;
import static com.sparta.legendofdelivery.global.entity.ErrorCode.CANNOT_LIKE_OWN_REVIEW;
import static com.sparta.legendofdelivery.global.entity.ErrorCode.REVIEW_NOT_LIKED;

import com.sparta.legendofdelivery.domain.like.entity.Like;
import com.sparta.legendofdelivery.domain.like.repository.LikeRepository;
import com.sparta.legendofdelivery.domain.review.entity.Review;
import com.sparta.legendofdelivery.domain.review.service.ReviewService;
import com.sparta.legendofdelivery.domain.user.entity.User;
import com.sparta.legendofdelivery.global.exception.BadRequestException;
import com.sparta.legendofdelivery.global.exception.NotFoundException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

  private final LikeRepository likeRepository;

  private final ReviewService reviewService;

  @Transactional
  public void addLike(Long reviewId, User user) {

    Review review = reviewService.findByReviewId(reviewId);

    if (Objects.equals(review.getUser().getId(), user.getId())) {
      throw new BadRequestException(CANNOT_LIKE_OWN_REVIEW.getMessage());
    }
    if (null != findLikeByReviewIdAndUserId(reviewId, user.getId())) {
      throw new BadRequestException(ALREADY_LIKED_REVIEW.getMessage());
    }

    Like like = new Like(review, user);
    likeRepository.save(like);
    review.incrementLikeCount();

  }

  @Transactional
  public void deleteLike(Long reviewId, User user) {

    Review review = reviewService.findByReviewId(reviewId);
    Like checkIslike = findLikeByReviewIdAndUserId(reviewId, user.getId());
    if (null == checkIslike ) {
      throw new NotFoundException(REVIEW_NOT_LIKED.getMessage());
    }

    likeRepository.delete(checkIslike);
    review.decrementLikeCount();
  }

  private Like findLikeByReviewIdAndUserId(Long reviewId, Long userId) {
    return likeRepository.findLikeByReviewIdAndUserId(reviewId, userId).orElse(null);
  }

}
