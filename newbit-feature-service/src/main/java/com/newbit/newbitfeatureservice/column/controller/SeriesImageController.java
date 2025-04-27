package com.newbit.newbitfeatureservice.column.controller;

import com.newbit.newbitfeatureservice.column.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/series/images")
@RequiredArgsConstructor
@Tag(name = "시리즈 이미지 API", description = "시리즈 이미지 업로드 관련 API")
public class SeriesImageController {

    private final ImageService imageService;

    @Operation(summary = "시리즈 썸네일 이미지 업로드", description = "시리즈 썸네일 이미지를 S3에 업로드합니다.")
    @PostMapping("/thumbnail")
    public ResponseEntity<String> uploadSeriesThumbnail(@RequestPart MultipartFile file) {
        String imageUrl = imageService.uploadSeriesThumbnail(file);
        return ResponseEntity.ok(imageUrl);
    }
}
