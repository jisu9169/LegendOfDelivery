package com.sparta.legendofdelivery.domain.review.entity;

import com.sparta.legendofdelivery.domain.like.entity.Like;
import com.sparta.legendofdelivery.domain.review.dto.CreateReviewRequestDto;
import com.sparta.legendofdelivery.domain.review.dto.UpdateReviewRequestDto;
import com.sparta.legendofdelivery.domain.store.entity.Store;
import com.sparta.legendofdelivery.domain.user.entity.User;
import com.sparta.legendofdelivery.global.entity.Timestamped;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Review extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id")
  private Store store;

  @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Like> likeList = new ArrayList<>();

  public Review(CreateReviewRequestDto requestDto,Store store, User user) {
    this.content = requestDto.getComment();
    this.store = store;
    this.user = user;
  }

  public void updateReview(UpdateReviewRequestDto requestDto,Store store, User user) {
    this.content = requestDto.getContent();
    this.store = store;
    this.user = user;
  }

}
