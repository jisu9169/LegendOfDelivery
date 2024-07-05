package com.sparta.legendofdelivery.domain.like.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.legendofdelivery.domain.like.entity.QLike;
import com.sparta.legendofdelivery.domain.review.entity.QReview;
import com.sparta.legendofdelivery.domain.review.entity.Review;
import com.sparta.legendofdelivery.domain.user.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Page<Review> findByReviewsByUser(User user, Pageable pageable) {
    QLike qLike = QLike.like;
    QReview qReview = QReview.review;

    List<Review> reviewList = jpaQueryFactory
        .select(qReview)
        .from(qLike)
        .join(qLike.review, qReview)
        .where(qLike.user.eq(user))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    long total = jpaQueryFactory
        .selectFrom(qLike)
        .where(qLike.user.eq(user))
        .fetch()
        .size();

    return new PageImpl<>(reviewList, pageable, total);
  }

  @Override
  public int countLikedReviewsByUser(Long userId) {
    QLike qLike = QLike.like;

    Long count = jpaQueryFactory
        .select(qLike.count())
        .from(qLike)
        .where(qLike.user.id.eq(userId))
        .fetchOne();
    return count !=null ? count.intValue() : 0 ;
  }

}
