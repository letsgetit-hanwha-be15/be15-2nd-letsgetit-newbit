package com.newbit.post.service;

import com.newbit.post.entity.Post;
import com.newbit.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    // 글 작성 처리
    public void postWrite(Post post, MultipartFile file) throws Exception {
        if (file != null && !file.isEmpty()) {
            saveFile(post, file);
        }
        postRepository.save(post);
    }

    // 파일 저장 처리
    private void saveFile(Post post, MultipartFile file) throws Exception {
        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";
        File directory = new File(projectPath);
        if (!directory.exists()) directory.mkdirs();

        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID() + "_" + originalFilename;
        File saveFile = new File(projectPath, fileName);
        file.transferTo(saveFile);

        post.setFilename(fileName);
        post.setFilepath("/files/" + fileName);
    }
}
