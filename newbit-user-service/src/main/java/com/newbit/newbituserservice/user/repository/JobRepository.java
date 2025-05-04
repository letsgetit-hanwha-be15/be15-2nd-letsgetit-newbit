package com.newbit.newbituserservice.user.repository;

import com.newbit.newbituserservice.user.entity.Job;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    Optional<Job> findByJobName(String jobName);
    List<Job> findAllByOrderByJobIdAsc();
}