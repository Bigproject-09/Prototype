package com.example.agent_rnd.domain.presentation;

import com.example.agent_rnd.domain.enums.PresentationStatus;
import com.example.agent_rnd.domain.proposal.Proposal;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "PRESENTATIONS")
public class Presentation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "presentation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false)
    private Proposal proposal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PresentationStatus status; // GENERATING, COMPLETED, FAILED

    @Column(name = "total_tokens")
    private Integer totalTokens;

    @Column(nullable = false)
    private Integer version;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Presentation(Proposal proposal, Integer version) {
        this.proposal = proposal;
        this.version = version;
        this.status = PresentationStatus.GENERATING; // 기본값
    }

    public static Presentation create(Proposal proposal, Integer version) {
        return Presentation.builder()
                .proposal(proposal)
                .version(version)
                .build();
    }

    public void completeCreation(int totalTokens) {
        this.status = PresentationStatus.COMPLETED;
        this.totalTokens = totalTokens;
    }

    public void failCreation() {
        this.status = PresentationStatus.FAILED;
    }
}