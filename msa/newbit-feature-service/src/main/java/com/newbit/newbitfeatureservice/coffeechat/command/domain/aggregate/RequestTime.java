package com.newbit.newbitfeatureservice.coffeechat.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "request_time")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
@ToString
public class RequestTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_time_id")
    private Long requestTimeId;
    @Column(name = "event_date")
    private LocalDate eventDate;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;
    @Column(name = "coffeechat_id")
    private Long coffeechatId;
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static RequestTime of(@NotNull LocalDate eventDate, LocalDateTime startTime, LocalDateTime endTime, Long coffeechatId) {
        RequestTime requestTime = new RequestTime();
        requestTime.endTime = endTime;
        requestTime.startTime = startTime;
        requestTime.eventDate = eventDate;
        requestTime.coffeechatId = coffeechatId;

        return requestTime;
    }
}
