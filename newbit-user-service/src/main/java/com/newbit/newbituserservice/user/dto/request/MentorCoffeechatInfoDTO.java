package com.newbit.newbituserservice.user.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MentorCoffeechatInfoDTO {
    private Boolean isActive;
    private String preferredTime;
    private Integer price;
}
