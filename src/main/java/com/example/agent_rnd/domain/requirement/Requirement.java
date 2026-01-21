package com.example.agent_rnd.domain.requirement;

import com.example.agent_rnd.domain.notice.ProjectNotice;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "REQUIREMENTS")
public class Requirement {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requirement_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private ProjectNotice projectNotice;

    // QUALIFICATION(자격), CONSTRAINT(제약), SECTION_WRITE(작성)
    @Column(name = "req_type", nullable = false, length = 50)
    private String reqType;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description; // AI가 분석한 지침

    @Column(name = "source_text", columnDefinition = "TEXT")
    private String sourceText; // 원본 텍스트

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}