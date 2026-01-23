package com.example.agent_rnd.domain.proposal;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "PROPOSALS")
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proposal_id")
    private Long id;

    // 타 도메인 연결
    @Column(name = "notice_id", nullable = false)
    private Long noticeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "parsed_json", nullable = false, columnDefinition = "json")
    private String parsedJson;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Proposal(Long noticeId, Long userId, String title, String fileName, String parsedJson) {
        this.noticeId = noticeId;
        this.userId = userId;
        this.title = title;
        this.fileName = fileName;
        this.parsedJson = parsedJson;
    }
}