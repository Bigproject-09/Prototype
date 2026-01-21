package com.example.agent_rnd.service;

import com.example.agent_rnd.domain.ai.job.Job;
import com.example.agent_rnd.domain.ai.job.JobStatus;
import com.example.agent_rnd.domain.ai.job.JobStep;
import com.example.agent_rnd.domain.proposal.Proposal;
import com.example.agent_rnd.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiJobService {

    private final JobRepository jobRepository;

    /**
     * AI 작업을 생성하고 DB에 저장 (파이썬 서버가 이걸 가져감)
     */
    @Transactional
    public void createJob(Proposal proposal, JobStep step) {
        Job job = Job.builder()
                .proposal(proposal)
                .step(step) // 예: PARSE, GENERATE
                .status(JobStatus.RUNNING) // 생성되자마자 실행 대기 상태
                .attemptCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        jobRepository.save(job);
    }
}