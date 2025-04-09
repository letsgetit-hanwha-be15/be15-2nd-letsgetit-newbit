package com.newbit.user.dto.response;

import com.newbit.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class UserIdDTO {
    private String email;

    public static UserIdDTO from(User user) {
        return new UserIdDTO(user.getEmail());
    }
}
