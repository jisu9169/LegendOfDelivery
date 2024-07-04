package com.sparta.legendofdelivery.domain.review.controller;

import static com.sparta.legendofdelivery.global.entity.successMessage.REVIEW_CREATED;
import static com.sparta.legendofdelivery.global.entity.successMessage.REVIEW_DELETION_SUCCESS;
import static com.sparta.legendofdelivery.global.entity.successMessage.REVIEW_UPDATE_SUCCESS;
import static com.sparta.legendofdelivery.global.entity.successMessage.STORE_REVIEWS_FETCHED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.legendofdelivery.MockSpringSecurityFilter;
import com.sparta.legendofdelivery.domain.review.dto.CreateReviewRequestDto;
import com.sparta.legendofdelivery.domain.review.dto.CreateReviewResponseDto;
import com.sparta.legendofdelivery.domain.review.dto.DeleteReviewRequestDto;
import com.sparta.legendofdelivery.domain.review.dto.StoreByReviewResponseDto;
import com.sparta.legendofdelivery.domain.review.dto.UpdateReviewRequestDto;
import com.sparta.legendofdelivery.domain.review.dto.UserReviewResponseDto;
import com.sparta.legendofdelivery.domain.review.entity.Review;
import com.sparta.legendofdelivery.domain.review.service.ReviewService;
import com.sparta.legendofdelivery.domain.store.entity.Category;
import com.sparta.legendofdelivery.domain.store.entity.Store;
import com.sparta.legendofdelivery.domain.user.entity.User;
import com.sparta.legendofdelivery.domain.user.entity.UserOauth;
import com.sparta.legendofdelivery.domain.user.entity.UserRole;
import com.sparta.legendofdelivery.domain.user.entity.UserStatus;
import com.sparta.legendofdelivery.domain.user.security.UserDetailsImpl;
import com.sparta.legendofdelivery.global.config.SecurityConfig;
import com.sparta.legendofdelivery.global.dto.DataResponse;
import com.sparta.legendofdelivery.domain.review.util.TestUtil;
import com.sparta.legendofdelivery.global.dto.MessageResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;



@WebMvcTest(value = ReviewController.class, excludeFilters =
    {
    @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = SecurityConfig.class
    )
})
class ReviewControllerTest {

  private MockMvc mvc;

  private Principal principal;

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ReviewService reviewService;

  User user = new User();
  User user2 = new User();

  Store store = new Store();

  Review review = new Review();
  Review review2 = new Review();
  Review review3 = new Review();
  Review review4 = new Review();

  @BeforeEach
  public void setUp() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(context)
        .apply(springSecurity(new MockSpringSecurityFilter()))
        .build();
    getPrincipal();
  }

  private void getPrincipal() throws Exception {

    TestUtil.setField(user, "id", 1L);
    TestUtil.setField(user, "userId", "test1234");
    TestUtil.setField(user, "password", "test1234!");
    TestUtil.setField(user, "name", "이름");
    TestUtil.setField(user, "role", UserRole.USER);
    TestUtil.setField(user, "status", UserStatus.NORMAL);
    TestUtil.setField(user, "oauth", UserOauth.OUR);

    UserDetailsImpl userDetails = new UserDetailsImpl(user);

    principal = new UsernamePasswordAuthenticationToken(userDetails, null,
        userDetails.getAuthorities());

  }

  @BeforeEach
  void init() throws Exception {

    TestUtil.setField(user2, "id", 2L);
    TestUtil.setField(user2, "userId", "test1");
    TestUtil.setField(user2, "password", "test1234!");
    TestUtil.setField(user2, "name", "이름2");
    TestUtil.setField(user2, "role", UserRole.USER);
    TestUtil.setField(user2, "status", UserStatus.NORMAL);
    TestUtil.setField(user2, "oauth", UserOauth.OUR);

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
    TestUtil.setField(review3, "user", user2);
    TestUtil.setField(review3, "store", store);

    TestUtil.setField(review4, "id", 4L);
    TestUtil.setField(review4, "content", "");
    TestUtil.setField(review4, "user", user2);
    TestUtil.setField(review4, "store", store);

  }

  @Test
  @DisplayName("리뷰 생성")
  void createReview() throws Exception {
    CreateReviewRequestDto requestDto = new CreateReviewRequestDto();

    TestUtil.setField(requestDto, "storeId", 1L);
    TestUtil.setField(requestDto, "comment", "");

    CreateReviewResponseDto responseDto = new CreateReviewResponseDto(review);
    DataResponse<CreateReviewResponseDto> response =
        new DataResponse<>(REVIEW_CREATED.getStatus(), REVIEW_CREATED.getMessage(), responseDto);

    when(reviewService.createReview(any(CreateReviewRequestDto.class))).thenReturn(responseDto);

    String jsonRequest = objectMapper.writeValueAsString(requestDto);

    mvc.perform(post("/api/reviews")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest)
           .principal(principal))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();

  }

  @Test
  @DisplayName("가게 별 리뷰 목록 조회")
  void getStoreReviewList() throws Exception {
    List<Review> reviewList = new ArrayList<>();
    reviewList.add(review);
    reviewList.add(review2);
    reviewList.add(review3);
    reviewList.add(review4);

    StoreByReviewResponseDto storeByReviewResponseDto = new StoreByReviewResponseDto(store.getId(),user.getUserId(), reviewList);
    DataResponse<StoreByReviewResponseDto> response =
        new DataResponse<>(STORE_REVIEWS_FETCHED.getStatus(), STORE_REVIEWS_FETCHED.getMessage(), storeByReviewResponseDto);

    when(reviewService.getStoreReviewList(any(Long.class))).thenReturn(storeByReviewResponseDto);

    mvc.perform(get("/api/reviews/stores/{storedId}", store.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .principal(principal))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();
  }

  @Test
  @DisplayName("유저 별 리뷰 목록 조회")
  void getUserReviewList() throws Exception {
    List<Review> reviewList = new ArrayList<>();
    reviewList.add(review);
    reviewList.add(review2);

    UserReviewResponseDto userReviewResponseDto = new UserReviewResponseDto(user.getUserId(), reviewList);
    DataResponse<UserReviewResponseDto> response = new DataResponse<>(STORE_REVIEWS_FETCHED.getStatus(), STORE_REVIEWS_FETCHED.getMessage(), userReviewResponseDto);

    when(reviewService.getUserReviewList()).thenReturn(userReviewResponseDto);

    mvc.perform(get("/api/reviews/users")
            .contentType(MediaType.APPLICATION_JSON)
            .principal(principal))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();
  }

  @Test
  @DisplayName("리뷰 삭제")
  void deleteReview() throws Exception {
    DeleteReviewRequestDto requestDto = new DeleteReviewRequestDto();
    TestUtil.setField(requestDto, "password", "test1234!");

    MessageResponse response = new MessageResponse(REVIEW_DELETION_SUCCESS.getStatus(), REVIEW_DELETION_SUCCESS.getMessage());

    doNothing().when(reviewService).deleteReview(any(Long.class), any(DeleteReviewRequestDto.class));


    String jsonRequest = objectMapper.writeValueAsString(requestDto);

    mvc.perform(delete("/api/reviews/{reviewId}", review.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest)
            .principal(principal))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();
  }

  @Test
  @DisplayName("리뷰 수정")
  void updateReview() throws Exception {
    UpdateReviewRequestDto requestDto = new UpdateReviewRequestDto();
    TestUtil.setField(requestDto, "storeId", 1L);
    TestUtil.setField(requestDto, "password", "test1234!");
    TestUtil.setField(requestDto, "content", "hi");

    MessageResponse response = new MessageResponse(REVIEW_UPDATE_SUCCESS.getStatus(), REVIEW_UPDATE_SUCCESS.getMessage());

    doNothing().when(reviewService).updateReview(any(Long.class), any(UpdateReviewRequestDto.class));



    String jsonRequest = objectMapper.writeValueAsString(requestDto);


    mvc.perform(put("/api/reviews/{reviewId}", review.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest)
            .principal(principal))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();
  }
}