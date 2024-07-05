package com.sparta.legendofdelivery.domain.user.dto;

import com.sparta.legendofdelivery.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserProfileResponseDto {

    private final String userId;
    private final String email;
    private final String name;

    private int count;

    public UserProfileResponseDto(User user, int count) {
        this.userId = user.getUserId();
        this.email = user.getEmail() != null ? user.getEmail() : "이메일을 등록하지 않았습니다.";
        this.name = user.getName();
        this.count = count;
    }

    public UserProfileResponseDto(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail() != null ? user.getEmail() : "이메일을 등록하지 않았습니다.";
        this.name = user.getName();
    }


}
