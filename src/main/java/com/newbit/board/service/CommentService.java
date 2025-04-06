package com.newbit.board.service;

import com.newbit.board.entity.Comment;
import com.newbit.board.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getComments(Integer boardId) {
        return commentRepository.findByBoardId(boardId);
    }

    public void addComment(Comment comment) {
        commentRepository.save(comment);
    }
}
