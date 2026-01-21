package com.example.agent_rnd.domain.feedback;

import com.example.agent_rnd.domain.ai.AiModel;
import com.example.agent_rnd.domain.proposal.Proposal;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PROPOSAL_FEEDBACKS")
public class ProposalFeedback {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    // [중요] DB 컬럼명이 proposal_fb_id 입니다.
    @Column(name = "proposal_fb_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false)
    private Proposal proposal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private AiModel aiModel;

    @Column(name = "score")
    private Integer score;

    @Lob
    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    // JSON 타입은 String으로 매핑 (필요시 별도 컨버터 사용)
    @Column(name = "detail", columnDefinition = "JSON")
    private String detail;

    @Column(name = "total_tokens", nullable = false)
    private Integer totalTokens;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}