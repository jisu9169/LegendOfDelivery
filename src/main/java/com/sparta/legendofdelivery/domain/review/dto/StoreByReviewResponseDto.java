package com.sparta.legendofdelivery.domain.review.dto;


import com.sparta.legendofdelivery.domain.review.entity.Review;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class StoreByReviewResponseDto {

  private final Long storeId;
  private final String userId;
  private final Page<ReviewResponseDto> responseDtoList;

  public StoreByReviewResponseDto(Long storeId, String userId, Page<Review> reviews) {

    this.storeId = storeId;
    this.userId = userId;
    this.responseDtoList = reviews.map(ReviewResponseDto::new);
  }
}
