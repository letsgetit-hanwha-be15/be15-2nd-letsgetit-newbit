package com.newbit.newbitfeatureservice.post.repository;

import com.newbit.newbitfeatureservice.post.entity.Post;
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
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND p.likeCount >= :minLikes ORDER BY p.likeCount DESC")
    List<Post> findPopularPosts(@Param("minLikes") int minLikes);

    @Query("SELECT COALESCE(SUM(p.reportCount), 0) FROM Post p WHERE p.userId = :userId")
    int sumReportCountByUserId(@Param("userId") Long userId);

    @Query("SELECT p.userId FROM Post p WHERE p.id = :postId AND p.deletedAt IS NULL")
    Optional<Long> findUserIdByPostId(@Param("postId") Long postId);

}
