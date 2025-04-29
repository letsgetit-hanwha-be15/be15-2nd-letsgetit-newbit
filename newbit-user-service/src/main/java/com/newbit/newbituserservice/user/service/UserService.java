package com.newbit.newbituserservice.user.service;


import com.newbit.newbituserservice.common.exception.BusinessException;
import com.newbit.newbituserservice.common.exception.ErrorCode;
import com.newbit.newbituserservice.user.dto.request.FindIdDTO;
import com.newbit.newbituserservice.user.dto.request.UserRequestDTO;
import com.newbit.newbituserservice.user.dto.response.UserDTO;
import com.newbit.newbituserservice.user.dto.response.UserIdDTO;
import com.newbit.newbituserservice.user.entity.*;
import com.newbit.newbituserservice.user.repository.JobRepository;
import com.newbit.newbituserservice.user.repository.TechstackRepository;
import com.newbit.newbituserservice.user.repository.UserAndTechstackRepository;
import com.newbit.newbituserservice.user.repository.UserRepository;
import com.newbit.newbituserservice.user.support.MailServiceSupport;
import com.newbit.newbituserservice.user.support.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final TechstackRepository techstackRepository;
    private final UserAndTechstackRepository  userAndTechstackRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private  final MailServiceSupport mailServiceSupport;

    // 회원 가입
    @Transactional
    public void registerUser(UserRequestDTO request) {
        // 중복 회원 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }
        if (!PasswordValidator.isValid(request.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_PHONENUMBER);
        }
        if (request.getPhoneNumber() == null || !request.getPhoneNumber().matches("\\d{10,11}")) {
            throw new BusinessException(ErrorCode.INVALID_PHONE_NUMBER_FORMAT);
        }

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new BusinessException(ErrorCode.ALREADY_REGISTERED_NICKNAME);
        }

        // 회원 가입
        User user = modelMapper.map(request, User.class);
        user.setEncodedPassword(passwordEncoder.encode(request.getPassword()));

        // jobName이 null이 아닐 때만 조회 후 세팅
        if (request.getJobName() != null) {
            Job job = jobRepository.findByJobName(request.getJobName())
                    .orElseThrow(() -> new BusinessException(ErrorCode.JOB_NOT_FOUND));
            user.setJobId(job.getJobId());
        } else {
            user.setJobId(null); // 명시적으로 null 세팅 (선택)
        }

        User savedUser = userRepository.save(user);

        // techstackNames가 null이 아니고 비어있지 않을 때만 처리
        if (request.getTechstackNames() != null && !request.getTechstackNames().isEmpty()) {
            for (String techstackName : request.getTechstackNames()) {
                Techstack techstack = techstackRepository.findByTechstackName(techstackName)
                        .orElseThrow(() -> new BusinessException(ErrorCode.TECHSTACK_NOT_FOUND));

                UserAndTechstack mapping = UserAndTechstack.builder()
                        .id(new UserAndTechstackId(savedUser.getUserId(), techstack.getTechstackId()))
                        .build();

                userAndTechstackRepository.save(mapping);
            }
        }
    }


    public UserIdDTO findEmailByNameAndPhone(FindIdDTO findIdDTO) {
        return userRepository.findByUserNameAndPhoneNumber(findIdDTO.getUserName(), findIdDTO.getPhoneNumber())
                .map(UserIdDTO::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.FIND_EMAIL_BY_NAME_AND_PHONE_ERROR));
    }

    @Transactional
    public void findPasswordByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String tempPassword = generateTempPassword();
        user.findPassword(passwordEncoder.encode(tempPassword));

        sendTemporaryPassword(email, tempPassword);
    }

    private String generateTempPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public void sendTemporaryPassword(String toEmail, String tempPassword) {
        String subject = "Newbit 임시 비밀번호 안내";
        String content = "<p>안녕하세요!,</p>" +
                "<p>임시 비밀번호는 다음과 같습니다:</p>" +
                "<h3>" + tempPassword + "</h3>" +
                "<p>로그인 후 비밀번호를 반드시 변경해주세요.</p>";

        mailServiceSupport.sendMailSupport(toEmail, subject, content);
    }



    @Transactional
    public Integer useDiamond(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getDiamond() < amount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_DIAMOND);
        }

        user.useDiamond(amount);// 도메인 로직에 위임 (Entity 내부에 구현된 로직)
        return user.getDiamond();
    }

    @Transactional
    public Integer addDiamond(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.addDiamond(amount);// 도메인 로직에 위임 (Entity 내부에 구현된 로직)
        return user.getDiamond();
    }

    @Transactional
    public Integer usePoint(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getPoint() < amount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_POINT);
        }

        user.usePoint(amount);// 도메인 로직에 위임 (Entity 내부에 구현된 로직)
        return user.getPoint();
    }

    @Transactional
    public Integer addPoint(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.addPoint(amount);// 도메인 로직에 위임 (Entity 내부에 구현된 로직)
        return user.getPoint();
    }


    public UserDTO getUserByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return UserDTO.builder()
                .userId(userId)
                .authority(user.getAuthority())
                .diamond(user.getDiamond())
                .point(user.getPoint())
                .build();
    }

}
