package com.newbit.post.repository;

import com.newbit.post.entity.Post;
import com.newbit.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUsername(Post post, String username);
}
