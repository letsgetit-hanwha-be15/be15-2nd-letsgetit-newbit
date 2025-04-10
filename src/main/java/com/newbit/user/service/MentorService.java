package com.newbit.user.service;

import com.newbit.common.exception.BusinessException;
import com.newbit.common.exception.ErrorCode;
import com.newbit.user.dto.response.MentorDTO;
import com.newbit.user.entity.Mentor;
import com.newbit.user.entity.User;
import com.newbit.user.repository.MentorRepository;
import com.newbit.user.repository.UserRepository;
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

    public Mentor getMentorEntityById(Long mentorId) {
        return mentorRepository.findById(mentorId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENTOR_NOT_FOUND));
    }
}
