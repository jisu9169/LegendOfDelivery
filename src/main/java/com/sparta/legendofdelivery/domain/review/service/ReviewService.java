package com.sparta.legendofdelivery.domain.review.service;


import static com.sparta.legendofdelivery.global.entity.ErrorCode.DELETE_REVIEW_PERMISSION_DENIED;
import static com.sparta.legendofdelivery.global.entity.ErrorCode.REVIEW_CREATION_LIMIT_EXCEEDED;
import static com.sparta.legendofdelivery.global.entity.ErrorCode.REVIEW_NOT_FOUND;
import static com.sparta.legendofdelivery.global.entity.ErrorCode.SPECIFIED_REVIEW_NOT_FOUND;
import static com.sparta.legendofdelivery.global.entity.ErrorCode.STORE_REVIEW_NOT_FOUND;

import com.sparta.legendofdelivery.domain.order.repository.OrderRepository;
import com.sparta.legendofdelivery.domain.review.dto.CreateReviewRequestDto;
import com.sparta.legendofdelivery.domain.review.dto.CreateReviewResponseDto;
import com.sparta.legendofdelivery.domain.review.dto.DeleteReviewRequestDto;
import com.sparta.legendofdelivery.domain.review.dto.ReviewResponseDto;
import com.sparta.legendofdelivery.domain.review.dto.StoreByReviewResponseDto;
import com.sparta.legendofdelivery.domain.review.dto.UpdateReviewRequestDto;
import com.sparta.legendofdelivery.domain.review.dto.UserReviewResponseDto;
import com.sparta.legendofdelivery.domain.review.entity.Review;
import com.sparta.legendofdelivery.domain.review.repository.ReviewRepository;
import com.sparta.legendofdelivery.domain.store.entity.Store;
import com.sparta.legendofdelivery.domain.store.service.StoreService;
import com.sparta.legendofdelivery.domain.user.entity.User;
import com.sparta.legendofdelivery.domain.user.service.UserService;
import com.sparta.legendofdelivery.global.dto.PageRequestDto;
import com.sparta.legendofdelivery.global.exception.BadRequestException;
import com.sparta.legendofdelivery.global.exception.NotFoundException;
import com.sparta.legendofdelivery.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final OrderRepository orderRepository;

  private final StoreService storeService;
  private final UserService userService;

  private final PasswordEncoder passwordEncoder;

  @Transactional
  public CreateReviewResponseDto createReview(CreateReviewRequestDto requestDto) {

    Store store = storeService.findStoreById(requestDto.getStoreId());
    User user = userService.getUser();

    int orderCount = orderRepository.countByUserAndStore(user, store);
    int reviewCount = reviewRepository.countByUserAndStore(user, store);

    if (orderCount <= reviewCount) {
      throw new BadRequestException(REVIEW_CREATION_LIMIT_EXCEEDED.getMessage());
    }

    Review review = reviewRepository.save(new Review(requestDto, store, user));

    return new CreateReviewResponseDto(review);

  }

  public ReviewResponseDto getReview(Long reviewId) {
    Review review = findByReviewId(reviewId);
    return new ReviewResponseDto(review);
  }

  @Transactional(readOnly = true)
  public StoreByReviewResponseDto getStoreReviewList(Long storeId, PageRequestDto pagerequestDto) {
    Pageable pageable = getPageable(pagerequestDto);
    Store store = storeService.findStoreById(storeId);
    User user = userService.getUser();
    Page<Review> reviewList = reviewRepository.findByUserAndStore(user, store, pageable);
    if (null == reviewList) {
      throw new NotFoundException(STORE_REVIEW_NOT_FOUND.getMessage());
    }
    return new StoreByReviewResponseDto(storeId, user.getUserId(), reviewList);
  }

  @Transactional(readOnly = true)
  public UserReviewResponseDto getUserReviewList(PageRequestDto pagerequestDto) {
    User user = userService.getUser();
    Pageable pageable = getPageable(pagerequestDto);
    Page<Review> reviewList = reviewRepository.findByUser(user, pageable);
    if (null == reviewList) {
      throw new NotFoundException(REVIEW_NOT_FOUND.getMessage());
    }

    return new UserReviewResponseDto(user.getUserId(), reviewList);
  }

  @Transactional
  public void deleteReview(Long reviewId, DeleteReviewRequestDto requestDto) {
    Review review = findByReviewId(reviewId);
    User user = userService.getUser();
    validatePassword(requestDto.getPassword(), user.getPassword());

    reviewRepository.delete(review);
  }

  @Transactional
  public void updateReview(Long reviewId, UpdateReviewRequestDto requestDto) {
    Store store = storeService.findStoreById(requestDto.getStoreId());
    Review review = findByReviewId(reviewId);
    User user = userService.getUser();
    validatePassword(requestDto.getPassword(), user.getPassword());
    review.updateReview(requestDto, store, user);

  }

  public Pageable getPageable(PageRequestDto pagerequestDto) {
    Sort.Direction direction = pagerequestDto.isAsc() ? Sort.Direction.ASC : Sort.Direction.DESC;

    Sort sort = Sort.by(direction, pagerequestDto.getSortBy());
    return PageRequest.of(pagerequestDto.getPage() - 1, pagerequestDto.getSize(), sort);
  }

  public Review findByReviewId(Long reviewId) {
    return reviewRepository.findById(reviewId)
        .orElseThrow(() -> new NotFoundException(SPECIFIED_REVIEW_NOT_FOUND.getMessage()));
  }

  public void validatePassword(String inputPassword, String dbPassword) {
    if (!passwordEncoder.matches(inputPassword, dbPassword)) {
      throw new UnauthorizedException(DELETE_REVIEW_PERMISSION_DENIED.getMessage());
    }
  }


}
