package com.newbit.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private Integer point = 0;

    @Column(nullable = false)
    private Integer diamond = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority role = Authority.USER;

    @Column(nullable = false)
    private Boolean isSuspended = false;

    private String profileImageUrl;

    public void setEncodedPassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
