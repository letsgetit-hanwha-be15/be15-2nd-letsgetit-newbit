-- 기존 데이터가 있다면 삭제
DELETE FROM report_type;

-- 신고 유형 데이터 삽입
INSERT INTO report_type (report_type_id, name, description) VALUES (1, 'SPAM', '스팸 신고');
INSERT INTO report_type (report_type_id, name, description) VALUES (2, 'ABUSE', '욕설/비방 신고');
INSERT INTO report_type (report_type_id, name, description) VALUES (3, 'INAPPROPRIATE', '부적절한 내용 신고');
INSERT INTO report_type (report_type_id, name, description) VALUES (4, 'COPYRIGHT', '저작권 침해 신고');
INSERT INTO report_type (report_type_id, name, description) VALUES (5, 'OTHER', '기타 신고'); 