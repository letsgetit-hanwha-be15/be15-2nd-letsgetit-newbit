package com.newbit.board.service;

import com.newbit.board.entity.Board;
import com.newbit.board.entity.BoardLike;
import com.newbit.board.repository.BoardLikeRepository;
import com.newbit.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    // 글 작성 처리
    public void write(Board board, MultipartFile file) throws Exception {

        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

        UUID uuid = UUID.randomUUID();

        String fileName = uuid + "_" + file.getOriginalFilename();

        File saveFile = new File(projectPath, fileName);

        file.transferTo(saveFile);

        board.setFilename(fileName);
        board.setFilepath("/files/" + fileName);

        boardRepository.save(board);
    }

    // 게시글 리스트 처리
    public Page<Board> boardList(Pageable pageable) {

        return boardRepository.findAll(pageable);
    }

    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable) {

        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    // 특정 게시글 불러오기
    public Board boardView(Integer id) {

        return boardRepository.findById(id).get();
    }

    // 특정 게시글 삭제
    public void boardDelete(Integer id) {

        boardRepository.deleteById(id);
    }

    // 좋아요 증가 처리
    public void increaseLike(Integer id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        board.setLikeCount(board.getLikeCount() + 1);
        boardRepository.save(board);
    }

    @Autowired
    private BoardLikeRepository likeRepository;

    public boolean toggleLike(Integer boardId, String username) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        // 이미 좋아요 눌렀는지 확인
        Optional<BoardLike> existingLike = likeRepository.findByBoardAndUsername(board, username);

        if (existingLike.isPresent()) {
            // 좋아요 취소
            likeRepository.delete(existingLike.get());
            board.setLikeCount(board.getLikeCount() - 1);
            boardRepository.save(board);
            return false; // 좋아요 취소됨
        } else {
            // 좋아요 추가
            BoardLike like = new BoardLike();
            like.setBoard(board);
            like.setUsername(username);
            likeRepository.save(like);

            board.setLikeCount(board.getLikeCount() + 1);
            boardRepository.save(board);
            return true; // 좋아요 추가됨
        }
    }



}
