package com.sparta.legendofdelivery.domain.review.dto;


import com.sparta.legendofdelivery.domain.review.entity.Review;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class StoreByReviewResponseDto {

  private final Long storeId;
  private final String userId;
  private final List<ReviewResponseDto> responseDtoList;

  public StoreByReviewResponseDto(Long storeId, String userId, List<Review> reviews) {

    this.storeId = storeId;
    this.userId = userId;
    this.responseDtoList = reviews.stream()
                                          .map(review -> new ReviewResponseDto(
                                                  review.getId(),
                                                  review.getContent(),
                                                  review.getCreateAt(),
                                                  review.getModifiedAt(),
                                              review.getLikeCount()
                                              )
                                          )
                                          .collect(Collectors.toList());

  }

}
