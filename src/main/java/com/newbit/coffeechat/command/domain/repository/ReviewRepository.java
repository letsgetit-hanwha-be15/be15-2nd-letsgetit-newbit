package com.newbit.coffeechat.command.domain.repository;

import com.newbit.coffeechat.command.domain.aggregate.Review;

import java.util.Optional;

public interface ReviewRepository {
    Review save(Review review);

    Optional<Review> findByCoffeechatId(Long coffeechatId);
}
