package com.newbit.newbituserservice.user.service;


import com.newbit.newbituserservice.common.exception.BusinessException;
import com.newbit.newbituserservice.common.exception.ErrorCode;
import com.newbit.newbituserservice.user.dto.response.MentorDTO;
import com.newbit.newbituserservice.user.entity.Mentor;
import com.newbit.newbituserservice.user.entity.User;
import com.newbit.newbituserservice.user.repository.MentorRepository;
import com.newbit.newbituserservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentorService {

    private final MentorRepository mentorRepository;
    private final UserRepository userRepository;

    public MentorDTO getMentorInfo(Long mentorId) {
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return new MentorDTO(mentor.getIsActive(), mentor.getPrice());
    }

    @Transactional
    public void createMentor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Mentor mentor = Mentor.createDefault(user);
        user.grantMentorAuthority();
        mentorRepository.save(mentor);
    }

    public Long getUserIdByMentorId(Long mentorId) {
        return mentorRepository.findById(mentorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENTOR_NOT_FOUND))
                .getUser().getUserId();
    }

    public Long getMentorIdByUserId(Long userId) {
        return mentorRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)).getMentorId();
    }
}
