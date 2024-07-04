package com.sparta.legendofdelivery.domain.like.controller;

import static com.sparta.legendofdelivery.global.entity.successMessage.LIKE_CREATED;
import static com.sparta.legendofdelivery.global.entity.successMessage.LIKE_REMOVED;
import static com.sparta.legendofdelivery.global.entity.successMessage.USER_LIKED_REVIEWS_FETCHED;

import com.sparta.legendofdelivery.domain.like.service.LikeService;
import com.sparta.legendofdelivery.domain.review.dto.UserReviewResponseDto;
import com.sparta.legendofdelivery.domain.user.security.UserDetailsImpl;
import com.sparta.legendofdelivery.global.dto.DataResponse;
import com.sparta.legendofdelivery.global.dto.MessageResponse;
import com.sparta.legendofdelivery.global.dto.PageRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {

  private final LikeService likeService;

  @PostMapping("/reviews/{reviewId}/like")
  public ResponseEntity<MessageResponse> addLike(@PathVariable Long reviewId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    likeService.addLike(reviewId, userDetails.getUser());

    return ResponseEntity.ok(
        new MessageResponse(
            LIKE_CREATED.getStatus(),
            LIKE_CREATED.getMessage()
        )
    );
  }

  @DeleteMapping("/reviews/{reviewId}/like")
  public ResponseEntity<MessageResponse> deleteLike(@PathVariable Long reviewId,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    likeService.deleteLike(reviewId, userDetails.getUser());

    return ResponseEntity.ok(
        new MessageResponse(
            LIKE_REMOVED.getStatus(),
            LIKE_REMOVED.getMessage()
        )
    );
  }

  @GetMapping("/reviews/my/like")
  public ResponseEntity<DataResponse<UserReviewResponseDto>> getLikeReviews(@RequestBody PageRequestDto requestDto,
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    return ResponseEntity.ok(
        new DataResponse<>(
            USER_LIKED_REVIEWS_FETCHED.getStatus(),
            USER_LIKED_REVIEWS_FETCHED.getMessage(),
            likeService.getLikeReviews(userDetails.getUser(), requestDto)
        )
    );
  }
}
