package com.newbit.user.repository;

import com.newbit.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserNameAndPhoneNumber(String userName, String phoneNumber);
    Optional<User> findByEmail(String email);

    default User getById(Long userId){
        return findById(userId).orElseThrow();
    }

    boolean existsByEmail(String email);
}