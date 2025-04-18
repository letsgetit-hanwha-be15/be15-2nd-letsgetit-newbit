package com.newbit.newbituserservice.auth.repository;

import com.newbit.newbituserservice.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
