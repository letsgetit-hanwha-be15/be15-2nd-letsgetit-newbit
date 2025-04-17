package com.newbit.post.service;

import com.newbit.post.repository.CommentRepository;
import com.newbit.post.repository.PostRepository;
import com.newbit.user.port.ReportCountPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportCountService implements ReportCountPort {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    public int getTotalReportCountByUserId(Long userId) {
        int postCount = postRepository.sumReportCountByUserId(userId);
        int commentCount = commentRepository.sumReportCountByUserId(userId);
        return postCount + commentCount;
    }
}
