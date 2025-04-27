package com.newbit.newbitfeatureservice.column.service;

import com.newbit.newbitfeatureservice.s3.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Uploader s3Uploader;

    public String uploadColumnThumbnail(MultipartFile file) {
        return s3Uploader.upload(file, "columns/thumbnails");
    }

    public String uploadColumnContentImage(MultipartFile file) {
        return s3Uploader.upload(file, "columns/content-images");
    }

    public String uploadSeriesThumbnail(MultipartFile file) {
        return s3Uploader.upload(file, "series/thumbnails");
    }
}
