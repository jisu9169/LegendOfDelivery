package com.sparta.legendofdelivery.domain.review.service;

import static com.sparta.legendofdelivery.global.entity.ErrorCode.DELETE_REVIEW_PERMISSION_DENIED;
import static com.sparta.legendofdelivery.global.entity.ErrorCode.REVIEW_CREATION_LIMIT_EXCEEDED;
import static com.sparta.legendofdelivery.global.entity.ErrorCode.REVIEW_NOT_FOUND;
import static com.sparta.legendofdelivery.global.entity.ErrorCode.SPECIFIED_REVIEW_NOT_FOUND;
import static com.sparta.legendofdelivery.global.entity.ErrorCode.STORE_REVIEW_NOT_FOUND;
import static com.sparta.legendofdelivery.global.entity.successMessage.REVIEW_CREATED;
import static com.sparta.legendofdelivery.global.entity.successMessage.REVIEW_DELETION_SUCCESS;
import static com.sparta.legendofdelivery.global.entity.successMessage.REVIEW_UPDATE_SUCCESS;
import static com.sparta.legendofdelivery.global.entity.successMessage.STORE_REVIEWS_FETCHED;
import static com.sparta.legendofdelivery.global.entity.successMessage.USER_REVIEWS_FETCHED;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
import com.sparta.legendofdelivery.domain.review.util.TestUtil;
import com.sparta.legendofdelivery.domain.store.entity.Category;
import com.sparta.legendofdelivery.domain.store.entity.Store;
import com.sparta.legendofdelivery.domain.store.service.StoreService;
import com.sparta.legendofdelivery.domain.user.entity.User;
import com.sparta.legendofdelivery.domain.user.entity.UserOauth;
import com.sparta.legendofdelivery.domain.user.entity.UserRole;
import com.sparta.legendofdelivery.domain.user.entity.UserStatus;
import com.sparta.legendofdelivery.domain.user.service.UserService;
import com.sparta.legendofdelivery.global.dto.DataResponse;
import com.sparta.legendofdelivery.global.dto.MessageResponse;
import com.sparta.legendofdelivery.global.exception.BadRequestException;
import com.sparta.legendofdelivery.global.exception.NotFoundException;
import com.sparta.legendofdelivery.global.exception.UnauthorizedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private StoreService storeService;

  @Mock
  private UserService userService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private ReviewService reviewService;

  User user = new User();

  Store store = new Store();

  Review review = new Review();
  Review review2 = new Review();
  Review review3 = new Review();
  Review review4 = new Review();

  @BeforeEach
  void setUp() throws Exception {
    TestUtil.setField(user, "id", 1L);
    TestUtil.setField(user, "userId", "test1");
    TestUtil.setField(user, "password", "test1234!");
    TestUtil.setField(user, "name", "이름1");
    TestUtil.setField(user, "role", UserRole.USER);
    TestUtil.setField(user, "status", UserStatus.NORMAL);
    TestUtil.setField(user, "oauth", UserOauth.OUR);

    TestUtil.setField(store, "id", 1L);
    TestUtil.setField(store, "name", "스토어가게");
    TestUtil.setField(store, "category", Category.CHINA);
    TestUtil.setField(store, "intro", "안녕하세요.");

    TestUtil.setField(review, "id", 1L);
    TestUtil.setField(review, "content", "");
    TestUtil.setField(review, "user", user);
    TestUtil.setField(review, "store", store);

    TestUtil.setField(review2, "id", 2L);
    TestUtil.setField(review2, "content", "");
    TestUtil.setField(review2, "user", user);
    TestUtil.setField(review2, "store", store);

    TestUtil.setField(review3, "id", 3L);
    TestUtil.setField(review3, "content", "");
    TestUtil.setField(review3, "user", user);
    TestUtil.setField(review3, "store", store);

    TestUtil.setField(review4, "id", 4L);
    TestUtil.setField(review4, "content", "");
    TestUtil.setField(review4, "user", user);
    TestUtil.setField(review4, "store", store);
  }

  @Test
  @DisplayName("리뷰 생성 성공")
  void createReview_success() throws Exception {
    CreateReviewRequestDto requestDto = new CreateReviewRequestDto();
    TestUtil.setField(requestDto, "storeId", 1L);
    TestUtil.setField(requestDto, "comment", "");

    when(storeService.findStoreById(requestDto.getStoreId())).thenReturn(store);
    when(userService.getUser()).thenReturn(user);
    when(orderRepository.countByUserAndStore(user, store)).thenReturn(1);
    when(reviewRepository.countByUserAndStore(user, store)).thenReturn(0);

    when(reviewRepository.save(any(Review.class))).thenReturn(review);

    CreateReviewResponseDto response = reviewService.createReview(requestDto);

    assertNotNull(response);

    assertNotNull(response);
  }

  @Test
  @DisplayName("리뷰 생성 실패")
  void createReview_fail() throws Exception {
    CreateReviewRequestDto requestDto = new CreateReviewRequestDto();
    TestUtil.setField(requestDto, "storeId", 1L);
    TestUtil.setField(requestDto, "comment", "");

    when(storeService.findStoreById(requestDto.getStoreId())).thenReturn(store);
    when(userService.getUser()).thenReturn(user);
    when(orderRepository.countByUserAndStore(user, store)).thenReturn(0);
    when(reviewRepository.countByUserAndStore(user, store)).thenReturn(0);

    BadRequestException exception = assertThrows(BadRequestException.class,
        () -> reviewService.createReview(requestDto));

    System.out.println(exception.getMessage());
    System.out.println(REVIEW_CREATION_LIMIT_EXCEEDED.getMessage());
    assertEquals(REVIEW_CREATION_LIMIT_EXCEEDED.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("가게별 리뷰 리스트 성공")
  void getStoreReviewList_success() {
    when(storeService.findStoreById(store.getId())).thenReturn(store);
    when(userService.getUser()).thenReturn(user);
    List<Review> reviewList = new ArrayList<>();
    reviewList.add(review);
    reviewList.add(review2);
    reviewList.add(review3);
    reviewList.add(review4);
    when(reviewRepository.findByUserAndStore(user, store)).thenReturn(reviewList);

    DataResponse<StoreByReviewResponseDto> response = reviewService.getStoreReviewList(store.getId());
    assertNotNull(response);
    assertEquals(STORE_REVIEWS_FETCHED.getStatus(), response.getStatusCode());
    assertEquals(STORE_REVIEWS_FETCHED.getMessage(), response.getMessage());

    assertNotNull(response.getData());

    List<ReviewResponseDto> responseData = response.getData().getResponseDtoList();
    assertEquals(reviewList.size(), responseData.size());

    for (int i = 0; i < reviewList.size(); i++) {
      Review review1 = reviewList.get(i);
      ReviewResponseDto reviewResponseDto = responseData.get(i);
      assertEquals(review1.getId(), reviewResponseDto.getReviewId());
      assertEquals(review1.getContent(), reviewResponseDto.getContent());
    }
  }

  @Test
  @DisplayName("가게별 리뷰 리스트 실패")
  void getStoreReviewList_fail() {
    when(storeService.findStoreById(store.getId())).thenReturn(store);
    when(userService.getUser()).thenReturn(user);
    when(reviewRepository.findByUserAndStore(user, store)).thenReturn(null);

    NotFoundException exception = assertThrows(NotFoundException.class,
        () -> reviewService.getStoreReviewList(store.getId()));

    System.out.println(exception.getMessage());
    System.out.println(STORE_REVIEW_NOT_FOUND.getMessage());
    assertEquals(STORE_REVIEW_NOT_FOUND.getMessage(), exception.getMessage());

  }

  @Test
  @DisplayName("유저별 리뷰 리스트 성공")
  void getUserReviewList_success() {
    when(userService.getUser()).thenReturn(user);
    List<Review> reviewList = new ArrayList<>();
    reviewList.add(review);
    reviewList.add(review2);
    reviewList.add(review3);
    reviewList.add(review4);
    when(reviewRepository.findByUser(user)).thenReturn(reviewList);
    DataResponse<UserReviewResponseDto> response = reviewService.getUserReviewList();
    assertNotNull(response);
    assertEquals(USER_REVIEWS_FETCHED.getStatus(), response.getStatusCode());
    assertEquals(USER_REVIEWS_FETCHED.getMessage(), response.getMessage());

    assertNotNull(response.getData());

    List<ReviewResponseDto> responseData = response.getData().getResponseDtoList();
    assertEquals(reviewList.size(), responseData.size());

    for (int i = 0; i < reviewList.size(); i++) {
      Review review1 = reviewList.get(i);
      ReviewResponseDto reviewResponseDto = responseData.get(i);
      assertEquals(review1.getId(), reviewResponseDto.getReviewId());
      assertEquals(review1.getContent(), reviewResponseDto.getContent());
    }
  }

  @Test
  @DisplayName("유저별 리뷰 리스트 실패")
  void getUserReviewList_fail() {
    when(userService.getUser()).thenReturn(user);
    when(reviewRepository.findByUser(user)).thenReturn(null);
    NotFoundException exception = assertThrows(NotFoundException.class,
        () -> reviewService.getUserReviewList());

    System.out.println(exception.getMessage());
    System.out.println(REVIEW_NOT_FOUND.getMessage());
    assertEquals(REVIEW_NOT_FOUND.getMessage(), exception.getMessage());
  }

  @Test
  @DisplayName("리뷰 삭제 성공")
  void deleteReview_success() throws Exception {
    DeleteReviewRequestDto requestDto = new DeleteReviewRequestDto();
    TestUtil.setField(requestDto, "password", "test1234!");
    System.out.println(review.getId());
    when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));

//    when(reviewService.findByReviewId(review.getId())).thenReturn(review);
    when(userService.getUser()).thenReturn(user);
    when(passwordEncoder.matches(user.getPassword(), user.getPassword())).thenReturn(true);

    MessageResponse response = reviewService.deleteReview(review.getId(), requestDto);
    assertNotNull(response);
    assertEquals(REVIEW_DELETION_SUCCESS.getStatus(), response.getStatusCode());
    assertEquals(REVIEW_DELETION_SUCCESS.getMessage(), response.getMessage());
  }

  @Test
  @DisplayName("리뷰 수정")
  void updateReview() throws Exception {
    String changeContent = "바뀐 내용";
    UpdateReviewRequestDto requestDto = new UpdateReviewRequestDto();
    TestUtil.setField(requestDto, "storeId", store.getId());
    TestUtil.setField(requestDto, "content",changeContent);
    TestUtil.setField(requestDto, "password","test1234!");
    when(storeService.findStoreById(requestDto.getStoreId())).thenReturn(store);
    when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
    when(userService.getUser()).thenReturn(user);
    when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(true);
    MessageResponse response = reviewService.updateReview(review.getId(), requestDto);
    assertNotNull(response);
    assertEquals(REVIEW_UPDATE_SUCCESS.getStatus(), response.getStatusCode());
    assertEquals(REVIEW_UPDATE_SUCCESS.getMessage(), response.getMessage());

    System.out.println("requestDto.getContent() = " + requestDto.getContent());
    System.out.println("review.getContent() = " + review.getContent());
    assertEquals(requestDto.getContent(), review.getContent());

  }

  @Test
  @DisplayName("비밀 번호 실패")
  void validatePassword_fail(){
    UnauthorizedException exception = assertThrows(UnauthorizedException.class,
        () -> reviewService.validatePassword("password", "password"));
    System.out.println(exception.getMessage());
    System.out.println(DELETE_REVIEW_PERMISSION_DENIED.getMessage());
    assertEquals(DELETE_REVIEW_PERMISSION_DENIED.getMessage(), exception.getMessage());
  }


  @Test
  @DisplayName("리뷰 orElseThrow")
  void findByReviewId_fail(){
    NotFoundException exception = assertThrows(NotFoundException.class,
        () -> reviewService.findByReviewId(5L));
    System.out.println(exception.getMessage());
    System.out.println(SPECIFIED_REVIEW_NOT_FOUND.getMessage());
    assertEquals(SPECIFIED_REVIEW_NOT_FOUND.getMessage(), exception.getMessage());
  }
}