package com.newbit.newbituserservice.user.controller;

import com.newbit.newbituserservice.common.dto.ApiResponse;
import com.newbit.newbituserservice.user.support.MailServiceSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/mail")
public class MailSupportController {

    private final MailServiceSupport mailServiceSupport;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Void>> sendMail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String content
    ) {
        mailServiceSupport.sendMailSupport(to, subject, content);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}