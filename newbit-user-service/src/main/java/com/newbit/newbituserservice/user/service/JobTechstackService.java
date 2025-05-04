package com.newbit.newbituserservice.user.service;

import com.newbit.newbituserservice.user.dto.response.JobDTO;
import com.newbit.newbituserservice.user.dto.response.TechstackDTO;
import com.newbit.newbituserservice.user.repository.JobRepository;
import com.newbit.newbituserservice.user.repository.TechstackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobTechstackService {
    private final JobRepository jobRepository;
    private final TechstackRepository  techstackRepository;

    public List<JobDTO> getAllJobs() {
        return jobRepository.findAllByOrderByJobIdAsc().stream()
                .map(job -> new JobDTO(job.getJobId(), job.getJobName()))
                .collect(Collectors.toList());
    }

    public List<TechstackDTO> getAllTechstacks() {
        return techstackRepository.findAllByOrderByTechstackIdAsc().stream()
                .map(t -> new TechstackDTO(t.getTechstackId(), t.getTechstackName()))
                .collect(Collectors.toList());
    }
}
