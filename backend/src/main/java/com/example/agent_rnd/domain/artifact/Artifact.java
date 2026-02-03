package com.example.agent_rnd.domain.artifact;

import com.example.agent_rnd.domain.enums.ArtifactType;
import com.example.agent_rnd.domain.presentation.Presentation;
import com.example.agent_rnd.domain.script.Script;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ARTIFACTS")
public class Artifact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artifact_id")
    private Long id;

    // 발표자료와 연결 (N:1)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presentation_id", nullable = false)
    private Presentation presentation;

    // 스크립트와 연결 (N:1) - DB의 script_id와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_id", nullable = false)
    private Script script;

    // [수정] Enums 패키지 사용
    @Enumerated(EnumType.STRING)
    @Column(name = "artifact_type", length = 50)
    private ArtifactType artifactType;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl; // S3 URL

    // fileName 삭제
//    @Column(name = "file_name")
//    private String fileName;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // [수정] 변경된 필드에 맞게 수정 (fileName 제거, script 추가)
    @Builder
    public Artifact(Presentation presentation, Script script, ArtifactType artifactType, String fileUrl) {
        this.presentation = presentation;
        this.script = script; // 추가됨
        this.artifactType = artifactType;
        this.fileUrl = fileUrl;
    }
}