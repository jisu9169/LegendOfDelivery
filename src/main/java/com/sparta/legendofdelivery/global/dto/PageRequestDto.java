package com.sparta.legendofdelivery.global.dto;

import lombok.Getter;

@Getter
public class PageRequestDto {

  private int page;
  private int size;
  private String sortBy;
  private boolean isAsc;

}
