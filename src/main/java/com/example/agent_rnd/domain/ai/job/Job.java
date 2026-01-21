package com.example.agent_rnd.domain.ai.job;

import com.example.agent_rnd.domain.artifact.Artifact;
import com.example.agent_rnd.domain.proposal.Proposal;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JOBS")
public class Job {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false)
    private Proposal proposal;

    // [중요] CSV 명세의 'step' 컬럼 (PARSE, REQUIREMENTS 등)
    @Enumerated(EnumType.STRING)
    @Column(name = "step", length = 50, nullable = false)
    private JobStep step;

    // [중요] CSV 명세의 'status' 컬럼 (RUNNING, DONE, FAILED)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private JobStatus status;

    // [중요] 입력 산출물 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "input_artifact_id")
    private Artifact inputArtifact;

    // [중요] 출력 산출물 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "output_artifact_id")
    private Artifact outputArtifact;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    // [중요] 재시도 횟수 (기본값 0)
    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}