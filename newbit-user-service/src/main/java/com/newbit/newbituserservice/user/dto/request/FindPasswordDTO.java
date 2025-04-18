package com.newbit.newbituserservice.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class FindPasswordDTO {
    private String email;
}
