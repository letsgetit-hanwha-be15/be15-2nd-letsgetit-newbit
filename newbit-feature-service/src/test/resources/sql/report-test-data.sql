-- 테스트 데이터 초기화
DELETE FROM tb_report;
-- DELETE FROM tb_report_type; -- 실제 데이터 사용을 위해 삭제하지 않음

-- 신고 데이터
INSERT INTO tb_report (id, user_id, post_id, comment_id, report_type_id, content, status, created_at, updated_at) VALUES
-- 게시글 신고
(1, 100, 1, NULL, 1, '욕설이 포함된 게시글입니다', 'PENDING', NOW(), NOW()),
(2, 101, 1, NULL, 2, '불법 콘텐츠가 포함된 게시글입니다', 'PENDING', NOW(), NOW()),
(3, 102, 2, NULL, 5, '광고성 게시글입니다', 'PROCESSED', NOW(), NOW()),
-- 댓글 신고
(4, 100, NULL, 1, 1, '욕설이 포함된 댓글입니다', 'PENDING', NOW(), NOW()),
(5, 101, NULL, 2, 10, '허위 정보가 포함된 댓글입니다', 'PROCESSED', NOW(), NOW()),
(6, 102, NULL, 1, 3, '음란물이 포함된 댓글입니다', 'PENDING', NOW(), NOW()),
-- 여러 조건 조합 테스트용
(7, 103, 3, NULL, 5, '스팸 게시글입니다', 'PENDING', NOW(), NOW()),
(8, 104, NULL, 3, 5, '스팸 댓글입니다', 'PENDING', NOW(), NOW()); 