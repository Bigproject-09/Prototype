package com.example.agent_rnd.repository;

import com.example.agent_rnd.domain.ai.job.Job;
import com.example.agent_rnd.domain.ai.job.JobStatus; // Status Enum import
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    // 특정 제안서의 모든 작업 조회
    List<Job> findByProposalId(Long proposalId);

    // 상태별 조회 (예: 실행 중인 작업 찾기)
    List<Job> findByStatus(JobStatus status);
}