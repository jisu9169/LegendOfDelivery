package com.sparta.legendofdelivery.domain.review.dto;

import com.sparta.legendofdelivery.domain.review.entity.Review;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class UserReviewResponseDto {

  private final String userId;
  private final Page<ReviewResponseDto> responseDtoList;

  public UserReviewResponseDto(String userId, Page<Review> reviews) {
    this.userId = userId;
    this.responseDtoList = reviews.map(ReviewResponseDto::new);
  }
}
