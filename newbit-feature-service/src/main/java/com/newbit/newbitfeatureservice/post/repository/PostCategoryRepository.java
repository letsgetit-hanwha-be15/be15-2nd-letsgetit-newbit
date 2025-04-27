package com.newbit.newbitfeatureservice.post.repository;

import com.newbit.newbitfeatureservice.post.entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCategoryRepository extends JpaRepository<PostCategory, Long> {
}