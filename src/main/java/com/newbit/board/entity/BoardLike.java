package com.newbit.board.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BoardLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username; // 좋아요 누른 사용자

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;
}
