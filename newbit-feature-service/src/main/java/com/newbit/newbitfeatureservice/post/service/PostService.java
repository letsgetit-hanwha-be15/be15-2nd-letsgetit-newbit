package com.newbit.newbitfeatureservice.post.service;

import com.newbit.newbitfeatureservice.client.user.UserFeignClient;
import com.newbit.newbitfeatureservice.client.user.dto.UserDTO;
import com.newbit.newbitfeatureservice.common.dto.ApiResponse;
import com.newbit.newbitfeatureservice.security.model.CustomUser;
import com.newbit.newbitfeatureservice.common.exception.BusinessException;
import com.newbit.newbitfeatureservice.common.exception.ErrorCode;
import com.newbit.newbitfeatureservice.post.dto.request.PostCreateRequest;
import com.newbit.newbitfeatureservice.post.dto.request.PostUpdateRequest;
import com.newbit.newbitfeatureservice.post.dto.response.CommentResponse;
import com.newbit.newbitfeatureservice.post.dto.response.PostDetailResponse;
import com.newbit.newbitfeatureservice.post.dto.response.PostResponse;
import com.newbit.newbitfeatureservice.post.entity.Comment;
import com.newbit.newbitfeatureservice.post.entity.Post;
import com.newbit.newbitfeatureservice.post.repository.CommentRepository;
import com.newbit.newbitfeatureservice.post.repository.PostRepository;
import com.newbit.newbitfeatureservice.purchase.command.application.service.PointTransactionCommandService;
import com.newbit.newbitfeatureservice.purchase.command.domain.PointTypeConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PointTransactionCommandService pointTransactionCommandService;
    private final UserFeignClient userFeignClient;

    @Transactional
    public PostResponse createPost(PostCreateRequest request, CustomUser user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.ONLY_USER_CAN_CREATE_POST);
        }

        boolean isUser = user.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_USER".equals(auth.getAuthority()));

        if (!isUser) {
            throw new BusinessException(ErrorCode.ONLY_USER_CAN_CREATE_POST);
        }

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .userId(user.getUserId())
                .postCategoryId(request.getPostCategoryId())
                .likeCount(0)
                .reportCount(0)
                .isNotice(false)
                .build();

        postRepository.save(post);
        pointTransactionCommandService.givePointByType(user.getUserId(), PointTypeConstants.POSTS, post.getId());
        return new PostResponse(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> searchPosts(String keyword) {
        List<Post> posts = postRepository.searchByKeyword(keyword);
        return posts.stream().map(PostResponse::new).toList();
    }

    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest request, CustomUser user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_TO_UPDATE_POST);
        }

        post.update(request.getTitle(), request.getContent());
        return new PostResponse(post);
    }

    @Transactional
    public void deletePost(Long postId, CustomUser user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_TO_DELETE_POST);
        }

        post.softDelete();
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getPostList(Pageable pageable) {
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.map(PostResponse::new);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        List<Comment> comments = commentRepository.findByPostIdAndDeletedAtIsNull(postId);
        List<CommentResponse> commentResponses = comments.stream()
                .map(CommentResponse::new)
                .toList();

        Long userId = post.getUserId();
        ApiResponse<UserDTO> userByUserId = userFeignClient.getUserByUserId(userId);
        String writerName = userByUserId.getData().getNickname();
        String categoryName = post.getPostCategory().getName();

        return new PostDetailResponse(post, commentResponses, writerName, categoryName);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getMyPosts(Long userId) {
        List<Post> posts = postRepository.findByUserIdAndDeletedAtIsNull(userId);
        return posts.stream()
                .map(PostResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPopularPosts() {
        List<Post> posts = postRepository.findPopularPosts(10);
        return posts.stream()
                .map(PostResponse::new)
                .toList();
    }

    @Transactional
    public PostResponse createNotice(PostCreateRequest request, CustomUser user) {
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

        if (!isAdmin) {
            throw new BusinessException(ErrorCode.ONLY_ADMIN_CAN_CREATE_NOTICE);
        }

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .userId(user.getUserId())
                .postCategoryId(request.getPostCategoryId())
                .likeCount(0)
                .reportCount(0)
                .isNotice(true)
                .build();

        postRepository.save(post);
        return new PostResponse(post);
    }

    @Transactional
    public PostResponse updateNotice(Long postId, PostUpdateRequest request, CustomUser user) {
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

        if (!isAdmin) {
            throw new BusinessException(ErrorCode.ONLY_ADMIN_CAN_UPDATE_NOTICE);
        }

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.isNotice()) {
            throw new BusinessException(ErrorCode.NOT_A_NOTICE);
        }

        post.update(request.getTitle(), request.getContent());
        return new PostResponse(post);
    }

    @Transactional
    public void deleteNotice(Long postId, CustomUser user) {
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));

        if (!isAdmin) {
            throw new BusinessException(ErrorCode.ONLY_ADMIN_CAN_DELETE_NOTICE);
        }

        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.isNotice()) {
            throw new BusinessException(ErrorCode.NOT_A_NOTICE);
        }

        post.softDelete();
    }

    @Transactional
    public void increaseLikeCount(Long postId) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        post.increaseLikeCount();
    }

    @Transactional
    public void decreaseLikeCount(Long postId) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        post.decreaseLikeCount();
    }

    @Transactional
    public void increaseReportCount(Long postId) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        post.increaseReportCount();
    }

    @Transactional(readOnly = true)
    public int getReportCountByPostId(Long postId) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        return post.getReportCount();
    }

    @Transactional(readOnly = true)
    public Long getWriterIdByPostId(Long postId) {
        return postRepository.findUserIdByPostId(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Post getPost(Long postId) {
        return postRepository.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }
    
    @Transactional(readOnly = true)
    public String getPostTitle(Long postId) {
        Post post = getPost(postId);
        return post.getTitle();
    }

}