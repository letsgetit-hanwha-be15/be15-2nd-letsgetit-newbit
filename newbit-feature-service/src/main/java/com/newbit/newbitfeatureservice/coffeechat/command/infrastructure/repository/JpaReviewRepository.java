package com.newbit.newbitfeatureservice.coffeechat.command.infrastructure.repository;

import com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate.Review;
import com.newbit.newbitfeatureservice.coffeechat.command.domain.repository.ReviewRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaReviewRepository extends ReviewRepository, JpaRepository<Review, Long> {
}
