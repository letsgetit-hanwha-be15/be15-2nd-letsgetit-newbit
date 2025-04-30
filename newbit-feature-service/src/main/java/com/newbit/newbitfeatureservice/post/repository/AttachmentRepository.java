package com.newbit.newbitfeatureservice.post.repository;

import com.newbit.newbitfeatureservice.post.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByPostId(Long postId);
}
