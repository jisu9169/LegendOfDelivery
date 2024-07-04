package com.sparta.legendofdelivery.domain.review.repository;

import com.sparta.legendofdelivery.domain.review.entity.Review;
import com.sparta.legendofdelivery.domain.store.entity.Store;
import com.sparta.legendofdelivery.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

  Page<Review> findByUserAndStore(User user, Store store, Pageable pageable);

  int countByUserAndStore(User user, Store store);

  Page<Review> findByUser(User user, Pageable pageable);
}
