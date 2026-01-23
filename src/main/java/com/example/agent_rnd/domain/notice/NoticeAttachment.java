package com.example.agent_rnd.domain.notice;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "NOTICE_ATTACHMENTS") // DB가 이렇게 되어있다면 그대로, 아니면 NOTICE_ATTACHMENTS로 수정
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

    @Column(name = "origin_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "file_path", nullable = false, length = 1000)
    private String filePath;     // 저장 키/경로(S3 key든 로컬 path든)

    // 운영 확장(ERD에 아직 없으면 나중에 컬럼 추가)
    // @Column(name = "mime_type", length = 100) private String mimeType;
    // @Column(name = "file_size") private Long fileSize;
    // @Column(name = "checksum", length = 128) private String checksum;
}
