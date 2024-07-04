package com.sparta.legendofdelivery.domain.review.dto;

import com.sparta.legendofdelivery.domain.review.entity.Review;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ReviewResponseDto {

  private final Long reviewId;
  private final String content;
  private final LocalDateTime createAt;
  private final LocalDateTime modifiedAt;

  private final int likeCount;

  public ReviewResponseDto(Long reviewId, String content, LocalDateTime createAt,
      LocalDateTime modifiedAt, int likeCount) {
    this.reviewId = reviewId;
    this.content = content;
    this.createAt = createAt;
    this.modifiedAt = modifiedAt;
    this.likeCount = likeCount;
  }


  public ReviewResponseDto(Review review) {
    this.reviewId = review.getId();
    this.content = review.getContent();
    this.createAt = review.getCreateAt();
    this.modifiedAt = review.getModifiedAt();
    this.likeCount = review.getLikeCount();
  }
}
