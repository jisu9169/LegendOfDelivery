package com.sparta.legendofdelivery.domain.user.service;

import com.sparta.legendofdelivery.domain.like.repository.LikeRepository;
import com.sparta.legendofdelivery.domain.user.dto.*;
import com.sparta.legendofdelivery.domain.user.entity.User;
import com.sparta.legendofdelivery.domain.user.entity.UserOauth;
import com.sparta.legendofdelivery.domain.user.entity.UserRole;
import com.sparta.legendofdelivery.domain.user.entity.UserStatus;
import com.sparta.legendofdelivery.domain.user.repository.UserRepository;
import com.sparta.legendofdelivery.domain.user.security.UserDetailsImpl;
import com.sparta.legendofdelivery.global.dto.DataResponse;
import com.sparta.legendofdelivery.global.dto.MessageResponse;
import com.sparta.legendofdelivery.global.exception.BadRequestException;
import com.sparta.legendofdelivery.global.exception.NotFoundException;
import com.sparta.legendofdelivery.global.exception.UnauthorizedException;
import com.sparta.legendofdelivery.global.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final LikeRepository likeRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider,
        LikeRepository likeRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
      this.likeRepository = likeRepository;
    }

    @Transactional
    public MessageResponse signup(UserSignupRequestDto requestDto) {

        userRepository.findByUserId(requestDto.getUserId()).ifPresent( (el) -> {
            throw new BadRequestException("이미 존재하는 아이디입니다.");
        });

        User user = new User(requestDto, UserRole.USER, UserStatus.NORMAL, UserOauth.OUR);

        String encryptionPassword = passwordEncoder.encode(requestDto.getPassword());
        user.encryptionPassword(encryptionPassword);

        userRepository.save(user);

        return new MessageResponse(201, "회원가입에 성공했습니다.");

    }

    @Transactional
    public void logout() {

        User user = getUser();
        user.updateRefreshToken(null);

    }

    @Transactional
    public void withdrawal(UserWithdrawalRequestDto requestDto) {

        User user = getUser();

        checkPassword(requestDto.getPassword(), user.getPassword());

        if (user.getStatus() == UserStatus.LEAVE) {
            throw new NotFoundException("이미 탈퇴한 회원입니다.");
        }

        UserStatus status = UserStatus.LEAVE;

        user.updateStatus(status);
        user.updateRefreshToken(null);

    }

    @Transactional
    public HttpHeaders refreshToken(HttpServletRequest request) {

        String token = getRefreshToken(request);

        if (!StringUtils.hasText(token)) {
            throw new BadRequestException("잘못된 요청입니다.");
        }

        return validateToken(token);

    }

    @Transactional
    public MessageResponse updateUserRole() {

        User user = getUser();

        user.updateUserRole(UserRole.ADMIN);

        return new MessageResponse(200, "회원 권한 변경에 성공했습니다.");
    }

    public DataResponse<UserProfileResponseDto> getProfile() {

        User user = getUser();
        int myLikeReviewsCount = likeRepository.countLikedReviewsByUser(user.getId());
        log.info("myLikeReviewsCount: {}", myLikeReviewsCount);

        return new DataResponse<>(200, "프로필 조회에 성공했습니다.", new UserProfileResponseDto(user, myLikeReviewsCount));

    }

    @Transactional
    public DataResponse<UserProfileResponseDto> modifyProfile(UserProfileModifyRequestDto requestDto) {

        User user = getUser();

        checkPassword(requestDto.getPassword(), user.getPassword());

        if (StringUtils.hasText(requestDto.getEmail())) {
            user.updateEmail(requestDto.getEmail());
        }

        if (StringUtils.hasText(requestDto.getName())) {
            user.updateName(requestDto.getName());
        }

        userRepository.save(user);

        return new DataResponse<>(200, "프로필 수정에 성공했습니다.", new UserProfileResponseDto(user));

    }

    @Transactional
    public MessageResponse updatePassword(UserProfileUpdatePasswordRequestDto requestDto) {

        User user = getUser();

        checkPassword(requestDto.getPassword(), user.getPassword());

        if (requestDto.getNewPassword().equals(requestDto.getPassword())) {
            throw new BadRequestException("현재 비밀번호와 동일한 비밀번호로는 변경할 수 없습니다.");
        }

        if (!requestDto.getNewPassword().equals(requestDto.getCheckPassword())) {
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }

        List<String> passwordList = user.getPasswordList();

        for (String el : passwordList) {
            if (passwordEncoder.matches(requestDto.getNewPassword(), el)) {
                throw new BadRequestException("최근 3번 안에 사용한 비밀번호는 사용할 수 없습니다.");
            }
        }

        String encryptionPassword = passwordEncoder.encode(requestDto.getNewPassword());
        user.encryptionPassword(encryptionPassword);

        userRepository.save(user);

        return new MessageResponse(200, "비밀번호 수정에 성공했습니다.");

    }

    public User getUser() {

        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userRepository.findByUserId(userDetails.getUsername()).orElseThrow( () -> new NotFoundException("해당 회원은 존재하지 않습니다."));
    }

    private void checkPassword(String inputPassword, String dbPassword) {
        if (!passwordEncoder.matches(inputPassword, dbPassword)) {
            throw new BadRequestException("비밀번호를 확인해주세요.");
        }
    }

    private String getRefreshToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new BadRequestException("잘못된 요청입니다.");
        }

        for (Cookie el : cookies) {
            if ("refreshToken".equals(el.getName())) {
                return el.getValue();
            }
        }

        return null;
    }

    private HttpHeaders validateToken(String token) {

        try {

            Claims info = jwtProvider.getClaimsFromToken(token);
            User user = userRepository.findByUserId(info.getSubject()).orElseThrow( () -> new NotFoundException("해당 회원은 존재하지 않습니다."));

            if (!user.getRefreshToken().equals(token)) {
                throw new UnauthorizedException("유효하지 않은 토큰 입니다.");
            }

            return setHeaders(info, user);

        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("만료된 토큰 입니다.");
        } catch (JwtException e) {
            throw new UnauthorizedException("유효하지 않은 토큰 입니다.");
        }

    }

    private HttpHeaders setHeaders(Claims info, User user) {

        String accessToken = jwtProvider.createAccessToken(user.getUserId(), user.getRole().getRole());
        String refreshToken = jwtProvider.generateToken(user.getUserId(), user.getRole().getRole(), info.getExpiration());
        ResponseCookie responseCookie = jwtProvider.createCookieRefreshToken(refreshToken, info.getExpiration());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());

        user.updateRefreshToken(refreshToken);

        return headers;

    }

}
