package com.sparta.legendofdelivery.domain.like.controller;

import com.sparta.legendofdelivery.MockSpringSecurityFilter;
import com.sparta.legendofdelivery.domain.like.service.LikeService;
import com.sparta.legendofdelivery.domain.review.entity.Review;
import com.sparta.legendofdelivery.domain.review.util.TestUtil;
import com.sparta.legendofdelivery.domain.store.entity.Category;
import com.sparta.legendofdelivery.domain.store.entity.Store;
import com.sparta.legendofdelivery.domain.user.entity.User;
import com.sparta.legendofdelivery.domain.user.entity.UserOauth;
import com.sparta.legendofdelivery.domain.user.entity.UserRole;
import com.sparta.legendofdelivery.domain.user.entity.UserStatus;
import com.sparta.legendofdelivery.domain.user.security.UserDetailsImpl;
import com.sparta.legendofdelivery.global.config.SecurityConfig;
import com.sparta.legendofdelivery.global.dto.MessageResponse;
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

import java.security.Principal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = LikeController.class, excludeFilters =
        {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        })
class LikeControllerTest {

    private MockMvc mvc;

    private Principal principal;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private LikeService likeService;

    User user = new User();
    Store store = new Store();
    Review review = new Review();

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
        TestUtil.setField(store, "id", 1L);
        TestUtil.setField(store, "name", "스토어가게");
        TestUtil.setField(store, "category", Category.CHINA);
        TestUtil.setField(store, "intro", "안녕하세요.");

        TestUtil.setField(review, "id", 1L);
        TestUtil.setField(review, "content", "");
        TestUtil.setField(review, "user", user);
        TestUtil.setField(review, "store", store);
    }

    @Test
    @DisplayName("가게 찜 등록")
    void addDibs() throws Exception {

        MessageResponse response = new MessageResponse(200, "좋아요 등록에 성공했습니다.");

        doNothing().when(likeService).addLike(any(Long.class), any(User.class));

        mvc.perform(post("/api/reviews/{reviewId}/like", review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(principal))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }
}