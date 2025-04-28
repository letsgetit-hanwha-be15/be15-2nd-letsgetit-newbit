package com.newbit.newbituserservice.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAndTechstackId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "techstack_id")
    private Long techstackId;
}
