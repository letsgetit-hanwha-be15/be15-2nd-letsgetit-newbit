package com.newbit.newbitfeatureservice.post.service;

import com.newbit.newbitfeatureservice.s3.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final S3Uploader s3Uploader;

    public List<String> uploadImages(List<MultipartFile> files) {
        return files.stream()
                .map(file -> s3Uploader.upload(file, "posts"))
                .collect(Collectors.toList());
    }

    public void deleteImage(String fileUrl) {
        s3Uploader.delete(fileUrl);
    }
}
