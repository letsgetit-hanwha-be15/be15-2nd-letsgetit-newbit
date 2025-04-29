package com.newbit.newbituserservice.user.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MentorIntroduceInfoDTO {

    // 내 소개
    private String introduction;

    // 외부 소개 링크
    private String externalLinkUrl;
}