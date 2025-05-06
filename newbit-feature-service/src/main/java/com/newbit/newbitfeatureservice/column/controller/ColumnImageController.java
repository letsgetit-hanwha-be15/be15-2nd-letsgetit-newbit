package com.newbit.newbitfeatureservice.column.controller;

import com.newbit.newbitfeatureservice.column.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/columns")
@RequiredArgsConstructor
@Tag(name = "칼럼 이미지 API", description = "칼럼 이미지 업로드 관련 API ")
public class ColumnImageController {
    private final ImageService imageService;

    @Operation(summary = "칼럼 썸네일 이미지 업로드", description = "칼럼 썸네일 이미지를 S3에 업로드합니다.")
    @PostMapping("/thumbnails")
    public ResponseEntity<String> uploadColumnThumbnail(@RequestParam MultipartFile file) {
        String url = imageService.uploadColumnThumbnail(file);
        return ResponseEntity.ok(url);
    }

    @Operation(summary = "칼럼 본문 이미지 업로드", description = "칼럼 본문 이미지를 S3에 업로드합니다.")
    @PostMapping("/content-images")
    public ResponseEntity<String> uploadColumnContentImage(@RequestParam MultipartFile file) {
        String url = imageService.uploadColumnContentImage(file);
        return ResponseEntity.ok(url);
    }
}
