package com.newbit.newbituserservice.auth.repository;

import com.newbit.newbituserservice.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
