package com.newbit.newbitfeatureservice.post.service;

import com.newbit.newbitfeatureservice.post.repository.CommentRepository;
import com.newbit.newbitfeatureservice.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportCountService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public int getTotalReportCountByUserId(Long userId) {
        int postCount = postRepository.sumReportCountByUserId(userId);
        int commentCount = commentRepository.sumReportCountByUserId(userId);
        return postCount + commentCount;
    }
}
