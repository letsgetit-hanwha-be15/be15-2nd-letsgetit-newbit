package com.newbit.newbituserservice.user.repository;

import com.newbit.newbituserservice.user.entity.UserAndTechstack;
import com.newbit.newbituserservice.user.entity.UserAndTechstackId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAndTechstackRepository extends JpaRepository<UserAndTechstack, UserAndTechstackId> {
}