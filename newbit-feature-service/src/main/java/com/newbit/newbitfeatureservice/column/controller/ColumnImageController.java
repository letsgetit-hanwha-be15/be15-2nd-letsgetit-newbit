package com.newbit.newbitfeatureservice.column.controller;

import com.newbit.newbitfeatureservice.column.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/columns")
@RequiredArgsConstructor
public class ColumnImageController {
    private final ImageService imageService;

    @PostMapping("/thumbnails")
    public ResponseEntity<String> uploadColumnThumbnail(@RequestParam MultipartFile file) {
        String url = imageService.uploadColumnThumbnail(file);
        return ResponseEntity.ok(url);
    }

    @PostMapping("/content-images")
    public ResponseEntity<String> uploadColumnContentImage(@RequestParam MultipartFile file) {
        String url = imageService.uploadColumnContentImage(file);
        return ResponseEntity.ok(url);
    }
}
