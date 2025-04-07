package com.newbit.post.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostDto {
    private String title;
    private String content;
    private MultipartFile file;
}
