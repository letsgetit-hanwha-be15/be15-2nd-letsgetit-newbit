-- 기존 데이터가 있으면 삭제
DELETE FROM post;

-- 테스트용 게시글 카테고리가 필요하다면 추가
INSERT INTO post_category (post_category_id, category_name) 
VALUES (1, '일반 게시글') ON DUPLICATE KEY UPDATE category_name = '일반 게시글';

-- 테스트용 게시글 데이터 삽입
INSERT INTO post (post_id, user_id, post_category_id, title, content, is_notice, created_at, updated_at) 
VALUES (1, 1, 1, '테스트 게시글 1', '테스트 게시글 내용입니다.', false, NOW(), NOW());

INSERT INTO post (post_id, user_id, post_category_id, title, content, is_notice, created_at, updated_at) 
VALUES (2, 2, 1, '테스트 게시글 2', '두 번째 테스트 게시글 내용입니다.', false, NOW(), NOW()); 