package com.newbit.post.repository;

import com.newbit.post.entity.Comment;
import com.newbit.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByPost(Post post);
}

