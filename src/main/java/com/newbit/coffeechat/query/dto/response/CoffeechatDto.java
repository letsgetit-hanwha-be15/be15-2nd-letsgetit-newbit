package com.newbit.coffeechat.query.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CoffeechatDto {
    private Long coffeechatId;
    private ProgressStatus progressStatus;
    private String requestMessage;
    private LocalDateTime confirmedSchedule;
    private LocalDateTime endedAt;
    private Long mentorId;
    private Double temperature;
    private UserDto mentor;
    private UserDto mentee;
}
