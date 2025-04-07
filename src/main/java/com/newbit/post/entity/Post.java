package com.newbit.post.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String content;

    @Column(name = "filename")
    private String filename;

    @Column(name = "filepath")
    private String filepath;
}
