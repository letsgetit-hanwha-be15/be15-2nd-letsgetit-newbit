package com.newbit.newbitfeatureservice.post.controller;

import com.newbit.newbitfeatureservice.post.dto.request.PostCreateRequest;
import com.newbit.newbitfeatureservice.post.service.PostImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/images")
public class PostImageController {

    private final PostImageService postImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> uploadImages(@RequestPart("images") List<MultipartFile> files) {
        List<String> imageUrls = postImageService.uploadImages(files);
        return ResponseEntity.ok(imageUrls);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        postImageService.deleteImage(imageUrl);
        return ResponseEntity.noContent().build();
    }
}