package com.sparta.legendofdelivery.domain.review.controller;


import static com.sparta.legendofdelivery.global.entity.successMessage.REVIEW_DELETION_SUCCESS;
import static com.sparta.legendofdelivery.global.entity.successMessage.REVIEW_UPDATE_SUCCESS;
import static com.sparta.legendofdelivery.global.entity.successMessage.STORE_REVIEWS_FETCHED;
import static com.sparta.legendofdelivery.global.entity.successMessage.USER_REVIEWS_FETCHED;

import com.sparta.legendofdelivery.domain.review.dto.CreateReviewRequestDto;
import com.sparta.legendofdelivery.domain.review.dto.CreateReviewResponseDto;
import com.sparta.legendofdelivery.domain.review.dto.DeleteReviewRequestDto;
import com.sparta.legendofdelivery.global.dto.PageRequestDto;
import com.sparta.legendofdelivery.domain.review.dto.ReviewResponseDto;
import com.sparta.legendofdelivery.domain.review.dto.StoreByReviewResponseDto;
import com.sparta.legendofdelivery.domain.review.dto.UpdateReviewRequestDto;
import com.sparta.legendofdelivery.domain.review.dto.UserReviewResponseDto;
import com.sparta.legendofdelivery.domain.review.service.ReviewService;
import com.sparta.legendofdelivery.global.dto.DataResponse;
import com.sparta.legendofdelivery.global.dto.MessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping
  public ResponseEntity<DataResponse<CreateReviewResponseDto>> createReview(
      @Valid @RequestBody CreateReviewRequestDto requestDto) {

    return ResponseEntity.ok(
        new DataResponse<>(
            STORE_REVIEWS_FETCHED.getStatus(),
            STORE_REVIEWS_FETCHED.getMessage(),
            reviewService.createReview(requestDto)
        )
    );
  }

  @GetMapping("/{reviewId}")
  public ResponseEntity<DataResponse<ReviewResponseDto>> getReview(@PathVariable Long reviewId) {

    return ResponseEntity.ok(
        new DataResponse<>(
            STORE_REVIEWS_FETCHED.getStatus(),
            STORE_REVIEWS_FETCHED.getMessage(),
            reviewService.getReview(reviewId)
        )
    );
  }

  @GetMapping("/stores/{storeId}")
  public ResponseEntity<DataResponse<StoreByReviewResponseDto>> getStoreReviewList(
      @PathVariable Long storeId, @RequestBody PageRequestDto pageRequestDto
  ) {

    return ResponseEntity.ok(
        new DataResponse<>(
            STORE_REVIEWS_FETCHED.getStatus(),
            STORE_REVIEWS_FETCHED.getMessage(),
            reviewService.getStoreReviewList(storeId, pageRequestDto)
        )
    );
  }

  @GetMapping("/my")
  public ResponseEntity<DataResponse<UserReviewResponseDto>> getUserReviewList() {

    return ResponseEntity.ok(
        new DataResponse<>(
            USER_REVIEWS_FETCHED.getStatus(),
            USER_REVIEWS_FETCHED.getMessage(),
            reviewService.getUserReviewList()
        )
    );
  }

  @DeleteMapping("/{reviewId}")
  public ResponseEntity<MessageResponse> deleteReview(@PathVariable Long reviewId,
      @RequestBody DeleteReviewRequestDto requestDto) {

    reviewService.deleteReview(reviewId, requestDto);
    return ResponseEntity.ok(
        new MessageResponse(
            REVIEW_DELETION_SUCCESS.getStatus(),
            REVIEW_DELETION_SUCCESS.getMessage()
        )
    );
  }

  @PutMapping("/{reviewId}")
  public ResponseEntity<MessageResponse> updateReview(@PathVariable Long reviewId,
      @RequestBody UpdateReviewRequestDto requestDto) {

    return ResponseEntity.ok(
        new MessageResponse(
            REVIEW_UPDATE_SUCCESS.getStatus(),
            REVIEW_UPDATE_SUCCESS.getMessage()
        )
    );
  }



}
