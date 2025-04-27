package com.newbit.newbituserservice.user.repository;

import com.newbit.newbituserservice.user.entity.Techstack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TechstackRepository extends JpaRepository<Techstack, Long> {
    Optional<Techstack> findByTechstackName(String techstackName);
}

