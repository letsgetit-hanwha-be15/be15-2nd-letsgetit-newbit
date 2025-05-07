package com.newbit.newbituserservice.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BalanceDTO {
    private int diamondBalance;
    private int pointBalance;
}
