package com.example.agent_rnd.domain.artifact;

import com.example.agent_rnd.domain.proposal.Proposal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ARTIFACTS")
public class Artifact {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artifact_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false)
    private Proposal proposal;

    @Column(nullable = false)
    private Long requirementId; // 어떤 요구사항 관련 파일인지 (선택적)

    @Column(name = "type", nullable = false, length = 50)
    private String type; // UPLOADED_DOC, GENERATED_PDF, JSON_ANALYSIS

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}