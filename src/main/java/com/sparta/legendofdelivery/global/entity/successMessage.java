package com.sparta.legendofdelivery.global.entity;

import lombok.Getter;

@Getter
public enum successMessage {
  REVIEW_CREATED(201, "리뷰 생성에 성공했습니다."),
  STORE_REVIEWS_FETCHED(200, "가게 별 리뷰 목록 조회에 성공했습니다."),
  USER_REVIEWS_FETCHED(200, "사용자 별 리뷰 목록 조회에 성공했습니다."),
  REVIEW_DELETION_SUCCESS(200,"리뷰 삭제에 성공했습니다."),
  REVIEW_UPDATE_SUCCESS(200,"리뷰 수정에 성공했습니다."),


  LIKE_CREATED(200, "좋아요 등록에 성공했습니다."),
  LIKE_REMOVED(200, "좋아요 취소를 성공했습니다."),
  USER_LIKED_REVIEWS_FETCHED(200, "사용자가 좋아요 했던 게시글 목록 조회에 성공했습니다.");


  private final int status;
  private final String message;

  successMessage(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
