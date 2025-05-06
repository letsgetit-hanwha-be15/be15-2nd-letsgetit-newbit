package com.newbit.newbitfeatureservice.payment.command.application.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateOrderRequest {
    private String orderId;
    private Long userId;
    private String orderName;
    private BigDecimal amount;
} 