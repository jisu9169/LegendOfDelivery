package com.sparta.legendofdelivery.domain.like.controller;

import static com.sparta.legendofdelivery.global.entity.successMessage.LIKE_CREATED;
import static com.sparta.legendofdelivery.global.entity.successMessage.LIKE_REMOVED;

import com.sparta.legendofdelivery.domain.like.service.LikeService;
import com.sparta.legendofdelivery.domain.user.security.UserDetailsImpl;
import com.sparta.legendofdelivery.global.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
}
