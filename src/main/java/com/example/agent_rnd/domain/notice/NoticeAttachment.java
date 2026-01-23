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

    // ★ mappedBy="notice" 와 맞춰서 필드명은 notice
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private ProjectNotice notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ERD: original_name(또는 origin_name) — 지금 DB가 origin_name이면 그대로 유지
    @Column(name = "origin_name", nullable = false, length = 255)
    private String originName;

    @Column(name = "parsed_json", columnDefinition = "json")
    private String parsedJson; // NULL 허용

    @Column(name = "parse_status", nullable = false, length = 20)
    private String parseStatus; // WAIT, PROCESSING, DONE, FAILED

    @Column(name = "error_msg", columnDefinition = "TEXT")
    private String errorMsg; // NULL 허용

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
