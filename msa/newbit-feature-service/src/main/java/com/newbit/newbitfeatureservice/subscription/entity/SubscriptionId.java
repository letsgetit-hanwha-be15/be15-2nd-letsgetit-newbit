package com.newbit.newbitfeatureservice.subscription.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SubscriptionId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long userId;
    private Long seriesId;
} 