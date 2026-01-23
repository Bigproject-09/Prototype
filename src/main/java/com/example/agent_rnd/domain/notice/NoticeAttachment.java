package com.example.agent_rnd.domain.notice;

import com.example.agent_rnd.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "NOTICE_ATTACHMENTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private ProjectNotice notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ERD: "파일명/경로" 역할 (샘플에 경로처럼 들어가도 컬럼명은 original_name)
    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    // MySQL JSON 컬럼
    @Column(name = "parsed_json", columnDefinition = "json")
    private String parsedJson; // ERD: NULL 허용

    // WAIT, PROCESSING, DONE, FAILED (enum 안 만들고 문자열로 맞춤)
    @Column(name = "parse_status", nullable = false, length = 20)
    private String parseStatus;

    @Column(name = "error_msg", columnDefinition = "TEXT")
    private String errorMsg; // ERD 상 실패시에만 존재하는 게 자연스러워서 NULL 허용

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
