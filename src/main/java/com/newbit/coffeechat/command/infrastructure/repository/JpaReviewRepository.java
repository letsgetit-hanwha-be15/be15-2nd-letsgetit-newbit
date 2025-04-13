package com.newbit.coffeechat.command.infrastructure.repository;

import com.newbit.coffeechat.command.domain.aggregate.Review;
import com.newbit.coffeechat.command.domain.repository.ReviewRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReviewRepository extends ReviewRepository, JpaRepository<Review, Long> {
}
