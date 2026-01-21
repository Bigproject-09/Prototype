package com.example.agent_rnd.domain.requirement;

import com.example.agent_rnd.domain.artifact.Artifact;
import com.example.agent_rnd.domain.proposal.Proposal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "REQUIREMENT_PROGRESS")
public class RequirementProgress {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    // [중요] DB 스키마의 오타(propress_id) 반영
    @Column(name = "propress_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false)
    private Proposal proposal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id", nullable = false)
    private Requirement requirement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_artifact_id")
    private Artifact evidenceArtifact;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}