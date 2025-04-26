package com.newbit.newbitfeatureservice.column.controller;

import com.newbit.newbitfeatureservice.column.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/series/images")
@RequiredArgsConstructor
public class SeriesImageController {

    private final ImageService imageService;

    @PostMapping("/thumbnail")
    public ResponseEntity<String> uploadSeriesThumbnail(@RequestPart MultipartFile file) {
        String imageUrl = imageService.uploadSeriesThumbnail(file);
        return ResponseEntity.ok(imageUrl);
    }
}
