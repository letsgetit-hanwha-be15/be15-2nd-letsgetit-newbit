package com.newbit.newbitfeatureservice.post.controller;


import com.newbit.newbitfeatureservice.post.service.ReportCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportCountService reportCountService;

    @GetMapping("/report-count/{userId}")
    public int getTotalReportCountByUserId(@PathVariable Long userId) {
        return reportCountService.getTotalReportCountByUserId(userId);
    }
}
