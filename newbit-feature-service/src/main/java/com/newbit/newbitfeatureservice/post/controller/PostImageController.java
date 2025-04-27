package com.newbit.newbitfeatureservice.post.controller;

import com.newbit.newbitfeatureservice.post.service.PostImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/images")
public class PostImageController {

    private final PostImageService postImageService;

    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestPart("image") MultipartFile file) {
        String imageUrl = postImageService.uploadImage(file);
        return ResponseEntity.ok(imageUrl);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        postImageService.deleteImage(imageUrl);
        return ResponseEntity.noContent().build();
    }
}
