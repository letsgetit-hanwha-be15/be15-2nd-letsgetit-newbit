package com.newbit.newbituserservice.user.repository;

import com.newbit.newbituserservice.user.entity.Mentor;
import com.newbit.newbituserservice.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
    Optional<Mentor> findByUser(User user);

    Optional<Mentor> findByUser_UserId(Long userId);
}