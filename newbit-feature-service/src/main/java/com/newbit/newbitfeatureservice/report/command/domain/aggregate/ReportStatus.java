package com.newbit.newbitfeatureservice.report.command.domain.aggregate;

public enum ReportStatus {
    SUBMITTED,    // 제출됨 (초기 상태)
    PENDING,      // 대기 중
    UNDER_REVIEW, // 검토 중 (관리자가 해당 신고글을 읽음)
    SUSPENDED,    // 일시 정지
    NOT_VIOLATED, // 정책 위반 없음
    DELETED       // 삭제됨
}
