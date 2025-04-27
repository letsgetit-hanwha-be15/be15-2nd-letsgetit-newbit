package com.newbit.newbitfeatureservice.post.service;

import com.newbit.newbitfeatureservice.s3.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final S3Uploader s3Uploader;

    public String uploadImage(MultipartFile file) {
        return s3Uploader.upload(file, "posts");
    }

    public void deleteImage(String fileUrl) {
        s3Uploader.delete(fileUrl);
    }
}
