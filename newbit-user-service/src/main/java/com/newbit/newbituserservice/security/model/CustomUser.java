package com.newbit.newbituserservice.security.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class CustomUser implements UserDetails {

    private final Long userId;
    private final String email;
    private final String password;
    private final String nickname;   // ✅ 추가
    private final Integer point;     // ✅ 추가
    private final Integer diamond;   // ✅ 추가
    private final Long mentorId;     // ✅ 추가
    private final Collection<? extends GrantedAuthority> authorities;

    @Builder
    public CustomUser(Long userId,
                      String email,
                      String password,
                      String nickname,
                      Integer point,
                      Integer diamond,
                      Long mentorId,
                      Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.point = point;
        this.diamond = diamond;
        this.mentorId = mentorId;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
