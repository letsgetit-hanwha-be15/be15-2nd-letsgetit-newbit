package com.newbit.newbituserservice.user.repository;

import com.newbit.newbituserservice.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    Optional<User> findByUserNameAndPhoneNumber(String userName, String phoneNumber);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Long userid);
    boolean existsByNickname(String nickname);
    boolean existsByPhoneNumber(String phoneNumber);
}