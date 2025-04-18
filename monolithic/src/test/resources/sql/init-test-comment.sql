-- 기존 데이터가 있으면 삭제
DELETE FROM comment;

-- 테스트용 댓글 데이터 삽입
INSERT INTO comment (comment_id, user_id, post_id, content, created_at, updated_at) 
VALUES (1, 1, 1, '첫 번째 댓글입니다.', NOW(), NOW());

INSERT INTO comment (comment_id, user_id, post_id, content, created_at, updated_at) 
VALUES (2, 2, 1, '두 번째 댓글입니다.', NOW(), NOW());

INSERT INTO comment (comment_id, user_id, post_id, content, created_at, updated_at) 
VALUES (3, 1, 2, '다른 게시글의 댓글입니다.', NOW(), NOW()); 