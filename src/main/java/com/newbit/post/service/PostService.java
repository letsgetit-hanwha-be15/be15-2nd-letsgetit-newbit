package com.newbit.post.service;

import com.newbit.post.entity.Post;
import com.newbit.post.entity.PostLike;
import com.newbit.post.repository.PostLikeRepository;
import com.newbit.post.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository likeRepository;

    // 글 작성 처리 (파일 업로드 포함)
    public void postWrite(Post post, MultipartFile file) throws Exception {
        // 파일이 없거나 비어있다면 파일 저장 없이 게시글 저장
        if (file == null || file.isEmpty()) {
            postRepository.save(post);
            return;
        }

        // 파일 저장 경로 설정
        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";
        File directory = new File(projectPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("업로드 파일 이름이 유효하지 않습니다.");
        }

        UUID uuid = UUID.randomUUID();
        String fileName = uuid + "_" + originalFilename;
        File saveFile = new File(projectPath, fileName);
        file.transferTo(saveFile);

        post.setFilename(fileName);
        post.setFilepath("/files/" + fileName);

        postRepository.save(post);
    }

    // 게시글 리스트 처리
    public Page<Post> postList(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    // 게시글 검색 처리
    public Page<Post> postSearch(String keyword, Pageable pageable) {
        return postRepository.findByTitleContaining(keyword, pageable);
    }

    // 특정 게시글 불러오기
    public Post postView(Integer id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
    }

    // 특정 게시글 삭제
    public void postDelete(Integer id) {
        postRepository.deleteById(id);
    }

    // 좋아요 토글 처리
    public boolean postLikeToggle(Integer postId, String username) {
        Post post = postView(postId);
        Optional<PostLike> existingLike = likeRepository.findByPostAndUsername(post, username);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            post.setLikeCount(post.getLikeCount() - 1);
        } else {
            PostLike like = new PostLike();
            like.setPost(post);
            like.setUsername(username);
            likeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
        }

        postRepository.save(post);
        return existingLike.isEmpty();
    }

    // 파일 없이 게시글 수정
    public void postUpdateWithoutFile(Post post) {
        postRepository.save(post);
    }

    // 이전에 사용하던 메서드명 통일을 위한 별칭 (필요에 따라 사용)
    public Post postGetById(Integer id) {
        return postView(id);
    }
}
