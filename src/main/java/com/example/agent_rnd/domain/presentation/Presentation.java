package com.example.agent_rnd.domain.presentation;

import com.example.agent_rnd.domain.enums.PresentationStatus;
import com.example.agent_rnd.domain.proposal.Proposal;

import jakarta.persistence.*;
import lombok.*;
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

    // 제안서와 연결 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false)
    private Proposal proposal;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private String theme;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PresentationStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Presentation(Proposal proposal, Integer version, String theme) {
        this.proposal = proposal;
        this.version = version;
        this.theme = theme;
        this.status = PresentationStatus.GENERATING; // 기본값: 생성중
    }

    // 상태 변경 메서드
    public void complete() {
        this.status = PresentationStatus.COMPLETED;
    }

    public void fail() {
        this.status = PresentationStatus.FAILED;
    }
}