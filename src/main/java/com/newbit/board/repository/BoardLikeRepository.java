package com.newbit.board.repository;

import com.newbit.board.entity.Board;
import com.newbit.board.entity.BoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    Optional<BoardLike> findByBoardAndUsername(Board board, String username);
}
