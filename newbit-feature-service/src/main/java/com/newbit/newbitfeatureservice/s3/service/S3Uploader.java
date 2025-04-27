package com.newbit.newbitfeatureservice.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));
            // ACL 설정 제거! (버킷 정책으로 접근 제어)
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 업로드에 실패했습니다.", e);
        }

        return amazonS3.getUrl(bucket, fileName).toString(); // 업로드된 파일의 URL 반환
    }

    public void delete(String fileUrl) {
        String key = fileUrl.substring(fileUrl.indexOf(bucket) + bucket.length() + 1); // 버킷 이후 경로 추출
        amazonS3.deleteObject(bucket, key);
    }
}