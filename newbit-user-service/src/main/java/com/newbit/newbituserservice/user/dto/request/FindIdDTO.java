package com.newbit.newbituserservice.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class FindIdDTO {
    private String userName;
    private String phoneNumber;

}
