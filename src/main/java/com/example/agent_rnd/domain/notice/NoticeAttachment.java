package com.example.agent_rnd.domain.notice;

import com.example.agent_rnd.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notice_attachments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class NoticeAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long attachmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * notice_attachments.file_id 는 UNIQUE 제약.
     * 파일 1개당 첨부(파싱) 결과 1개.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false, unique = true)
    @Setter(AccessLevel.PACKAGE)
    private NoticeFile file;

    @Column(name = "parsed_json", columnDefinition = "json")
    private String parsedJson;

    @Column(name = "parse_status", nullable = false, length = 20)
    private String parseStatus; // WAIT, PROCESSING, DONE, FAILED

    @Column(name = "error_msg", columnDefinition = "TEXT")
    private String errorMsg;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static NoticeAttachment create(User user, NoticeFile file, String parseStatus) {
        NoticeAttachment a = NoticeAttachment.builder()
                .user(user)
                .file(file)
                .parseStatus(parseStatus)
                .build();
        return a;
    }

    public void markDone(String parsedJson) {
        this.parseStatus = "DONE";
        this.parsedJson = parsedJson;
        this.errorMsg = null;
    }

    public void markFailed(String errorMsg) {
        this.parseStatus = "FAILED";
        this.errorMsg = errorMsg;
    }
}
