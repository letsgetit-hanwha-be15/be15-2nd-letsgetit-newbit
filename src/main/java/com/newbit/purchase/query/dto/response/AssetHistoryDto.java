package com.newbit.purchase.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetHistoryDto {

    private Long historyId;

    private String serviceType;

    private Long serviceId;

    private Integer increaseAmount;

    private Integer decreaseAmount;

    private int balance;

    private LocalDateTime createdAt;
}
