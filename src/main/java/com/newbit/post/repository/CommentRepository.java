package com.newbit.post.repository;

import com.newbit.post.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndDeletedAtIsNull(Long postId);

}
