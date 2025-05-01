package com.newbit.newbituserservice.user.repository;

import com.newbit.newbituserservice.user.dto.response.MentorDTO;
import com.newbit.newbituserservice.user.entity.Mentor;
import com.newbit.newbituserservice.user.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Long> {

    @Query("SELECT new com.newbit.newbituserservice.user.dto.response.MentorDTO(" +
            "m.isActive, m.price, m.preferredTime, m.temperature, " +
            "u.nickname, u.profileImageUrl, " +
            "(SELECT j.jobName FROM Job j WHERE j.jobId = u.jobId), " +
            "m.introduction, m.externalLinkUrl) " +
            "FROM Mentor m " +
            "JOIN m.user u " +
            "WHERE m.mentorId = :mentorId")
    Optional<MentorDTO> findMentorDTOByMentorId(@Param("mentorId") Long mentorId);

    Optional<Mentor> findByUser_UserId(Long userId);
}