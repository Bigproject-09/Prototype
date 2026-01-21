package com.example.agent_rnd.domain.ai.job;

import com.example.agent_rnd.domain.artifact.Artifact;
import com.example.agent_rnd.domain.proposal.Proposal;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder // [추가]
@AllArgsConstructor(access = AccessLevel.PRIVATE) // [추가]
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JOBS")
public class Job {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false)
    private Proposal proposal;

    @Enumerated(EnumType.STRING)
    @Column(name = "step", length = 50, nullable = false)
    private JobStep step;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private JobStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "input_artifact_id")
    private Artifact inputArtifact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "output_artifact_id")
    private Artifact outputArtifact;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}