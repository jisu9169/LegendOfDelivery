package com.sparta.legendofdelivery.global.entity;

import lombok.Getter;

@Getter
public enum ErrorCode {
  REVIEW_CREATION_LIMIT_EXCEEDED("더 이상 리뷰 작성이 안됩니다."),
  REVIEW_NOT_FOUND("작성한 리뷰가 없습니다."),
  STORE_REVIEW_NOT_FOUND("해당 가게의 리뷰를 찾을 수 없습니다."),
  DELETE_REVIEW_PERMISSION_DENIED("권한이 없습니다."),
  SPECIFIED_REVIEW_NOT_FOUND("지정한 리뷰를 찾을 수 없습니다."),


  CANNOT_LIKE_OWN_REVIEW("본인이 작성한 리뷰에는 좋아요를 할 수 없습니다."),
  ALREADY_LIKED_REVIEW("이미 좋아요를 누른 리뷰입니다."),
  REVIEW_NOT_LIKED("해당 리뷰는 좋아요가 등록되어 있지 않습니다.");
  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }
}
