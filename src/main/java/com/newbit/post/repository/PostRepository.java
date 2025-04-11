package com.newbit.post.repository;

import com.newbit.post.entity.Post;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR p.content LIKE CONCAT('%', :keyword, '%')")
    List<Post> searchByKeyword(@Param("keyword") String keyword);
    Optional<Post> findByIdAndDeletedAtIsNull(Long postId);
    List<Post> findByUserIdAndDeletedAtIsNull(Long userId);


}
